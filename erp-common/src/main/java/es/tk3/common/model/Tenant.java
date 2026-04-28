package es.tk3.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tenants")
public class Tenant {
    @Id
    @NotBlank(message = "El ID del tenant es obligatorio")
    @Size(min = 3, max = 20, message = "El ID debe tener entre 3 y 20 caracteres")
    @Pattern(regexp = "^[a-z0-9_-]+$", message = "El ID solo puede contener letras minúsculas, números y guiones")
    private String id;

    @NotBlank(message = "La URL de la base de datos es obligatoria")
    @Column(name = "db_url")
    private String dbUrl;

    @NotBlank(message = "El usuario de la base de datos es obligatorio")
    @Column(name = "db_username")
    private String dbUsername;

    @NotBlank(message = "La contraseña de la base de datos es obligatoria")
    @Size(min = 4, message = "La contraseña debe tener al menos 4 caracteres")
    @Column(name = "db_password")
    private String dbPassword;

    public Tenant() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getDbUsername() {
        return dbUsername;
    }

    public void setDbUsername(String dbUsername) {
        this.dbUsername = dbUsername;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }
}
