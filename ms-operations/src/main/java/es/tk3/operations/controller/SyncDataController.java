package es.tk3.operations.controller;

import es.tk3.operations.model.CommercialMenu;
import es.tk3.operations.model.Dish;
import es.tk3.operations.repository.CommercialMenuRepository;
import es.tk3.operations.repository.DishReferenceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/operations/sync-data")
public class SyncDataController {
    private final CommercialMenuRepository commercialMenuRepository;
    private final DishReferenceRepository dishReferenceRepository;

    public SyncDataController(
            CommercialMenuRepository commercialMenuRepository,
            DishReferenceRepository dishReferenceRepository) {
        this.commercialMenuRepository = commercialMenuRepository;
        this.dishReferenceRepository = dishReferenceRepository;
    }

    @GetMapping("/first-menu")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<CommercialMenu> getFirstMenuId() {
        return commercialMenuRepository.findAll().stream().findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/dish-uuid/{kitchenDishId}")
    @PreAuthorize("hasRole('TENANT_ADMIN')")
    public ResponseEntity<Dish> getOperationDishUuid(@PathVariable Long kitchenDishId) {
        return dishReferenceRepository.findByKitchenDishId(kitchenDishId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
