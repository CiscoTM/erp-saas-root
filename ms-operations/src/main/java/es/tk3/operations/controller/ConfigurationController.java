package es.tk3.operations.controller;

import es.tk3.operations.dto.OperationalParamRequestDTO;
import es.tk3.operations.dto.OperationalParamResponseDTO;
import es.tk3.operations.model.OperationalParameter;
import es.tk3.operations.model.Season;
import es.tk3.operations.model.Tariff;
import es.tk3.operations.service.ConfigurationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/operations/config")
public class ConfigurationController {
    private final ConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    @PostMapping("/seasons")
    public ResponseEntity<Season> createSeason(@RequestBody Season season) {
        return new ResponseEntity<>(configurationService.createSeason(season), HttpStatus.CREATED);
    }

    @PostMapping("/tariffs")
    public ResponseEntity<Tariff> createTariff(@RequestBody Tariff tariff) {
        return new ResponseEntity<>(configurationService.createTariff(tariff), HttpStatus.CREATED);
    }

    @PostMapping("/parameters")
    public ResponseEntity<OperationalParamResponseDTO> createParameter(@RequestBody OperationalParamRequestDTO param) {
        OperationalParameter saved = configurationService.createParameter(configurationService.toEntity(param));

        return new ResponseEntity<>(configurationService.toDto(saved), HttpStatus.CREATED);
    }

    @PatchMapping("/parameters/{id}")
    public ResponseEntity<OperationalParamResponseDTO> patchParameter(
            @PathVariable Long id,
            @RequestBody OperationalParamRequestDTO param) {

        OperationalParamResponseDTO updated = configurationService.partialUpdateParameter(id, param);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

}
