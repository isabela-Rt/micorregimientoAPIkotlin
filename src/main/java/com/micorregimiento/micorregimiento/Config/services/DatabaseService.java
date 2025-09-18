package com.micorregimiento.micorregimiento.Config.services;

import com.micorregimiento.micorregimiento.Config.interfaces.DatabaseConnection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;

@Service
public class DatabaseService {

    private final DatabaseConnection connection;

    @Autowired
    public DatabaseService(DatabaseConnection connection) {
        this.connection = connection;
    }

    public DataSource getDataSource() { return connection.getDataSource(); }

    public Connection getConnection() { return connection.getConnection(); }

    public boolean isConnected() { return connection.testConnection(); }
}