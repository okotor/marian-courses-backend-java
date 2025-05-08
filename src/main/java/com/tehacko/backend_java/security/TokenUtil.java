package com.tehacko.backend_java.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class TokenUtil {

    // Method to generate access token and refresh token and set them in cookies
    public void setTokensInCookies(String accessToken, String refreshToken, HttpServletResponse response) {
        // Manually set the access token cookie with SameSite=None
        String accessTokenCookie = String.format(
                "jwtToken=%s; Path=/; HttpOnly; Secure; Max-Age=%d; SameSite=None",
                accessToken, 15 * 60 // 15 minutes
        );
        response.addHeader("Set-Cookie", accessTokenCookie);

        // Manually set the refresh token cookie with SameSite=None
        String refreshTokenCookie = String.format(
                "refreshToken=%s; Path=/; HttpOnly; Secure; Max-Age=%d; SameSite=None",
                refreshToken, 7 * 24 * 60 * 60 // 7 days
        );
        response.addHeader("Set-Cookie", refreshTokenCookie);
    }

    public void clearCookies(HttpServletResponse response) {
        String clearAccessTokenCookie = "jwtToken=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=None";
        String clearRefreshTokenCookie = "refreshToken=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=None";

        response.addHeader("Set-Cookie", clearAccessTokenCookie);
        response.addHeader("Set-Cookie", clearRefreshTokenCookie);
    }
}
