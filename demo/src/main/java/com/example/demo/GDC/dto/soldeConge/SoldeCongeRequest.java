package com.example.demo.GDC.dto.soldeConge;

import com.example.demo.common.enums.UniteSolde;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SoldeCongeRequest {

    @NotNull(message = "L'employé est requis")
    private Long employeId;

    @NotNull(message = "Le type de congé est requis")
    private Long typeCongeId;

    @NotNull(message = "L'année est requise")
    private Integer annee;

    @NotNull(message = "L'unité est requise")
    private UniteSolde unite;

    @NotNull(message = "Le solde initial est requis")
    private Double soldeInitial;

    private Double soldeReporte;

    // Plage de validité
    private LocalDate dateDebutPlage;
    private LocalDate dateFinPlage;
    private LocalDate dateExpiration;
}