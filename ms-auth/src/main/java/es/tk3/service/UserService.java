package es.tk3.service;

import es.tk3.common.model.TenantUser;
import es.tk3.common.repository.TenantUserRepository;
import es.tk3.common.tenant.TenantContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final TenantUserRepository tenantUserRepository; // Repositorio JPA de erp-common
    private final PasswordEncoder passwordEncoder;

    public UserService(TenantUserRepository tenantUserRepository, PasswordEncoder passwordEncoder) {
        this.tenantUserRepository = tenantUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createLocalUser(String username, String password, String email, String role, String tenantId) {
        TenantContext.setTenantId(tenantId);

        try {
            TenantUser localAdmin = new TenantUser();
            localAdmin.setUsername(username);
            localAdmin.setPassword(passwordEncoder.encode(password));
            localAdmin.setEmail(email);
            localAdmin.setRole(role);

            tenantUserRepository.saveAndFlush(localAdmin);

            System.out.println("✅ [JPA] Usuario administrador '" + username + "' guardado con éxito en el esquema del tenant: " + tenantId);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el usuario local mediante JPA: " + e.getMessage(), e);
        }
    }
}