package com.smartwallet.security;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private final Map<String,Integer>
            failedAttempts = new HashMap<>();

    private static final int MAX_ATTEMPTS = 5;

    public void loginFailed(
            String email){

        failedAttempts.put(
                email,
                failedAttempts.getOrDefault(
                        email,
                        0
                ) + 1
        );
    }

    public void loginSucceeded(
            String email){

        failedAttempts.remove(email);
    }

    public boolean isBlocked(
            String email){

        return failedAttempts
                .getOrDefault(email,0)
                >= MAX_ATTEMPTS;
    }
}