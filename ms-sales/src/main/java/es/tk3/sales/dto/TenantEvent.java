package es.tk3.sales.dto;

public class TenantEvent {
    private String type;
    private String tenantId;
    private String adminUsername;
    private String adminPass;
    private String dbUrl;
    private String dbUsername;
    private String dbPassword;

    public TenantEvent() {
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