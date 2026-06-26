package es.tk3.common.tenant.flyway;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import es.tk3.common.tenant.DynamicRoutingDataSource;
import jakarta.annotation.PostConstruct;
import org.flywaydb.core.Flyway;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class TenantMigrationService {

    private static final Logger log = LoggerFactory.getLogger(TenantMigrationService.class);

    @Autowired
    private DataSource centralDataSource;

    @Value("${app.tenant.datasource.base-url}")
    private String tenantBaseUrl;

    @Value("${app.tenant.datasource.username}")
    private String tenantUsername;

    @Value("${app.tenant.datasource.password}")
    private String tenantPassword;

    @Value("${spring.application.name:APP}")
    private String appName;

    @PostConstruct
    public void migrateCentral() {
        try {
            Flyway flyway = Flyway.configure()
                    .dataSource(centralDataSource)
                    .locations("classpath:db/migration/main")
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();
            log.info("✅ [FLYWAY CENTRAL] Base de datos maestra actualizada.");
        } catch (Exception e) {
            log.error("❌ [FLYWAY CENTRAL] Falla crítica al migrar la DB Central: {}", e.getMessage(), e);
            throw new RuntimeException("No se puede arrancar la app sin la estructura de erp_central", e);
        }
    }

     public void migrateAllTenants(DataSource dataSource, String migrationLocation, String historyTable) {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        List<String> tenants = jdbc.queryForList("SELECT id FROM tenants", String.class);

        for (String tenantId : tenants) {
            migrateTenantInternal(tenantId, migrationLocation, historyTable, false);
        }
    }

    public void migrateSingleTenant(String tenantId, String migrationLocation, String historyTable) {
        log.info("🚀 Iniciando migración instantánea para el nuevo tenant: {}", tenantId);
        migrateTenantInternal(tenantId, migrationLocation, historyTable, true);
    }

 
    private void migrateTenantInternal(String tenantId, String migrationLocation, String historyTable, boolean throwError) {
        HikariDataSource tenantDataSource = getHikariDataSource(tenantId);

        Flyway flyway = Flyway.configure()
                .dataSource(tenantDataSource)
                .locations(migrationLocation)
                .table(historyTable)
                .baselineOnMigrate(true)
                .outOfOrder(true)
                .ignoreMigrationPatterns("*:missing", "*:ignored", "*:future")
                .load();

        try {
            log.info("🛠️ [FLYWAY] Aplicando scripts en tenant: {} (Carpeta: {})", tenantId, migrationLocation);
            flyway.migrate();
            log.info("✅ Esquema procesado con éxito para: {}", tenantId);

            DynamicRoutingDataSource router;
            if (centralDataSource.isWrapperFor(DynamicRoutingDataSource.class)) {
                router = centralDataSource.unwrap(DynamicRoutingDataSource.class);
            } else if (centralDataSource instanceof DynamicRoutingDataSource) {
                router = (DynamicRoutingDataSource) centralDataSource;
            } else {
                throw new IllegalStateException("El DataSource primario no es un DynamicRoutingDataSource");
            }

            router.addDataSource(tenantId, tenantDataSource);

        } catch (Exception e) {
            log.error("⚠️ [FLYWAY] Error en tenant {}: {}", tenantId, e.getMessage());

            tenantDataSource.close();

            try {
                flyway.repair();
            } catch (Exception ex) {
                log.error("No se pudo reparar la tabla de historial: {}", ex.getMessage());
            }

            if (throwError) {
                throw new RuntimeException("Fallo crítico al inicializar el esquema del inquilino: " + tenantId, e);
            }
        }
    }

    private @NonNull HikariDataSource getHikariDataSource(String tenantId) {
        String dbUrl = tenantBaseUrl + "db_" + tenantId;

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(tenantUsername);
        config.setPassword(tenantPassword);
        config.setDriverClassName("org.postgresql.Driver");


        config.setMaximumPoolSize(2);
        config.setMinimumIdle(0);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);

        String poolName = "TenantPool-" + appName.toUpperCase() + "-" + tenantId.toUpperCase();
        config.setPoolName(poolName);
        config.addDataSourceProperty("ApplicationName", poolName);

        return new HikariDataSource(config);
    }
}