package es.tk3.events;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    @KafkaListener(topics = "user-creation-topic", groupId = "notification-group")
    public void handleUserCreated(UserCreatedEvent event) {
        // Simulación de lógica de negocio (Envío de Email)
        System.out.println("=========================================");
        System.out.println("NOTIFICACIÓN RECIBIDA (KAFKA)");
        System.out.println("Enviando email de bienvenida a: " + event.getEmail());
        System.out.println("Usuario: " + event.getUsername());
        System.out.println("Tenant Destino: " + event.getTenantId());
        System.out.println("Rol asignado: " + event.getRole());
        System.out.println("=========================================");
    }
}