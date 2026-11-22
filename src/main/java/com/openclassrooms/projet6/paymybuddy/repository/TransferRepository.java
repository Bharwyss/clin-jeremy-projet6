package com.openclassrooms.projet6.paymybuddy.repository;


import com.openclassrooms.projet6.paymybuddy.entity.Transfer;
import com.openclassrooms.projet6.paymybuddy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Integer> {
    @Query("SELECT t FROM Transfer t " +
            "LEFT JOIN FETCH t.sender " +
            "LEFT JOIN FETCH t.receiver " +
            "WHERE t.sender = :user OR t.receiver = :user " +
            "ORDER BY t.createdAt DESC")
    List<Transfer> findHistoryByUser(@Param("user") User user);
}
