package es.tk3.kitchen.controllers;

import es.tk3.kitchen.dto.recipe.RecipeCostResponseDTO;
import es.tk3.kitchen.dto.recipe.RecipeCreateDTO;
import es.tk3.kitchen.dto.recipe.RecipeResponseDTO; // <-- Importamos el nuevo DTO
import es.tk3.kitchen.model.Recipe;
import es.tk3.kitchen.service.RecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping
    public ResponseEntity<RecipeResponseDTO> create(@RequestBody RecipeCreateDTO dto){
        Recipe recipe = recipeService.createRecipe(dto);
        return ResponseEntity.ok(new RecipeResponseDTO(recipe));
    }

    @PutMapping("/{id}/recalculate")
    public ResponseEntity<RecipeCostResponseDTO> calculateCost(@PathVariable Long id){
        return new ResponseEntity<>(new RecipeCostResponseDTO(
                recipeService.recalculateRecipeCost(id),
                "Costo recalculado con éxito. Se ha propagado el efecto dominó a padres y platos."
        ), HttpStatus.OK);
    }
}