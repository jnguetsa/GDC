# Documentation GDC — Entités et modifications

---

## Nouvelles entités

### CatalogueTypeConge

**Pourquoi ?**
Sans catalogue, chaque RH saisit librement le nom et la description de ses types de congé, produisant des données hétérogènes impossibles à exploiter globalement ("congé annuel", "Congé Annuel", "CA" seraient 3 entrées distinctes pour la même réalité).

**Solution**
Un catalogue système pré-rempli avec tous les types standard. Le RH sélectionne un type → `nom`, `description` et valeurs suggérées se pré-remplissent automatiquement. Il ne configure que les paramètres propres à son entreprise.

> Pour la documentation complète (attributs, exemples d'utilisation, flow de configuration initiale), voir `CatalogueTypeConge-TypeConge.md`.

**Relation avec TypeConge**
```
CatalogueTypeConge (1) ──── (N) TypeConge
   système global              par entreprise
```

---

### RetourConge

**Pourquoi ?**
Aucune trace du retour effectif de l'employé n'existait. Impossible de savoir si un employé est revenu à temps, en retard, ou avant la fin de son congé.

**Solution**
Une entité dédiée liée à chaque `DemandeConge`. Le RH saisit la `dateRetourEffective` → tout est calculé automatiquement via `@PreUpdate`.

**Relations**
```
DemandeConge (1) ──── (1) RetourConge
RetourConge  (1) ──── (N) PieceJointe
```

**Calcul automatique**
```
dateRetourEffective saisie
        │
        ├── ecart = 0  → A_L_HEURE
        ├── ecart > 0  → motif fourni ? EN_RETARD : ABSENT_INJUSTIFIE
        └── ecart < 0  → RETOUR_ANTICIPE, joursNonPrisRecuperables = |ecart|
```

**Champs clés**

| Champ | Rôle |
|---|---|
| `dateRetourPrevue` | dateFin + 1, fixée à l'approbation |
| `dateRetourEffective` | Saisie par le RH/manager |
| `joursEcart` | Positif = retard, négatif = anticipé, 0 = à l'heure |
| `joursNonPrisRecuperables` | Jours à recréditer si retour anticipé |
| `joursRecuperesSurSolde` | Décision RH : recréditer ou non |
| `motifEcart` | Justification de l'employé |
| `piecesJointes` | Documents justificatifs via `PieceJointe` |

---

### ExerciceConge

**Pourquoi ?**
`SoldeConge` donne le solde courant mais ne garde pas de trace historique. Impossible de répondre à "combien de jours avait Jean en 2023 ?".

**Solution**
À chaque clôture annuelle, un `ExerciceConge` est créé par combinaison employé/typeConge. Il archive l'état complet de l'année.

**Relations**
```
SoldeConge   (1) ──── (1) ExerciceConge
Employe      (1) ──── (N) ExerciceConge
DemandeConge (N) ──── (1) ExerciceConge
```

**Champs clés**

| Champ | Rôle |
|---|---|
| `soldeOuverture` | Jours attribués au 1er janvier |
| `soldeReporteEntrant` | Jours reportés depuis l'année précédente |
| `joursPris` | Total consommé dans l'année |
| `soldeFinExercice` | Solde restant au 31 décembre (calculé automatiquement) |
| `joursReportes` | Jours transférés vers l'année suivante (plafonné par maxJoursReport) |
| `joursExpires` | Jours perdus définitivement |
| `cloturePar` | RH qui a déclenché la clôture |
| `soldeConge` | Lien vers le SoldeConge source pour traçabilité |

---

---

## TypeConge — Documentation complète

> Entité créée par le RH d'une entreprise. Elle représente la configuration effective d'un type de congé pour une entreprise donnée, initialisée depuis le catalogue mais personnalisable.

---

### Attributs

---

#### `id`
Identifiant technique unique auto-généré. Référencé par `DemandeConge`, `SoldeConge`, `ReglePlageConge`.

**Exemple :** Le `TypeConge` CA de la société Acme a `id = 5`, celui de Beta a `id = 12` — deux instances distinctes du même type catalogue.

---

#### `catalogue`
Lien vers l'entrée `CatalogueTypeConge` depuis laquelle ce type a été créé (`@ManyToOne`, nullable).

Permet de retrouver l'origine du type, de déclencher une resync nom/description si le catalogue est mis à jour, et de grouper les types de même nature entre entreprises pour les statistiques globales.

**Exemple :** Le RH d'Acme sélectionne "Congé Annuel" → `catalogue.id = 1`. Pour un type personnalisé non couvert par le catalogue (ex : "Congé Ancienneté 10 ans"), `catalogue = null`.

---

#### `nom`
Libellé affiché aux employés et managers dans l'interface de l'entreprise. Pré-rempli depuis `catalogue.nom`, personnalisable ensuite. Contrainte unique sur `(nom, entreprise_id)` — deux entreprises différentes peuvent avoir le même nom, le doublon est interdit au sein d'une même entreprise.

**Exemple :** Pré-rempli à `"Congé Annuel"`, le RH d'Acme le renomme `"Congé Payé Acme"` pour coller au vocabulaire interne.

---

#### `code` — méthode dérivée

`code` n'est **pas** un champ stocké en base — c'est une méthode dérivée qui lit toujours depuis le catalogue :

```java
public CodeTypeConge getCode() {
    return catalogue != null ? catalogue.getCode() : CodeTypeConge.AUTRE;
}
```

Cela garantit que le code ne peut jamais diverger du catalogue. Pour un type personnalisé (`catalogue = null`), `AUTRE` est retourné automatiquement.

| Code | Signification | Règle automatique déclenchée |
|---|---|---|
| `CA` | Congé annuel | Décompte du solde principal |
| `CM` | Congé maladie | Justificatif médical obligatoire |
| `CSS` | Congé sans solde | Déduction du salaire, pas du solde |
| `CP` | Congé paternité | Vérification que l'employé est un homme |
| `CM2` | Congé maternité | Vérification que l'employée est une femme |
| `CE` | Congé exceptionnel | Deuil, mariage, naissance... |
| `CF` | Congé de formation | Peut être payé ou non |
| `AUTRE` | Type personnalisé | Aucune règle automatique |

---

#### `description`
Texte explicatif visible par l'employé lors de la création de sa demande. Pré-rempli depuis `catalogue.description`, le RH peut l'adapter pour ajouter des précisions internes (procédure, contact RH, délai de dépôt).

**Exemple :** `"Congé annuel payé. Chez Acme, toute demande doit être soumise au moins 15 jours à l'avance et validée par votre N+1."`.

---

#### `couleur`
Code couleur hexadécimal pour l'affichage dans le calendrier partagé. Choix purement visuel, propre à chaque entreprise.

| Type | Couleur | Rendu |
|---|---|---|
| Congé annuel | `#4CAF50` | Vert |
| Congé maladie | `#F44336` | Rouge |
| Congé maternité | `#E91E63` | Rose |
| Congé formation | `#2196F3` | Bleu |

---

#### `joursParDefaut`
Nombre de jours attribués automatiquement à chaque employé lors de l'initialisation du solde annuel. Utilisé par le batch de réinitialisation au 1er janvier pour créditer les `SoldeConge`.

**Exemple :**
- `CA` → `joursParDefaut = 25` : chaque employé reçoit 25 jours en début d'année
- `CP` → `joursParDefaut = 11` : le congé paternité donne droit à 11 jours
- `CM` → `joursParDefaut = null` : le congé maladie n'a pas de quota fixe

---

#### `sansSolde`
Si `true`, la demande ne décompte pas de jours sur le `SoldeConge` de l'employé — le salaire est réduit proportionnellement à la place.

| Valeur | Comportement |
|---|---|
| `false` | Congé payé, décompté du solde de jours (ex: CA) |
| `true` | Congé non payé, le salaire est réduit proportionnellement (ex: CSS) |

**Exemple :** Un employé prend 5 jours de CSS en juillet → son salaire de juillet est réduit de 5/22 jours ouvrables. Son `SoldeConge` CA reste intact.

---

#### `politiqueFinAnnee` + `maxJoursReport` + `tauxIndemnisation`

Ces trois champs contrôlent ce qu'il advient des jours non consommés au 31 décembre lors de la clôture de l'`ExerciceConge`.

---

**`NOUVEAU_DEPART`** — les jours restants sont perdus, l'employé repart à zéro.

```
Solde CA au 31 décembre : 8 jours restants
→ Au 1er janvier : solde remis à 25 jours (joursParDefaut)
→ Les 8 jours sont perdus définitivement (→ ExerciceConge.joursExpires)
```

Cas typique : entreprises qui veulent une comptabilité simple sans gestion de report.

---

**`REPORTER`** — les jours non pris sont transférés sur l'année suivante, plafonnés par `maxJoursReport`.

```
Solde CA au 31 décembre : 8 jours restants, maxJoursReport = 5
→ Au 1er janvier : 25 (nouveaux) + 5 (reportés) = 30 jours
→ Les 3 jours au-delà du plafond sont perdus (→ ExerciceConge.joursExpires)
```

| Situation au 31 déc | `maxJoursReport = 5` | Solde au 1er janv |
|---|---|---|
| 3 jours restants | 3 reportés (sous le plafond) | 25 + 3 = **28 jours** |
| 5 jours restants | 5 reportés (exactement le plafond) | 25 + 5 = **30 jours** |
| 12 jours restants | 5 reportés (plafonné), 7 expirés | 25 + 5 = **30 jours** |

Si `maxJoursReport = null` → aucun plafond, tous les jours sont reportés sans limite. À éviter — un employé peut accumuler des centaines de jours sur plusieurs années.

---

**`PAYER`** — les jours non pris sont indemnisés financièrement. `tauxIndemnisation` exprime le taux (décimal : `1.0` = 100%, `0.5` = 50% du salaire journalier).

```
Solde CA au 31 décembre : 8 jours restants, tauxIndemnisation = 1.0
→ Au 1er janvier : solde remis à 25 jours (nouveau départ)
→ 8 jours × salaire journalier × 1.0 → ligne de paie janvier
```

Cas typique : conventions collectives BTP, transport.

---

| `politiqueFinAnnee` | `maxJoursReport` | `tauxIndemnisation` | Résultat sur 8 jours restants |
|---|---|---|---|
| `NOUVEAU_DEPART` | ignoré | ignoré | 8 jours perdus |
| `REPORTER` | `5` | ignoré | 5 reportés, 3 perdus |
| `REPORTER` | `null` | ignoré | 8 reportés, aucun perdu |
| `PAYER` | ignoré | `1.0` | 8 × salaire journalier indemnisés |

---

#### `necessiteJustificatif`
Si `true`, l'employé doit joindre un document (`PieceJointe`) pour que la demande soit acceptée. Le service rejette automatiquement toute demande sans pièce jointe si ce flag est actif.

| Type | Justificatif attendu |
|---|---|
| `CM` (maladie) | Certificat médical |
| `CE` (mariage) | Acte de mariage |
| `CE` (deuil) | Acte de décès |
| `CA` (annuel) | Aucun — `necessiteJustificatif = false` |

---

#### `necessiteValidationRh`
Si `true`, le circuit d'approbation inclut une étape RH après celle du manager (3ème étape du workflow). Si `false`, la validation manager seule suffit.

**Exemple :** `necessiteValidationRh = true` pour CF — la formation engage un budget, le RH doit confirmer la disponibilité de fonds. `necessiteValidationRh = false` pour CA courant.

---

#### `necessiteAdresseConge`
Si `true`, l'employé doit indiquer son adresse de résidence pendant le congé. Utilisé pour les congés longs ou les postes sensibles où l'entreprise peut avoir besoin de joindre l'employé en urgence.

**Exemple :** `necessiteAdresseConge = true` pour CM2 (4 mois de congé maternité) → l'employée saisit son adresse, stockée dans `DemandeConge.adresseConge`.

---

#### `delaiMinimumJours` + `uniteDelai`

Délai de prévenance minimum entre la soumission de la demande et la date de début du congé. Empêche les demandes de dernière minute.

- `delaiMinimumJours` — durée du délai
- `uniteDelai` — `JOURS_CALENDAIRES` ou `JOURS_OUVRABLES` (les weekends comptent ou non)

**Exemple :** `delaiMinimumJours = 7`, `uniteDelai = JOURS_OUVRABLES`
- Demande soumise le **lundi 21 juillet** → début du congé au plus tôt le **lundi 28 juillet**
- Demande pour le **mercredi 23** → rejetée automatiquement

`delaiMinimumJours = 0` pour CM (urgence médicale, aucun préavis possible).

---

#### `ageMinimumEnfant` + `ageMaximumEnfant` + `uniteAge`

Fenêtre d'âge de l'enfant pendant laquelle ce type de congé peut être demandé. Vérifié à la soumission pour CP, CM2, congé parental. Si les deux champs sont `null`, aucune vérification d'âge (CA, CM, CF...).

- `uniteAge` — `MOIS` ou `ANS` pour une granularité fine sur les premiers mois de vie

| Type de congé | `ageMin` | `ageMax` | `uniteAge` | Signification |
|---|---|---|---|---|
| Congé paternité (CP) | 0 | 3 | MOIS | Enfant entre 0 et 3 mois |
| Congé maternité (CM2) | 0 | 6 | MOIS | Possible jusqu'aux 6 mois de l'enfant |
| Congé parental | 12 | 3 | ANS | Enfant entre 1 et 3 ans |
| Congé d'adoption | 0 | 6 | MOIS | Dans les 6 premiers mois après l'arrivée |

**Exemple :** `ageMaximumEnfant = 3, uniteAge = MOIS` pour CP → un employé dont l'enfant a 4 mois voit sa demande rejetée automatiquement.

---

#### `entreprise`
Lien vers l'`Entreprise` propriétaire (`@ManyToOne`, `nullable = false`). Isole les configurations entre entreprises — `findByEntrepriseId()` ne retourne que les types de l'entreprise demandée.

---

#### `reglesPlage`
Relation inverse vers les `ReglePlageConge` de ce type (`@OneToMany`). Définit les fenêtres mensuelles pendant lesquelles les employés peuvent poser ce congé, **par département**. Configuration entièrement optionnelle — absence de règle = aucune contrainte calendaire.

| Département | Plage définie | Comportement |
|---|---|---|
| Production | Juillet uniquement | Demande hors juillet → rejetée |
| Administratif | Aucune | Libre toute l'année |
| Tous | CM | Aucune (urgence médicale) |

Si `departements` est vide sur une règle → elle s'applique à tous les départements de l'entreprise.

---

#### `actif`
Désactive ce type de congé dans l'entreprise sans le supprimer. Les demandes passées restent consultables, aucune nouvelle demande ne peut être créée.

**Exemple :** Acme supprime son accord de formation. CF passe à `actif = false` — l'historique reste intact, le type disparaît du formulaire de dépôt.

---

#### `dateCreation` + `dateModification`
- `dateCreation` — remplie automatiquement par `@CreationTimestamp`, lecture seule.
- `dateModification` — mise à jour automatiquement par `@UpdateTimestamp` à chaque modification, lecture seule.

`dateModification` est essentiel pour les audits de paie : si un litige survient sur un solde, on peut établir que `joursParDefaut` est passé de 25 à 27 après telle demande.

---

---

## Entités modifiées

### TypeConge
- `@ManyToOne catalogue` : lien vers `CatalogueTypeConge` — nom/description/code auto-remplis à la sélection
- `@OneToMany reglesPlage` : relation inverse vers `ReglePlageConge` (manquait)
- `code` : supprimé comme champ, remplacé par méthode dérivée `getCode()` depuis le catalogue
- `reportable` : supprimé, remplacé par `PolitiqueFinAnnee politiqueFinAnnee` (NOUVEAU_DEPART / REPORTER / PAYER)
- `tauxIndemnisation` : ajouté (utilisé si `politiqueFinAnnee = PAYER`)
- `delaiMinimumJours` + `uniteDelai` : remplace l'ancien champ sans unité
- `ageMinimumEnfant` + `ageMaximumEnfant` + `uniteAge` : remplace l'ancien `ageMinimum` sans borne haute
- `dateModification` : ajouté (`@UpdateTimestamp`)
- Contrainte `unique` sur `nom` : remplacée par contrainte composite `(nom, entreprise_id)`

---

### DemandeConge
- `@ManyToOne exerciceConge` : lien vers l'exercice impacté par la demande
- `@OneToMany piecesJointes` : remplace l'ancien `String pieceJointe` (chemin texte)
- `@OneToOne retourConge` : relation inverse vers le retour associé
- `@PrePersist validerDates()` : validation automatique dateDebut < dateFin

**Suppression**
- `String pieceJointe` supprimé (remplacé par la vraie relation JPA)

---

### Entreprise
**Avant** : `Boolean active` — ne pouvait représenter que actif/inactif.
**Après** : `@Enumerated StatutEntreprise statut` — cohérence avec le reste du projet (ACTIVE, INACTIVE).

---

### PieceJointe
**Avant** : Liée uniquement à `DemandeConge`.
**Après** :
```
PieceJointe
  ├── demandeConge (nullable) — justificatif de demande
  └── retourConge  (nullable) — justificatif de retard / retour anticipé
```

---

### Notification
**Avant** : Liée uniquement à `DemandeConge`. `TypeNotification` = 4 valeurs visuelles (INFO, SUCCES, ALERTE, ERREUR).
**Après** :
- Ajout `@ManyToOne retourConge`
- `TypeNotification` : 16 valeurs métier

| Catégorie | Valeurs |
|---|---|
| Demande | DEMANDE_SOUMISE, APPROUVEE_MANAGER, APPROUVEE_RH, REJETEE, ANNULEE |
| Retour | RETOUR_PREVU_DEMAIN, EN_RETARD, ANTICIPE, ENREGISTRE |
| Solde | SOLDE_FAIBLE, SOLDE_EXPIRE, SOLDE_AJUSTE |
| Exercice | EXERCICE_CLOTURE, JOURS_REPORTES, JOURS_PERDUS |

---

### HistoriqueAction
**Avant** : `String action` libre — aucune cohérence garantie.
**Après** : `@Enumerated TypeAction` — 20 valeurs typées.

| Catégorie | Valeurs |
|---|---|
| Demande | CREATION, MODIFICATION, ANNULATION, APPROBATION_MANAGER, REJET_MANAGER, APPROBATION_RH, REJET_RH |
| Retour | ENREGISTREMENT, MODIFICATION |
| Solde | INITIALISATION, AJUSTEMENT, RECALCUL |
| Exercice | CREATION, CLOTURE |
| Type de congé | CREATION, MODIFICATION, DESACTIVATION |
| Auth | CONNEXION, DECONNEXION |

---

### SoldeConge
- `@OneToOne exerciceConge` : lien vers l'exercice clôturé correspondant pour traçabilité des chiffres.

---

### Role
**Avant** : `Boolean active`, `LocalDate dateCreation/dateModification`, `Set<Employe> employes`, `@ManyToMany EAGER`.
**Après** :
- `boolean actif` (primitive)
- `LocalDateTime dateCreation/dateModification` (cohérence avec le reste)
- `Set<Utilisateur> utilisateurs` (cohérence avec `mappedBy = "roles"` sur `Utilisateur`)
- `@ManyToMany LAZY` (évite le problème N+1)

---

### Permission
**Avant** : `Boolean active`, `LocalDate dateCreation/dateModification`.
**Après** :
- `boolean actif` (primitive)
- `LocalDateTime dateCreation/dateModification` (cohérence)

---

### Utilisateur
**Avant** : `@ManyToMany(fetch = FetchType.EAGER)` sur `roles`.
**Après** : `@ManyToMany(fetch = FetchType.LAZY)` — évite le chargement systématique de tous les rôles à chaque chargement d'un utilisateur.

---

### Employe
- `@OneToMany exercicesConge` : historique annuel des soldes
- `@OneToMany notifications` : toutes ses notifications
- `@OneToMany historique` : toutes ses actions auditées

---

### ReglePlageConge
**Avant** : `@ManyToMany Set<Employe>` — règles assignées nominativement, un nouvel embauché n'héritait d'aucune règle automatiquement.
**Après** : `@ManyToMany Set<Departement>` — règles assignées par département entier. Tout employé rattaché au département hérite automatiquement des règles.
- Table de jointure renommée : `regle_plage_employes` → `regle_plage_departements`
- Si `departements` vide → règle s'applique à tous les départements de l'entreprise

---

---

## Suppression

### GDU/entity/ReglePlageConge.java — SUPPRIMÉ
Doublon de `GDC/entity/ReglePlageConge.java`. La version GDU était incomplète (seulement `nom`, `description`, `jourPardefaut`) et n'avait aucune relation avec les autres entités. La version GDC est complète avec `@ManyToMany departements`, `@ElementCollection categories` et lien vers `TypeConge`.

---

---

## Nouveaux enums

| Enum | Valeurs | Utilisé dans |
|---|---|---|
| `PolitiqueFinAnnee` | NOUVEAU_DEPART, REPORTER, PAYER | `TypeConge.politiqueFinAnnee`, `CatalogueTypeConge.politiqueFinAnneeSuggere` |
| `StatutRetour` | EN_ATTENTE, A_L_HEURE, EN_RETARD, ABSENT_INJUSTIFIE, RETOUR_ANTICIPE | `RetourConge.statut` |
| `TypeNotification` | 16 valeurs métier | `Notification.type` |
| `TypeAction` | 20 valeurs | `HistoriqueAction.action` |
| `UniteTemps` | JOURS_CALENDAIRES, JOURS_OUVRABLES, MOIS, ANS | `TypeConge.uniteDelai/uniteAge` |

---

---

## Vue d'ensemble des relations GDC

```
CatalogueTypeConge ──(1:N)──► TypeConge ──(1:N)──► ReglePlageConge ──(N:M)──► Departement
                                  │
                                  └──(1:N)──► SoldeConge ──(1:1)──► ExerciceConge
                                                                          ▲
Employe ──────────────────────────────────────────────────────────────────┘
   │
   └──(1:N)──► DemandeConge ──(N:1)──► ExerciceConge
                    │
                    ├──(1:N)──► PieceJointe
                    ├──(1:N)──► Notification
                    └──(1:1)──► RetourConge ──(1:N)──► PieceJointe
```
