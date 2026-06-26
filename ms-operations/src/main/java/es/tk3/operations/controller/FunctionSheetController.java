package es.tk3.operations.controller;

import es.tk3.operations.dto.FunctionSheetDetailRequest;
import es.tk3.operations.model.FunctionSheetDetail;
import es.tk3.operations.service.FunctionSheetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/operations/function-sheets")
public class FunctionSheetController {

    private final FunctionSheetService service;

    public FunctionSheetController(FunctionSheetService service) {
        this.service = service;
    }

    @PostMapping("/{id}/details")
    public ResponseEntity<FunctionSheetDetail> addDetail(
            @PathVariable UUID id,
            @Valid @RequestBody FunctionSheetDetailRequest request) {

        FunctionSheetDetail result = service.addDetailToSheet(id, request);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }


}