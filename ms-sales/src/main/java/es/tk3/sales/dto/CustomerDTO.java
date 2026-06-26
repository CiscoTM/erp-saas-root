package es.tk3.sales.dto;

import es.tk3.sales.model.CustomerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CustomerDTO(
        UUID id,

        @NotBlank(message = "El identificador fiscal es obligatorio")
        String taxId,

        @NotBlank(message = "El nombre es obligatorio")
        String name,

        @Email(message = "El formato del email no es válido")
        String email,

        @NotNull(message = "El tipo de cliente es obligatorio")
        CustomerType customerType
) {
}
