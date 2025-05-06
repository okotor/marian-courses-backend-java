package com.tehacko.backend_java.controller;

import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.security.TokenUtil;
import com.tehacko.backend_java.service.JwtService;
import com.tehacko.backend_java.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "https://marian-courses-next-js-frontend.vercel.app"}) // Allow React
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private TokenUtil tokenUtil;

    @GetMapping({"/", "home"})
    public String home() {
        return "home";
    }

    @PostMapping("register")
    public ResponseEntity<?> register(@RequestBody User user){
        try {
            if (userService.emailExists(user.getEmail())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Email již existuje. Zkuste se přihlásit."));
            }
            User newUser = userService.saveUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Během registrace nastala chyba. Zkuste to ještě jednou."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            if (!authentication.isAuthenticated()) {
                responseBody.put("error", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }

            User authenticatedUser = userService.findByEmail(user.getEmail());

            // Generate both tokens
            String accessToken = jwtService.generateAccessToken(authenticatedUser.getEmail());
            String refreshToken = jwtService.generateRefreshToken(authenticatedUser);

            // Use TokenUtil to set tokens in cookies
            tokenUtil.setTokensInCookies(accessToken, refreshToken, response);

            responseBody.put("success", true);
            responseBody.put("user", Map.of("email", authenticatedUser.getEmail(), "is_admin", authenticatedUser.isAdmin()));
            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            responseBody.put("error", "Login failed due to an internal error.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<Map<String, Object>> googleLogin(@RequestBody Map<String, String> request, HttpServletResponse response) {
        System.out.println("Received POST /google-login request");
        Map<String, Object> responseBody = new HashMap<>();
        String googleToken = request.get("token");
        try {
            User googleUser = userService.validateGoogleToken(googleToken);
            if (googleUser != null) {
                // Check if the user already exists in the database
                System.out.println("Google user validated: " + googleUser.getEmail());
                User existingUser = userService.findByEmail(googleUser.getEmail());
                if (existingUser != null) {
                    // User exists, log them in
                    String accessToken = jwtService.generateAccessToken(existingUser.getEmail());
                    String refreshToken = jwtService.generateRefreshToken(existingUser);

                    // Use TokenUtil to set tokens in cookies
                    tokenUtil.setTokensInCookies(accessToken, refreshToken, response);

                    responseBody.put("success", true);
                    responseBody.put("user", Map.of("email", existingUser.getEmail(), "is_admin", existingUser.isAdmin()));
                    return ResponseEntity.ok(responseBody);
                } else {
                    // User does not exist, register them
                    User newUser = userService.saveUser(googleUser);
                    String accessToken = jwtService.generateAccessToken(newUser.getEmail());
                    String refreshToken = jwtService.generateRefreshToken(newUser);

                    // Use TokenUtil to set tokens in cookies
                    tokenUtil.setTokensInCookies(accessToken, refreshToken, response);
                    responseBody.put("success", true);
                    responseBody.put("user", Map.of("email", newUser.getEmail(), "is_admin", newUser.isAdmin()));
                    return ResponseEntity.ok(responseBody);
                }
            } else {
                responseBody.put("success", false);
                responseBody.put("message", "Google login failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }
        } catch (Exception e) {
            responseBody.put("success", false);
            responseBody.put("message", "An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @GetMapping("/auth/check")
    public ResponseEntity<?> checkAuth(@CookieValue(value = "jwtToken", required = false) String token) {
        if (token == null || !jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body(Map.of("authenticated", false));
        }

        String email = jwtService.extractUsername(token);
        User user = userService.findByEmail(email);

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "user", Map.of("email", user.getEmail(), "is_admin", user.isAdmin())
        ));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Missing refresh token");
        }

        User user = userService.findByRefreshToken(refreshToken); // ✅ Use service layer
        if (user == null) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        String newAccessToken = jwtService.refreshAccessToken(refreshToken);
        if (newAccessToken == null) {
            return ResponseEntity.status(401).body("Invalid refresh token");
        }

        // Use TokenUtil to set the new access token in the cookie
        tokenUtil.setTokensInCookies(newAccessToken, refreshToken, response);

        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                    HttpServletResponse response) {
        if (refreshToken != null) {
            userService.clearRefreshToken(refreshToken); // ✅ Use service
        }

        tokenUtil.clearCookies(response);
        System.out.println("Logged out successfully.");
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("adduser")
    public String addUser(){
        return "adduser";
    }

    @PostMapping("handleForm")
    public String handleForm(User user){
        userService.addUser(user);
        return "success";

    }

    @GetMapping("viewallusers")
    public String viewUsers(Model m) {
        List<User> users = userService.getAllUsers();
        m.addAttribute("users", users);
        return "viewallusers";
    }

    //User Details View, Edit, Delete
    //View
    @GetMapping("/user/{uId}")
    public ResponseEntity<User> viewUserDetails(@PathVariable("uId") Integer uId) {
        User user = userService.getUser(uId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Edit
    @PutMapping("user")
    public User updateUser(@RequestBody User user){
        userService.updateUser(user);
        return userService.getUser(user.getUId());
    }
    //Delete
    @DeleteMapping("user/{uId}")
    public String deleteUser(@PathVariable int uId) {
        userService.deleteUser(uId);
        return "Uživatelský účet by smazán.";
    }
    //Search by Keyword
    @GetMapping("users/keyword/{keyword}")
    public List<User> searchByKeyword(@PathVariable("keyword") String keyword){
        return userService.search(keyword);
    }

//    @GetMapping("load")
//    public String loadData(){
//        userService.load();
//        return "Data byla úspěšně načtena.";
//    }

}
