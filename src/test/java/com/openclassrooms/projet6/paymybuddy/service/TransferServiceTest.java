package com.openclassrooms.projet6.paymybuddy.service;


import com.openclassrooms.projet6.paymybuddy.entity.Transfer;
import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.*;
import com.openclassrooms.projet6.paymybuddy.repository.TransferRepository;
import com.openclassrooms.projet6.paymybuddy.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransferRepository transferRepository;

    @InjectMocks
    private TransferService transferService;

    @Test
    public void shouldThrowWhenReceiverNotInConnections() {
        // Given
        User sender = new User();
        sender.setId(1);
        sender.setBalance(new BigDecimal("800.12"));
        sender.setConnections(new ArrayList<>());

        User receiver = new User();
        receiver.setId(2);

        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));

        // When / Then
        assertThrows(NotConnectedException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("300.00"), "test"));

        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenSenderNotFound() {
        // Given
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("300.00"), "test"));

        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenReceiverNotFound() {
        // Given
        User sender = new User();
        sender.setId(1);
        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.empty());

        // When / Then
        assertThrows(UserNotFoundException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("300.00"), "test"));

        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenSenderIsReceiver() {
        // When / Then
        assertThrows(SelfTransferException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 1, new BigDecimal("300.00"), "test"));

        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAmountIsNegative() {
        // When / Then
        assertThrows(InvalidAmountException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("-10.00"), "test"));

        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenAmountIsZero() {
        // When / Then
        assertThrows(InvalidAmountException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("0.00"), "test"));

        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldThrowWhenBalanceIsInsufficient() {
        // Given
        User sender = new User();
        sender.setId(1);
        sender.setBalance(new BigDecimal("20.00"));

        User receiver = new User();
        receiver.setId(2);
        receiver.setBalance(new BigDecimal("50.00"));
        sender.setConnections(new ArrayList<>(List.of(receiver)));

        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));

        // When / Then
        assertThrows(InsufficientBalanceException.class, () ->
                transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("300.00"), "test"));

        assertEquals(0, sender.getBalance().compareTo(new BigDecimal("20.00")));
        assertEquals(0, receiver.getBalance().compareTo(new BigDecimal("50.00")));
        verify(transferRepository, never()).save(any());
    }

    @Test
    public void shouldTransferMoneyWhenAllConditionsAreMet() {
        // Given
        User sender = new User();
        sender.setId(1);
        sender.setBalance(new BigDecimal("200.00"));

        User receiver = new User();
        receiver.setId(2);
        receiver.setBalance(new BigDecimal("50.00"));
        sender.setConnections(new ArrayList<>(List.of(receiver)));

        when(userRepository.findById(1)).thenReturn(Optional.of(sender));
        when(userRepository.findById(2)).thenReturn(Optional.of(receiver));

        // When
        transferService.sendMoneyFromSenderToReceiver(1, 2, new BigDecimal("100.00"), "test");

        // Then
        assertEquals(0, sender.getBalance().compareTo(new BigDecimal("100.00")));
        assertEquals(0, receiver.getBalance().compareTo(new BigDecimal("150.00")));

        verify(transferRepository).save(any(Transfer.class));
        verify(userRepository).save(sender);
        verify(userRepository).save(receiver);
    }
}