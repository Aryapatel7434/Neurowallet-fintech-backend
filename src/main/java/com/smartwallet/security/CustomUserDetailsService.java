package com.smartwallet.security;

import com.smartwallet.model.User;
import com.smartwallet.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service//Tells spring create object automatically//object store it inside spring container
//THis class bridge between database and spring security
//it loads user data from database and give it to.
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    //Spring Security Always call this method during login
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(user);
        //Without this class spring security does not know how to fetch users.
    }
}