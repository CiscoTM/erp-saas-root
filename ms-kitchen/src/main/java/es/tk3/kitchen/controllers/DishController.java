package es.tk3.kitchen.controllers;

import es.tk3.kitchen.dto.dish.DishPreparationDTO;
import es.tk3.kitchen.dto.dish.DishRequestDTO;
import es.tk3.kitchen.dto.dish.DishResponseDTO;
import es.tk3.kitchen.model.Dish;
import es.tk3.kitchen.model.DishPreparation;
import es.tk3.kitchen.service.DishService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dishes")
public class DishController {

    private final DishService dishService;

    public DishController(DishService dishService) {
        this.dishService = dishService;
    }

    @GetMapping()
    public ResponseEntity<List<DishResponseDTO>> getAllDishes(){

        return ResponseEntity.ok(dishService.findAll().stream()
                .map(this::toDTO)
                .toList());

    }

    @GetMapping("/{id}")
    public ResponseEntity<DishResponseDTO> getDishById(@PathVariable Long id){
        Dish dish = dishService.findById(id);
        return ResponseEntity.ok(toDTO(dish));
    }

    @PostMapping()
    public ResponseEntity<DishResponseDTO> createDish(@RequestBody @Valid DishRequestDTO dto){
        return new ResponseEntity<>(
                toDTO(dishService.saveAndPublish(
                        toEntity(dto), "DISH_CREATED")
                ), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishResponseDTO> updateDish(@PathVariable Long id,@RequestBody @Valid DishRequestDTO dto){
        return new ResponseEntity<>(
                toDTO(
                        dishService.update(
                                id,
                                toEntity(dto))
                ),
                HttpStatus.CREATED
        );
    }

    private DishResponseDTO toDTO(Dish dish) {
        DishResponseDTO dto = new DishResponseDTO();
        dto.setTechnicalName(dish.getTechnicalName());
        dto.setBaseCost(dish.getBaseCost() != null ? dish.getBaseCost() : BigDecimal.ZERO);

        List<DishPreparation> preparations = (dish.getPreparations() == null)
                ? new ArrayList<>()
                : dish.getPreparations();

        List<DishPreparationDTO> prepDTOs = preparations.stream()
                .map(p -> {
                    DishPreparationDTO pdto = new DishPreparationDTO();
                    pdto.setQuantity(p.getQuantityRequired());
                    // Uso de operador ternario para evitar nulos en nombres
                    String name = (p.getRecipe() != null) ? p.getRecipe().getName() :
                            (p.getRawMaterial() != null) ? p.getRawMaterial().getName() : "N/A";
                    pdto.setName(name);
                    return pdto;
                })
                .collect(Collectors.toList());

        dto.setPreparations(prepDTOs);
        dto.setId(dish.getId());
        return dto;
    }

    private Dish toEntity(DishRequestDTO dto){
        Dish dish = new Dish();
        dish.setTechnicalName(dto.getTechnicalName());
        dish.setPreparations(dto.getPreparations());
        dish.setDinersPerPlate(dto.getDinersPerPlate());
        dish.setServiceType(dto.getServiceType());
        return dish;
    }

}


