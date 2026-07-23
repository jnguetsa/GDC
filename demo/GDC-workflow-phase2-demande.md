# GDC — Phase 2 : Cycle de vie d'une demande

> Cœur du système. Couvre la soumission, le calcul du solde, les validations manager/RH et l'annulation.
> Dépend de la Phase 1 (TypeConge, JourFerie, ReglePlageConge doivent exister).

---

## Cycle de vie complet

```
                        ┌─────────────────────────────┐
                        │         SOUMISSION           │
                        │  Employé POST /demandes      │
                        └──────────────┬──────────────┘
                                       │
                               statut = EN_ATTENTE
                               soldeEnAttente += nombreJours
                                       │
              ┌────────────────────────┼────────────────────────┐
              │                        │                         │
        [Manager]                [Employé]               [Expiration]
     PATCH /validation         PATCH /annulation
              │                        │
     ┌────────┴────────┐         statut = ANNULEE
     │                 │         soldeEnAttente -= nombreJours
  APPROUVEE_MANAGER  REJETEE
                        │
                  statut = REJETEE
                  soldeEnAttente -= nombreJours
                        │
          ┌─────────────┘
          │
    [Si necessiteValidationRh = true]
          │
   PATCH /validation-rh
          │
   ┌──────┴──────┐
   │             │
APPROUVEE      REJETEE
   │             │
soldePris +=   soldeEnAttente -= nombreJours
nombreJours
soldeEnAttente -= nombreJours
   │
   └──→ Phase 3 (RetourConge)
```

---

## 5. SoldeConge

### Rôle
Solde de jours disponibles par combinaison `(Employe, TypeConge, Annee)`. Initialisé automatiquement à l'embauche ou au 1er janvier. Mis à jour à chaque transition de statut d'une demande.

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/soldes` | Soldes de l'employé connecté (année en cours) |
| `GET` | `/api/soldes/employe/{employeId}` | Soldes d'un employé (RH/manager) |
| `GET` | `/api/soldes/employe/{employeId}/annee/{annee}` | Soldes d'une année donnée |
| `POST` | `/api/soldes/initialiser` | Initialiser les soldes d'un employé (RH) |
| `PATCH` | `/api/soldes/{id}/ajustement` | Ajustement manuel du solde (RH uniquement) |

### DTOs

**Response (`SoldeCongeResponse`)** :
```java
Long id
Long typeCongeId
String nomTypeConge
CodeTypeConge codeTypeConge
Integer annee
UniteSolde unite
Double soldeInitial
Double soldeReporte
Double soldePris
Double soldeEnAttente
Double soldeRestant
Double soldeAjuste
String motifAjustement
LocalDate dateExpiration
boolean estExpire
```

**Request ajustement (`AjustementSoldeRequest`)** :
```java
@NotNull Double montant          // positif = crédit, négatif = débit
@NotBlank String motif
```

### Logique service
```
initialiserSoldes(employeId, annee) :
  Pour chaque TypeConge actif de l'entreprise de l'employé :
    Si SoldeConge(employe, typeConge, annee) n'existe pas :
      Créer SoldeConge(
        soldeInitial = typeConge.joursParDefaut ?? 0,
        soldeReporte = 0,
        soldePris = 0,
        soldeEnAttente = 0
      )
      // @PrePersist calcule soldeRestant automatiquement

verifierSolde(employeId, typeCongeId, nombreJours) :
  Charger SoldeConge(employe, typeConge, anneeEnCours)
  Si typeConge.sansSolde → true (pas de vérification)
  Si soldeRestant < nombreJours → lever InsufficientSoldeException

ajusterSolde(id, montant, motif) :
  solde.soldeAjuste = (solde.soldeAjuste ?? 0) + montant
  solde.motifAjustement = motif
  // @PreUpdate recalcule soldeRestant
  Logger HistoriqueAction(SOLDE_AJUSTEMENT)
```

---

## 6. DemandeConge — Soumission

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/demandes` | Demandes de l'employé connecté |
| `GET` | `/api/demandes/{id}` | Détail d'une demande |
| `GET` | `/api/demandes/equipe` | Demandes de l'équipe (manager) |
| `GET` | `/api/demandes/entreprise` | Toutes les demandes (RH) |
| `POST` | `/api/demandes` | Soumettre une demande |
| `PATCH` | `/api/demandes/{id}/annulation` | Annuler sa propre demande |

### DTOs

**Request (`DemandeCongeRequest`)** :
```java
@NotNull Long typeCongeId
@NotNull LocalDate dateDebut
@NotNull LocalDate dateFin
boolean demiJourneeDebut
boolean demiJourneeFin
String motif
String adresseConge               // requis si typeConge.necessiteAdresseConge
String contactUrgence
// piecesJointes uploadées séparément via POST /demandes/{id}/pieces-jointes
```

**Response (`DemandeCongeResponse`)** :
```java
Long id
Long employeId
String nomEmploye, prenomEmploye
Long typeCongeId
String nomTypeConge
LocalDate dateDebut
LocalDate dateFin
Integer nombreJours
boolean demiJourneeDebut
boolean demiJourneeFin
String motif
String adresseConge
String contactUrgence
StatutDemande statut
// Validation manager
String nomManager
String commentaireManager
LocalDateTime dateValidationManager
// Validation RH
String nomRh
String commentaireRh
LocalDateTime dateValidationRh
// Annulation
String motifAnnulation
LocalDateTime dateAnnulation
List<PieceJointeResponse> piecesJointes
LocalDateTime dateCreation
```

### Validations à la soumission (dans l'ordre)

```
1. dateDebut <= dateFin                        [@PrePersist déjà en place]
2. dateDebut >= aujourd'hui                    [pas de demande rétroactive]
3. Délai de préavis respecté :
      joursOuvrables(aujourd'hui, dateDebut) >= typeConge.delaiMinimumJours
      (en tenant compte des JourFerie si uniteDelai = JOURS_OUVRABLES)
4. Plage mensuelle autorisée :
      ReglePlageConge.estDansPlage(dateDebut, typeCongeId, employe.departement)
5. Pas de chevauchement avec une demande existante EN_ATTENTE ou APPROUVEE
6. Solde suffisant (si !sansSolde) :
      SoldeConge.soldeRestant >= nombreJours
7. Justificatif présent (si necessiteJustificatif) :
      → Bloquer si pas de PieceJointe uploadée (ou accepter et marquer PENDING_JUSTIFICATIF)
8. Adresse renseignée si necessiteAdresseConge
9. Contrainte d'âge de l'enfant (si CP/CM2) :
      ageEnfant entre ageMinimumEnfant et ageMaximumEnfant
10. Genre de l'employé (CP → MASCULIN, CM2 → FEMININ)
```

### Logique service
```
soumettre(request, employeId, adresseIp) :
  1. Charger TypeConge + Employe
  2. Calculer nombreJours (en excluant weekends + jours fériés)
  3. Passer les 10 validations ci-dessus
  4. Créer DemandeConge(statut = EN_ATTENTE)
  5. SoldeConge.soldeEnAttente += nombreJours  [→ @PreUpdate recalcule soldeRestant]
  6. Créer RetourConge(dateRetourPrevue = dateFin + 1, statut = EN_ATTENTE)
  7. Notifier le manager → Notification(DEMANDE_SOUMISE, destinataire = employe.manager)
  8. Logger HistoriqueAction(DEMANDE_CREATION, adresseIp)

annuler(id, motif, employeId) :
  1. Vérifier que statut = EN_ATTENTE et employe = demandeur
  2. statut = ANNULEE, motifAnnulation = motif, dateAnnulation = now()
  3. SoldeConge.soldeEnAttente -= nombreJours
  4. Notifier le manager → Notification(DEMANDE_ANNULEE)
  5. Logger HistoriqueAction(DEMANDE_ANNULATION)
```

---

## 7. Validation Manager

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/demandes/a-valider` | Demandes EN_ATTENTE de l'équipe du manager connecté |
| `PATCH` | `/api/demandes/{id}/validation-manager` | Approuver ou rejeter |

### DTOs

**Request (`ValidationManagerRequest`)** :
```java
@NotNull Boolean approuve
String commentaire                    // obligatoire si approuve = false
```

### Logique service
```
validerManager(id, request, managerId) :
  1. Charger demande
  2. Vérifier statut = EN_ATTENTE
  3. Vérifier que managerId = demande.employe.manager.id
  4. Si approuve = true :
       statut = APPROUVEE_MANAGER
       valideParManager = manager
       dateValidationManager = now()
       Si typeConge.necessiteValidationRh :
         Notifier RH → Notification(APPROUVEE_MANAGER)
       Sinon :
         → finaliserApprobation(demande)    [voir ci-dessous]
  5. Si approuve = false :
       statut = REJETEE
       commentaireManager = request.commentaire
       SoldeConge.soldeEnAttente -= nombreJours
       Notifier employé → Notification(DEMANDE_REJETEE)
       Logger HistoriqueAction(DEMANDE_REJET_MANAGER)

finaliserApprobation(demande) :
  statut = APPROUVEE
  SoldeConge.soldePris += nombreJours
  SoldeConge.soldeEnAttente -= nombreJours
  Notifier employé → Notification(DEMANDE_APPROUVEE_RH)
  Logger HistoriqueAction(DEMANDE_APPROBATION_RH)
```

---

## 8. Validation RH

Déclenchée uniquement si `typeConge.necessiteValidationRh = true` et statut = `APPROUVEE_MANAGER`.

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/demandes/a-valider-rh` | Demandes APPROUVEE_MANAGER en attente de validation RH |
| `PATCH` | `/api/demandes/{id}/validation-rh` | Approuver ou rejeter (RH) |

### DTOs

**Request (`ValidationRhRequest`)** :
```java
@NotNull Boolean approuve
String commentaire                    // obligatoire si approuve = false
```

### Logique service
```
validerRh(id, request, rhId) :
  1. Charger demande
  2. Vérifier statut = APPROUVEE_MANAGER
  3. Si approuve = true :
       → finaliserApprobation(demande)
       valideParRh = rh
       dateValidationRh = now()
       commentaireRh = request.commentaire
  4. Si approuve = false :
       statut = REJETEE
       commentaireRh = request.commentaire
       SoldeConge.soldeEnAttente -= nombreJours
       Notifier employé → Notification(DEMANDE_REJETEE)
       Logger HistoriqueAction(DEMANDE_REJET_RH)
```

---

## Calcul du nombre de jours ouvrables

Méthode utilitaire partagée utilisée à la soumission et au calcul du délai de préavis :

```java
calculerJoursOuvrables(dateDebut, dateFin, entrepriseId) :
  jours = 0
  date = dateDebut
  Tant que date <= dateFin :
    Si date.dayOfWeek != SAMEDI && date.dayOfWeek != DIMANCHE :
      Si !estJourFerie(date, entrepriseId) :
        jours++
    date = date + 1 jour
  Retourner jours
```

Pour `uniteDelai = JOURS_CALENDAIRES` → simple différence de dates sans filtre.

---

## Règle de chevauchement

```java
existeDemandeChevauchante(employeId, dateDebut, dateFin) :
  SELECT COUNT(*) FROM demandes_conge
  WHERE employe_id = :employeId
  AND statut IN ('EN_ATTENTE', 'APPROUVEE_MANAGER', 'APPROUVEE')
  AND dateDebut <= :dateFin
  AND dateFin >= :dateDebut
```

---

## Notifications déclenchées en Phase 2

| Événement | Destinataire | Type |
|---|---|---|
| Demande soumise | Manager | `DEMANDE_SOUMISE` |
| Approuvée manager (sans RH) | Employé | `DEMANDE_APPROUVEE_RH` |
| Approuvée manager (avec RH) | RH | `APPROUVEE_MANAGER` |
| Approuvée RH | Employé | `DEMANDE_APPROUVEE_RH` |
| Rejetée manager | Employé | `DEMANDE_REJETEE` |
| Rejetée RH | Employé | `DEMANDE_REJETEE` |
| Annulée employé | Manager | `DEMANDE_ANNULEE` |