package com.example.demo.GDC.dto.reglePlageConge;

import com.example.demo.common.enums.CategorieEmploye;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class ReglePlageCongeRequest {

    @NotNull(message = "L'entreprise est requise")
    private Long entrepriseId;

    @NotBlank(message = "Le nom est requis")
    private String nom;

    private String description;

    // Si vide → règle s'applique à tous les départements de l'entreprise
    private Set<Long> departementIds = new HashSet<>();

    private Set<CategorieEmploye> categories = new HashSet<>();
    private Long typeCongeId;
    @NotNull(message = "Le mois de début autorisé est requis")
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    private Integer moisDebutAutorise;

    @NotNull(message = "Le mois de fin autorisé est requis")
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    private Integer moisFinAutorise;

    @NotNull(message = "Le mois d'expiration est requis")
    @Min(value = 1, message = "Le mois doit être entre 1 et 12")
    @Max(value = 12, message = "Le mois doit être entre 1 et 12")
    private Integer moisExpiration;

    @NotNull(message = "Le jour d'expiration est requis")
    @Min(value = 1, message = "Le jour doit être entre 1 et 31")
    @Max(value = 31, message = "Le jour doit être entre 1 et 31")
    private Integer jourExpiration;
}
