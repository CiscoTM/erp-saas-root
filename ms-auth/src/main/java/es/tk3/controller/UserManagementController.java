package es.tk3.controller;

import es.tk3.common.tenant.TenantContext;
import es.tk3.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserManagementController {

    @Autowired private UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody UserRequest req) {
        String currentTenant = TenantContext.getTenantId();
        userService.createLocalUser(
                req.getUsername(),
                req.getPassword(),
                req.getEmail(),
                req.getRole(),
                currentTenant
        );
        return ResponseEntity.ok("Usuario creado correctamente en el tenant: " + currentTenant);
    }
}

class UserRequest {
    private String username;
    private String password;
    private String email;
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}