package com.tehacko.backend_java.config;

import com.tehacko.backend_java.service.JwtService;
import com.tehacko.backend_java.service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MyUserDetailsService myUserDetailsService;

    public JwtFilter(JwtService jwtService, MyUserDetailsService myUserDetailsService) {
        this.jwtService = jwtService;
        this.myUserDetailsService = myUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String username = null;

        // Log start
        System.out.println("[JwtFilter] Checking authentication for request: " + request.getRequestURI());

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
                username = jwtService.extractUsername(token);
                System.out.println("[JwtFilter] Token belongs to: " + username);
            } catch (Exception e) {
                System.out.println("[JwtFilter] Invalid token: " + e.getMessage());
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("[JwtFilter] Authentication successful for user: " + username);
                } else {
                    System.out.println("[JwtFilter] Token validation failed for user: " + username);
                }
            }
        } else {
            System.out.println("[JwtFilter] No valid JWT token found.");
        }
        filterChain.doFilter(request, response);
    }
}