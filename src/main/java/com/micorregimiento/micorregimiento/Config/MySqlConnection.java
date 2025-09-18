package com.micorregimiento.micorregimiento.Config;

import com.micorregimiento.micorregimiento.Config.interfaces.DatabaseConfig;
import com.micorregimiento.micorregimiento.Config.interfaces.DatabaseConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class MySqlConnection implements DatabaseConnection {

    private final DataSource dataSource;

    @Autowired
    public MySqlConnection(DatabaseConfig config) {
        this.dataSource = createDataSource(config);
    }

    private DataSource createDataSource(DatabaseConfig config) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl());
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.setDriverClassName(config.getDriverClassName());
        hikariConfig.setMaximumPoolSize(10);
        return new HikariDataSource(hikariConfig);
    }

    @Override
    public DataSource getDataSource() { return dataSource; }

    @Override
    public Connection getConnection() {
        try { return dataSource.getConnection(); }
        catch (SQLException e) { throw new RuntimeException("Error getting connection", e); }
    }

    @Override
    public boolean testConnection() {
        try (Connection conn = getConnection()) { return conn != null && !conn.isClosed(); }
        catch (SQLException e) { return false; }
    }
}
