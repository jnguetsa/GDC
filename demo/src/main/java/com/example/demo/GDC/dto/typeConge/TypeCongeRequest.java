package com.example.demo.GDC.dto.typeConge;

import com.example.demo.common.enums.PolitiqueFinAnnee;
import com.example.demo.common.enums.UniteTemps;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class TypeCongeRequest {

    private Long catalogueId;
    @NotBlank(message = "Le nom est requis")
    private String nom;

    private String description;
    private String couleur;

    private Integer joursParDefaut;
    private boolean sansSolde;

    @NotNull(message = "La politique de fin d'année est requise")
    private PolitiqueFinAnnee politiqueFinAnnee;

    // Requis si politiqueFinAnnee = REPORTER
    @Min(value = 0, message = "Le nombre maximum de jours reportables ne peut pas être négatif")
    private Integer maxJoursReport;

    // Requis si politiqueFinAnnee = PAYER
    @DecimalMin(value = "0.01", message = "Le taux d'indemnisation doit être supérieur à 0")
    @DecimalMax(value = "1.0",  message = "Le taux d'indemnisation ne peut pas dépasser 1.0 (100%)")
    private BigDecimal tauxIndemnisation;

    private boolean necessiteJustificatif;
    private boolean necessiteValidationRh;
    private boolean necessiteAdresseConge;

    @Min(value = 0, message = "Le délai minimum ne peut pas être négatif")
    private Integer delaiMinimumJours;
    private UniteTemps uniteDelai;

    // Contrainte d'âge de l'enfant (CP, CM2, congé parental...)
    @Min(value = 0, message = "L'âge minimum de l'enfant ne peut pas être négatif")
    private Integer ageMinimumEnfant;

    @Min(value = 0, message = "L'âge maximum de l'enfant ne peut pas être négatif")
    private Integer ageMaximumEnfant;
    private UniteTemps uniteAge;

    @NotNull(message = "L'entreprise est requise")
    private Long entrepriseId;
}