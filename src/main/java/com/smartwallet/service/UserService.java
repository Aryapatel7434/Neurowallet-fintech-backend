package com.smartwallet.service;

import java.util.List;
import com.smartwallet.dto.RegisterRequest;
import com.smartwallet.exception.ResourceNotFoundException;
import com.smartwallet.model.User;
import com.smartwallet.model.Wallet;
import com.smartwallet.repository.UserRepository;
import com.smartwallet.repository.WalletRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       WalletRepository walletRepository,
                       BCryptPasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(RegisterRequest request) {

        User existingUser = userRepository.findByEmail(request.getEmail());

        if (existingUser != null) {
            return "User already exists";
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getName(),
                request.getEmail(),
                encodedPassword,
                "USER"//Every new user become Normal user by default
        );

        User savedUser = userRepository.save(user);

        String walletAddress =
                "WALLET_" + savedUser.getName().toUpperCase() + "_" + savedUser.getUserId();

        Wallet wallet = new Wallet(
                savedUser.getUserId(),
                0.0,
                walletAddress
        );

        walletRepository.save(wallet);

        return "User registered successfully. Wallet Address: " + walletAddress;
    }

    public Wallet getWalletByEmail(String email) {

        User user = userRepository.findByEmail(email);

      if(user==null){
          throw new ResourceNotFoundException(
               "User not found with email: "+email
          );
      }

        return walletRepository.findByUserId(user.getUserId());
    }
    public List<User>getAllUsers(){
        return userRepository.findAll();
    }
}