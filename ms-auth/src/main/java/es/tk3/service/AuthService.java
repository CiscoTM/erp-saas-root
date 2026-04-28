package es.tk3.service;

import es.tk3.common.repository.TenantUserRepository;
import es.tk3.common.security.JwtService;
import es.tk3.common.tenant.TenantContext;
import es.tk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private UserRepository userRepository;             // Tabla 'users' (Central)
    @Autowired private TenantUserRepository tenantUserRepository; // Tabla 'tenant_users' (Bares)

    public String login(String username, String password, String tenantId) {
        boolean isMaster = (tenantId == null ||
                "master".equalsIgnoreCase(tenantId) ||
                "central".equalsIgnoreCase(tenantId));

        if (isMaster) {
            TenantContext.clear();
            log.info("Intento de login central: {}", username);
            return handleCentralLogin(username, password);
        } else {
            TenantContext.setTenantId(tenantId);
            log.info("Intento de login tenant [{}]: {}", tenantId, username);
            return handleTenantLogin(username, password, tenantId);
        }
    }

    private String handleCentralLogin(String username, String password) {
        try {
            es.tk3.model.User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

            if (passwordEncoder.matches(password, user.getPassword())) {
                log.info("✅ Login central exitoso: {}", username);
                return jwtService.generateToken(username, "master", user.getRole());
            }
            throw new RuntimeException("Credenciales inválidas");
        } finally {
            TenantContext.clear();
        }
    }

    private String handleTenantLogin(String username, String password, String tenantId) {
        try {
            // Buscamos en 'tenant_users' de la base de datos del bar
            es.tk3.common.model.TenantUser tenantUser = tenantUserRepository.findByUsername(username)
                    .orElseThrow(() -> {
                        log.error("❌ Usuario de bar no encontrado: {} en tenant {}", username, tenantId);
                        return new RuntimeException("Credenciales inválidas");
                    });

            if (passwordEncoder.matches(password, tenantUser.getPassword())) {
                log.info("✅ Login tenant exitoso: {} (Bar: {})", username, tenantId);
                // Usamos el rol que viene de la tabla del bar (ej: TENANT_ADMIN)
                return jwtService.generateToken(username, tenantId, tenantUser.getRole());
            }
            throw new RuntimeException("Credenciales inválidas");
        } finally {
            TenantContext.clear();
        }
    }
}