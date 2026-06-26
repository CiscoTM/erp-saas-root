package es.tk3.common.tenant;

import es.tk3.common.model.Tenant;
import es.tk3.common.repository.TenantRepository;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.util.List;

@Component
public class TenantDataSourceLoader {

    private final TenantRepository tenantRepository;
    private final DataSource dataSource;

    public TenantDataSourceLoader(TenantRepository tenantRepository, DataSource dataSource) {
        this.tenantRepository = tenantRepository;
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadTenantsOnStartup() {
        System.out.println("🔄 [Tenant Loader] Restaurando conexiones de inquilinos en memoria...");

        try {
            List<Tenant> tenants = tenantRepository.findAll();

            DynamicRoutingDataSource router = dataSource.unwrap(DynamicRoutingDataSource.class);

            for (Tenant tenant : tenants) {
                DataSource ds = DataSourceBuilder.create()
                        .url(tenant.getDbUrl())
                        .username(tenant.getDbUsername())
                        .password(tenant.getDbPassword())
                        .driverClassName("org.postgresql.Driver")
                        .build();

                router.addDataSource(tenant.getId(), ds);
            }
            System.out.println("✅ [Tenant Loader] Memoria restaurada: Se cargaron " + tenants.size() + " inquilinos.");

        } catch (Exception e) {
            System.err.println("❌ [Tenant Loader] Fallo al cargar los inquilinos: " + e.getMessage());
        }
    }
}