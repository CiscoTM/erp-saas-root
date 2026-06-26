package es.tk3.kitchen.service;

import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.kitchen.dto.allergen.AllergenRequestDTO;
import es.tk3.kitchen.enums.RestrictionType;
import es.tk3.kitchen.model.Allergen;
import es.tk3.kitchen.repository.AllergenRepository;
import org.apache.kafka.common.errors.DuplicateResourceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AllergenService {
    private final AllergenRepository allergenRepository;
    private final OutboxEventService outboxEventService;

    public AllergenService(
       AllergenRepository allergenRepository,
       OutboxEventService outboxEventService
    ) {
        this.allergenRepository = allergenRepository;
        this.outboxEventService = outboxEventService;
    }

    @Transactional
    public void saveAllergen(AllergenRequestDTO dto){
        String processedCode = dto.getCode().startsWith("CUSTOM_")
                ? dto.getCode().toUpperCase()
                : "CUSTOM_" + dto.getCode().toUpperCase();

        boolean exist = allergenRepository.existsByCode(processedCode);
        if(exist) {
            throw new DuplicateResourceException("La restricción alimentaria con el código " + processedCode + " ya está registrada.");
        }

        Allergen allergen = new Allergen();
        allergen.setCode(processedCode);
        allergen.setName(dto.getName());
        allergen.setIconUrl(dto.getIconUrl());
        allergen.setDescription(dto.getDescription());
        allergen.setActive(true);

        allergen.setRestrictionType(RestrictionType.CUSTOM_INTOLERANCE);

        Allergen saved = allergenRepository.save(allergen);

        outboxEventService.createAndSaveEvent(
                saved.getId().toString(),
                "Allergen",
                "kitchen.allergens.sync",
                "ALLERGEN_CREATED",
                saved );
    }
}
