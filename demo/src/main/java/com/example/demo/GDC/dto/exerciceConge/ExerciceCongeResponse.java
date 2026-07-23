package com.example.demo.GDC.dto.exerciceConge;

import com.example.demo.GDC.dto.demandeConge.DemandeCongeResponse;
import com.example.demo.GDC.dto.soldeConge.SoldeCongeResponse;
import com.example.demo.GDU.dto.employe.EmployeInfo;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ExerciceCongeResponse {

    private Long id;
    private EmployeInfo employe;
    private Long typeCongeId;
    private String typeCongeNom;
    private Long entrepriseId;
    private String entrepriseNom;
    private Integer annee;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Soldes en ouverture d'exercice
    private Double soldeOuverture;
    private Double soldeReporteEntrant;

    // Mouvements pendant l'exercice
    private Double joursPris;
    private Double joursEnAttente;
    private Double joursAjustes;
    private String motifAjustement;

    // Soldes en fin d'exercice
    private Double soldeFinExercice;
    private Double joursReportes;
    private Double joursExpires;

    // Solde courant lié (snapshot temps réel)
    private SoldeCongeResponse soldeConge;

    // Toutes les demandes de congé imputées sur cet exercice
    private List<DemandeCongeResponse> demandes;

    // Clôture
    private boolean cloture;
    private LocalDateTime dateCloture;
    private EmployeInfo cloturePar;

    private LocalDateTime dateCreation;
}