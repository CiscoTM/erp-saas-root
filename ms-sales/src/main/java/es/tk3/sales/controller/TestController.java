package es.tk3.sales.controller;

import es.tk3.common.tenant.TenantContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sales")
public class TestController {

    @GetMapping("/check")
    public Map<String, String> check() {
        return Map.of(
                "status", "Acceso Concedido",
                "activeTenant", TenantContext.getTenantId(),
                "message", "El filtro JWT ha configurado el contexto correctamente"
        );
    }
}