# DTOs — Projet GDC (Gestion des Congés)

Stack : Spring Boot 3.4.1 · Java 17 · PostgreSQL · JWT · MapStruct · Lombok

---

## Modules

| Module | Rôle |
|--------|------|
| **GDU** | Utilisateurs, Employés, Rôles, Auth JWT |
| **GDO** | Entreprise, Département |
| **GDC** | Congés (Demandes, Retours, Soldes, Exercices…) |

---

## Module GDU — Utilisateurs & Auth

### Auth

#### `LoginRequest`
```java
String email           // @Email @NotBlank
String password        // @NotBlank
```

#### `RegisterRequest`
```java
String email           // @Email @NotBlank
String password        // @NotBlank @Size(min=8)
String confirmPassword // @NotBlank
```

#### `AuthResponse`
```java
String accessToken
String refreshToken
String tokenType
long   expiresIn
```

#### `RefreshTokenRequest`
```java
String refreshToken    // @NotBlank
```

#### `LogoutRequest`
```java
String refreshToken    // @NotBlank
```

#### `ProfileResponse`
```java
Long            id
String          email
boolean         compteActif
LocalDateTime   derniereConnexion
LocalDateTime   dateCreation
List<RoleResponse> roles
```

#### `ActivationRequest`
```java
Long    utilisateurId  // @NotNull
Boolean compteActif    // @NotNull
```

#### `AssignRoleRequest`
```java
Long       utilisateurId  // @NotNull
Set<Long>  roleIds         // @NotEmpty
```

---

### Employé

#### `EmployeRequest`
```java
String    nom           // @NotBlank
String    prenom        // @NotBlank
String    email         // @NotBlank
String    telephone     // @NotBlank
String    adresse       // @NotBlank
LocalDate dateEmbauche  // @NotNull
LocalDate dateDebut     // @NotNull
LocalDate dateFin       // @NotNull
Long      departementId
```

#### `EmployeResponse`
```java
Long         id
String       matricule
String       nom
String       prenom
String       email
String       telephone
String       poste
Long         departementId
String       departementNom
boolean      responsable       // true si responsable du département
LocalDate    dateEmbauche
LocalDate    dateDebut
LocalDate    dateFin
Set<RoleResponse> roles
```

#### `EmployeInfo`  *(objet léger réutilisé dans tous les DTOs)*
```java
Long   id
String matricule
String nom
String prenom
String email
String telephone
String poste
String photoProfil
Long   departementId
String departementNom
```

---

### Rôle

#### `RoleResponse`
```java
Long   id
String nom
String description
boolean actif
Set<PermissionResponse> permissions
```

#### `ActiveRole`
```java
Long    id     // @NotNull
Boolean actif  // @NotNull
```

---

### Permission

#### `PermissionResponse`
```java
Long   id
String nom
String description
boolean actif
```

#### `ActivePermission`
```java
Long    id     // @NotNull
Boolean actif  // @NotNull
```

---

## Module GDO — Entreprise & Département

### Entreprise

#### `EntrepriseRequest`
```java
String  nom                // @NotBlank @Size(max=150)
String  logo               // URL logo
String  adresse            // @NotBlank
String  ville              // @NotBlank
String  pays               // @NotBlank
String  numeroContribuable // @NotBlank
String  registreCommerce   // @NotBlank
String  telephone          // @Pattern ^\\+?[0-9]{8,15}$
String  email              // @Email @NotBlank
String  siteWeb            // @URL @NotBlank
Boolean active             // @NotNull
```

#### `EntrepriseResponse`
```java
Long                    id
String                  nom
String                  logo
String                  adresse
String                  ville
String                  pays
String                  numeroContribuable
String                  registreCommerce
String                  telephone
String                  email
String                  siteWeb
boolean                 active
LocalDateTime           dateCreation
LocalDateTime           dateModification
List<DepartementResponse> departements
```

---

### Département

#### `DepartementRequest`
```java
String nom          // @NotBlank @Size(max=100)
String code         // @Size(max=20)
String description  // @Size(max=255)
Long   entrepriseId // @NotNull
Long   responsableId
```

#### `DepartementResponse`  *(vue légère)*
```java
Long          id
String        nom
String        code
String        description
EmployeInfo   responsable
boolean       actif
int           nbrEmpl
LocalDateTime dateCreation
```

#### `DepartementResponseDetails`  *(avec liste employés)*
```java
Long               id
String             nom
String             code
String             description
EmployeInfo        responsable
boolean            actif
int                nbrEmpl
LocalDateTime      dateCreation
List<EmployeInfo>  employes
```

---

## Module GDC — Gestion des Congés

### Type de Congé

#### `TypeCongeRequest`
```java
Long           catalogueId
String         nom                   // @NotBlank
String         description
String         couleur               // hex color
Integer        joursParDefaut
boolean        sansSolde
PolitiqueFinAnnee politiqueFinAnnee  // @NotNull
Integer        maxJoursReport        // si REPORTER
BigDecimal     tauxIndemnisation     // si PAYER, 0.01–1.0
boolean        necessiteJustificatif
boolean        necessiteValidationRh
boolean        necessiteAdresseConge
Integer        delaiMinimumJours
UniteTemps     uniteDelai
Integer        ageMinimumEnfant
Integer        ageMaximumEnfant
UniteTemps     uniteAge
Long           entrepriseId          // @NotNull
```

#### `TypeCongeResponse`
```java
Long             id
Long             catalogueId
String           nomCatalogue
CodeTypeConge    code               // CA, CM, CP, CM2, CE, CF, CSS, AUTRE
String           nom
String           description
String           couleur
Integer          joursParDefaut
boolean          sansSolde
PolitiqueFinAnnee politiqueFinAnnee
Integer          maxJoursReport
BigDecimal       tauxIndemnisation
boolean          necessiteJustificatif
boolean          necessiteValidationRh
boolean          necessiteAdresseConge
Integer          delaiMinimumJours
UniteTemps       uniteDelai
Integer          ageMinimumEnfant
Integer          ageMaximumEnfant
UniteTemps       uniteAge
String           nomEntreprise
boolean          actif
LocalDateTime    dateCreation
LocalDateTime    dateModification
```

---

### Catalogue Type de Congé  *(référentiel global, admin)*

#### `CatalogueTypeCongeRequest`
```java
String          nom                      // @NotBlank
String          description              // @NotBlank
CodeTypeConge   code                     // @NotNull
Integer         joursParDefautSuggere
PolitiqueFinAnnee politiqueFinAnneeSuggere // @NotNull
Integer         maxJoursReportSuggere
BigDecimal      tauxIndemnisationSuggere  // 0.01–1.0
boolean         necessiteJustificatifSuggere
boolean         necessiteValidationRhSuggere
boolean         sansSoldeSuggere
```

#### `CatalogueTypeCongeResponse`
```java
Long              id
String            nom
String            description
CodeTypeConge     code
Integer           joursParDefautSuggere
PolitiqueFinAnnee politiqueFinAnneeSuggere
Integer           maxJoursReportSuggere
BigDecimal        tauxIndemnisationSuggere
boolean           necessiteJustificatifSuggere
boolean           necessiteValidationRhSuggere
boolean           sansSoldeSuggere
boolean           actif
```

---

### Demande de Congé

#### `DemandeCongeRequest`
```java
Long      typeCongeId      // @NotNull
LocalDate dateDebut        // @NotNull
LocalDate dateFin          // @NotNull
boolean   demiJourneeDebut
boolean   demiJourneeFin
String    motif
String    adresseConge
String    contactUrgence
```

#### `DemandeCongeResponse`
```java
Long                      id
EmployeInfo               employe
TypeCongeResponse         typeConge
LocalDate                 dateDebut
LocalDate                 dateFin
Integer                   nombreJours
boolean                   demiJourneeDebut
boolean                   demiJourneeFin
String                    motif
String                    adresseConge
String                    contactUrgence
StatutDemande             statut            // EN_ATTENTE | APPROUVEE | REJETEE | ANNULEE
List<PieceJointeResponse> piecesJointes
// Validation Manager
EmployeInfo               manager
String                    commentaireManager
LocalDateTime             dateValidationManager
// Validation RH
EmployeInfo               rh
String                    commentaireRh
LocalDateTime             dateValidationRh
// Annulation
String                    motifAnnulation
LocalDateTime             dateAnnulation
EmployeInfo               annulePar
LocalDateTime             dateCreation
LocalDateTime             dateModification
```

#### `DemandeCongerValidation`  *(approbation manager ou RH)*
```java
Boolean approuvee   // @NotNull
String  commentaire
```

#### `DemandeCongeAnnulationRequest`
```java
String motifAnnulation  // @NotBlank
```

---

### Retour de Congé

#### `RetourCongeRequest`
```java
LocalDate dateRetourEffective  // @NotNull
String    motifEcart
String    commentaire
```

#### `RetourCongeResponse`
```java
Long                      id
Long                      demandeCongeId
EmployeInfo               employe
String                    typeCongeNom
LocalDate                 demandeCongedateDebut
LocalDate                 demandeCongeDateFin
LocalDate                 dateRetourPrevue
LocalDate                 dateRetourEffective
Integer                   joursEcart
Integer                   joursNonPrisRecuperables
StatutRetour              statut   // EN_ATTENTE | A_L_HEURE | EN_RETARD | ABSENT_INJUSTIFIE | RETOUR_ANTICIPE
String                    motifEcart
String                    commentaire
List<PieceJointeResponse> piecesJointes
Boolean                   joursRecuperesSurSolde
String                    commentaireDecisionRh
EmployeInfo               enregistrePar
LocalDateTime             dateEnregistrement
```

---

### Solde de Congé

#### `SoldeCongeRequest`
```java
Long       employeId       // @NotNull
Long       typeCongeId     // @NotNull
Integer    annee           // @NotNull
UniteSolde unite           // @NotNull  JOURS | HEURES
Double     soldeInitial    // @NotNull
Double     soldeReporte
LocalDate  dateDebutPlage
LocalDate  dateFinPlage
LocalDate  dateExpiration
```

#### `SoldeCongeResponse`
```java
Long          id
EmployeInfo   employe
Long          typeCongeId
String        typeCongeNom
Integer       annee
UniteSolde    unite
Double        soldeInitial
Double        soldeReporte
Double        soldePris
Double        soldeEnAttente
Double        soldeRestant
Double        soldeAjuste
String        motifAjustement
LocalDate     dateDebutPlage
LocalDate     dateFinPlage
LocalDate     dateExpiration
boolean       estExpire
LocalDateTime dateCreation
LocalDateTime dateModification
```

---

### Exercice Congé  *(vue annuelle par employé)*

#### `ExerciceCongeRequest`
```java
Long      employeId          // @NotNull
Long      typeCongeId        // @NotNull
Long      entrepriseId       // @NotNull
Integer   annee              // @NotNull
LocalDate dateDebut
LocalDate dateFin
Double    soldeOuverture     // @NotNull
Double    soldeReporteEntrant
```

#### `ExerciceCongeResponse`
```java
Long                      id
EmployeInfo               employe
Long                      typeCongeId
String                    typeCongeNom
Long                      entrepriseId
String                    entrepriseNom
Integer                   annee
LocalDate                 dateDebut
LocalDate                 dateFin
// Ouverture
Double                    soldeOuverture
Double                    soldeReporteEntrant
// Mouvements
Double                    joursPris
Double                    joursEnAttente
Double                    joursAjustes
String                    motifAjustement
// Clôture
Double                    soldeFinExercice
Double                    joursReportes
Double                    joursExpires
// Snapshot
SoldeCongeResponse        soldeConge
List<DemandeCongeResponse> demandes
boolean                   cloture
LocalDateTime             dateCloture
EmployeInfo               cloturePar
LocalDateTime             dateCreation
```

#### `ExerciceCongeRapportResponse`  *(rapport annuel entreprise)*
```java
Integer                       annee
EntrepriseResponse            entreprise
int                           totalEmployes
Double                        totalJoursPris
Double                        totalJoursEnAttente
Double                        totalJoursRestants
List<DepartementExerciceInfo> parDepartement
```

#### `DepartementExerciceInfo`
```java
DepartementResponseDetails      departement
List<ExerciceCongeResponse>     exercices
int                             nombreEmployes
Double                          totalJoursPris
Double                          totalJoursEnAttente
Double                          totalJoursRestants
```

---

### Règle Plage de Congé

#### `ReglePlageCongeRequest`
```java
Long                  entrepriseId       // @NotNull
String                nom                // @NotBlank
String                description
Set<Long>             departementIds     // vide = toute l'entreprise
Set<CategorieEmploye> categories         // vide = toutes
Long                  typeCongeId
Integer               moisDebutAutorise  // @NotNull 1–12
Integer               moisFinAutorise    // @NotNull 1–12
Integer               moisExpiration     // @NotNull 1–12
Integer               jourExpiration     // @NotNull 1–31
```

#### `ReglePlageCongeResponse`
```java
Long                    id
String                  nom
String                  description
Long                    entrepriseId
String                  entrepriseNom
Set<DepartementResponse> departements
Set<CategorieEmploye>   categories
Long                    typeCongeId
String                  typeCongeNom
Integer                 moisDebutAutorise
Integer                 moisFinAutorise
Integer                 moisExpiration
Integer                 jourExpiration
boolean                 actif
LocalDateTime           dateCreation
```

---

### Jour Férié

#### `JourFerieRequest`
```java
String    nom           // @NotBlank
LocalDate date          // @NotNull
boolean   recurrentAnnuel
String    description
Long      entrepriseId  // @NotNull
```

#### `JourFerieResponse`
```java
Long      id
String    nom
LocalDate date
boolean   recurrentAnnuel
String    description
Long      entrepriseId
String    entrepriseNom
boolean   actif
```

---

### Pièce Jointe

#### `PieceJointeResponse`
```java
Long          id
String        nomFichier
String        typeMime
Long          tailleOctets
LocalDateTime dateUpload
```

> Upload via `POST /demandes-conge/{id}/pieces-jointes` avec `MultipartFile`

---

### Notification

#### `NotificationResponse`
```java
Long              id
Long              destinataireId
String            titre
String            message
TypeNotification  type
String            lienAction
Long              demandeCongeId
Long              retourCongeId
boolean           lue
LocalDateTime     dateLecture
boolean           envoyeeParEmail
LocalDateTime     dateEnvoiEmail
LocalDateTime     dateCreation
```

---

### Historique des Actions

#### `HistoriqueActionResponse`
```java
Long          id
EmployeInfo   employe
TypeAction    action
String        entiteType    // ex: "DemandeConge"
Long          entiteId
String        details
String        adresseIp
LocalDateTime dateAction
```

---

## Enums

### `StatutDemande`
`EN_ATTENTE` · `APPROUVEE` · `REJETEE` · `ANNULEE`

### `StatutRetour`
`EN_ATTENTE` · `A_L_HEURE` · `EN_RETARD` · `ABSENT_INJUSTIFIE` · `RETOUR_ANTICIPE`

### `CodeTypeConge`
`CA` (Congé annuel) · `CM` (Maladie) · `CP` (Paternité) · `CM2` (Maternité) · `CE` (Exceptionnel) · `CF` (Formation) · `CSS` (Sans solde) · `AUTRE`

### `PolitiqueFinAnnee`
`NOUVEAU_DEPART` · `REPORTER` · `PAYER`

### `CategorieEmploye`
`CADRE` · `AGENT` · `TECHNICIEN` · `OPERATEUR` · `STAGIAIRE` · `CONSULTANT`

### `TypeContrat`
`CDI` · `CDD` · `STAGE` · `CONSULTANT`

### `StatutEmploi`
`ACTIF` · `EN_CONGE` · `SUSPENDU` · `DEMISSIONNAIRE` · `LICENCIE` · `RETRAITE`

### `UniteSolde`
`JOURS` · `HEURES`

### `UniteTemps`
`JOURS_CALENDAIRES` · `JOURS_OUVRABLES` · `MOIS` · `ANS`

### `TypeAction`
Demande : `DEMANDE_CREATION` · `DEMANDE_MODIFICATION` · `DEMANDE_ANNULATION` · `DEMANDE_APPROBATION_MANAGER` · `DEMANDE_REJET_MANAGER` · `DEMANDE_APPROBATION_RH` · `DEMANDE_REJET_RH`  
Retour : `RETOUR_ENREGISTREMENT` · `RETOUR_MODIFICATION`  
Solde : `SOLDE_INITIALISATION` · `SOLDE_AJUSTEMENT` · `SOLDE_RECALCUL`  
Exercice : `EXERCICE_CREATION` · `EXERCICE_CLOTURE`  
Type congé : `TYPE_CONGE_CREATION` · `TYPE_CONGE_MODIFICATION` · `TYPE_CONGE_DESACTIVATION`  
Auth : `CONNEXION` · `DECONNEXION`

### `TypeNotification`
Demande : `DEMANDE_SOUMISE` · `DEMANDE_APPROUVEE_MANAGER` · `DEMANDE_APPROUVEE_RH` · `DEMANDE_REJETEE` · `DEMANDE_ANNULEE`  
Retour : `RETOUR_PREVU_DEMAIN` · `RETOUR_EN_RETARD` · `RETOUR_ANTICIPE` · `RETOUR_ENREGISTRE`  
Solde : `SOLDE_FAIBLE` · `SOLDE_EXPIRE` · `SOLDE_AJUSTE`  
Exercice : `EXERCICE_CLOTURE` · `JOURS_REPORTES` · `JOURS_PERDUS`