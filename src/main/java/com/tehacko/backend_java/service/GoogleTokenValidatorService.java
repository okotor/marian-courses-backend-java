package com.tehacko.backend_java.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.tehacko.backend_java.exception.CustomException;
import com.tehacko.backend_java.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Service
public class GoogleTokenValidatorService {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static GoogleIdTokenVerifier verifier;

    public GoogleTokenValidatorService(@Value("${google.clientId}") String clientId) throws GeneralSecurityException, IOException {
        if (verifier == null) {
            synchronized (GoogleTokenValidatorService.class) {
                if (verifier == null) {
                    verifier = new GoogleIdTokenVerifier.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY)
                            .setAudience(Collections.singletonList(clientId))
                            .build();
                }
            }
        }
    }

    public User validateGoogleToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            Payload payload = idToken.getPayload();
            String email = payload.getEmail();

            // Create or update user based on the retrieved information
            User user = new User();
            user.setEmail(email);
            user.setPassword(""); // Set an empty password for Google users
            user.setAdmin(determineIfUserIsAdmin(email));

            return user;
        } else {
            throw new CustomException("Neplatný Googlovský token.", 401);
        }
    }

    private boolean determineIfUserIsAdmin(String email) {
        // Implement your logic to determine if the user is an admin
        return false;
    }
}