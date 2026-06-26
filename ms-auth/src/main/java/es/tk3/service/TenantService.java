package es.tk3.service;

import es.tk3.common.model.Tenant;
import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.common.repository.TenantRepository;
import es.tk3.common.tenant.TenantContext;
import es.tk3.common.tenant.flyway.TenantMigrationService;
import es.tk3.model.TenantEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@PreAuthorize("hasRole('SUPERADMIN')")
public class TenantService {

    private final TenantRepository repository;
    private final JdbcTemplate jdbcTemplate;
    private final OutboxEventService outboxEventService;
    private final TenantMigrationService migrationService;
    private final UserService userService;
    private final TenantService self;
    private final String defaultDbUser;
    private final String defaultDbPassword;

    public TenantService(
            TenantRepository repository,
            JdbcTemplate jdbcTemplate,
            OutboxEventService outboxEventService,
            TenantMigrationService migrationService,
            UserService userService,
            @Lazy TenantService self,
            @Value("${app.tenant.default-db-user:admin}") String defaultDbUser,
            @Value("${app.tenant.default-db-password:admin_password}") String defaultDbPassword
    ) {
        this.repository = repository;
        this.jdbcTemplate = jdbcTemplate;
        this.outboxEventService = outboxEventService;
        this.migrationService = migrationService;
        this.userService = userService;
        this.self = self;
        this.defaultDbUser = defaultDbUser;
        this.defaultDbPassword = defaultDbPassword;
    }

    public void createTenant(String name, String adminUsername, String adminPass) {
        String tenantId = name.toLowerCase().trim().replaceAll("[^a-z0-9]", "_").replaceAll("_+", "_");
        String dbName = "db_" + tenantId;

        TenantContext.setTenantId(null);
        createPhysicalDatabase(dbName);

        self.registerTenantInCentral(tenantId, dbName);

        try {
            self.provisionTenantLocally(tenantId, name, adminUsername, adminPass);

            self.registerTenantOutboxEvent(tenantId, adminUsername, adminPass, dbName);

            System.out.println("✅ Inquilino '" + tenantId + "' creado, migrado y aprovisionado exitosamente.");

        } catch (Exception e) {
            System.err.println("❌ Fallo crítico en el aprovisionamiento local: " + e.getMessage());
            self.rollbackTenantRegistration(tenantId);
            throw new RuntimeException("Error en aprovisionamiento. Registro central revertido.", e);
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
    public void registerTenantInCentral(String tenantId, String dbName) {
        Tenant tenant = new Tenant();
        tenant.setId(tenantId);
        tenant.setDbUrl("jdbc:postgresql://localhost:5433/" + dbName);
        tenant.setDbUsername(defaultDbUser);
        tenant.setDbPassword(defaultDbPassword);

        repository.saveAndFlush(tenant);
        System.out.println("✅ Verificando en DB central: " + repository.findById(tenantId).isPresent());
    }

    public void provisionTenantLocally(String tenantId, String name, String adminUsername, String adminPass) {
        migrationService.migrateSingleTenant(tenantId, "classpath:db/migration/tenants", "flyway_schema_history");

        try {
            TenantContext.setTenantId(tenantId);
            String adminEmail = adminUsername + "@" + tenantId + ".com";
            userService.createLocalUser(adminUsername, adminPass, adminEmail, "TENANT_ADMIN", tenantId);
        } finally {
            TenantContext.clear();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerTenantOutboxEvent(String tenantId, String adminUsername, String adminPass, String dbName) {
        TenantContext.setTenantId(null);

        TenantEvent eventPayload = new TenantEvent();
        eventPayload.setType("CREATED");
        eventPayload.setTenantId(tenantId);
        eventPayload.setAdminUsername(adminUsername);
        eventPayload.setAdminPass(adminPass);
        eventPayload.setDbUrl("jdbc:postgresql://localhost:5433/" + dbName);
        eventPayload.setDbUsername(defaultDbUser);
        eventPayload.setDbPassword(defaultDbPassword);

        outboxEventService.createAndSaveEvent(
                tenantId,
                "TENANT",
                "core.tenant.events",
                "TENANT_CREATED",
                eventPayload
        );
        System.out.println("📦 Evento global de Inquilino persistido en Outbox central.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void rollbackTenantRegistration(String tenantId) {
        TenantContext.setTenantId(null);
        repository.deleteById(tenantId);
        System.out.println("⚠️ Compensación: Tenant '" + tenantId + "' eliminado de erp_central para mantener consistencia.");
    }
}