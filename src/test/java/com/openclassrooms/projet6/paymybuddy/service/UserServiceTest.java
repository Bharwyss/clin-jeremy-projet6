package com.openclassrooms.projet6.paymybuddy.service;

import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.*;
import com.openclassrooms.projet6.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;

    // ---------- register ----------

    @Test
    public void shouldThrowWhenEmailAlreadyUsed() {
        // Given
        when(userRepository.existsByEmail("jake@example.com")).thenReturn(true);

        // When / Then
        assertThrows(EmailAlreadyUsedException.class, () ->
                userService.register("jake", "jake@example.com", "password123"));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenUsernameAlreadyUsed() {
        // Given
        when(userRepository.existsByEmail("jake@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("jake")).thenReturn(true);

        // When / Then
        assertThrows(UsernameAlreadyUsedException.class, () ->
                userService.register("jake", "jake@example.com", "password123"));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldRegisterUserWithHashedPasswordAndZeroBalance() {
        // Given
        when(userRepository.existsByEmail("jake@example.com")).thenReturn(false);
        when(userRepository.existsByUsername("jake")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$hashedValue");

        // When
        userService.register("jake", "jake@example.com", "password123");

        // Then
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals("jake", saved.getUsername());
        assertEquals("jake@example.com", saved.getEmail());
        assertEquals("$2a$12$hashedValue", saved.getPassword());
        assertNotEquals("password123", saved.getPassword());
        assertEquals(0, saved.getBalance().compareTo(BigDecimal.ZERO));
    }

    // ---------- addConnection ----------

    @Test
    public void shouldThrowWhenCurrentUserNotFound() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () ->
                userService.addConnection(1, "poum@example.com"));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenConnectionEmailNotFound() {
        // Given
        User currentUser = new User();
        currentUser.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () ->
                userService.addConnection(1, "unknown@example.com"));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAddingSelfAsConnection() {
        // Given
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setEmail("jake@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("jake@example.com")).thenReturn(Optional.of(currentUser));

        // When / Then
        assertThrows(SelfConnectionException.class, () ->
                userService.addConnection(1, "jake@example.com"));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenConnectionAlreadyExists() {
        // Given
        User connection = new User();
        connection.setId(2);
        connection.setEmail("poum@example.com");

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setConnections(new ArrayList<>(List.of(connection)));

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("poum@example.com")).thenReturn(Optional.of(connection));

        // When / Then
        assertThrows(ConnectionAlreadyExistsException.class, () ->
                userService.addConnection(1, "poum@example.com"));

        verify(userRepository, never()).save(any());
    }

    @Test
    public void shouldAddConnectionWhenAllConditionsAreMet() {
        // Given
        User connection = new User();
        connection.setId(2);
        connection.setEmail("poum@example.com");

        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setConnections(new ArrayList<>());

        when(userRepository.findById(1)).thenReturn(Optional.of(currentUser));
        when(userRepository.findByEmail("poum@example.com")).thenReturn(Optional.of(connection));

        // When
        userService.addConnection(1, "poum@example.com");

        // Then
        assertEquals(1, currentUser.getConnections().size());
        assertTrue(currentUser.getConnections().contains(connection));
        assertTrue(connection.getConnections() == null || connection.getConnections().isEmpty());

        verify(userRepository).save(currentUser);
        verify(userRepository, never()).save(connection);
    }

    // ---------- getByEmail ----------

    @Test
    public void shouldThrowWhenEmailNotFound() {
        // Given
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () ->
                userService.getByEmail("unknown@example.com"));
    }

    @Test
    public void shouldReturnUserWhenEmailFound() {
        // Given
        User user = new User();
        user.setId(1);
        user.setEmail("jake@example.com");

        when(userRepository.findByEmail("jake@example.com")).thenReturn(Optional.of(user));

        // When
        User result = userService.getByEmail("jake@example.com");

        // Then
        assertEquals(1, result.getId());
        assertEquals("jake@example.com", result.getEmail());
    }
}