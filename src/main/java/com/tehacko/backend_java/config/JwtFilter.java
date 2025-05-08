package com.tehacko.backend_java.config;

import com.tehacko.backend_java.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        // Log start
        System.out.println("[JwtFilter] Checking authentication for request: " + request.getRequestURI());

        try {
            // 1. check cookies
            if (request.getCookies() != null) {
                for (Cookie cookie : request.getCookies()) {
                    if ("jwtToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        System.out.println("[JwtFilter] Token found in cookie.");
                        break;
                    }
                }
            }

            // 2. Validate token and set authentication
            if (token != null) {
                try {
                    String username = jwtService.extractUsername(token);
                    if (jwtService.isTokenValid(token)) {
                        // Extract roles from token claims
                        Claims claims = jwtService.getClaimsFromToken(token);
                        String roles = claims.get("roles", String.class);

                        // Create authentication object with roles
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(username, null,
                                        Collections.singleton(new SimpleGrantedAuthority(roles)));
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("[JwtFilter] Authentication successful for user: " + username);
                    } else {
                        System.out.println("[JwtFilter] Token validation failed.");
                    }
                } catch (Exception e) {
                    System.out.println("[JwtFilter] Invalid token: " + e.getMessage());
                }
            } else {
                System.out.println("[JwtFilter] No valid JWT token found.");
            }
        } finally {
            // Ensure the filter chain proceeds
            filterChain.doFilter(request, response);
        }
    }
}