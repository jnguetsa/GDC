package com.example.demo.GDO.dto.entreprise;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

@Getter
@Setter
public class EntrepriseRequest {

    @NotBlank(message = "Le nom de l'entreprise est requis")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    @Size(max = 500, message = "Le logo ne peut pas dépasser 500 caractères")
    private String logo;

    @NotBlank(message = "L'adresse de l'entreprise est requise")
    @Size(max = 255, message = "L'adresse ne peut pas dépasser 255 caractères")
    private String adresse;

    @NotBlank(message = "La ville de l'entreprise est requise")
    @Size(max = 100, message = "La ville ne peut pas dépasser 100 caractères")
    private String ville;

    @NotBlank(message = "Le pays de l'entreprise est requis")
    @Size(max = 100, message = "Le pays ne peut pas dépasser 100 caractères")
    private String pays;

    @NotBlank(message = "Le numéro de contribuable de l'entreprise est requis")
    @Size(max = 50, message = "Le numéro de contribuable ne peut pas dépasser 50 caractères")
    private String numeroContribuable;

    @NotBlank(message = "Le registre de commerce de l'entreprise est requis")
    @Size(max = 50, message = "Le registre de commerce ne peut pas dépasser 50 caractères")
    private String registreCommerce;

    @NotBlank(message = "Le téléphone de l'entreprise est requis")
    @Pattern(regexp = "^\\+?[0-9]{8,15}$", message = "Numéro de téléphone invalide (8 à 15 chiffres)")
    private String telephone;

    @NotBlank(message = "L'email de l'entreprise est requis")
    @Email(message = "L'email de l'entreprise doit être valide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    private String email;

    @NotBlank(message = "Le site web de l'entreprise est requis")
    @URL(message = "Le site web doit être une URL valide (ex: https://example.com)")
    private String siteWeb;

    @NotNull(message = "Le statut actif/inactif est requis")
    private Boolean active;
}
