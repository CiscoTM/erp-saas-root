package es.tk3.operations.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.tk3.common.outbox.service.OutboxEventService;
import es.tk3.common.tenant.TenantContext;
import es.tk3.operations.dto.MenuTemplateSyncEventDTO;
import es.tk3.operations.model.*;
import es.tk3.operations.dto.CommercialMenuRequestDTO;
import es.tk3.operations.repository.CommercialMenuRepository;
import es.tk3.operations.repository.CommercialMenuRecipeRepository;
import es.tk3.operations.repository.DishReferenceRepository;
import es.tk3.operations.repository.MenuTemplateRepository;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CommercialMenuService {

    private static final Logger logger = Logger.getLogger(CommercialMenuService.class.getName());

    private final CommercialMenuRepository commercialMenuRepository;
    private final MenuTemplateRepository menuTemplateRepository;
    private final CommercialMenuRecipeRepository commercialMenuRecipeRepository;
    private final DishReferenceRepository dishReferenceRepository;
    private final OutboxEventService outboxEventService;
    private final PricingEngine pricingEngine;
    private final ObjectMapper mapper;

    public CommercialMenuService(
            CommercialMenuRepository commercialMenuRepository,
            MenuTemplateRepository menuTemplateRepository,
            CommercialMenuRecipeRepository commercialMenuRecipeRepository,
            DishReferenceRepository dishReferenceRepository,
            OutboxEventService outboxEventService,
            PricingEngine pricingEngine,
            ObjectMapper mapper
    ) {
        this.commercialMenuRepository = commercialMenuRepository;
        this.menuTemplateRepository = menuTemplateRepository;
        this.commercialMenuRecipeRepository = commercialMenuRecipeRepository;
        this.dishReferenceRepository = dishReferenceRepository;
        this.outboxEventService = outboxEventService;
        this.pricingEngine = pricingEngine;
        this.mapper = mapper;
    }

    @Transactional
    public void createCommercialMenu(CommercialMenuRequestDTO dto){
        var template = menuTemplateRepository.findById(dto.getMenuTemplateId())
                .orElseThrow(() -> new ResourceNotFoundException("Plantilla no encontrada"));

        MenuTemplateSyncEventDTO syncEventDTO;
        try {
            syncEventDTO = mapper.readValue(template.getCategoryStructureJson(), MenuTemplateSyncEventDTO.class);
            syncEventDTO.setTenantId(TenantContext.getTenantId());
            syncEventDTO.setTemplateId(template.getId());
            syncEventDTO.setName(template.getName());
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la estructura de la plantilla para el cálculo de precios", e);
        }
        BigDecimal floor = pricingEngine.calculateMenuAutomatedCost(syncEventDTO, TenantContext.getTenantId());
        BigDecimal roof = pricingEngine.suggestOfficialRoof(syncEventDTO, TenantContext.getTenantId());

        CommercialMenu menu = getCommercialMenu(dto, floor, roof);

        CommercialMenu savedMenu = commercialMenuRepository.save(menu);

        saveRecipeMappings(savedMenu, template.getCategoryStructureJson());

        outboxEventService.createAndSaveEvent(
                savedMenu.getId().toString(),
                "CommercialMenu",
                "operations.commercial-menus",
                "CREATED",
                dto
        );
    }

    private void saveRecipeMappings(CommercialMenu menu, String jsonStructure) {
        if (jsonStructure == null || jsonStructure.isBlank()) return;
        try {
            JsonNode root = mapper.readTree(jsonStructure);
            JsonNode categories = root.get("categories");

            if (categories != null && categories.isArray()) {
                for (JsonNode category : categories) {
                    processAndSaveDishes(category.get("fixedDishIds"), menu);
                    processAndSaveDishes(category.get("optionalDishIds"), menu);
                }
            }
        } catch (Exception e) {
            logger.severe("❌ Error crítico extrayendo recetas del JSON de estructura: " + e.getMessage());
        }
    }

    private void processAndSaveDishes(JsonNode dishIdsNode, CommercialMenu menu) {
        if (dishIdsNode == null || !dishIdsNode.isArray()) return;

        for (JsonNode idNode : dishIdsNode) {
            Long kitchenDishId = idNode.asLong();

            dishReferenceRepository.findByKitchenDishId(kitchenDishId).ifPresentOrElse(dish -> {
                CommercialMenuRecipe recipe = new CommercialMenuRecipe();
                recipe.setCommercialMenuId(menu.getId().toString());
                recipe.setDish(dish);
                recipe.setDefaultQuantity(1);

                commercialMenuRecipeRepository.save(recipe);
                logger.info("✅ Vinculado plato '" + dish.getName() + "' al menú comercial '" + menu.getName() + "'");
            }, () -> {
                logger.warning("⚠️ El plato con kitchenDishId " + kitchenDishId + " no está sincronizado en ms-operations.");
            });
        }
    }

    private static @NonNull CommercialMenu getCommercialMenu(CommercialMenuRequestDTO dto, BigDecimal floor, BigDecimal roof) {
        CommercialMenu menu = new CommercialMenu();
        menu.setName(dto.getName());
        menu.setDescription(dto.getDescription());
        menu.setMenuTemplateId(dto.getMenuTemplateId());
        menu.setActive(true);
        menu.setBasePriceFloor(floor);
        menu.setSuggestedOfficialPrice(roof);

        List<MenuPrice> prices = new ArrayList<>();
        for (CommercialMenuRequestDTO.MenuPriceRequestDTO priceDto : dto.getPrices()){
            MenuPrice price = new MenuPrice();
            price.setCommercialMenu(menu);
            Season season = new Season();
            season.setId(priceDto.getSeasonId());
            price.setSeason(season);
            Tariff tariff = new Tariff();
            tariff.setId(priceDto.getTariffId());
            price.setTariff(tariff);
            price.setPricePerPax(priceDto.getPricePerPax());
            prices.add(price);
        }
        menu.setPrices(prices);
        return menu;
    }
}