package com.openclassrooms.projet6.paymybuddy.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record TransferForm(
        @NotNull(message = "Sélectionnez une relation")
        Integer receiverId,

        @Size(max = 200)
        String description,

        @NotNull(message = "Le montant est obligatoire")
        @DecimalMin(value = "0.01", message = "Le montant doit être positif")
        BigDecimal amount
) {}