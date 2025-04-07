package com.tehacko.backend_java.service;

import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

@Service
public class UserService {


    @Autowired
    private UserRepo userRepo;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Autowired
    private GoogleTokenValidatorService googleTokenValidatorService;

    public User saveUser(User user){
        try {
            user.setPassword(encoder.encode(user.getPassword()));
            return userRepo.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Nepodařilo se uživatele uložit do databáze: " + e.getMessage());
        }
    }

    public User findByEmail(String email) {
        try {
            return userRepo.findByEmail(email);
        } catch (Exception e) {
            throw new RuntimeException("Stala se chyba při vyhledávání uživatele v databázi. Zkuste to znovu.");
        }
    }

    public User validateGoogleToken(String googleToken) throws GeneralSecurityException, IOException {
        return googleTokenValidatorService.validateGoogleToken(googleToken);
    }

    public void addUser(User user){
        userRepo.save(user);
    }

    public List<User> getAllUsers() {
        try {
            return userRepo.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Nepodařilo se načíst všechny uživatele. Zkuste to znovu.");
        }
    }

    public User getUser(int uId) {
        return userRepo.findById(uId).orElse(new User());
    }

    public void updateUser(User user) {
        userRepo.save(user);
    }

    public void deleteUser(int uId) {
        try {
            userRepo.deleteById(uId);
        } catch (Exception e) {
            throw new RuntimeException("Nepodařilo se uživatele smazat. Zkuste to znovu.");
        }
    }

    public boolean emailExists(String email) {
        try {
            return userRepo.findByEmail(email) != null;
        } catch (Exception e) {
            throw new RuntimeException("Nepodařilo se ověřit, jestli uživatel s daným emailem existuje. Zkuste to znovu.");
        }
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
