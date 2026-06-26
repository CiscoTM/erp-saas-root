package es.tk3.kitchen.controllers;

import es.tk3.kitchen.dto.rawMaterial.RawMaterialCreateDTO;
import es.tk3.kitchen.dto.rawMaterial.RawMaterialPriceResponseDTO;
import es.tk3.kitchen.dto.rawMaterial.RawMaterialResponseDTO;
import es.tk3.kitchen.model.RawMaterial;
import es.tk3.kitchen.service.RawMaterialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/raw-materials")
public class RawMaterialController {
    private final RawMaterialService materialService;

    public RawMaterialController(RawMaterialService materialService) {
        this.materialService = materialService;
    }

    @PostMapping
    public ResponseEntity<RawMaterialResponseDTO> create(@RequestBody RawMaterialCreateDTO dto) {
        RawMaterial created = materialService.createRawMaterial(dto);
        RawMaterialResponseDTO response = RawMaterialResponseDTO.fromEntity(created);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/price")
    public ResponseEntity<RawMaterialPriceResponseDTO> updatePrice(
            @PathVariable Long id,
            @RequestBody BigDecimal newPurchasePrice){

        if(newPurchasePrice == null || newPurchasePrice.compareTo(BigDecimal.ZERO) <= 0){
            return ResponseEntity.badRequest().body(new RawMaterialPriceResponseDTO(
                    null,
                    null,
                    "Error: El precio debe ser estrictamente mayor a cero."
            ));
        }
        RawMaterial update = materialService.requestPriceUpdate(id, newPurchasePrice);

        return ResponseEntity.ok(new RawMaterialPriceResponseDTO(
                update.getPurchasePrice(),
                update.getKitchenUnit(),
                "Petición de actualización encolada mediante Outbox para el insumo " + id
        ));
    }
}