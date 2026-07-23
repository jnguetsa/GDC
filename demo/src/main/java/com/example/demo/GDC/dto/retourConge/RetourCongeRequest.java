package com.example.demo.GDC.dto.retourConge;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class RetourCongeRequest {

    @NotNull(message = "La date de retour effective est requise")
    private LocalDate dateRetourEffective;

    private String motifEcart;
    private String commentaire;
}
