package es.tk3.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.tk3.common.tenant.flyway.TenantMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
@ComponentScan(basePackages = {
        "es.tk3.sales",
        "es.tk3.common.tenant",
        "es.tk3.common.outbox.service",
        "es.tk3.common.security"
})
@EnableJpaRepositories(basePackages = {
        "es.tk3.sales.repository",
        "es.tk3.common.outbox.repository",
        "es.tk3.common.repository"
})
@EntityScan(basePackages = {
        "es.tk3.sales.model",
        "es.tk3.common.outbox.model",
        "es.tk3.common.model"
})
@EnableKafka
@EnableScheduling
public class MsSalesApplication {

    private static final Logger log = LoggerFactory.getLogger(MsSalesApplication.class);

    @Value("${app.tenant.flyway.locations}")
    private String flywayLocations;

    public static void main(String[] args) {
        SpringApplication.run(MsSalesApplication.class, args);
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
            log.info("=======================================================");
            log.info(">>> [STARTUP] Iniciando migraciones de MS-SALES...");

            try {
                migrationService.migrateAllTenants(dataSource, flywayLocations, "flyway_schema_history_sales");

                log.info(">>> [STARTUP] Migraciones de MS-SALES completadas con éxito.");
            } catch (Exception e) {
                log.error(">>> [ERROR] Fallo crítico en la migración de tenants: {}", e.getMessage());

            }

            log.info("=======================================================");
        };
    }
}