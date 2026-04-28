package es.tk3.common.tenant.flyway;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MultiTenantConnectionProviderImpl implements MultiTenantConnectionProvider<String> {

    private final DataSource dataSource;
    private final String dbUrlPrefix;

    public MultiTenantConnectionProviderImpl(DataSource dataSource, @Value("${spring.datasource.url}") String dbUrl) {
        this.dataSource = dataSource;
        this.dbUrlPrefix = dbUrl.substring(0, dbUrl.lastIndexOf("/") + 1);
    }

    @Override
    public Connection getAnyConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void releaseAnyConnection(Connection connection) throws SQLException {
        connection.close();
    }

    @Override
    public Connection getConnection(String tenantId) throws SQLException {
        Connection connection = getAnyConnection();
        connection.createStatement().execute("SET search_path TO public");
        String dbName = "db_" + tenantId;
        connection.setCatalog(dbName);
        return connection;
    }

    @Override
    public void releaseConnection(String tenantId, Connection connection) throws SQLException {
        releaseAnyConnection(connection);
    }

    @Override
    public boolean supportsAggressiveRelease() {
        return false;
    }

    @Override
    public boolean isUnwrappableAs(@NonNull Class<?> unwrapType) { return false; }

    @Override
    public <T> T unwrap(@NonNull Class<T> unwrapType) { return null; }
}
