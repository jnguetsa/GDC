package com.example.demo.GDC.dto.soldeConge;

import com.example.demo.GDU.dto.employe.EmployeInfo;
import com.example.demo.common.enums.UniteSolde;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class SoldeCongeResponse {

    private Long id;
    private EmployeInfo employe;
    private Long typeCongeId;
    private String typeCongeNom;
    private Integer annee;
    private UniteSolde unite;

    // Soldes
    private Double soldeInitial;
    private Double soldeReporte;
    private Double soldePris;
    private Double soldeEnAttente;
    private Double soldeRestant;
    private Double soldeAjuste;
    private String motifAjustement;

    // Plage de validité
    private LocalDate dateDebutPlage;
    private LocalDate dateFinPlage;
    private LocalDate dateExpiration;
    private boolean estExpire;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}