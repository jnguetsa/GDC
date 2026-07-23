package com.example.demo.GDC.entity;

import com.example.demo.GDU.entity.Employe;
import com.example.demo.common.enums.UniteSolde;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "soldes_conge",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"employe_id", "type_conge_id", "annee"}
        ))
public class SoldeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employe_id", nullable = false)
    private Employe employe;

    @ManyToOne
    @JoinColumn(name = "type_conge_id", nullable = false)
    private TypeConge typeConge;

    @Column(nullable = false)
    private Integer annee;

    @Enumerated(EnumType.STRING)
    private UniteSolde unite;

    private Double soldeInitial;
    private Double soldeReporte;
    private Double soldePris;
    private Double soldeEnAttente;
    private Double soldeRestant;

    private Double soldeAjuste;
    private String motifAjustement;

    private LocalDate dateDebutPlage;
    private LocalDate dateFinPlage;
    private LocalDate dateExpiration;
    private boolean estExpire = false;

    // Lien vers l'exercice clôturé correspondant (null si année en cours)
    @OneToOne(mappedBy = "soldeConge")
    private ExerciceConge exerciceConge;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;

    @PrePersist
    @PreUpdate
    public void calculerSoldeRestant() {
        double ajuste = soldeAjuste != null ? soldeAjuste : 0;
        this.soldeRestant = (soldeInitial + soldeReporte + ajuste)
                - soldePris - soldeEnAttente;
    }
}