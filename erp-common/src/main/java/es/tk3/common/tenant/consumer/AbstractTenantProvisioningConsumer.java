package es.tk3.common.tenant.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import es.tk3.common.tenant.DynamicRoutingDataSource;
import es.tk3.common.tenant.flyway.TenantFlywayInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Map;

public abstract class AbstractTenantProvisioningConsumer {
    private static final Logger log = LoggerFactory.getLogger(AbstractTenantProvisioningConsumer.class);

    protected final ObjectMapper mapper;
    protected final DataSource dataSource;

    public AbstractTenantProvisioningConsumer(ObjectMapper mapper, DataSource dataSource) {
        this.mapper = mapper;
        this.dataSource = dataSource;
    }

    protected void processTenantProvisioning(String payload, String flywayLocations, String historyTable, String moduleName) {
        try {
            JsonNode eventNode = mapper.readTree(payload);
            String eventType = eventNode.path("type").asText();
            if (!"CREATED".equalsIgnoreCase(eventType) && !"TENANT_CREATED".equalsIgnoreCase(eventType)) {
                return;
            }
            String tenantId = eventNode.path("tenantId").asText();
            String dbUrl = eventNode.path("dbUrl").asText();
            String dbUsername = eventNode.path("dbUsername").asText();
            String dbPassword = eventNode.path("dbPassword").asText();

            TenantFlywayInitializer.migrate(tenantId, dbUrl, dbUsername, dbPassword, flywayLocations, historyTable);

            DynamicRoutingDataSource routingDS = this.dataSource.unwrap(DynamicRoutingDataSource.class);

            try {
                Map<Object, DataSource> dataSources = routingDS.getResolvedDataSources();
                if (dataSources != null && dataSources.containsKey(tenantId)) {
                    log.info("[{}] El tenant {} ya tiene un pool. Cerrando viejo para evitar fugas.", moduleName, tenantId);
                    DataSource oldDs = dataSources.get(tenantId);
                    if (oldDs instanceof HikariDataSource) {
                        ((HikariDataSource) oldDs).close();
                    }
                }
            } catch (Exception e) {
                log.warn("[{}] No se pudo verificar pool previo para {}: {}", moduleName, tenantId, e.getMessage());
            }

            HikariConfig runtimeConfig = new HikariConfig();
            runtimeConfig.setJdbcUrl(dbUrl);
            runtimeConfig.setUsername(dbUsername);
            runtimeConfig.setPassword(dbPassword);
            runtimeConfig.setDriverClassName("org.postgresql.Driver");

            runtimeConfig.setMaximumPoolSize(2);
            runtimeConfig.setMinimumIdle(0);
            runtimeConfig.setIdleTimeout(30000);
            runtimeConfig.setConnectionTimeout(30000);

            String poolName = "HikariPool-" + moduleName + "-" + tenantId;
            runtimeConfig.setPoolName(poolName);
            runtimeConfig.addDataSourceProperty("ApplicationName", poolName);

            HikariDataSource runtimeDataSource = new HikariDataSource(runtimeConfig);
            routingDS.addDataSource(tenantId, runtimeDataSource);

            log.info("✅ [{}] Tenant {} migrado y registrado en el enrutador con éxito.", moduleName, tenantId);

        } catch (Exception e) {
            log.error("❌ [{}] Error procesando tenant: {}", moduleName, e.getMessage());
        }
    }
}