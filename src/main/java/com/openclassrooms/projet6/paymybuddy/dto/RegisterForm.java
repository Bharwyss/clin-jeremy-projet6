package com.openclassrooms.projet6.paymybuddy.dto;

import jakarta.validation.constraints.*;

public record RegisterForm(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        @Size(max = 30, message = "30 caractères maximum")
        String username,

        @NotBlank(message = "L'adresse mail est obligatoire")
        @Email(message = "Adresse mail invalide")
        @Size(max = 255)
        String email,

        @NotBlank(message = "Le mot de passe est obligatoire")
        @Size(min = 8, message = "8 caractères minimum")
        String password
) {}