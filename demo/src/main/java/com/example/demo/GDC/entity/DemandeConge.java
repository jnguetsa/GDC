package com.example.demo.GDC.entity;

import com.example.demo.GDU.entity.Employe;
import com.example.demo.common.enums.StatutDemande;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "demandes_conge")
public class DemandeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "type_conge_id", nullable = false)
    private TypeConge typeConge;

    // Exercice sur lequel la demande impacte le solde
    @ManyToOne
    @JoinColumn(name = "exercice_conge_id")
    private ExerciceConge exerciceConge;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    private Integer nombreJours;
    private boolean demiJourneeDebut;
    private boolean demiJourneeFin;

    @Column(columnDefinition = "TEXT")
    private String motif;

    private String adresseConge;
    private String contactUrgence;

    @Enumerated(EnumType.STRING)
    private StatutDemande statut;

    // Pièces jointes (justificatifs)
    @OneToMany(mappedBy = "demandeConge", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PieceJointe> piecesJointes;

    // Retour lié à cette demande
    @OneToOne(mappedBy = "demandeConge", cascade = CascadeType.ALL)
    private RetourConge retourConge;

    @ManyToOne
    @JoinColumn(name = "valide_par_manager_id")
    private Employe valideParManager;

    private String commentaireManager;
    private LocalDateTime dateValidationManager;

    @ManyToOne
    @JoinColumn(name = "valide_par_rh_id")
    private Employe valideParRh;

    private String commentaireRh;
    private LocalDateTime dateValidationRh;

    private String motifAnnulation;
    private LocalDateTime dateAnnulation;

    @ManyToOne
    @JoinColumn(name = "annule_par_id")
    private Employe annulePar;

    private String adresseIp;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @PrePersist
    public void validerDates() {
        if (dateDebut != null && dateFin != null && dateDebut.isAfter(dateFin)) {
            throw new IllegalArgumentException("La date de début doit être avant la date de fin");
        }
    }
}