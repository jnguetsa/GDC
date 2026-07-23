package com.example.demo.GDC.dto.reglePlageConge;

import com.example.demo.GDO.dto.departement.DepartementResponse;
import com.example.demo.common.enums.CategorieEmploye;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ReglePlageCongeResponse {

    private Long id;
    private String nom;
    private String description;

    // Entreprise
    private Long entrepriseId;
    private String entrepriseNom;

    // Départements concernés (vide = toute l'entreprise)
    private Set<DepartementResponse> departements;

    // Catégories concernées (vide = toutes)
    private Set<CategorieEmploye> categories;

    // Type de congé concerné
    private Long typeCongeId;
    private String typeCongeNom;

    // Plage autorisée
    private Integer moisDebutAutorise;
    private Integer moisFinAutorise;

    // Date limite de prise
    private Integer moisExpiration;
    private Integer jourExpiration;

    private boolean actif;
    private LocalDateTime dateCreation;
}
