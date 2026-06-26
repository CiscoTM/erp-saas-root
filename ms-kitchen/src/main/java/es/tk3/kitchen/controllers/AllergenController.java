package es.tk3.kitchen.controllers;


import es.tk3.kitchen.dto.allergen.AllergenRequestDTO;
import es.tk3.kitchen.service.AllergenService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/allergens")
public class AllergenController {

    private final AllergenService allergenService;

    public AllergenController(AllergenService allergenService) {
        this.allergenService = allergenService;
    }

    @PostMapping
    public void createAllergen(@RequestBody @Valid AllergenRequestDTO dto){
        allergenService.saveAllergen(dto);
    }
}
