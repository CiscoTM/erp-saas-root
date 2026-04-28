package es.tk3.events;

public class UserCreatedEvent {
    private String username;
    private String email;
    private String role;
    private String tenantId;

    public UserCreatedEvent() {}
    public UserCreatedEvent(String username, String email, String role, String tenantId) {
        this.username = username;
        this.email = email;
        this.role = role;
        this.tenantId = tenantId;
    }

    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getTenantId() { return tenantId; }
}
