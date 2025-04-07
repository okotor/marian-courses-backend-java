//package com.tehacko.backend_java.service;
//
//import com.tehacko.backend_java.dto.UserRequest;
//import com.tehacko.backend_java.model.User;
//import com.tehacko.backend_java.repo.UserRepo;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class UserService {
//
//    @Autowired
//    private UserRepo userRepo;
//    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
//
//    public User saveUser(UserRequest userRequest){
//        User user = new User();
//        user.setEmail(userRequest.getEmail());
//        user.setPassword(encoder.encode(userRequest.getPassword()));
//        return userRepo.save(user);
//    }
//
//    public void addUser(User user){
//        userRepo.save(user);
//    }
//
//    public List<User> getAllUsers() {
//        return userRepo.findAll();
//    }
//
//    public User getUser(int uId) {
//        return userRepo.findById(uId).orElse(null);
//    }
//
//    public User updateUser(UserRequest userRequest) {
//        User user = userRepo.findByEmail(userRequest.getEmail());
//        if (user != null) {
//            user.setPassword(encoder.encode(userRequest.getPassword()));
//            return userRepo.save(user);
//        } else {
//            return null;
//        }
//    }
//
//    public void deleteUser(int uId) {
//        userRepo.deleteById(uId);
//    }
//
//    public List<User> search(String keyword) {
//        return userRepo.findByEmailContainingOrPassword(keyword, keyword);
//    }
//
//    public boolean emailExists(String email) {
//        return userRepo.findByEmail(email) != null;
//    }
//}