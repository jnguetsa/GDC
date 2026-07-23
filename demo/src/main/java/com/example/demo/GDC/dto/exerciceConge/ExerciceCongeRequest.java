package com.example.demo.GDC.dto.exerciceConge;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ExerciceCongeRequest {

    @NotNull(message = "L'employé est requis")
    private Long employeId;

    @NotNull(message = "Le type de congé est requis")
    private Long typeCongeId;

    @NotNull(message = "L'entreprise est requise")
    private Long entrepriseId;

    @NotNull(message = "L'année est requise")
    private Integer annee;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    @NotNull(message = "Le solde d'ouverture est requis")
    private Double soldeOuverture;

    private Double soldeReporteEntrant;
}