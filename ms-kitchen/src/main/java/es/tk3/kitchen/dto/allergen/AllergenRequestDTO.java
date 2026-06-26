package es.tk3.kitchen.dto.allergen;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class AllergenRequestDTO {
    @NotBlank(message = "El código del alérgeno es mandatorio.")
    @Size(max = 50, message = "El código no puede superar los 50 caracteres.")
    @Pattern(regexp = "^[A-Z0-8_]+$", message = "El código debe ser alfanumérico en mayúsculas y usar guiones bajos.")
    private String code;

    @NotBlank(message = "El nombre del alérgeno es mandatorio.")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres.")
    private String name;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres.")
    private String description;

    @Size(max = 500, message = "La URL del icono es demasiado larga.")
    private String iconUrl;


    public AllergenRequestDTO() {}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private AllergenRequestDTO(Builder builder) {
        this.code = builder.code;
        this.name = builder.name;
        this.iconUrl = builder.iconUrl;
        this.description = builder.description;
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String code;
        private String name;
        private String iconUrl;
        private String description;

        public Builder code(String code){
            this.code = code;
            return this;
        }
        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder iconUrl(String iconUrl){
            this.iconUrl = iconUrl;
            return this;
        }

        public Builder description(String description){
            this.description = description;
            return this;
        }

        public AllergenRequestDTO build(){
            return new AllergenRequestDTO(this);
        }




    }

































}
