package com.tehacko.backend_java.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

    // Method to generate access token and refresh token and set them in cookies
    public void setTokensInCookies(String accessToken, String refreshToken, HttpServletResponse response) {
        // Set JWT access token in HTTP-only cookie
        Cookie accessTokenCookie = new Cookie("jwtToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);  // Use secure cookies in production
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // Set expiration for 15 minutes
        response.addCookie(accessTokenCookie);

        // Set refresh token in a separate HTTP-only cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);  // Use secure cookies in production
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // Set expiration for 7 days
        response.addCookie(refreshTokenCookie);
    }
}
