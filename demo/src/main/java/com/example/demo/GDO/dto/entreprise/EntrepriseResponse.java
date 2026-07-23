package com.example.demo.GDO.dto.entreprise;

import com.example.demo.GDO.dto.departement.DepartementResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EntrepriseResponse {
    private Long id;
    private String nom;
    private String logo;
    private String adresse;
    private String ville;
    private String pays;
    private String numeroContribuable;
    private String registreCommerce;
    private String telephone;
    private String email;
    private String siteWeb;
    private boolean active;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    private List<DepartementResponse> departements;
}
