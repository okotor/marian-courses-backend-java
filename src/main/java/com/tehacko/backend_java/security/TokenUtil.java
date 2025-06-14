package com.tehacko.backend_java.security;

import com.tehacko.backend_java.model.User;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class TokenUtil {

    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    @Value("${jwt.cookieExpirationSeconds}")
    private long cookieExpirationSeconds;

    @Value("${jwt.refreshCookieExpirationSeconds}")
    private long refreshCookieExpirationSeconds;

    private static final String CLEAR_ACCESS_COOKIE = "jwtToken=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=None";
    private static final String CLEAR_REFRESH_COOKIE = "refreshToken=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=None";

    public void setTokensInCookies(String accessToken, String refreshToken, HttpServletResponse response, boolean allowPersistent) {
        if (accessToken != null && !"null".equals(accessToken)) {
            String accessTokenCookie = buildCookie("jwtToken", accessToken, true, true, allowPersistent, cookieExpirationSeconds);
            response.addHeader("Set-Cookie", accessTokenCookie);
        } else {
            logger.warn("⚠ Attempted to set null or invalid accessToken cookie");
            response.addHeader("Set-Cookie", CLEAR_ACCESS_COOKIE);
        }

        if (refreshToken != null) {
            String refreshTokenCookie = buildCookie("refreshToken", refreshToken, true, true, allowPersistent, refreshCookieExpirationSeconds);
            response.addHeader("Set-Cookie", refreshTokenCookie);
        } else {
            response.addHeader("Set-Cookie", CLEAR_REFRESH_COOKIE);
        }
    }

    public void clearCookies(HttpServletResponse response) {
        response.addHeader("Set-Cookie", CLEAR_ACCESS_COOKIE);
        response.addHeader("Set-Cookie", CLEAR_REFRESH_COOKIE);
    }

    public String generateTokenForPasswordReset(User user) {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private String buildCookie(String name, String value, boolean httpOnly, boolean secure, boolean persistent, long maxAge) {
        String cookie = String.format("%s=%s; Path=/; SameSite=None", name, value);
        if (httpOnly) cookie += "; HttpOnly";
        if (secure) cookie += "; Secure";
        if (persistent) cookie += "; Max-Age=" + maxAge;
        return cookie;
    }
}