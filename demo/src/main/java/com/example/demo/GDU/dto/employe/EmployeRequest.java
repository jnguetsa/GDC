package com.example.demo.GDU.dto.employe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class EmployeRequest {

    @NotBlank(message = "Le nom est requis")
    private String nom;
    @NotBlank(message = "Le prénom est requis")
    private String prenom;
    @NotBlank(message = "L'email est requis")
    private String email;
    @NotBlank(message = "Le téléphone est requis")
    private String telephone;
    @NotBlank(message = "L'adresse est requise")
    private String adresse;
    @NotNull(message = "La date d'embauche est requise")
    private LocalDate dateEmbauche;
    @NotNull(message = "La date de début est requise")
    private LocalDate dateDebut;
    @NotNull(message = "La date de fin est requise")
    private LocalDate dateFin;

    private Long departementId;
}
