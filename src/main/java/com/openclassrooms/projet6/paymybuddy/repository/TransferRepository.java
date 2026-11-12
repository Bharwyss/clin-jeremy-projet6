package com.openclassrooms.projet6.paymybuddy.repository;


import com.openclassrooms.projet6.paymybuddy.entity.Transfer;
import com.openclassrooms.projet6.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {
    List<Transfer> findBySenderOrReceiverOrderByCreatedAtDesc(User sender, User receiver);
}
