package com.example.demo.GDU.dto.employe;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeInfo {

    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String poste;
    private String photoProfil;
    private Long departementId;
    private String departementNom;
}