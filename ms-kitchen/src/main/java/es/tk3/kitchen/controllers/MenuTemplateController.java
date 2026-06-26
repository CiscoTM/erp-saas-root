package es.tk3.kitchen.controllers;

import es.tk3.kitchen.dto.menu.MenuTemplateRequestDTO;
import es.tk3.kitchen.dto.menu.MenuTemplateResponseDTO;
import es.tk3.kitchen.service.MenuTemplateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-templates")
public class MenuTemplateController {

    private final MenuTemplateService menuTemplateService;

    public MenuTemplateController(MenuTemplateService menuTemplateService) {
        this.menuTemplateService = menuTemplateService;
    }

    @PostMapping
    public ResponseEntity<Void> createTemplate(@Valid @RequestBody MenuTemplateRequestDTO dto){
        menuTemplateService.createAndSyncTemplate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MenuTemplateResponseDTO>> getAllTemplates() {
        List<MenuTemplateResponseDTO> templates = menuTemplateService.getAllTemplates();
        return ResponseEntity.ok(templates);
    }
}
