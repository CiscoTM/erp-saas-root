package es.tk3.common.tenant;

import es.tk3.common.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Value; // Importante
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    // Extraemos los valores del application.yml
    @Value("${spring.datasource.url}")
    private String centralUrl;

    @Value("${spring.datasource.username}")
    private String centralUsername;

    @Value("${spring.datasource.password}")
    private String centralPassword;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Bean
    @Primary
    public DataSource dataSource(@Lazy TenantRepository repository) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();

        routingDataSource.setTenantRepository(repository);

        // 1. Usamos las variables inyectadas en lugar de Strings fijos
        DataSource centralDS = DataSourceBuilder.create()
                .url(centralUrl)
                .username(centralUsername)
                .password(centralPassword)
                .driverClassName(driverClassName)
                .build();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("DEFAULT", centralDS);
        targetDataSources.put("master", centralDS);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(centralDS);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }
}