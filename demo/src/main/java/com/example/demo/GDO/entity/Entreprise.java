package com.example.demo.GDO.entity;

import com.example.demo.common.enums.StatutEntreprise;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "entreprises")
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nom;

    private String logo;
    private String adresse;
    private String ville;
    private String pays;

    @Column(unique = true)
    private String numeroContribuable;

    private String registreCommerce;
    private String telephone;
    private String email;
    private String siteWeb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutEntreprise statut = StatutEntreprise.ACTIVE;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL)
    private List<Departement> departements;

    @CreationTimestamp
    private LocalDateTime dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateModification;
}