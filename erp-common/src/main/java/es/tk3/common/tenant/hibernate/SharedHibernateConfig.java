package es.tk3.common.tenant.hibernate;

import es.tk3.common.tenant.flyway.MultiTenantConnectionProviderImpl;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class SharedHibernateConfig {

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            JpaProperties jpaProperties,
            MultiTenantConnectionProviderImpl multiTenantConnectionProvider,
            TenantIdentifierResolver tenantIdentifierResolver
    ){
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("es.tk3");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
        properties.put("hibernate.multiTenancy", "DATABASE");
        properties.put("hibernate.multi_tenant_connection_provider", multiTenantConnectionProvider);
        properties.put("hibernate.tenant_identifier_resolver", tenantIdentifierResolver);

        em.setJpaPropertyMap(properties);
        return em;
    }
}
