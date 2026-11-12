package com.openclassrooms.projet6.paymybuddy.service;

import com.openclassrooms.projet6.paymybuddy.entity.Transfer;
import com.openclassrooms.projet6.paymybuddy.entity.User;
import com.openclassrooms.projet6.paymybuddy.exception.*;
import com.openclassrooms.projet6.paymybuddy.repository.TransferRepository;
import com.openclassrooms.projet6.paymybuddy.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransferService {

    private static final Logger logger = LoggerFactory.getLogger(TransferService.class);

    private final TransferRepository transferRepository;
    private final UserRepository userRepository;

    public TransferService(TransferRepository transferRepository,
                           UserRepository userRepository) {
        this.transferRepository = transferRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void sendMoneyFromSenderToReceiver(Integer senderId, Integer receiverId, BigDecimal amount, String description) {

        if (senderId.equals(receiverId)) {
            throw new SelfTransferException("Sender and receiver are the same");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be positive");
        }

        User sender = userRepository.findById(senderId).orElseThrow(() -> new UserNotFoundException("Sender not found: " + senderId));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new UserNotFoundException("Receiver not found: " + receiverId));

        if (!sender.getConnections().contains(receiver)) {
            throw new NotConnectedException("Sender must have connected to receiver");
        }

        if (sender.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Balance must be superior to amount" );
        }

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        userRepository.save(sender);
        userRepository.save(receiver);

        Transfer transfer = new Transfer();
        transfer.setSender(sender);
        transfer.setReceiver(receiver);
        transfer.setDescription(description);
        transfer.setCreatedAt(LocalDateTime.now());
        transferRepository.save(transfer);
    }
}
