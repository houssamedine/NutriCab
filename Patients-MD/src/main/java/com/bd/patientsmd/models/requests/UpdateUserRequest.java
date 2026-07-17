package com.bd.patientsmd.models.requests;

import com.bd.patientsmd.models.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        @NotBlank(message = "Le nom complet est obligatoire")
        String fullName,

        @NotBlank(message = "L'email est obligatoire")
        @Email(message = "L'email est invalide")
        String email,

        @NotNull(message = "Le role est obligatoire")
        UserRole role
) {
}
