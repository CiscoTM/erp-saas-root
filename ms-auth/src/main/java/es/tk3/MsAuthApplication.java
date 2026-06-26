package es.tk3;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import es.tk3.common.tenant.flyway.TenantMigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "es.tk3")
@EntityScan(basePackages = "es.tk3")
@EnableKafka
@EnableScheduling
public class MsAuthApplication {

	private static final Logger log = LoggerFactory.getLogger(MsAuthApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(MsAuthApplication.class, args);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	@Bean
	public CommandLineRunner runAuthMigrations(DataSource dataSource, TenantMigrationService migrationService) {
		return args -> {
			log.info("=======================================================");
			log.info("🔐 [STARTUP] Iniciando migración de identidad en Tenants...");
			try {
				migrationService.migrateAllTenants(dataSource, "classpath:db/migration/tenants", "flyway_schema_history_auth");
				log.info("✅ [STARTUP] Migración de identidad completada.");
			} catch (Exception e) {
				log.error("❌ [ERROR] Fallo en migración: {}", e.getMessage());
			}
			log.info("=======================================================");
		};
	}
}