package com.tehacko.backend_java;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendJavaApplication {
    public static void main(String[] args) {
        try {
            // Try to load .env if it exists (for local dev)
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing() // ✅ Don't crash if .env is missing (e.g., on Render)
                    .load();

            dotenv.entries().forEach(entry ->
                    System.setProperty(entry.getKey(), entry.getValue())
            );
        } catch (Exception e) {
            System.out.println("No .env file found or error reading it. Using system environment variables.");
        }

        // Start Spring Boot application
        SpringApplication.run(BackendJavaApplication.class, args);
    }
}