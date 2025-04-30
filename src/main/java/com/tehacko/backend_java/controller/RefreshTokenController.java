package com.tehacko.backend_java.controller;

import com.tehacko.backend_java.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:3000") // Allow React
public class RefreshTokenController {

    @Autowired
    private JwtService jwtService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> request) {
        System.out.println("Received POST /api/refresh-token request");
        String refreshToken = request.get("refreshToken");
        String newToken = jwtService.refreshAccessToken(refreshToken);
        if (newToken == null) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }
        System.out.println("Token refreshed successfully"); // log success
        return ResponseEntity.ok(Collections.singletonMap("token", newToken));
    }
}