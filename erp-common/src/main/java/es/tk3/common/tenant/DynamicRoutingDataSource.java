package es.tk3.common.tenant;

import es.tk3.common.repository.TenantRepository;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

    protected final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    private TenantRepository tenantRepository;

    @Override
    protected Object determineCurrentLookupKey() {
        return TenantContext.getTenantId();
    }

    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
        System.out.println("✅ [Router] TenantRepository inyectado correctamente.");
    }

    public void addDataSource(String tenantId, DataSource dataSource) {
        if (tenantId == null || dataSource == null) {
            throw new IllegalArgumentException("El TenantId y el DataSource no pueden ser nulos.");
        }

        this.targetDataSources.put(tenantId, dataSource);
        setTargetDataSources(new HashMap<>(this.targetDataSources));
        afterPropertiesSet();

        System.out.println("✅ [Router] Conexión registrada en caliente para el tenant: " + tenantId);
    }

    public void removeDataSource(String tenantId) {
        if (tenantId != null) {
            this.targetDataSources.remove(tenantId);
            setTargetDataSources(new HashMap<>(this.targetDataSources));
            afterPropertiesSet();
            System.out.println("⚠️ [Router] Conexión removida de la memoria para el tenant: " + tenantId);
        }
    }

    public Set<String> getTenantIds() {
        Set<String> ids = new HashSet<>();
        for (Object key : targetDataSources.keySet()) {
            ids.add(String.valueOf(key));
        }
        return ids;
    }
}