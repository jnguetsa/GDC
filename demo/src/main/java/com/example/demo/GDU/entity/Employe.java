package com.example.demo.GDU.entity;

import com.example.demo.GDC.entity.DemandeConge;
import com.example.demo.GDC.entity.ExerciceConge;
import com.example.demo.GDC.entity.HistoriqueAction;
import com.example.demo.GDC.entity.Notification;
import com.example.demo.GDC.entity.RetourConge;
import com.example.demo.GDC.entity.SoldeConge;
import com.example.demo.GDO.entity.Departement;
import com.example.demo.GDO.entity.Entreprise;
import com.example.demo.GDU.enums.Genre;
import com.example.demo.common.enums.CategorieEmploye;
import com.example.demo.common.enums.StatutEmploi;
import com.example.demo.common.enums.TypeContrat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@DiscriminatorValue("EMPLOYE")
public class Employe extends Utilisateur {

    private String photoProfil;
    private String matricule;
    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Genre genre;

    private LocalDate dateNaissance;
    private String lieuNaissance;

    @Column(unique = true)
    private String numeroCni;

    private String nationalite;
    private String telephone;
    private String telephoneUrgence;
    private String adresse;
    private String ville;
    private String quartier;
    private String numeroCompte;
    private String banque;
    private String agence;
    private String poste;

    @Enumerated(EnumType.STRING)
    private CategorieEmploye categorie;

    @ManyToOne
    @JoinColumn(name = "entreprise_id")
    private Entreprise entreprise;

    @ManyToOne
    @JoinColumn(name = "departement_id")
    private Departement departement;

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employe manager;

    @OneToMany(mappedBy = "manager")
    private List<Employe> subordonnes;

    @Enumerated(EnumType.STRING)
    private TypeContrat typeContrat;

    private LocalDate dateEmbauche;
    private LocalDate dateFinContrat;

    @Enumerated(EnumType.STRING)
    private StatutEmploi statutEmploi;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<DemandeConge> demandesConge;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<SoldeConge> soldesConge;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<ExerciceConge> exercicesConge;
    @OneToMany(mappedBy = "destinataire", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @OneToMany(mappedBy = "employe", cascade = CascadeType.ALL)
    private List<HistoriqueAction> historique;
}