package es.tk3.controller;

import es.tk3.common.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

@RestController
public class TenantTestController {

    @Autowired
    private DataSource dataSource;

    @GetMapping("/api/test-tenant")
    public Map<String, String> test() throws SQLException {
        // Al pedir una conexión al dataSource, el DynamicRoutingDataSource
        // mirará qué hay en TenantContext.getTenantId()
        try(Connection conn = dataSource.getConnection()){
            return Map.of(
                    "tenant_en_contexto", TenantContext.getTenantId() != null ? TenantContext.getTenantId() : "CENTRAL",
                    "url_base_datos_real", conn.getMetaData().getURL()
            );
        }
    }

    @GetMapping("/api/tenant-check")
    public Map<String, String> check() {
        return Map.of(
                "tenantActual", TenantContext.getTenantId() != null ? TenantContext.getTenantId() : "CENTRAL",
                "mensaje", "Si lees esto, el ruteo de paquetes common funciona"
        );
    }
}