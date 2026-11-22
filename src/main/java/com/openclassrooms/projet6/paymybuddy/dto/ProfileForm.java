package com.openclassrooms.projet6.paymybuddy.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProfileForm(
        @NotBlank @Size(max = 30) String username,
        @NotBlank @Email @Size(max = 255) String email,
        @Pattern(regexp = "^$|.{8,}", message = "8 caractères minimum")
        String password
) {}