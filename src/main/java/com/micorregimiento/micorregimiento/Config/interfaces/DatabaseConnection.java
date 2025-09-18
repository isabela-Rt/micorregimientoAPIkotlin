package com.micorregimiento.micorregimiento.Config.interfaces;

import javax.sql.DataSource;
import java.sql.Connection;

public interface DatabaseConnection {
    DataSource getDataSource();
    Connection getConnection();
    boolean testConnection();
}
