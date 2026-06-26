package es.tk3.common.tenant.flyway;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

public class TenantFlywayInitializer {
    public static void migrate(
            String tenantId, String dbUrl,
            String username, String password,
            String location, String tableName
    ){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(2);
        config.setMinimumIdle(0);
        config.setIdleTimeout(10000);
        config.setConnectionTimeout(30000);

        String poolName = "FlywayPool-" + tenantId.toUpperCase();
        config.setPoolName(poolName);
        config.addDataSourceProperty("ApplicationName", poolName);

        try (HikariDataSource migrationDs = new HikariDataSource(config)){

        Flyway.configure()
                .dataSource(dbUrl, username, password)
                .locations(location)
                .baselineOnMigrate(true)
                .baselineVersion("0")
                .table(tableName)
                .load()
                .migrate();
            System.out.println("✅ [Flyway] Migración completada con éxito para: " + tenantId);

        } catch (Exception e) {
            System.err.println("❌ [Flyway] Error crítico migrando tenant " + tenantId + ": " + e.getMessage());
            throw e;
        }
    }
}