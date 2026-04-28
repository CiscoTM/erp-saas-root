package es.tk3.common.tenant;

import es.tk3.common.repository.TenantRepository;
import es.tk3.common.tenant.flyway.TenantMigrationService;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    private static final Logger log = LoggerFactory.getLogger(TenantMigrationService.class);

    // Usamos ConcurrentHashMap para evitar problemas de concurrencia al añadir tenants
    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
    private TenantRepository tenantRepository;
    private DataSource defaultDataSource;
    @Autowired @Lazy private TenantMigrationService migrationService;

    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public void setDefaultTargetDataSource(@NonNull Object defaultTargetDataSource) {
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        this.defaultDataSource = (DataSource) defaultTargetDataSource;
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();

        // Si no hay tenant (arranque) o es la central, devolvemos el default directamente
        if (lookupKey == null || "master".equals(lookupKey) || "DEFAULT".equals(lookupKey)) {
            return defaultDataSource;
        }

        // Si ya existe en el mapa, lo devolvemos
        if (targetDataSources.containsKey(lookupKey)) {
            return (DataSource) targetDataSources.get(lookupKey);
        }

        // Si es un tenant nuevo, intentamos crearlo dinámicamente
        return createTenantDataSource(lookupKey.toString());
    }

    private synchronized DataSource createTenantDataSource(String tenantId) {
        // Doble check por si otro hilo lo creó mientras esperábamos el lock
        if (targetDataSources.containsKey(tenantId)) {
            return (DataSource) targetDataSources.get(tenantId);
        }

        if (tenantRepository == null) {
            throw new IllegalStateException("TenantRepository no inyectado en DynamicRoutingDataSource");
        }

        // Limpiamos contexto para que la consulta al repo vaya a la central (erp_central)
        TenantContext.clear();

        try {
            return tenantRepository.findById(tenantId)
                    .map(tenant -> {
                        DataSource ds = DataSourceBuilder.create()
                                .url(tenant.getDbUrl())
                                .username(tenant.getDbUsername())
                                .password(tenant.getDbPassword())
                                .driverClassName("org.postgresql.Driver")
                                .build();

                        // Actualizamos el mapa interno
                        this.targetDataSources.put(tenantId, ds);

                        // Notificamos a la clase padre del cambio
                        super.setTargetDataSources(new HashMap<>(this.targetDataSources));
                        super.afterPropertiesSet();

                        System.out.println("🔥 Conexión creada dinámicamente para el tenant: " + tenantId);
                        return ds;
                    })
                    .orElseThrow(() -> new RuntimeException("No existe configuración para el tenant: " + tenantId));
        } finally {
            // Restauramos el tenant actual para que la petición original siga
            TenantContext.setTenantId(tenantId);
        }
    }

    public void addDataSource(String tenantId, DataSource dataSource, String migrationLocation) {
        // 1. Ahora pasamos el migrationLocation para usar el nuevo servicio
        try {
            // Como el nuevo migrateAllTenants es para procesos batch,
            // aquí podemos llamar a Flyway directamente para este DataSource específico
            // o adaptar una pequeña función de ayuda.

            org.flywaydb.core.Flyway flyway = org.flywaydb.core.Flyway.configure()
                    .dataSource(dataSource)
                    .locations(migrationLocation)
                    .baselineOnMigrate(true)
                    .load();
            flyway.migrate();

        } catch (Exception e) {
            log.error("❌ Falló la migración para el tenant: {}", tenantId, e);
            throw new RuntimeException("No se pudo inicializar la base de datos del tenant");
        }

        // 2. Registramos el DataSource en el mapa de ruteo
        this.targetDataSources.put(tenantId, dataSource);
        setTargetDataSources(new HashMap<>(this.targetDataSources));
        afterPropertiesSet();
        log.info("✅ Inquilino [{}] registrado y migrado correctamente.", tenantId);
    }

    public Set<String> getTenantIds() {
        Set<String> ids = new java.util.HashSet<>();
        for (Object key : targetDataSources.keySet()) {
            ids.add(String.valueOf(key));
        }
        return ids;
    }
}