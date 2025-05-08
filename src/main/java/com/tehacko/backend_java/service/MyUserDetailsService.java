package com.tehacko.backend_java.service;

import com.tehacko.backend_java.model.User;
import com.tehacko.backend_java.model.UserPrincipal;
import com.tehacko.backend_java.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepo.findByEmail(email);
        if(user == null){
            System.out.println("Uživatel nenalezen.");
            throw new UsernameNotFoundException("Uživatel nenalezen.");
        }

        return new UserPrincipal(user);
    }
}
