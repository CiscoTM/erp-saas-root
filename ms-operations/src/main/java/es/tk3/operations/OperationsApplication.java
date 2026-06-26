package es.tk3.operations;

import es.tk3.common.tenant.flyway.TenantMigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan(basePackages = {"es.tk3.operations", "es.tk3.common"}) // Escanea componentes/servicios
@EnableJpaRepositories(basePackages = {"es.tk3.operations", "es.tk3.common"}) // Escanea repositorios
@EntityScan(basePackages = {"es.tk3.operations", "es.tk3.common"}) // Escanea entidades (Tenant, etc.)
public class OperationsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OperationsApplication.class, args);
    }

    @Bean
    public CommandLineRunner runMigrations(DataSource dataSource, TenantMigrationService migrationService) {
        return args -> {
            System.out.println("=======================================================");
            System.out.println(">>> [STARTUP] Iniciando migraciones de Operaciones...");

            migrationService.migrateAllTenants(dataSource, "classpath:db/migration/operations", "flyway_schema_history_operations");

            System.out.println(">>> [STARTUP] Migraciones de Operaciones completadas.");
            System.out.println("=======================================================");
        };
    }
}