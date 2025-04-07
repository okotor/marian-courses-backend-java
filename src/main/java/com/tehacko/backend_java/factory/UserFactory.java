package com.tehacko.backend_java.factory;

import com.tehacko.backend_java.model.User;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype") // Ensures each request gets a new instance
public class UserFactory {
    public User createUser() {
        return new User(); // Always returns a fresh User instance
    }
}
