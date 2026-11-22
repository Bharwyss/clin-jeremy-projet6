package com.openclassrooms.projet6.paymybuddy.service;

import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.*;
import com.openclassrooms.projet6.paymybuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Inscrit un nouvel utilisateur avec un solde initial nul.
     * Le mot de passe est haché avant persistance, jamais stocké en clair.
     */
    @Transactional
    public void register(String username, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email already used: " + email);
        }

        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyUsedException("Username already used: " + username);
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setBalance(BigDecimal.ZERO);
        user.setConnections(new ArrayList<>());

        userRepository.save(user);
        logger.info("New user registered: {}", email);
    }

    /**
     * Ajoute une relation unilatérale : currentUser ajoute le titulaire de connectionEmail.
     * La réciproque n'est pas créée automatiquement.
     */
    @Transactional
    public void addConnection(Integer currentUserId, String connectionEmail) {

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUserId));

        User connection = userRepository.findByEmail(connectionEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + connectionEmail));

        if (currentUser.getId().equals(connection.getId())) {
            throw new SelfConnectionException("Cannot add yourself as a connection");
        }

        if (currentUser.getConnections().contains(connection)) {
            throw new ConnectionAlreadyExistsException("Connection already exists: " + connectionEmail);
        }

        currentUser.getConnections().add(connection);
        userRepository.save(currentUser);
        logger.info("User {} added connection {}", currentUserId, connectionEmail);
    }

    /**
     * Charge un utilisateur par son email, pour l'affichage du profil
     * et pour l'authentification.
     */
    @Transactional(readOnly = true)
    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + email));
    }

    @Transactional(readOnly = true)
    public List<User> getConnections(Integer userId) {
        User user = userRepository.findByIdWithConnections(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return user.getConnections();
    }

    @Transactional
    public User updateProfile(Integer userId, String username, String email, String password) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new EmailAlreadyUsedException("Email already used: " + email);
        }

        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyUsedException("Username already used: " + username);
        }

        if (password !=null && !password.isBlank()) {
             user.setPassword(passwordEncoder.encode(password));
        }

        user.setUsername(username);
        user.setEmail(email);

        return userRepository.save(user);
    }
}