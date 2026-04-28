package es.tk3.controller;

import es.tk3.common.tenant.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantHealthController {

    @Autowired private JdbcTemplate jdbcTemplate;

    @GetMapping("/check")
    public ResponseEntity<?> checkHealth(@RequestParam String tenantId) {
        try {
            // Seteamos el contexto temporalmente para probar la conexión
            TenantContext.setTenantId(tenantId);

            // Intentamos una consulta simple a la tabla de usuarios de ese tenant
            jdbcTemplate.execute("SELECT 1");

            return ResponseEntity.ok(Map.of(
                    "status", "UP",
                    "tenant", tenantId,
                    "message", "Conexión con la base de datos del cliente establecida correctamente."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(404).body(Map.of(
                    "status", "DOWN",
                    "message", "El cliente especificado no existe o su base de datos no está disponible."
            ));
        } finally {
            TenantContext.clear();
        }
    }
}