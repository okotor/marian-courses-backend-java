package com.tehacko.backend_java.database;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@Component
public class PostgreSQLExample implements CommandLineRunner {
    @Override
    public void run(String... args) throws Exception {
        // PostgreSQL connection URL and credentials
        String url = "jdbc:postgresql://ballast.proxy.rlwy.net:57638/railway";
        String user = "postgres";
        String password = "RLtRhJkYIzytRBcToZYJLmHqHioKlqnf";

        // Try-with-resources statement to ensure the connection is closed automatically
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            // Check if the connection is successful
            if (conn != null) {
                System.out.println("Connected to the database!");

                // Create a statement object to execute SQL queries
                try (Statement stmt = conn.createStatement()) {

                    // Create a table for logging if it doesn't already exist
                    String createTableSQL = "CREATE TABLE IF NOT EXISTS connection_logs (" +
                            "id SERIAL PRIMARY KEY, " +
                            "log_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
                    stmt.execute(createTableSQL);
                }
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) {
            // Print the stack trace for any exception that occurs
            e.printStackTrace();
        }
    }
}