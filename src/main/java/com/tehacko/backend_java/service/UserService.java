package com.tehacko.backend_java.service;

import com.tehacko.backend_java.exception.CustomException;
import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.repo.UserRepo;
import com.tehacko.backend_java.security.TokenUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenUtil tokenUtil;

    @Autowired
    private GoogleTokenValidatorService googleTokenValidatorService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserService(UserRepo userRepo, JwtService jwtService,
                          AuthenticationManager authenticationManager, TokenUtil tokenUtil) {
        this.userRepo = userRepo;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.tokenUtil = tokenUtil;
    }

    public User userRegister(User user) {
        if (emailExists(user.getEmail())) {
            throw new CustomException("Uživatel s tímto emailem již existuje, prosím přihlašte se.", 400);
        }
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            return userRepo.save(user);
        } catch (Exception e) {
            throw new CustomException("Nepodařilo se uživatele zaregistrovat: " + e.getMessage(), 500);
        }
    }

    public Map<String, Object> userLogin(User user, HttpServletResponse response) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new CustomException("Email a heslo musí být vyplněny.", HttpStatus.BAD_REQUEST.value());
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new CustomException("Neplatné přihlašovací údaje.", HttpStatus.UNAUTHORIZED.value());
        }

        User authenticatedUser = findByEmail(user.getEmail());
        if (authenticatedUser == null) {
            throw new CustomException("Uživatel nebyl nalezen.", HttpStatus.NOT_FOUND.value());
        }

        String accessToken = jwtService.generateAccessToken(authenticatedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        tokenUtil.setTokensInCookies(accessToken, refreshToken, response);

        return Map.of(
                "success", true,
                "user", Map.of("email", authenticatedUser.getEmail(), "is_admin", authenticatedUser.isAdmin())
        );
    }

    public Map<String, Object> userGoogleLogin(String googleToken, HttpServletResponse response) throws GeneralSecurityException, IOException {
        User googleUser = validateGoogleToken(googleToken);
        if (googleUser == null) {
            throw new CustomException("Neplatný Google token.", HttpStatus.UNAUTHORIZED.value());
        }

        User existingUser = findByEmail(googleUser.getEmail());
        if (existingUser == null) {
            existingUser = userRepo.save(googleUser);
        }

        String accessToken = jwtService.generateAccessToken(existingUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(existingUser);
        tokenUtil.setTokensInCookies(accessToken, refreshToken, response);

        return Map.of(
                "success", true,
                "user", Map.of("email", existingUser.getEmail(), "is_admin", existingUser.isAdmin())
        );
    }

    public Map<String, Object> userCheckAuth(String token) {
        if (token == null || !jwtService.isTokenValid(token)) {
            throw new CustomException("Token není platný.", HttpStatus.UNAUTHORIZED.value());
        }

        String email = jwtService.extractUsername(token);
        User user = findByEmail(email);

        return Map.of(
                "authenticated", true,
                "user", Map.of("email", user.getEmail(), "is_admin", user.isAdmin())
        );
    }

    public Map<String, Object> userRefreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new CustomException("Chybí refresh token.", HttpStatus.UNAUTHORIZED.value());
        }

        User user = findByRefreshToken(refreshToken);
        if (user == null) {
            throw new CustomException("Neplatný refresh token.", HttpStatus.UNAUTHORIZED.value());
        }

        String newAccessToken = jwtService.refreshAccessToken(refreshToken);
        tokenUtil.setTokensInCookies(newAccessToken, refreshToken, response);

        return Map.of("success", true);
    }

    public User findByRefreshToken(String refreshToken) {
        return userRepo.findByRefreshToken(refreshToken);
    }

    public void clearRefreshToken(String refreshToken) {
        User user = userRepo.findByRefreshToken(refreshToken);
        if (user != null) {
            user.setRefreshToken(null);
            userRepo.save(user);
        }
    }

    public User validateGoogleToken(String googleToken) {
        try {
            return googleTokenValidatorService.validateGoogleToken(googleToken);
        } catch (GeneralSecurityException | IOException e) {
            throw new CustomException("Chyba při ověřování Google tokenu: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    public Map<String, Object> userLogout(String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            clearRefreshToken(refreshToken);
        }
        tokenUtil.clearCookies(response);
        return Map.of("message", "Odhlášení proběhlo úspěšně.");
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User userFindById(int uId) {
        return userRepo.findById(uId).orElseThrow(() ->
                new CustomException("Uživatel nebyl nalezen.",
                        HttpStatus.NOT_FOUND.value()));
    }

    public void userUpdate(User user) {
        userRepo.save(user);
    }

    //ALTERNATIVELY:
//    public User updateUser(UserRequest userRequest) {
//        User user = userRepo.findByEmail(userRequest.getEmail());
//        if (user != null) {
//            user.setPassword(encoder.encode(userRequest.getPassword()));
//            return userRepo.save(user);
//        } else {
//            return null;
//        }
//    }

    public void deleteUser(int uId) {
        userRepo.deleteById(uId);
    }

    public boolean emailExists(String email) {
        return userRepo.findByEmail(email) != null;
    }

    public List<User> search(String keyword) {
        return userRepo.findByEmailContainingOrPassword(keyword, keyword);
    }

//    public void load() {
//        // ArrayList to store User objects
//        List<User> users = new ArrayList<>(Arrays.asList(
//                new User(1, "lopoto@seznam.cz", "lol", true),
//                new User(2, "kolotoc@seznam.cz", "lolo", false),
//                new User(3, "mato@seznam.cz", "olmo", false)
//        ));
//        userRepo.saveAll(users);
//    }
}
