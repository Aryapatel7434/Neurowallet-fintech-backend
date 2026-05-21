package com.smartwallet.security;
 //SPRING SECURITY AUTHENTICATION SYSTEM
//This class acts like as a transalator
import com.smartwallet.model.User;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
//This class provides user details to spring  security
public class CustomUserDetails implements UserDetails {
//User object
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

@Override
public Collection<? extends GrantedAuthority> getAuthorities() {

    return List.of(
            new SimpleGrantedAuthority(
                    "ROLE_" + user.getRole()
            )
    );
}

    @Override
    //Return store encypted password //uses during login verification
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    //Return username for authentication
    public String getUsername() {
        return user.getEmail();
    }

    public String getRole() {
        return user.getRole();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    //this class acts like identity card for spring security
    //it tells spring who is this user?,what is password?,what role?,Is account active?
}