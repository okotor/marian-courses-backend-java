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
    // Load .env from backend-java directory
    Dotenv dotenv = Dotenv.configure().load();
    System.setProperty("GOOGLE_CLIENT_ID", dotenv.get("GOOGLE_CLIENT_ID"));
    System.setProperty("GOOGLE_CLIENT_SECRET", dotenv.get("GOOGLE_CLIENT_SECRET"));
    System.setProperty("JWT_SECRET", dotenv.get("JWT_SECRET"));

//    // Debug: Print loaded environment variables
//        System.out.println("GOOGLE_CLIENT_ID: " + dotenv.get("GOOGLE_CLIENT_ID"));
//        System.out.println("GOOGLE_CLIENT_SECRET: " + dotenv.get("GOOGLE_CLIENT_SECRET"));

	SpringApplication.run(BackendJavaApplication.class, args);

//		ApplicationContext context =
//		 Get the UserRepo bean from the Spring context
//		UserRepo userRepo = context.getBean(UserRepo.class);
//
//		UserFactory userFactory = context.getBean(UserFactory.class);
//
//		User u1 = userFactory.createUser();
//		User u2 = userFactory.createUser();
//		User u3 = userFactory.createUser();
//
//		u1.setEmail("tehacko@seznam.cz");
//		u1.setPassword("lol");
//		u1.setAdmin(true);
//
//		u2.setEmail("tehko@seznam.cz");
//		u2.setPassword("lolo");
//		u2.setAdmin(false);
//
//		u3.setEmail("solisara@example.com");
//		u3.setPassword("lo");
//		u3.setAdmin(false);
//
//		userRepo.save(u1);
//		userRepo.save(u2);
//		userRepo.save(u3);

		//	Optional<Student> s = repo.findById(103);
//
//	System.out.println(repo.findAll());
//
//	System.out.println(s.orElse(new Student()));
//
//	repo.findByName("Lameen");

	}

}
