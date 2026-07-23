package com.example.demo.GDC.entity;

import com.example.demo.GDO.entity.Entreprise;
import com.example.demo.GDU.entity.Employe;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "exercices_conge",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employe_id", "type_conge_id", "annee"}
        ))
public class ExerciceConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "type_conge_id", nullable = false)
    private TypeConge typeConge;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Lien vers le SoldeConge source — traçabilité des chiffres
    @OneToOne
    @JoinColumn(name = "solde_conge_id")
    private SoldeConge soldeConge;

    @Column(nullable = false)
    private Integer annee;

    private LocalDate dateDebut;
    private LocalDate dateFin;

    // Soldes en début d'exercice
    private Double soldeOuverture;
    private Double soldeReporteEntrant;

    // Mouvements pendant l'exercice
    private Double joursPris;
    private Double joursEnAttente;
    private Double joursAjustes;
    private String motifAjustement;

    // Soldes en fin d'exercice (calculés à la clôture)
    private Double soldeFinExercice;
    private Double joursReportes;
    private Double joursExpires;

    // Statut de l'exercice
    private boolean cloture;
    private LocalDateTime dateCloture;

    @ManyToOne
    @JoinColumn(name = "cloture_par_id")
    private Employe cloturePar;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @PrePersist
    @PreUpdate
    public void calculerSoldeFinExercice() {
        double ouverture = soldeOuverture != null ? soldeOuverture : 0;
        double reporte = soldeReporteEntrant != null ? soldeReporteEntrant : 0;
        double pris = joursPris != null ? joursPris : 0;
        double attente = joursEnAttente != null ? joursEnAttente : 0;
        double ajuste = joursAjustes != null ? joursAjustes : 0;
        this.soldeFinExercice = (ouverture + reporte + ajuste) - pris - attente;
    }
}
