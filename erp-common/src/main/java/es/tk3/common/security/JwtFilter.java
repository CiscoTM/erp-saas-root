package es.tk3.common.security;

import es.tk3.common.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                String tenantId = jwtService.extractTenantId(token);
                String username = jwtService.extractUsername(token);
                String role = jwtService.extractRole(token);

                List<SimpleGrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority(role),           // "ADMIN"
                        new SimpleGrantedAuthority("ROLE_" + role)  // "ROLE_ADMIN"
                );

                // Establecemos el contexto del tenant para esta petición
                TenantContext.setTenantId(tenantId);

                // CUMPLIMIENTO D: Propagación de Roles con prefijo ROLE_
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                );

                SecurityContextHolder.getContext().setAuthentication(auth);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido o expirado");
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // CUMPLIMIENTO B: Limpieza del Contexto (ThreadLocal) al finalizar la petición
            TenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/");
    }
}