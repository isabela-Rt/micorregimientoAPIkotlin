package com.micorregimiento.micorregimiento.Config;

import com.micorregimiento.micorregimiento.Config.interfaces.DatabaseConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MySqlConfig implements DatabaseConfig {

    @Value("${spring.datasource.url:jdbc:mysql://localhost:3306/micorregimiento}")
    private String url;

    @Value("${spring.datasource.username:root}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Value("${spring.datasource.driver-class-name:com.mysql.cj.jdbc.Driver}")
    private String driverClassName;

    @Override
    public String getUrl() { return url; }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getDriverClassName() { return driverClassName; }
}