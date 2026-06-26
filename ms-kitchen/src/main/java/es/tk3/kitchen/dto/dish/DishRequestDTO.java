package es.tk3.kitchen.dto.dish;

import es.tk3.kitchen.enums.ServiceType;
import es.tk3.kitchen.model.DishPreparation;

import java.util.List;

public class DishRequestDTO {

    private String technicalName;
    private List<DishPreparation> preparations;
    private Integer dinersPerPlate;
    private ServiceType serviceType;

    public DishRequestDTO() {}

    public String getTechnicalName() { return technicalName; }

    public void setTechnicalName(String technicalName) {
        this.technicalName = technicalName;
    }

    public List<DishPreparation> getPreparations() {
        return preparations;
    }

    public void setPreparations(List<DishPreparation> preparations) {
        this.preparations = preparations;
    }

    public Integer getDinersPerPlate() {
        return dinersPerPlate;
    }

    public void setDinersPerPlate(Integer dinersPerPlate) {
        this.dinersPerPlate = dinersPerPlate;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
}
