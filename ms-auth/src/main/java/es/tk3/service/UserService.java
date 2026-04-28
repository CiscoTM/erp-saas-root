package es.tk3.service;

import es.tk3.events.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder; // Añadir esto
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired private JdbcTemplate jdbcTemplate;
    @Autowired private KafkaTemplate<String, Object> kafkaTemplate;
    @Autowired private PasswordEncoder passwordEncoder; // Añadir esto

    public void createLocalUser(String username, String password, String email, String role, String tenantId) {
        // 1. Cambiamos 'users' por 'tenant_users' (la tabla del esquema del tenant)
        String sql = "INSERT INTO tenant_users (username, password, email, role) VALUES (?, ?, ?, ?)";

        // 2. Encriptamos la contraseña para que el login funcione
        String encodedPassword = passwordEncoder.encode(password);

        // 3. Ejecutamos la actualización
        jdbcTemplate.update(sql, username, encodedPassword, email, role);

        // 4. Notificamos a Kafka
        UserCreatedEvent event = new UserCreatedEvent(username, email, role, tenantId);
        kafkaTemplate.send("user-creation-topic", tenantId, event);

        System.out.println("✅ Usuario " + username + " creado en tabla tenant_users del tenant " + tenantId);
    }
}