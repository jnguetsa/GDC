package com.example.demo.GDC.dto.typeConge;

import com.example.demo.common.enums.CodeTypeConge;
import com.example.demo.common.enums.PolitiqueFinAnnee;
import com.example.demo.common.enums.UniteTemps;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class TypeCongeResponse {

    private Long id;

    // Origine catalogue
    private Long catalogueId;
    private String nomCatalogue;       // catalogue.nom — pour affichage dans le formulaire RH
    private CodeTypeConge code;        // dérivé via getCode()

    // Champs personnalisables par l'entreprise
    private String nom;
    private String description;
    private String couleur;

    private Integer joursParDefaut;
    private boolean sansSolde;

    private PolitiqueFinAnnee politiqueFinAnnee;
    private Integer maxJoursReport;        // pertinent si REPORTER
    private BigDecimal tauxIndemnisation;  // pertinent si PAYER

    private boolean necessiteJustificatif;
    private boolean necessiteValidationRh;
    private boolean necessiteAdresseConge;

    private Integer delaiMinimumJours;
    private UniteTemps uniteDelai;

    private Integer ageMinimumEnfant;
    private Integer ageMaximumEnfant;
    private UniteTemps uniteAge;
    private String nomEntreprise;
    private boolean actif;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}