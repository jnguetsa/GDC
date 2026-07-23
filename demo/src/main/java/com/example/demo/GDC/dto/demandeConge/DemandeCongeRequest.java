package com.example.demo.GDC.dto.demandeConge;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DemandeCongeRequest {

    @NotNull(message = "Le type de congé est requis")
    private Long typeCongeId;
    @NotNull(message = "La date de début est requise")
    private LocalDate dateDebut;
    @NotNull(message = "La date de fin est requise")
    private LocalDate dateFin;
    private boolean demiJourneeDebut;
    private boolean demiJourneeFin;
    private String motif;
    private String adresseConge;
    private String contactUrgence;
}