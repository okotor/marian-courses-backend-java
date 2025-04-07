package com.tehacko.backend_java;

import com.tehacko.backend_java.factory.UserFactory;
import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.repo.UserRepo;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BackendJavaApplication {
    public static void main(String[] args) {
        // Directly access environment variables
        System.setProperty("GOOGLE_CLIENT_ID", System.getenv("GOOGLE_CLIENT_ID"));
        System.setProperty("GOOGLE_CLIENT_SECRET", System.getenv("GOOGLE_CLIENT_SECRET"));
        System.setProperty("JWT_SECRET", System.getenv("JWT_SECRET"));

        SpringApplication.run(BackendJavaApplication.class, args);

	}

}
