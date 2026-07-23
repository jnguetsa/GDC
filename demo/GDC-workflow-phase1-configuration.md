# GDC — Phase 1 : Configuration

> Prérequis obligatoires avant que les employés puissent déposer des demandes.
> Ces ressources sont créées par l'administrateur système ou le RH lors de l'onboarding d'une entreprise.

---

## Ordre d'implémentation

```
CatalogueTypeConge → TypeConge → JourFerie → ReglePlageConge
       ↑                ↑
   (admin sys)        (RH)
```

---

## 1. CatalogueTypeConge

### Rôle
Référentiel système des types de congé standards. Créé par l'administrateur, lu par les RH lors de la configuration de leur entreprise. Pas de modification ni suppression depuis l'API (géré en base ou via un fichier d'init).

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/catalogue-types-conge` | Liste tous les types du catalogue (actifs + inactifs) |
| `GET` | `/api/catalogue-types-conge/actifs` | Liste uniquement les actifs (affichés au RH) |
| `GET` | `/api/catalogue-types-conge/{id}` | Détail d'une entrée catalogue |
| `POST` | `/api/catalogue-types-conge` | Créer une entrée (admin uniquement) |
| `PATCH` | `/api/catalogue-types-conge/{id}/activation` | Activer/désactiver une entrée |

### DTOs

**Request (`CatalogueTypeCongeRequest`)** — déjà créé, à compléter :
```java
@NotBlank String nom
@NotBlank String description
@NotNull CodeTypeConge code
Integer joursParDefautSuggere
@NotNull PolitiqueFinAnnee politiqueFinAnneeSuggere
Integer maxJoursReportSuggere
BigDecimal tauxIndemnisationSuggere   // requis si politiqueFinAnneeSuggere = PAYER
boolean necessiteJustificatifSuggere
boolean necessiteValidationRhSuggere
boolean sansSoldeSuggere
```

**Response (`CatalogueTypeCongeResponse`)** :
```java
Long id
String nom
String description
CodeTypeConge code
Integer joursParDefautSuggere
PolitiqueFinAnnee politiqueFinAnneeSuggere
Integer maxJoursReportSuggere
BigDecimal tauxIndemnisationSuggere
boolean necessiteJustificatifSuggere
boolean necessiteValidationRhSuggere
boolean sansSoldeSuggere
boolean actif
```

### Validations
- `code` doit être unique dans le catalogue — lever `ConflictException` si doublon
- Si `politiqueFinAnneeSuggere = PAYER` → `tauxIndemnisationSuggere` obligatoire (entre 0.0 et 1.0)
- Si `politiqueFinAnneeSuggere = REPORTER` → `maxJoursReportSuggere` recommandé (warning si null)

### Logique service
```
creerCatalogue(request) :
  1. Vérifier unicité du code
  2. Valider tauxIndemnisationSuggere si PAYER
  3. Sauvegarder

getActifs() :
  SELECT * FROM catalogue_types_conge WHERE actif = true ORDER BY nom
```

---

## 2. TypeConge

### Rôle
Configuration effective d'un type de congé pour une entreprise donnée. Créé par le RH en sélectionnant une entrée du catalogue. Le RH personnalise les valeurs suggérées selon la politique de son entreprise.

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/types-conge` | Tous les types de l'entreprise du RH connecté |
| `GET` | `/api/types-conge/actifs` | Uniquement les types actifs (affichés aux employés) |
| `GET` | `/api/types-conge/{id}` | Détail d'un type |
| `POST` | `/api/types-conge` | Créer depuis le catalogue |
| `PUT` | `/api/types-conge/{id}` | Modifier la configuration |
| `PATCH` | `/api/types-conge/{id}/activation` | Activer/désactiver |
| `POST` | `/api/types-conge/{id}/resync-catalogue` | Resynchroniser nom/description depuis le catalogue |

### DTOs

**Request (`TypeCongeRequest`)** :
```java
@NotNull Long catalogueId              // null si type personnalisé
@NotBlank String nom
String description
String couleur
Integer joursParDefaut
boolean sansSolde
@NotNull PolitiqueFinAnnee politiqueFinAnnee
Integer maxJoursReport                 // requis si REPORTER
BigDecimal tauxIndemnisation           // requis si PAYER
boolean necessiteJustificatif
boolean necessiteValidationRh
boolean necessiteAdresseConge
Integer delaiMinimumJours
UniteTemps uniteDelai
Integer ageMinimumEnfant
Integer ageMaximumEnfant
UniteTemps uniteAge
@NotNull Long entrepriseId
```

**Response (`TypeCongeResponse`)** :
```java
Long id
Long catalogueId
String nomCatalogue                    // depuis catalogue.nom
String nom
String description
CodeTypeConge code                     // getCode() dérivé
String couleur
Integer joursParDefaut
boolean sansSolde
PolitiqueFinAnnee politiqueFinAnnee
Integer maxJoursReport
BigDecimal tauxIndemnisation
boolean necessiteJustificatif
boolean necessiteValidationRh
boolean necessiteAdresseConge
Integer delaiMinimumJours
UniteTemps uniteDelai
Integer ageMinimumEnfant
Integer ageMaximumEnfant
UniteTemps uniteAge
boolean actif
LocalDateTime dateCreation
LocalDateTime dateModification
```

### Validations
- `(nom, entrepriseId)` unique — `ConflictException` si doublon
- Si `politiqueFinAnnee = PAYER` → `tauxIndemnisation` obligatoire, entre 0.01 et 1.0
- Si `politiqueFinAnnee = REPORTER` → `maxJoursReport` >= 0
- Si `code` est CP ou CM2 → `ageMaximumEnfant` et `uniteAge` obligatoires
- Si `necessiteAdresseConge = true` → `delaiMinimumJours` >= 1

### Logique service
```
creerTypeConge(request) :
  1. Charger CatalogueTypeConge si catalogueId fourni
  2. Vérifier unicité (nom, entreprise)
  3. Mapper request → TypeConge
  4. Lier catalogue si fourni (nom/description pré-remplis depuis catalogue si non surchargés)
  5. Sauvegarder
  6. Logger HistoriqueAction(TYPE_CONGE_CREATION)

resyncCatalogue(id) :
  1. Charger TypeConge
  2. Vérifier que catalogue != null
  3. typeConge.nom = catalogue.nom
  4. typeConge.description = catalogue.description
  5. Sauvegarder
```

---

## 3. JourFerie

### Rôle
Jours fériés de l'entreprise. Utilisés lors du calcul du nombre de jours ouvrables d'une demande et de la vérification du délai de préavis (`uniteDelai = JOURS_OUVRABLES`).

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/jours-feries` | Tous les jours fériés de l'entreprise |
| `GET` | `/api/jours-feries/{annee}` | Jours fériés d'une année donnée |
| `POST` | `/api/jours-feries` | Créer un jour férié |
| `PUT` | `/api/jours-feries/{id}` | Modifier |
| `DELETE` | `/api/jours-feries/{id}` | Supprimer |

### DTOs

**Request (`JourFerieRequest`)** :
```java
@NotBlank String nom
@NotNull LocalDate date
boolean recurrentAnnuel
String description
@NotNull Long entrepriseId
```

**Response (`JourFerieResponse`)** :
```java
Long id
String nom
LocalDate date
boolean recurrentAnnuel
String description
boolean actif
LocalDateTime dateCreation
```

### Logique service
```
getByAnnee(annee, entrepriseId) :
  SELECT * FROM jours_feries
  WHERE entreprise_id = :entrepriseId
  AND (YEAR(date) = :annee OR recurrentAnnuel = true)
  AND actif = true

estJourFerie(date, entrepriseId) :
  Vérifie si une date donnée est un jour férié → utilisé dans le calcul des jours ouvrables
```

---

## 4. ReglePlageConge

### Rôle
Définit les fenêtres mensuelles pendant lesquelles un type de congé peut être posé, par département. Configuration optionnelle — absence de règle = aucune contrainte calendaire.

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/regles-plage` | Toutes les règles de l'entreprise |
| `GET` | `/api/regles-plage/type-conge/{typeCongeId}` | Règles d'un type de congé donné |
| `POST` | `/api/regles-plage` | Créer une règle |
| `PUT` | `/api/regles-plage/{id}` | Modifier |
| `PATCH` | `/api/regles-plage/{id}/activation` | Activer/désactiver |
| `DELETE` | `/api/regles-plage/{id}` | Supprimer |

### DTOs

**Request (`ReglePlageCongeRequest`)** :
```java
@NotBlank String nom
String description
@NotNull Long typeCongeId
@NotNull Long entrepriseId
Set<Long> departementIds              // vide = s'applique à tous les départements
Set<String> categories                // catégories d'employés concernées
@NotNull Integer moisDebutAutorise    // 1-12
@NotNull Integer moisFinAutorise      // 1-12
@NotNull Integer moisExpiration
@NotNull Integer jourExpiration
```

**Response (`ReglePlageCongeResponse`)** :
```java
Long id
String nom
String description
Long typeCongeId
String nomTypeConge
List<DepartementInfo> departements    // id + nom
Set<String> categories
Integer moisDebutAutorise
Integer moisFinAutorise
Integer moisExpiration
Integer jourExpiration
boolean actif
LocalDateTime dateCreation
```

### Validations
- `moisDebutAutorise` et `moisFinAutorise` entre 1 et 12
- `moisFinAutorise` >= `moisDebutAutorise` (ou règle chevauchant fin d'année → documenter)
- `jourExpiration` entre 1 et 31

### Logique service
```
getReglesApplicables(typeCongeId, departementId) :
  SELECT r FROM regles_plage_conge r
  JOIN regle_plage_departements d ON d.regle_id = r.id
  WHERE r.type_conge_id = :typeCongeId
  AND r.actif = true
  AND (d.departement_id = :departementId OR r.departements IS EMPTY)

estDansPlage(dateDebut, typeCongeId, departementId) :
  1. Charger règles applicables
  2. Si aucune règle → true (pas de contrainte)
  3. Vérifier que le mois de dateDebut est entre moisDebutAutorise et moisFinAutorise
  4. Si hors plage → false + message explicite
```

---

## Dépendances entre les 4 ressources

```
CatalogueTypeConge
      ↓ sélectionné par
  TypeConge ←── entreprise
      ↓ référencé par
  ReglePlageConge ←── departements

  JourFerie ←── entreprise
      ↓ utilisé par (Phase 2)
  calcul jours ouvrables dans DemandeConge
```

## Fichier d'init recommandé (`DataInitializer.java`)

```java
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationListener<ApplicationReadyEvent> {

    // Insérer les entrées CatalogueTypeConge si la table est vide
    // Exemple :
    // CA : joursParDefautSuggere=21, politiqueFinAnneeSuggere=REPORTER, maxJoursReportSuggere=10
    // CM : joursParDefautSuggere=null, necessiteJustificatifSuggere=true
    // CP : joursParDefautSuggere=11, ageMaximumEnfant=6, uniteAge=MOIS
    // CM2: joursParDefautSuggere=112, necessiteAdresseConge=true
    // CSS: sansSoldeSuggere=true, necessiteValidationRhSuggere=true
    // CE : necessiteJustificatifSuggere=true, delaiMinimumJours=0
    // CF : necessiteValidationRhSuggere=true
}
```