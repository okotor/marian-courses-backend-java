package com.tehacko.backend_java.controller;
import com.tehacko.backend_java.model.User;
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
@CrossOrigin(origins = "http://localhost:3000") // Allow React
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

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
            String token = jwtService.generateToken(authenticatedUser.getEmail());

            // Set JWT token in HTTP-only cookie
            Cookie cookie = new Cookie("jwtToken", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Use secure cookies in production
            cookie.setPath("/");
            cookie.setMaxAge(7 * 24 * 60 * 60); // Set cookie expiry to 7 days
            response.addCookie(cookie);

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
        String googleToken = request.get("token");
        try {
            User googleUser = userService.validateGoogleToken(googleToken);
            if (googleUser != null) {
                // Check if the user already exists in the database
                User existingUser = userService.findByEmail(googleUser.getEmail());
                if (existingUser != null) {
                    // User exists, log them in
                    String token = jwtService.generateToken(existingUser.getEmail());
                    // Set JWT token in HTTP-only cookie
                    Cookie cookie = new Cookie("jwtToken", token);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true); // Use secure cookies in production
                    cookie.setPath("/");
                    cookie.setMaxAge(7 * 24 * 60 * 60); // Set cookie expiry to 7 days
                    response.addCookie(cookie);

                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("success", true);
                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("email", existingUser.getEmail());
                    userDetails.put("is_admin", existingUser.isAdmin());
                    responseBody.put("user", userDetails);
                    return ResponseEntity.ok(responseBody);
                } else {
                    // User does not exist, register them
                    User newUser = userService.saveUser(googleUser);
                    String token = jwtService.generateToken(newUser.getEmail());

                    // Set JWT token in HTTP-only cookie
                    Cookie cookie = new Cookie("jwtToken", token);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true); // Use secure cookies in production
                    cookie.setPath("/");
                    cookie.setMaxAge(7 * 24 * 60 * 60); // Set cookie expiry to 7 days
                    response.addCookie(cookie);

                    Map<String, Object> responseBody = new HashMap<>();
                    responseBody.put("success", true);
                    Map<String, Object> userDetails = new HashMap<>();
                    userDetails.put("email", newUser.getEmail());
                    userDetails.put("is_admin", newUser.isAdmin());
                    responseBody.put("user", userDetails);
                    return ResponseEntity.ok(responseBody);
                }
            } else {
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("success", false);
                responseBody.put("message", "Google login failed");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
            }
        } catch (Exception e) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("success", false);
            responseBody.put("message", "An error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        // Clear the JWT token cookie
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // Use secure cookies in production
        cookie.setPath("/");
        cookie.setMaxAge(0); // Expire the cookie immediately

        response.addCookie(cookie);

        return ResponseEntity.ok("Logout successful");
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
    @GetMapping({"user/{uId}"})
    public User viewUserDetails(@PathVariable("uId") Integer uId) {
        return userService.getUser(uId);
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
