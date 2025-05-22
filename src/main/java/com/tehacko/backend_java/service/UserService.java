package com.tehacko.backend_java.service;

import com.tehacko.backend_java.exception.CustomException;
import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.repo.UserRepo;
import com.tehacko.backend_java.security.TokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenUtil tokenUtil;

    @Autowired
    private GoogleTokenValidatorService googleTokenValidatorService;

    @Autowired
    private EmailService emailService;

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
            user.setEnabled(false);
            User savedUser = userRepo.save(user);

            // Generate verification token
            String token = jwtService.generateAccessToken(savedUser.getEmail());
            savedUser.setVerificationToken(token);
            userRepo.save(savedUser);

            // Send confirmation email
            String verifyUrl = emailService.getFrontendBaseUrl() + "/confirm-email?token=" + token;
            emailService.sendEmail(savedUser.getEmail(), "Potvrďte registraci",
                    "Klikněte na tento odkaz pro potvrzení: " + verifyUrl);


            return savedUser;
        } catch (Exception e) {
            throw new CustomException("Nepodařilo se uživatele zaregistrovat: " + e.getMessage(), 500);
        }
    }

    public void confirmEmail(String token) {
        String email = jwtService.extractUsername(token);
        User user = findByEmail(email);

        if (user == null) {
            throw new CustomException("Uživatel nebyl nalezen.", HttpStatus.NOT_FOUND.value());
        }

        if (!token.equals(user.getVerificationToken())) {
            throw new CustomException("Neplatný ověřovací token.", HttpStatus.BAD_REQUEST.value());
        }

        user.setEnabled(true);
        user.setVerificationToken(null);
        userRepo.save(user);
    }

    public void resendConfirmationEmail(String email) {
        User user = findByEmail(email);

        if (user == null) {
            throw new CustomException("Uživatel nebyl nalezen.", HttpStatus.NOT_FOUND.value());
        }

        if (user.isEnabled()) {
            throw new CustomException("Uživatel je již ověřen. Zkuste se přihlásit", HttpStatus.BAD_REQUEST.value());
        }

        String token = jwtService.generateAccessToken(user.getEmail());
        user.setVerificationToken(token);
        userRepo.save(user);

        String verifyUrl = emailService.getFrontendBaseUrl() + "/confirm-email?token=" + token;
        emailService.sendEmail(user.getEmail(), "Znovu odesláno potvrzení",
                "Klikněte na tento odkaz pro potvrzení: " + verifyUrl);
    }

    public Map<String, Object> userLogin(User user, HttpServletResponse response) {
        if (user.getEmail() == null || user.getPassword() == null) {
            throw new CustomException("Email a heslo musí být vyplněny.", HttpStatus.BAD_REQUEST.value());
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );
        } catch (BadCredentialsException ex) {
            throw new CustomException("Neplatné přihlašovací údaje.", HttpStatus.UNAUTHORIZED.value());
        }

        User authenticatedUser = findByEmail(user.getEmail());
        if (authenticatedUser == null) {
            throw new CustomException("Uživatel nebyl nalezen.", HttpStatus.NOT_FOUND.value());
        }

        if (!authenticatedUser.isEnabled()) {
            throw new CustomException("Účet není ověřen. Zkontrolujte svůj e-mail pro potvrzení registrace.", HttpStatus.UNAUTHORIZED.value());
        }

        String accessToken = jwtService.generateAccessToken(authenticatedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(authenticatedUser);
        tokenUtil.setTokensInCookies(accessToken, refreshToken, response);

        return Map.of(
                "success", true,
                "user", Map.of("email", authenticatedUser.getEmail(), "is_admin", authenticatedUser.isAdmin())
        );
    }

    public ResponseEntity<?> initiatePasswordReset(Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Email je povinný."));
        }
        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByEmail(email.trim()));
        if (optionalUser.isEmpty()) {
            // Optionally, always return 200 to prevent email enumeration
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Tento uživatel zatím neexistuje. Zaregistrujte se prosím."));
        }
        User user = optionalUser.get();
        String token = tokenUtil.generateTokenForPasswordReset(user);
        // Save token, optionally with expiration
        user.setResetToken(token);
        user.setResetTokenCreatedAt(LocalDateTime.now());
        userRepo.save(user);
        // Construct reset link
        String resetLink = emailService.getFrontendBaseUrl() + "/reset-password?token=" + token;
        // Send email
        String subject = "Obnovení hesla";
        String body = String.format(
                "Ahoj %s,\n\nPro obnovení hesla klikněte na následující odkaz:\n%s\n\nPokud jste žádost neposlali, ignorujte tento e-mail.",
                user.getEmail(), resetLink
        );
        try {
            emailService.sendEmail(user.getEmail(), subject, body);
            return ResponseEntity.ok(Map.of("message", "Pokyny pro obnovení hesla byly odeslány na váš email."));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Nepodařilo se odeslat e-mail s pokyny."));
        }
    }

    public ResponseEntity<?> resetPasswordWithToken(Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("password");

        if (token == null || token.isBlank() || newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Zadejte prosím nové heslo."));
        }

        Optional<User> optionalUser = userRepo.findByResetToken(token);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Odkaz není platný. Požádejte prosím znovu o obnovení hesla."));
        }

        User user = optionalUser.get();

        // Optionally validate token age (e.g., 1 hour expiration)
        if (user.getResetTokenCreatedAt() == null || user.getResetTokenCreatedAt().isBefore(LocalDateTime.now().minusHours(1))) {
            return ResponseEntity.status(HttpStatus.GONE).body(Map.of("message", "Platnost odkazu vypršela. Požádejte prosím znovu o obnovení hesla."));
        }

        user.setPassword(encoder.encode(newPassword));
        user.setResetToken(null);
        user.setResetTokenCreatedAt(null);
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Heslo bylo úspěšně změněno."));
    }


    public Map<String, Object> userGoogleLogin(String googleToken, HttpServletResponse response) throws GeneralSecurityException, IOException {
        User googleUser = validateGoogleToken(googleToken);
        if (googleUser == null) {
            throw new CustomException("Neplatné přihlášení s Google. Zkuste to prosím znovu.", HttpStatus.UNAUTHORIZED.value());
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

    public ResponseEntity<?> changeUserPassword(Map<String, String> payload, HttpServletRequest request) {
        String oldPassword = payload.get("oldPassword");
        String newPassword = payload.get("newPassword");

        if (oldPassword == null || newPassword == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Původní nebo nové heslo chybí."));
        }

        String userEmail = (String) request.getAttribute("userEmail");
        if (userEmail == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Uživatel není autentizován."));
        }

        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByEmail(userEmail));
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Uživatel nebyl nalezen."));
        }

        User user = optionalUser.get();
        if (!encoder.matches(oldPassword, user.getPassword())) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Původní heslo není správné."));
        }

        user.setPassword(encoder.encode(newPassword));
        userRepo.save(user);

        return ResponseEntity.ok(Map.of("message", "Heslo bylo úspěšně změněno."));
    }

    public Map<String, Object> userCheckAuth(String token) {
        if (token == null || !jwtService.isTokenValid(token)) {
            throw new CustomException("Uživatel odhlášen.", HttpStatus.UNAUTHORIZED.value());
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
            throw new CustomException("Neplatný nebo expirovaný token. Přihlaste se znovu.", HttpStatus.UNAUTHORIZED.value());
        }

        if (!user.isEnabled()) {
            throw new CustomException("Účet není ověřen. Prosím ověřte svůj e-mail.", HttpStatus.UNAUTHORIZED.value());
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

    public ResponseEntity<?> deleteAccount(HttpServletRequest request) {
        String userEmail = (String) request.getAttribute("userEmail");

        if (userEmail == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Uživatel není autentizován."));
        }

        Optional<User> optionalUser = Optional.ofNullable(userRepo.findByEmail(userEmail));
        if (optionalUser.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Uživatel nebyl nalezen."));
        }

        userRepo.delete(optionalUser.get());

        return ResponseEntity.ok(Map.of("message", "Účet byl úspěšně smazán."));
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
