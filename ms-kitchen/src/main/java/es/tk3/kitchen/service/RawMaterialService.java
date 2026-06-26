package es.tk3.kitchen.service;

import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.common.tenant.TenantContext;
import es.tk3.kitchen.dto.event.RawMaterialPriceUpdatedEvent;
import es.tk3.kitchen.dto.rawMaterial.RawMaterialCreateDTO;
import es.tk3.kitchen.model.Allergen;
import es.tk3.kitchen.model.RawMaterial;
import es.tk3.kitchen.model.Recipe;
import es.tk3.kitchen.repository.RawMaterialRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RawMaterialService {

    private final RawMaterialRepository rawMaterialRepository;
    private final RecipeService recipeService;
    private final OutboxEventService outboxEventService;

    @PersistenceContext
    private EntityManager entityManager;

    public RawMaterialService(
            RawMaterialRepository rawMaterialRepository,
            RecipeService recipeService,
            OutboxEventService outboxEventService
    ) {
        this.rawMaterialRepository = rawMaterialRepository;
        this.recipeService = recipeService;
        this.outboxEventService = outboxEventService;
    }

    @Transactional
    public RawMaterial createRawMaterial(RawMaterialCreateDTO dto) {
        RawMaterial material = new RawMaterial();
        material.setName(dto.getName());
        material.setPurchaseUnit(dto.getPurchaseUnit());
        material.setPurchasePrice(dto.getPurchasePrice());
        material.setConversionFactor(dto.getConversionFactor());
        material.setKitchenUnit(dto.getKitchenUnit());

        // Mapear la colección intermedia teniendo en cuenta si es traza o contenido directo
        if (dto.getAllergens() != null && !dto.getAllergens().isEmpty()) {
            for (RawMaterialCreateDTO.AllergenInput allergenInput : dto.getAllergens()) {
                // Mantenemos la optimización de rendimiento con getReference
                Allergen allergen = entityManager.getReference(Allergen.class, allergenInput.getId());

                material.addAllergen(allergen, allergenInput.getPresenceType());
            }
        }
        return rawMaterialRepository.save(material);
    }

    @Transactional
    public void updateRawMaterialPrice(Long rawMaterialId, BigDecimal newPurchasePrice){
        RawMaterial material = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material " + rawMaterialId + " no encontrado"));

        material.setPurchasePrice(newPurchasePrice);
        material.calculateInternalCost();

        List<Recipe> parentRecipes = recipeService.recalculateRecipeCostsByMaterial(rawMaterialId);
        for (Recipe recipe : parentRecipes) {
            recipeService.recalculateRecipeCost(recipe.getId());
        }
    }

    @Transactional
    public RawMaterial requestPriceUpdate(Long rawMaterialId, BigDecimal newPurchasePrice) {
        RawMaterial material = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Material no encontrado"));

        material.setPurchasePrice(newPurchasePrice);
        material.calculateInternalCost();

        String currentTenant = TenantContext.getTenantId();

        RawMaterialPriceUpdatedEvent event = new RawMaterialPriceUpdatedEvent(
               currentTenant,
                rawMaterialId,
                newPurchasePrice
        );

        outboxEventService.createAndSaveEvent(
                rawMaterialId.toString(),
                "RawMaterial",
                "kitchen.raw-materials.price-updated",
                "PRICE_UPDATE_REQUESTED",
                event
        );
        return rawMaterialRepository.save(material);
    }
}