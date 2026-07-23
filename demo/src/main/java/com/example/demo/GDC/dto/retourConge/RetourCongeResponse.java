package com.example.demo.GDC.dto.retourConge;

import com.example.demo.GDC.dto.pieceJointe.PieceJointeResponse;
import com.example.demo.GDU.dto.employe.EmployeInfo;
import com.example.demo.common.enums.StatutRetour;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RetourCongeResponse {

    private Long id;

    // Contexte de la demande liée
    private Long demandeCongeId;
    private EmployeInfo employe;
    private String typeCongeNom;
    private LocalDate demandeCongedateDebut;
    private LocalDate demandeCongeDateFin;

    // Retour
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private Integer joursEcart;
    private Integer joursNonPrisRecuperables;
    private StatutRetour statut;
    private String motifEcart;
    private String commentaire;
    private List<PieceJointeResponse> piecesJointes;

    // Décision RH (retour anticipé)
    private Boolean joursRecuperesSurSolde;
    private String commentaireDecisionRh;

    private EmployeInfo enregistrePar;
    private LocalDateTime dateEnregistrement;
}
