package com.openclassrooms.projet6.paymybuddy.service;

import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.EmailAlreadyUsedException;
import com.openclassrooms.projet6.paymybuddy.exception.UsernameAlreadyUsedException;
import com.openclassrooms.projet6.paymybuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void Register(String username, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email already exists");
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyUsedException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setBalance(BigDecimal.ZERO);
        userRepository.save(user);
    }
}
