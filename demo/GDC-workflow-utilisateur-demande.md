# Workflow utilisateur — Déposer une demande de congé

> Ce document décrit chaque étape du point de vue de l'utilisateur (employé, manager, RH).
> Pour la logique technique derrière chaque étape, voir `GDC-workflow-phase2-demande.md`.

---

## Vue d'ensemble

```
Employé                    Manager                      RH
   │                          │                          │
   │ 1. Consulte ses soldes   │                          │
   │ 2. Choisit le type       │                          │
   │ 3. Saisit les dates      │                          │
   │ 4. Ajoute justificatif   │                          │
   │    (si requis)           │                          │
   │ 5. Soumet la demande     │                          │
   │          │               │                          │
   │          └──────────────►│ 6. Reçoit notification  │
   │                          │ 7. Consulte la demande  │
   │                          │ 8. Approuve ou rejette  │
   │                          │          │               │
   │◄─────────────────────────┘          │               │
   │ 9. Reçoit notification              │               │
   │    (si rejeté → fin)                │               │
   │                                     │               │
   │                          [Si necessiteValidationRh] │
   │                                     └──────────────►│
   │                                                     │ 10. Reçoit notification
   │                                                     │ 11. Consulte la demande
   │                                                     │ 12. Approuve ou rejette
   │◄────────────────────────────────────────────────────┘
   │ 13. Reçoit notification finale
```

---

## Étape 1 — Consulter ses soldes

Avant de déposer une demande, l'employé vérifie ses jours disponibles.

**Appel API :**
```
GET /api/soldes
Authorization: Bearer <token>
```

**Réponse :**
```json
[
  {
    "typeCongeId": 1,
    "nomTypeConge": "Congé Annuel",
    "codeTypeConge": "CA",
    "annee": 2025,
    "soldeInitial": 25.0,
    "soldeReporte": 5.0,
    "soldePris": 10.0,
    "soldeEnAttente": 3.0,
    "soldeRestant": 17.0
  },
  {
    "typeCongeId": 2,
    "nomTypeConge": "Congé Maladie",
    "codeTypeConge": "CM",
    "soldeRestant": null
  }
]
```

---

## Étape 2 — Consulter les types de congé disponibles

L'employé consulte les types actifs de son entreprise pour choisir lequel demander.

**Appel API :**
```
GET /api/types-conge/actifs
Authorization: Bearer <token>
```

**Réponse :** liste des types avec leurs contraintes (délai, justificatif requis, adresse requise...) — l'interface peut ainsi afficher dynamiquement les champs obligatoires avant la saisie.

---

## Étape 3 — Saisir la demande

L'employé remplit le formulaire et soumet.

**Appel API :**
```
POST /api/demandes
Authorization: Bearer <token>
Content-Type: application/json

{
  "typeCongeId": 1,
  "dateDebut": "2025-08-01",
  "dateFin": "2025-08-15",
  "demiJourneeDebut": false,
  "demiJourneeFin": false,
  "motif": "Vacances en famille",
  "adresseConge": "12 rue des Fleurs, Yaoundé",   // si necessiteAdresseConge
  "contactUrgence": "+237 699 000 000"
}
```

**Champs calculés automatiquement par le service (non à envoyer) :**
- `nombreJours` — calculé depuis dateDebut/dateFin en excluant weekends et jours fériés
- `statut` — initialisé à `EN_ATTENTE`
- `exerciceCongeId` — résolu automatiquement selon l'année en cours

**Réponse succès (201) :**
```json
{
  "id": 42,
  "typeCongeId": 1,
  "nomTypeConge": "Congé Annuel",
  "dateDebut": "2025-08-01",
  "dateFin": "2025-08-15",
  "nombreJours": 11,
  "statut": "EN_ATTENTE",
  "dateCreation": "2025-07-22T09:30:00"
}
```

**Erreurs possibles :**
| Code | Cause |
|---|---|
| 400 | dateDebut après dateFin |
| 400 | Délai de préavis non respecté |
| 400 | Hors plage mensuelle autorisée pour ce département |
| 400 | Chevauchement avec une demande existante |
| 400 | Adresse manquante (si necessiteAdresseConge) |
| 400 | Genre incompatible (CP/CM2) |
| 409 | Solde insuffisant |

---

## Étape 4 — Uploader un justificatif (si requis)

Si `typeConge.necessiteJustificatif = true`, l'employé doit joindre un document.
Cet upload se fait **après** la création de la demande, en utilisant l'`id` retourné.

**Appel API :**
```
POST /api/demandes/42/pieces-jointes
Authorization: Bearer <token>
Content-Type: multipart/form-data

fichier: <certificat_medical.pdf>
```

**Contraintes :**
- Taille max : 10 MB
- Formats acceptés : PDF, JPEG, PNG

**Réponse (201) :**
```json
{
  "id": 7,
  "nomFichier": "certificat_medical.pdf",
  "typeMime": "application/pdf",
  "tailleOctets": 245760,
  "dateUpload": "2025-07-22T09:31:00"
}
```

---

## Étape 5 — Suivre l'état de sa demande

L'employé peut consulter ses demandes à tout moment.

**Appel API :**
```
GET /api/demandes
Authorization: Bearer <token>
```

**Ou une demande précise :**
```
GET /api/demandes/42
Authorization: Bearer <token>
```

**Statuts possibles :**

| Statut | Signification |
|---|---|
| `EN_ATTENTE` | En attente de validation manager |
| `APPROUVEE_MANAGER` | Validée par le manager, en attente RH |
| `APPROUVEE` | Approuvée définitivement |
| `REJETEE` | Refusée (commentaire disponible) |
| `ANNULEE` | Annulée par l'employé |

---

## Étape 6 — Annuler sa demande (optionnel)

L'employé peut annuler uniquement si la demande est encore `EN_ATTENTE`.

**Appel API :**
```
PATCH /api/demandes/42/annulation
Authorization: Bearer <token>
Content-Type: application/json

{
  "motif": "Changement de planning"
}
```

---

## Étape 7 — Validation manager

Le manager reçoit une notification et consulte les demandes en attente de son équipe.

**Consulter les demandes à valider :**
```
GET /api/demandes/a-valider
Authorization: Bearer <token>
```

**Approuver :**
```
PATCH /api/demandes/42/validation-manager
Authorization: Bearer <token>
Content-Type: application/json

{
  "approuve": true,
  "commentaire": "Validé, bon courage"
}
```

**Rejeter :**
```
PATCH /api/demandes/42/validation-manager
Authorization: Bearer <token>
Content-Type: application/json

{
  "approuve": false,
  "commentaire": "Période trop chargée, merci de décaler en septembre"
}
```

> Si `typeConge.necessiteValidationRh = false` → la demande passe directement à `APPROUVEE` et l'employé est notifié.
> Si `typeConge.necessiteValidationRh = true` → la demande passe à `APPROUVEE_MANAGER` et le RH est notifié.

---

## Étape 8 — Validation RH (si necessiteValidationRh)

Le RH consulte les demandes approuvées par le manager qui attendent sa validation.

**Consulter les demandes à valider :**
```
GET /api/demandes/a-valider-rh
Authorization: Bearer <token>
```

**Approuver ou rejeter :**
```
PATCH /api/demandes/42/validation-rh
Authorization: Bearer <token>
Content-Type: application/json

{
  "approuve": true,
  "commentaire": "Budget formation disponible, validé"
}
```

---

## Étape 9 — Notifications reçues par l'employé

L'employé consulte ses notifications pour suivre l'évolution de sa demande.

**Appel API :**
```
GET /api/notifications/non-lues
Authorization: Bearer <token>
```

**Marquer comme lue :**
```
PATCH /api/notifications/5/lue
Authorization: Bearer <token>
```

**Notifications liées à une demande :**

| Notification | Déclencheur |
|---|---|
| `DEMANDE_SOUMISE` | Confirmation de soumission |
| `DEMANDE_APPROUVEE_RH` | Approbation finale (manager seul ou RH) |
| `DEMANDE_REJETEE` | Rejet manager ou RH (avec commentaire) |
| `DEMANDE_ANNULEE` | Confirmation d'annulation |
| `RETOUR_PREVU_DEMAIN` | Rappel la veille du retour prévu |

---

## Résumé des appels API dans l'ordre

```
1. GET  /api/soldes                              → vérifier le solde disponible
2. GET  /api/types-conge/actifs                  → choisir le type
3. POST /api/demandes                            → soumettre la demande
4. POST /api/demandes/{id}/pieces-jointes        → uploader justificatif (si requis)
5. GET  /api/demandes/{id}                       → suivre l'état
6. GET  /api/notifications/non-lues              → recevoir les mises à jour
```