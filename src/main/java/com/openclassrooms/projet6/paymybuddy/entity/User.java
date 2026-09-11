package com.openclassrooms.projet6.paymybuddy.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 60)
    private String password;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    @ManyToMany
    @JoinTable(
            name = "user_connection",
            joinColumns = @JoinColumn(name = "adder_id"),
            inverseJoinColumns = @JoinColumn(name = "added_id")
    )
    private List<User> connections;
}
