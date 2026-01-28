package com.upb.agripos.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static DatabaseConnection instance;
    private HikariDataSource dataSource;

    private DatabaseConnection() {
        // Load .env if present; don't fail if it's missing (allows running without an .env file)
        Dotenv dotenv = Dotenv.configure()
            .ignoreIfMissing()
            .load();
        String password = dotenv.get("DB_PASSWORD");
        if (password == null) {
            password = "";
        }
        String url = "jdbc:postgresql://localhost:5432/agripos";


        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername("postgres");
        config.setPassword(password);
        config.setMaximumPoolSize(10);
        this.dataSource = new HikariDataSource(config);
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
