package com.openclassrooms.projet6.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConnectionForm(
        @NotBlank(message = "L'adresse mail est obligatoire")
        @Email(message = "Adresse mail invalide")
        @Size(max = 255)
        String email
) {}