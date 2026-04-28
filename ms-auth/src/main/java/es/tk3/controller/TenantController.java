package es.tk3.controller;

import es.tk3.dto.TenantRequest;
import es.tk3.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/tenants")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<String> register(@Valid @RequestBody TenantRequest request) {
        // 1. Ya no normalizamos aquí, dejamos que el Servicio lo haga
        // o pasamos directamente los campos del nuevo DTO.

        tenantService.createTenant(
                request.getName(),
                request.getAdminUsername(),
                request.getAdminPass()
        );

        return ResponseEntity.ok("Tenant '" + request.getName() + "' registrado y proceso de aprovisionamiento iniciado.");
    }
}