package es.tk3.service;

import es.tk3.common.tenant.TenantContext;
import es.tk3.model.User;
import es.tk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // LOGS CRUCIALES: Mira esto en la consola de IntelliJ al intentar loguearte
        System.out.println("--- INTENTO DE LOGIN ---");
        System.out.println("Usuario: " + user.getUsername());
        System.out.println("Clave en DB: " + user.getPassword());
        System.out.println("Rol en DB: " + user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                // Forzamos el prefijo ROLE_ manualmente para que coincida con .hasAuthority("ROLE_ADMIN")
                .authorities("ROLE_" + user.getRole())
                .build();
    }
}
