package com.example.demo.GDC.dto.demandeConge;

import com.example.demo.GDC.dto.pieceJointe.PieceJointeResponse;
import com.example.demo.GDC.dto.typeConge.TypeCongeResponse;
import com.example.demo.GDU.dto.employe.EmployeInfo;
import com.example.demo.common.enums.StatutDemande;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class DemandeCongeResponse {

    private Long id;
    private EmployeInfo employe;
    private TypeCongeResponse typeConge;

    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Integer nombreJours;
    private boolean demiJourneeDebut;
    private boolean demiJourneeFin;
    private String motif;
    private String adresseConge;
    private String contactUrgence;
    private StatutDemande statut;

    private List<PieceJointeResponse> piecesJointes;

    // Validation manager
    private EmployeInfo manager;
    private String commentaireManager;
    private LocalDateTime dateValidationManager;

    // Validation RH
    private EmployeInfo rh;
    private String commentaireRh;
    private LocalDateTime dateValidationRh;

    // Annulation
    private String motifAnnulation;
    private LocalDateTime dateAnnulation;
    private EmployeInfo annulePar;

    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
}