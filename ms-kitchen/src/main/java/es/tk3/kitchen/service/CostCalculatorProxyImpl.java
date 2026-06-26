package es.tk3.kitchen.service;

import es.tk3.kitchen.model.RawMaterial;
import es.tk3.kitchen.repository.RawMaterialRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CostCalculatorProxyImpl implements CostCalculatorProxy{

    private final RawMaterialRepository rawMaterialRepository;

    public CostCalculatorProxyImpl(RawMaterialRepository rawMaterialRepository) {
        this.rawMaterialRepository = rawMaterialRepository;
    }

    @Override
    public BigDecimal getRawMaterialPrice(Long rawMaterialId) {
        RawMaterial rm = rawMaterialRepository.findById(rawMaterialId)
                .orElseThrow(() -> new ResourceNotFoundException("Materia prima " + rawMaterialId + " no encontrada."));
            return rm.getCostPerKitchenUnit();
    }
}
