package com.example.demo.GDC.entity;

import com.example.demo.GDO.entity.Departement;
import com.example.demo.GDO.entity.Entreprise;
import com.example.demo.common.enums.CategorieEmploye;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "regles_plage_conge")
public class ReglePlageConge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "entreprise_id", nullable = false)
    private Entreprise entreprise;

    @Column(nullable = false)
    private String nom;

    private String description;

    @ElementCollection
    @CollectionTable(name = "regle_plage_categories",
            joinColumns = @JoinColumn(name = "regle_id"))
    @Column(name = "categorie")
    @Enumerated(EnumType.STRING)
    private Set<CategorieEmploye> categories = new HashSet<>();

    // Si vide → règle s'applique à tous les départements de l'entreprise
    @ManyToMany
    @JoinTable(name = "regle_plage_departements",
            joinColumns = @JoinColumn(name = "regle_id"),
            inverseJoinColumns = @JoinColumn(name = "departement_id"))
    private Set<Departement> departements = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "type_conge_id")
    private TypeConge typeConge;

    @Column(nullable = false)
    private Integer moisDebutAutorise;

    @Column(nullable = false)
    private Integer moisFinAutorise;

    @Column(nullable = false)
    private Integer moisExpiration;

    @Column(nullable = false)
    private Integer jourExpiration;

    private boolean actif = true;

    @CreationTimestamp
    private LocalDateTime dateCreation;
}
