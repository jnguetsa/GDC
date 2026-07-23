package com.example.demo.GDC.entity;

import com.example.demo.GDO.entity.Entreprise;
import com.example.demo.common.enums.CodeTypeConge;
import com.example.demo.common.enums.PolitiqueFinAnnee;
import com.example.demo.common.enums.UniteTemps;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "types_conge", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"nom", "entreprise_id"})
})
public class TypeConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Sélectionné depuis le catalogue — nom et description pré-remplis automatiquement
    @ManyToOne
    @JoinColumn(name = "catalogue_id")
    private CatalogueTypeConge catalogue;

    @Column(nullable = false)
    private String nom;

    private String description;

    // Dérivé du catalogue — ne pas setter directement
    public CodeTypeConge getCode() {
        return catalogue != null ? catalogue.getCode() : CodeTypeConge.AUTRE;
    }

    private String couleur;

    private Integer joursParDefaut;
    private boolean sansSolde;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PolitiqueFinAnnee politiqueFinAnnee = PolitiqueFinAnnee.NOUVEAU_DEPART;

    // Utilisé uniquement si politiqueFinAnnee = REPORTER (ex: 5 → max 5 jours reportés)
    private Integer maxJoursReport;

    // Utilisé uniquement si politiqueFinAnnee = PAYER (ex: 1.0 = 100%, 0.5 = 50% du salaire journalier)
    private BigDecimal tauxIndemnisation;

    private boolean necessiteJustificatif;
    private boolean necessiteValidationRh;
    private boolean necessiteAdresseConge;

    // Délai de prévenance avant le début du congé
    private Integer delaiMinimumJours;
    @Enumerated(EnumType.STRING)
    private UniteTemps uniteDelai;

    // Contrainte d'âge de l'enfant (pour CP, CM2, congé parental...)
    private Integer ageMinimumEnfant;
    private Integer ageMaximumEnfant;
    @Enumerated(EnumType.STRING)
    private UniteTemps uniteAge;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    // Relation inverse vers les règles de plage
    @OneToMany(mappedBy = "typeConge")
    private List<ReglePlageConge> reglesPlage;

    private boolean actif;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private LocalDateTime dateModification;
}