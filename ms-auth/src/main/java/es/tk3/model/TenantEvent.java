package es.tk3.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TenantEvent {
    private String type;
    private String tenantId;

    @JsonProperty("adminUsername")
    private String adminUsername;

    @JsonProperty("adminPass")
    private String adminPass; // 👈 Unificado con el JSON y ms-sales

    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public TenantEvent() {}

    public TenantEvent(String type, String tenantId, String adminUsername, String adminPass, String dbUrl, String dbUsername, String dbPassword) {
        this.type = type;
        this.tenantId = tenantId;
        this.adminUsername = adminUsername;
        this.adminPass = adminPass;
        this.dbUrl = dbUrl;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }

    // Getters y Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }

    public String getAdminPass() { return adminPass; }
    public void setAdminPass(String adminPass) { this.adminPass = adminPass; }

    public String getDbUrl() { return dbUrl; }
    public void setDbUrl(String dbUrl) { this.dbUrl = dbUrl; }

    public String getDbUsername() { return dbUsername; }
    public void setDbUsername(String dbUsername) { this.dbUsername = dbUsername; }

    public String getDbPassword() { return dbPassword; }
    public void setDbPassword(String dbPassword) { this.dbPassword = dbPassword; }
}