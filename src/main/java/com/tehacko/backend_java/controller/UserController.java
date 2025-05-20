package com.tehacko.backend_java.controller;

import com.tehacko.backend_java.model.User;

import com.tehacko.backend_java.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
//SENDING TEST EMAILS
//import org.springframework.beans.factory.annotation.Autowired;
//import com.tehacko.backend_java.service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@RestController
//@CrossOrigin(origins = {"http://localhost:3000", "https://marian-courses-next-js-frontend.vercel.app"}) // Allow React
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //SENDING TEST EMAILS
//    @Autowired
//    private EmailService emailService;

    @GetMapping({"/", "home"})
    public String home() {
        return "home";
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user){
        User newUser = userService.userRegister(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestBody Map<String, String> payload) {
        String token = payload.get("token");
        userService.confirmEmail(token);
        return ResponseEntity.ok("Email byl úspěšně ověřen.");
    }

    @PostMapping("/resend-confirmation")
    public ResponseEntity<?> resendConfirmation(@RequestBody Map<String, String> payload) {
        userService.resendConfirmationEmail(payload.get("email"));
        return ResponseEntity.ok(Map.of("message", "Potvrzovací email byl odeslán."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        Map<String, Object> loginResponse = userService.userLogin(user, response);
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> request, HttpServletResponse response) throws GeneralSecurityException, IOException {
        Map<String, Object> googleLoginResponse = userService.userGoogleLogin(request.get("token"), response);
        return ResponseEntity.ok(googleLoginResponse);
    }

    @GetMapping("/auth/check")
    public ResponseEntity<?> checkAuth(@CookieValue(value = "jwtToken", required = false) String token) {
        return ResponseEntity.ok(userService.userCheckAuth(token));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refresh(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                     HttpServletResponse response) {
        return ResponseEntity.ok(userService.userRefreshToken(refreshToken, response));
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "refreshToken", required = false) String refreshToken,
                                    HttpServletResponse response) {
        return ResponseEntity.ok(userService.userLogout(refreshToken, response));
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
        User user = userService.userFindById(uId);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    //Edit
    @PutMapping("user")
    public User updateUser(@RequestBody User user){
        userService.userUpdate(user);
        return userService.userFindById(user.getUId());
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

    //SENDING TEST EMAILS
//    @GetMapping("/test-email")
//    public ResponseEntity<String> testEmail() {
//        emailService.sendEmail(
//                "cornelius.lundi@gmail.com", // Replace with your address
//                "Test Email from Marian Courses App",
//                "This is a test email to confirm that Gmail SMTP is working correctly."
//        );
//        return ResponseEntity.ok("Test email sent.");
//    }

    //LOADING DUMMY DATA
//    @GetMapping("load")
//    public String loadData(){
//        userService.load();
//        return "Data byla úspěšně načtena.";
//    }

}
