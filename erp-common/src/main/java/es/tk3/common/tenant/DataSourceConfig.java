package es.tk3.common.tenant;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import es.tk3.common.repository.TenantRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.url}")
    private String centralUrl;

    @Value("${spring.datasource.username}")
    private String centralUsername;

    @Value("${spring.datasource.password}")
    private String centralPassword;

    @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
    private String driverClassName;

    @Value("${spring.application.name:default-app}")
    private String applicationName;

    @Bean
    @Primary
    public DataSource dataSource(@Lazy TenantRepository repository) {
        DynamicRoutingDataSource routingDataSource = new DynamicRoutingDataSource();
        routingDataSource.setTenantRepository(repository);

        DataSource centralDS = getDataSource();

        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put("DEFAULT", centralDS);
        targetDataSources.put("master", centralDS);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(centralDS);
        routingDataSource.afterPropertiesSet();

        return routingDataSource;
    }

    private @NonNull DataSource getDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(centralUrl);
        hikariConfig.setUsername(centralUsername);
        hikariConfig.setPassword(centralPassword);
        hikariConfig.setDriverClassName(driverClassName);

        hikariConfig.setMaximumPoolSize(2);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(30000);
        hikariConfig.setConnectionTimeout(30000);

        String poolName = "CentralPool-" + applicationName.toUpperCase();
        hikariConfig.setPoolName(poolName);
        hikariConfig.addDataSourceProperty("ApplicationName", poolName);

        return new HikariDataSource(hikariConfig);
    }
}