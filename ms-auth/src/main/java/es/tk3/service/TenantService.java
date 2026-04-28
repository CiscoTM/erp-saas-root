package es.tk3.service;

import es.tk3.common.model.Tenant;
import es.tk3.common.repository.TenantRepository;
import es.tk3.common.tenant.TenantContext;
import es.tk3.kafka.TenantPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize("hasRole('SUPERADMIN')")
public class TenantService {
    @Autowired private TenantRepository repository;
    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private TenantPublisher tenantPublisher;

    @Value("${app.tenant.default-db-user:admin}")
    private String defaultDbUser;

    @Value("${app.tenant.default-db-password:admin_password}")
    private String defaultDbPassword;

    public void createTenant(String name, String adminUsername, String adminPass) {
        String tenantId = name.toLowerCase()
                .trim()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
        String dbName = "db_" + tenantId;

        TenantContext.setTenantId(null);

        createPhysicalDatabase(dbName);

        try {
            saveAndNotifyFinal(tenantId, name, adminUsername, adminPass, dbName);
        } catch (Exception e) {
            System.err.println("❌ Fallo en el registro final: " + e.getMessage());
            throw e;
        } finally {
            TenantContext.clear();
        }
    }

    private void createPhysicalDatabase(String dbName) {
        try (java.sql.Connection conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(true);
            try (java.sql.Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE " + dbName);
                System.out.println("✅ DB física " + dbName + " creada.");
            }
        } catch (Exception e) {
            if (!e.getMessage().contains("already exists")) throw new RuntimeException(e);
            System.out.println("⚠️ La DB ya existe, continuando...");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAndNotifyFinal(String tenantId, String name, String adminUsername, String adminPass, String dbName) {
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setDbUrl("jdbc:postgresql://localhost:5433/" + dbName);
        tenant.setDbUsername(defaultDbUser);
        tenant.setDbPassword(defaultDbPassword);

        repository.saveAndFlush(tenant);

        // Notar que pasamos adminPass tal cual llega del DTO de entrada
        tenantPublisher.publishTenantCreated(
                tenantId,
                adminUsername,
                adminPass,
                tenant.getDbUrl(),
                defaultDbUser,
                defaultDbPassword
        );
        System.out.println("✅ Registro central y evento Kafka enviado para: " + tenantId);
    }
}