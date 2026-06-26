package es.tk3.common.outbox.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.outbox.model.OutboxEvent;
import es.tk3.common.outbox.repository.OutboxEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OutboxEventService {
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper mapper;

    public OutboxEventService(OutboxEventRepository outboxEventRepository, ObjectMapper mapper) {
        this.outboxEventRepository = outboxEventRepository;
        this.mapper = mapper;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void createAndSaveEvent(String aggregateId, String aggregateType, String topic, String eventType, Object payload){
        try {
            String jsonPayload = mapper.writeValueAsString(payload);

            OutboxEvent event = new OutboxEvent(
                    UUID.randomUUID(),
                    aggregateId,
                    aggregateType,
                    topic,
                    eventType,
                    jsonPayload,
                    "PENDING"
            );
            outboxEventRepository.save(event);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error crítico al serializar el payload para el módulo Outbox Común", e);
        }
    }
}
