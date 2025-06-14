package com.tehacko.backend_java.service;

import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.repo.UserRepo;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpiration;

    private final UserRepo userRepo;

    public JwtService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // === ACCESS TOKEN ===
    public String generateAccessToken(String username) {
        return createToken(new HashMap<>(), username, expiration);
    }

    // === REFRESH TOKEN ===
    public String generateRefreshToken(User user) {
        String refreshToken = createToken(new HashMap<>(), user.getEmail(), refreshExpiration);
        user.setRefreshToken(refreshToken);
        userRepo.save(user); // Save refresh token to DB
        return refreshToken;
    }

    public String refreshAccessToken(String refreshToken) {
        if (refreshToken == null) return null;

        // 🔑 Trust DB as source of truth
        User user = userRepo.findByRefreshToken(refreshToken);
        if (user == null || !user.isEnabled()) {
            return null;
        }

        // ✅ No need to parse refresh token again — if it's valid enough to exist in DB, we issue a new access token
        return generateAccessToken(user.getEmail());
    }

//    public String refreshAccessToken(String refreshToken) {
//        try {
//            Claims claims = getClaimsFromToken(refreshToken);
//            if (claims.getExpiration().before(new Date())) {
//                return null;
//            }
//            return generateAccessToken(claims.getSubject());
//        } catch (JwtException | IllegalArgumentException e) {
//            return null;
//        }
//    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private String createToken(Map<String, Object> claims, String subject, long tokenValidity) {
        User user = userRepo.findByEmail(subject);
        if (user == null) {
            throw new IllegalStateException("🚨 Cannot create token — user not found for email: " + subject);
        }
        claims.put("roles", user.isAdmin() ? "ADMIN" : "USER");
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenValidity))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

//    public Boolean validateToken(String token, UserDetails userDetails) {
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }

    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }
}

