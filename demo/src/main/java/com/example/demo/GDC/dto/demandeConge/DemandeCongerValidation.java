package com.example.demo.GDC.dto.demandeConge;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandeCongerValidation {

    @NotNull(message = "La décision est requise")
    private Boolean approuvee;
    private String commentaire;
}

