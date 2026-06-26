package es.tk3.kitchen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.tk3.common.tenant.flyway.TenantMigrationService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@ComponentScan(basePackages = {"es.tk3.kitchen", "es.tk3.common"})
@EnableJpaRepositories(basePackages = {"es.tk3.kitchen", "es.tk3.common"})
@EntityScan(basePackages = {"es.tk3.kitchen", "es.tk3.common"})
@EnableKafka
@EnableScheduling
public class KitchenApplication {

    public static void main(String[] args) {
        SpringApplication.run(KitchenApplication.class, args);
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public CommandLineRunner runMigrations(DataSource dataSource, TenantMigrationService migrationService) {
        return args -> {
            System.out.println("=======================================================");
            System.out.println(">>> [STARTUP] Iniciando migraciones de Cocina...");

            migrationService.migrateAllTenants(dataSource, "classpath:db/migration/kitchen", "flyway_schema_history_kitchen");

            System.out.println(">>> [STARTUP] Migraciones de Cocina completadas.");
            System.out.println("=======================================================");
        };
    }
}