package es.tk3.operations.consumers;

import es.tk3.common.tenant.TenantContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import es.tk3.operations.dto.RecipeEventDTO;
import es.tk3.operations.model.Dish;
import es.tk3.operations.repository.DishReferenceRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class KitchenEventConsumer {
    private static final Logger logger = Logger.getLogger(KitchenEventConsumer.class.getName());
    private final DishReferenceRepository dishReferenceRepository;
    private final ObjectMapper objectMapper;

    public KitchenEventConsumer(DishReferenceRepository dishReferenceRepository, ObjectMapper objectMapper) {
        this.dishReferenceRepository = dishReferenceRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.recipe-events}",
            groupId = "${app.kafka.groups.recipe-sync}"
    )
    public void handleSync(String rawMessage) {
        try {
            RecipeEventDTO event = objectMapper.readValue(rawMessage, RecipeEventDTO.class);

            if (event.getTenantId() != null) {
                TenantContext.setTenantId(event.getTenantId());

                Set<String> allergenSet = new HashSet<>();
                if (event.getAllergens() != null && !event.getAllergens().isBlank()) {
                    allergenSet = Arrays.stream(event.getAllergens().split(","))
                            .map(String::trim)
                            .collect(Collectors.toSet());
                }

                Dish dish = new Dish(
                        UUID.randomUUID(),
                        event.getName(),
                        event.getKitchenDishId(),
                        event.getBasePrice(),
                        event.getAllergens(),
                        LocalDateTime.now()
                );

                dishReferenceRepository.save(dish);
                logger.info(">>> [KAFKA-SYNC] Catálogo actualizado: " + dish.getName());
            }
        } catch (Exception e) {
            logger.severe("❌ ERROR EN SINCRONIZACIÓN: " + e.getMessage());
        } finally {
            TenantContext.clear();
        }
    }
}