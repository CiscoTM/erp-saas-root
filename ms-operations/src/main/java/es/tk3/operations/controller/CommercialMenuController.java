package es.tk3.operations.controller;

import es.tk3.operations.dto.CommercialMenuRequestDTO;
import es.tk3.operations.service.CommercialMenuService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/operations/comercial-menu")
public class CommercialMenuController {
    private final CommercialMenuService service;

    public CommercialMenuController(CommercialMenuService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> createCommercialMenu(@Valid @RequestBody CommercialMenuRequestDTO dto){
        service.createCommercialMenu(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
