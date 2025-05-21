package com.tehacko.backend_java.repo;

import com.tehacko.backend_java.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
//import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;
import java.util.Optional;

@Repository
//@CrossOrigin(origins = "http://localhost:3000") // Allow React
public interface UserRepo extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    List<User> findByEmailContainingOrPassword(String email, String password);

    User findByRefreshToken(String refreshToken);

    Optional<User> findByResetToken(String resetToken);

//    public List<User> getAllUsers(){
//        return users;
//    }
//
//    public void addUser(User user) {
//        users.add(user);
//        System.out.println(users);
//    }
//    List<User> findByName(String name);
//    List<User> findById(int id);

}
