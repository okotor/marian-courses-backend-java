//package com.tehacko.backend_java.controller;
//
//import com.tehacko.backend_java.dto.UserRequest;
//import com.tehacko.backend_java.dto.UserResponse;
//import com.tehacko.backend_java.dto.SuccessResponse;
//import com.tehacko.backend_java.dto.ErrorResponse;
//import com.tehacko.backend_java.model.User;
//import com.tehacko.backend_java.service.JwtService;
//import com.tehacko.backend_java.service.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api")
//public class UserController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@Valid @RequestBody UserRequest userRequest){
//        try {
//            // Check if email already exists
//            if (userService.emailExists(userRequest.getEmail())) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//                        new ErrorResponse("Email already exists. Try logging in.")
//                );
//            }
//
//            // Save the user
//            User savedUser = userService.saveUser(userRequest);
//
//            // Generate a success response
//            SuccessResponse successResponse = new SuccessResponse(
//                    "You have been successfully registered!",
//                    new UserResponse(savedUser.getUId(), savedUser.getEmail())
//            );
//
//            return ResponseEntity.status(HttpStatus.CREATED).body(successResponse);
//
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    new ErrorResponse("Internal server error.")
//            );
//        }
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Valid @RequestBody UserRequest userRequest){
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(userRequest.getEmail(), userRequest.getPassword())
//            );
//
//            if(authentication.isAuthenticated()) {
//                String token = jwtService.generateToken(userRequest.getEmail());
//                return ResponseEntity.ok(new SuccessResponse("Login successful", token));
//            } else {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
//                        new ErrorResponse("Invalid credentials")
//                );
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
//                    new ErrorResponse("Login failed")
//            );
//        }
//    }
//
//    @GetMapping({"/", "/home"})
//    public String home() {
//        return "home";
//    }
//
//    @GetMapping("/adduser")
//    public String addUser(){
//        return "adduser";
//    }
//
//    @PostMapping("/handleForm")
//    public String handleForm(User user){
//        userService.addUser(user);
//        return "success";
//    }
//
//    @GetMapping("/viewallusers")
//    public String viewUsers(Model m) {
//        List<User> users = userService.getAllUsers();
//        m.addAttribute("users", users);
//        return "viewallusers";
//    }
//
//    // User Details View, Edit, Delete
//    // View
//    @GetMapping("/user/{uId}")
//    public ResponseEntity<User> viewUserDetails(@PathVariable("uId") Integer uId) {
//        User user = userService.getUser(uId);
//        if (user != null) {
//            return ResponseEntity.ok(user);
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//    }
//
//    // Edit
//    @PutMapping("/user")
//    public ResponseEntity<User> updateUser(@Valid @RequestBody UserRequest userRequest){
//        User updatedUser = userService.updateUser(userRequest);
//        return ResponseEntity.ok(updatedUser);
//    }
//
//    // Delete
//    @DeleteMapping("/user/{uId}")
//    public ResponseEntity<?> deleteUser(@PathVariable int uId) {
//        userService.deleteUser(uId);
//        return ResponseEntity.ok(new SuccessResponse("User account has been deleted.", null));
//    }
//
//    // Search by Keyword
//    @GetMapping("/users/keyword/{keyword}")
//    public ResponseEntity<List<User>> searchByKeyword(@PathVariable("keyword") String keyword){
//        List<User> users = userService.search(keyword);
//        return ResponseEntity.ok(users);
//    }
//}