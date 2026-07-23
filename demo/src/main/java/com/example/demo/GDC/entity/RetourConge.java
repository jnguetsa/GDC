package com.example.demo.GDC.entity;

import com.example.demo.GDU.entity.Employe;
import com.example.demo.common.enums.StatutRetour;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "retours_conge")
public class RetourConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "demande_conge_id", nullable = false, unique = true)
    private DemandeConge demandeConge;

    // Date à laquelle l'employé aurait dû revenir (dateFin + 1)
    @Column(nullable = false)
    private LocalDate dateRetourPrevue;

    // Date à laquelle l'employé est effectivement revenu (saisie par RH/manager)
    private LocalDate dateRetourEffective;

    // > 0 : retard | = 0 : à l'heure | < 0 : retour anticipé
    private Integer joursEcart;

    // Jours non consommés récupérables en cas de retour anticipé
    private Integer joursNonPrisRecuperables;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRetour statut = StatutRetour.EN_ATTENTE;

    // Motif saisi par l'employé en cas de retard ou retour anticipé
    private String motifEcart;

    // Pièces jointes (justificatifs de retard ou retour anticipé)
    @OneToMany(mappedBy = "retourConge", cascade = CascadeType.ALL)
    private List<PieceJointe> piecesJointes;

    // Décision RH sur les jours non pris (retour anticipé)
    private Boolean joursRecuperesSurSolde;
    private String commentaireDecisionRh;

    private String commentaire;

    @ManyToOne
    @JoinColumn(name = "enregistre_par_id")
    private Employe enregistrePar;

    private LocalDateTime dateEnregistrement;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @PrePersist
    @PreUpdate
    public void calculerEcartEtStatut() {
        if (dateRetourEffective == null || dateRetourPrevue == null) return;

        long ecart = dateRetourPrevue.until(dateRetourEffective, ChronoUnit.DAYS);
        this.joursEcart = (int) ecart;

        if (ecart == 0) {
            this.statut = StatutRetour.A_L_HEURE;
            this.joursNonPrisRecuperables = 0;
        } else if (ecart > 0) {
            this.joursNonPrisRecuperables = 0;
            if (motifEcart != null && !motifEcart.isBlank()) {
                this.statut = StatutRetour.EN_RETARD;
            } else {
                this.statut = StatutRetour.ABSENT_INJUSTIFIE;
            }
        } else {
            this.statut = StatutRetour.RETOUR_ANTICIPE;
            this.joursNonPrisRecuperables = (int) Math.abs(ecart);
        }
    }
}