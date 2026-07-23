package com.example.demo.GDU.dto.employe;

import com.example.demo.GDU.dto.role.RoleResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
public class EmployeResponse {
    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String poste;

    private Long departementId;
    private String departementNom;

    // true si cet employé est le responsable de son département
    private boolean responsable;

    private LocalDate dateEmbauche;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private Set<RoleResponse> roles;
}
