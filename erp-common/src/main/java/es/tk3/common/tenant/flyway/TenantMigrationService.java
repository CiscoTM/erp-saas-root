package es.tk3.common.tenant.flyway;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
import java.util.List;

@Service
public class TenantMigrationService {

    // Leemos los valores desde el application.yml del microservicio que use la clase
    @Value("${app.tenant.datasource.base-url}")
    private String tenantBaseUrl; // Ejemplo: jdbc:postgresql://localhost:5433/

    @Value("${app.tenant.datasource.username}")
    private String tenantUsername;

    @Value("${app.tenant.datasource.password}")
    private String tenantPassword;

    public void migrateAllTenants(DataSource centralDataSource, String migrationLocation) {
        JdbcTemplate jdbc = new JdbcTemplate(centralDataSource);

        // Obtenemos los IDs de los tenants de la tabla central
        List<String> tenants = jdbc.queryForList("SELECT id FROM tenants", String.class);

        for (String tenantId : tenants) {
            // Construimos la URL dinámicamente sin hardcoding
            String dbUrl = tenantBaseUrl + "db_" + tenantId;

            Flyway flyway = Flyway.configure()
                    .dataSource(dbUrl, tenantUsername, tenantPassword)
                    .locations(migrationLocation)
                    .baselineOnMigrate(true)
                    .ignoreMigrationPatterns("*:missing")
                    .load();

            flyway.migrate();
        }
    }
}