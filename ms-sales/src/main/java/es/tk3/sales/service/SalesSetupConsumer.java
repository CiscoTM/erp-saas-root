package es.tk3.sales.service;

import es.tk3.common.model.TenantUser;
import es.tk3.common.repository.TenantUserRepository;
import es.tk3.common.tenant.DynamicRoutingDataSource;
import es.tk3.common.tenant.TenantContext;
import es.tk3.sales.dto.TenantEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException; // Necesario para unwrap

@Service
public class SalesSetupConsumer {

    private static final Logger log = LoggerFactory.getLogger(SalesSetupConsumer.class);

    @Autowired private TenantUserRepository tenantUserRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    // Inyectamos el DataSource genérico (que Spring decorará con el Proxy de Micrometer)
    @Autowired private DataSource dataSource;

    @KafkaListener(topics = "tenant-creation-events", groupId = "sales-group-v2")
    public void setupSalesModule(TenantEvent event) {
        String tenantId = event.getTenantId();
        log.info("Iniciando aprovisionamiento del bar (tenant): {}", tenantId);

        try {
            // 1. "Desenvolvemos" el DataSource para llegar a tu implementación personalizada
            DynamicRoutingDataSource routingDS = dataSource.unwrap(DynamicRoutingDataSource.class);

            // 2. Crear y registrar el DataSource dinámico
            DataSource ds = createDataSource(event);
            routingDS.addDataSource(tenantId, ds, "classpath:db/migration/tenants");

            // 3. Establecer el contexto del Tenant para la persistencia del usuario
            TenantContext.setTenantId(tenantId);

            try {
                if (event.getAdminPass() == null || event.getAdminPass().isEmpty()) {
                    log.error("❌ Abortando: La contraseña del administrador para '{}' es NULL o vacía", tenantId);
                    return;
                }

                TenantUser admin = new TenantUser();
                String username = (event.getAdminUsername() != null) ? event.getAdminUsername() : "admin";
                admin.setUsername(username);
                admin.setEmail(username + "@" + tenantId + ".com");
                admin.setPassword(passwordEncoder.encode(event.getAdminPass()));
                admin.setRole("TENANT_ADMIN");

                tenantUserRepository.save(admin);

                log.info("✅ Infraestructura lista y usuario '{}' creado con éxito para: {}", username, tenantId);
            } finally {
                TenantContext.clear();
            }

        } catch (SQLException e) {
            log.error("❌ Error de unwrapping del DataSource: {}", e.getMessage());
        } catch (Exception e) {
            log.error("❌ Error crítico configurando tenant {}: {}", tenantId, e.getMessage());
            e.printStackTrace();
        }
    }

    private DataSource createDataSource(TenantEvent event) {
        return DataSourceBuilder.create()
                .url(event.getDbUrl())
                .username(event.getDbUsername())
                .password(event.getDbPassword())
                .driverClassName("org.postgresql.Driver")
                .build();
    }
}