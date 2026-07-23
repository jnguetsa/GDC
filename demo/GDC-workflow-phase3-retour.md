# GDC — Phase 3 : Suivi post-congé

> Couvre le retour effectif de l'employé et la gestion des pièces jointes.
> Dépend de la Phase 2 — une `RetourConge` est créée automatiquement lors de l'approbation finale d'une demande.

---

## Cycle de vie du retour

```
Demande APPROUVEE
      │
      ↓ (créé automatiquement à l'approbation finale)
RetourConge(statut = EN_ATTENTE, dateRetourPrevue = dateFin + 1)
      │
      │ [RH/Manager saisit dateRetourEffective]
      │ PATCH /retours/{id}
      ↓
@PreUpdate calculerEcartEtStatut() :
      │
      ├── ecart = 0  → A_L_HEURE
      ├── ecart > 0, motif fourni  → EN_RETARD
      ├── ecart > 0, sans motif    → ABSENT_INJUSTIFIE
      └── ecart < 0  → RETOUR_ANTICIPE
                              │
                              ↓ [Decision RH]
                    PATCH /retours/{id}/decision
                              │
                    joursRecuperesSurSolde = true/false
                    Si true → SoldeConge.soldePris -= joursNonPrisRecuperables
```

---

## 9. RetourConge

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/retours` | Retours de l'employé connecté |
| `GET` | `/api/retours/{id}` | Détail d'un retour |
| `GET` | `/api/retours/entreprise` | Tous les retours (RH) |
| `GET` | `/api/retours/en-attente` | Retours EN_ATTENTE à traiter (RH/manager) |
| `PATCH` | `/api/retours/{id}` | Enregistrer le retour effectif |
| `PATCH` | `/api/retours/{id}/decision` | Décision RH sur les jours récupérables |

### DTOs

**Request enregistrement (`EnregistrerRetourRequest`)** :
```java
@NotNull LocalDate dateRetourEffective
String motifEcart                     // obligatoire si dateRetourEffective > dateRetourPrevue
String commentaire
```

**Request décision RH (`DecisionRetourRequest`)** :
```java
@NotNull Boolean joursRecuperesSurSolde
@NotBlank String commentaireDecisionRh
```

**Response (`RetourCongeResponse`)** :
```java
Long id
Long demandeCongeId
Long employeId
String nomEmploye, prenomEmploye
LocalDate dateRetourPrevue
LocalDate dateRetourEffective
Integer joursEcart
Integer joursNonPrisRecuperables
StatutRetour statut
String motifEcart
Boolean joursRecuperesSurSolde
String commentaireDecisionRh
String commentaire
String nomEnregistrePar
LocalDateTime dateEnregistrement
List<PieceJointeResponse> piecesJointes
LocalDateTime dateCreation
LocalDateTime dateModification
```

### Validations

```
enregistrerRetour(id, request) :
  1. Vérifier statut = EN_ATTENTE
  2. Si dateRetourEffective > dateRetourPrevue ET motifEcart vide → ABSENT_INJUSTIFIE (pas de blocage, juste le statut)
  3. dateRetourEffective ne peut pas être dans le futur

decisionRetour(id, request) :
  1. Vérifier statut IN (EN_RETARD, ABSENT_INJUSTIFIE, RETOUR_ANTICIPE)
  2. Si statut = RETOUR_ANTICIPE → joursRecuperesSurSolde est pertinent
  3. Si statut = EN_RETARD / ABSENT_INJUSTIFIE → joursRecuperesSurSolde ignoré (pas de jours à récupérer)
```

### Logique service

```
enregistrerRetour(id, request, rhId, adresseIp) :
  1. Charger RetourConge
  2. retour.dateRetourEffective = request.dateRetourEffective
  3. retour.motifEcart = request.motifEcart
  4. retour.commentaire = request.commentaire
  5. retour.enregistrePar = rh
  6. retour.dateEnregistrement = now()
  7. Sauvegarder → @PreUpdate calcule joursEcart + statut automatiquement
  8. Notifier employé selon statut :
       EN_RETARD          → Notification(RETOUR_EN_RETARD)
       ABSENT_INJUSTIFIE  → Notification(RETOUR_EN_RETARD)
       RETOUR_ANTICIPE    → Notification(RETOUR_ANTICIPE)
       A_L_HEURE          → Notification(RETOUR_ENREGISTRE)
  9. Logger HistoriqueAction(RETOUR_ENREGISTREMENT, adresseIp)

decisionRetour(id, request, rhId) :
  1. Charger RetourConge
  2. retour.joursRecuperesSurSolde = request.joursRecuperesSurSolde
  3. retour.commentaireDecisionRh = request.commentaireDecisionRh
  4. Si joursRecuperesSurSolde = true ET statut = RETOUR_ANTICIPE :
       SoldeConge.soldePris -= retour.joursNonPrisRecuperables
       // @PreUpdate recalcule soldeRestant
  5. Logger HistoriqueAction(RETOUR_MODIFICATION)
```

---

## 10. PieceJointe

### Rôle
Upload de fichiers justificatifs liés soit à une `DemandeConge` (certificat médical, acte de mariage...) soit à un `RetourConge` (justificatif de retard ou retour anticipé). Stockage physique sur disque (`app.upload.dir=uploads/pieces-jointes`).

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `POST` | `/api/demandes/{id}/pieces-jointes` | Uploader un justificatif pour une demande |
| `POST` | `/api/retours/{id}/pieces-jointes` | Uploader un justificatif pour un retour |
| `GET` | `/api/pieces-jointes/{id}/telecharger` | Télécharger un fichier |
| `DELETE` | `/api/pieces-jointes/{id}` | Supprimer un fichier |

### DTOs

**Response (`PieceJointeResponse`)** :
```java
Long id
String nomFichier
String typeMime
Long tailleOctets
LocalDateTime dateUpload
```

### Validations

```
upload(file) :
  1. Taille max : 10 MB (configuré dans application.properties)
  2. Types MIME autorisés : image/jpeg, image/png, application/pdf
  3. Nom de fichier sanitisé (pas de path traversal)
  4. Si demandeConge.typeConge.necessiteJustificatif → flag de la demande comme "justifié"
```

### Logique service

```
uploader(file, demandeCongeId ou retourCongeId) :
  1. Valider taille + MIME
  2. Générer nom unique : UUID + extension originale
  3. Sauvegarder sur disque → uploads/pieces-jointes/{entrepriseId}/{annee}/{nomUnique}
  4. Créer PieceJointe(nomFichier, cheminFichier, typeMime, tailleOctets)
  5. Lier à la demande ou au retour

telecharger(id) :
  1. Charger PieceJointe
  2. Lire le fichier depuis cheminFichier
  3. Retourner Resource avec Content-Type et Content-Disposition: attachment

supprimer(id) :
  1. Charger PieceJointe
  2. Vérifier droits (propriétaire ou RH)
  3. Supprimer le fichier physique
  4. Supprimer l'entité
```

---

## Notifications déclenchées en Phase 3

| Événement | Destinataire | Type |
|---|---|---|
| Retour en retard enregistré | Employé | `RETOUR_EN_RETARD` |
| Absence injustifiée | Employé | `RETOUR_EN_RETARD` |
| Retour anticipé enregistré | Employé | `RETOUR_ANTICIPE` |
| Retour à l'heure enregistré | Employé | `RETOUR_ENREGISTRE` |

---

## Rappel automatique (optionnel)

Un scheduler peut envoyer un rappel la veille du retour prévu :

```java
@Scheduled(cron = "0 8 * * * *")   // chaque jour à 8h
verifierRetoursPrevu() :
  SELECT r FROM retours_conge r
  WHERE r.dateRetourPrevue = demain
  AND r.statut = EN_ATTENTE
  Pour chaque retour :
    Notifier employé → Notification(RETOUR_PREVU_DEMAIN)
```