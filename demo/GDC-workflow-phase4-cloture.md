# GDC — Phase 4 : Clôture annuelle

> Processus déclenché manuellement par le RH en fin d'année (ou automatisable).
> Archive l'état des soldes dans `ExerciceConge` et initialise les soldes N+1 selon la `politiqueFinAnnee` de chaque `TypeConge`.

---

## Cycle de clôture

```
[RH déclenche POST /exercices/cloture-annuelle?annee=2025]
        │
        ↓ Pour chaque SoldeConge(annee=2025, entreprise=X) :
        │
        ├── Calculer soldeFinExercice = (ouverture + reporte + ajuste) - pris - attente
        │         [@PreUpdate déjà en place sur ExerciceConge]
        │
        ├── Selon politiqueFinAnnee du TypeConge :
        │       NOUVEAU_DEPART → joursReportes = 0, joursExpires = soldeRestant
        │       REPORTER       → joursReportes = min(soldeRestant, maxJoursReport ?? ∞)
        │                        joursExpires  = soldeRestant - joursReportes
        │       PAYER          → joursIndemnises = soldeRestant × tauxIndemnisation
        │                        → générer ligne de paie (ou notification RH)
        │
        ├── Créer ExerciceConge(annee=2025, cloture=true, dateCloture=now(), cloturePar=rh)
        ├── Lier ExerciceConge → SoldeConge (traçabilité)
        │
        └── Créer SoldeConge(annee=2026) :
              soldeInitial    = typeConge.joursParDefaut
              soldeReporte    = joursReportes de l'exercice 2025
              soldePris       = 0
              soldeEnAttente  = 0
              // @PrePersist calcule soldeRestant
```

---

## 11. ExerciceConge

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/exercices` | Exercices de l'employé connecté |
| `GET` | `/api/exercices/employe/{employeId}` | Historique complet d'un employé (RH) |
| `GET` | `/api/exercices/{id}` | Détail d'un exercice |
| `GET` | `/api/exercices/entreprise/{annee}` | Tous les exercices d'une entreprise pour une année |
| `POST` | `/api/exercices/cloture-annuelle` | Déclencher la clôture pour une entreprise (RH) |
| `GET` | `/api/exercices/cloture-annuelle/preview` | Simuler la clôture sans la sauvegarder |

### DTOs

**Request clôture (`ClotureAnnuelleRequest`)** :
```java
@NotNull Long entrepriseId
@NotNull Integer annee
```

**Response exercice (`ExerciceCongeResponse`)** :
```java
Long id
Long employeId
String nomEmploye, prenomEmploye
Long typeCongeId
String nomTypeConge
CodeTypeConge codeTypeConge
Integer annee
LocalDate dateDebut
LocalDate dateFin
Double soldeOuverture
Double soldeReporteEntrant
Double joursPris
Double joursEnAttente
Double joursAjustes
String motifAjustement
Double soldeFinExercice
Double joursReportes
Double joursExpires
Double joursIndemnises         // si politiqueFinAnnee = PAYER
boolean cloture
LocalDateTime dateCloture
String nomCloturePar
```

**Response preview (`CloturePreviewResponse`)** :
```java
Integer annee
Integer nombreEmployes
Integer nombreExercices
List<ExercicePreviewItem> details    // par employé × typeConge
Double totalJoursReportes
Double totalJoursExpires
Double totalJoursIndemnises
```

```java
// ExercicePreviewItem
String nomEmploye
String nomTypeConge
Double soldeRestant
PolitiqueFinAnnee politique
Double joursReportes
Double joursExpires
Double joursIndemnises
```

### Logique service

```
cloturerAnnee(request, rhId) :
  1. Vérifier qu'aucune clôture n'existe déjà pour (entreprise, annee)
  2. Récupérer tous les SoldeConge(entreprise, annee) non expirés
  3. Pour chaque solde :
       a. Calculer joursReportes, joursExpires, joursIndemnises selon politiqueFinAnnee
       b. Créer ExerciceConge(
            soldeOuverture  = solde.soldeInitial,
            soldeReporteEntrant = solde.soldeReporte,
            joursPris       = solde.soldePris,
            joursAjustes    = solde.soldeAjuste,
            soldeFinExercice = solde.soldeRestant,   // @PreUpdate recalcule
            joursReportes   = calculé,
            joursExpires    = calculé,
            cloture         = true,
            dateCloture     = now(),
            cloturePar      = rh
          )
       c. Lier exercice → solde (exerciceConge.soldeConge)
       d. Créer SoldeConge(annee+1, soldeInitial, soldeReporte = joursReportes)
       e. Si PAYER → créer notification RH avec montant à verser
  4. Logger HistoriqueAction(EXERCICE_CLOTURE) pour chaque exercice créé
  5. Notifier chaque employé :
       Si joursReportes > 0  → Notification(JOURS_REPORTES)
       Si joursExpires > 0   → Notification(JOURS_PERDUS)
       Si joursIndemnises > 0 → Notification(SOLDE_AJUSTE)

previewCloture(request) :
  Même logique que cloturerAnnee() mais sans aucune écriture en base
  Retourner CloturePreviewResponse avec les chiffres simulés
```

### Calcul selon politique

```java
calculerMouvement(soldeRestant, typeConge) :
  switch (typeConge.politiqueFinAnnee) {
    case NOUVEAU_DEPART :
      joursReportes  = 0
      joursExpires   = soldeRestant
      joursIndemnises = 0

    case REPORTER :
      max = typeConge.maxJoursReport ?? Double.MAX_VALUE
      joursReportes  = min(soldeRestant, max)
      joursExpires   = soldeRestant - joursReportes
      joursIndemnises = 0

    case PAYER :
      joursReportes  = 0
      joursExpires   = 0
      joursIndemnises = soldeRestant × typeConge.tauxIndemnisation
  }
```

---

## Notifications déclenchées en Phase 4

| Événement | Destinataire | Type |
|---|---|---|
| Exercice clôturé, jours reportés | Employé | `JOURS_REPORTES` |
| Exercice clôturé, jours perdus | Employé | `JOURS_PERDUS` |
| Exercice clôturé, jours payés | Employé + RH | `SOLDE_AJUSTE` |
| Clôture déclenchée | RH | `EXERCICE_CLOTURE` |

---

## Automatisation (optionnel)

La clôture peut être déclenchée automatiquement le 1er janvier :

```java
@Scheduled(cron = "0 0 1 1 * *")   // 1er janvier à 00h00
cloturerAutomatiquement() :
  Pour chaque Entreprise active :
    cloturerAnnee(entreprise.id, annee - 1)
```

Le endpoint `POST /exercices/cloture-annuelle` reste disponible pour une clôture manuelle anticipée ou corrective.