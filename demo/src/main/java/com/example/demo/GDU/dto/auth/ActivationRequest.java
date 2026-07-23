package com.example.demo.GDU.dto.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ActivationRequest {
    @NotNull(message = "User ID is required")
    private Long utilisateurId;

    @NotNull(message = "Active status is required")
    private Boolean compteActif;
}
