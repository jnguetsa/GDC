# Documentation : CatalogueTypeConge & TypeConge

---

## Relation entre les deux entités

```
CatalogueTypeConge (1) ──── (N) TypeConge
   système global              par entreprise
```

C'est un pattern **Template → Instance**.

- `CatalogueTypeConge` est le **moule global** : il standardise les noms, codes et valeurs suggérées à l'échelle du système.
- `TypeConge` est la **configuration concrète** qu'un RH crée pour son entreprise, en s'appuyant sur le catalogue comme point de départ.

**Flow UX :** Le RH sélectionne un type dans le catalogue → `nom`, `description`, `code` et valeurs suggérées se pré-remplissent → le RH ajuste selon la politique de son entreprise → il sauvegarde.

---

## CatalogueTypeConge

### Objectif principal

`CatalogueTypeConge` résout un problème de **cohérence dans un système multi-entreprises** : sans catalogue, chaque RH saisit librement le nom et la description de ses types de congé, ce qui produit des données hétérogènes impossibles à exploiter globalement ("congé annuel", "Congé Annuel", "CA", "Cong. annuel" seraient 4 entrées distinctes pour la même réalité).

Le catalogue est une **liste fermée de références officielles**, pré-chargée par l'administrateur système, qui contient les types de congé légaux et courants. Le RH ne saisit rien à la main — il choisit dans cette liste. L'application pré-remplit automatiquement le formulaire de création du `TypeConge` à partir de l'entrée sélectionnée. Le RH n'a plus qu'à ajuster les valeurs propres à son entreprise.

**En résumé :** le catalogue standardise le *quoi* (quel type de congé), l'entreprise configure le *comment* (combien de jours, quel délai, quel circuit de validation).

### Exemples d'utilisation

---

**1. Création d'un type de congé par un RH**

Le RH d'Acme ouvre le formulaire "Nouveau type de congé". L'interface charge la liste depuis `CatalogueTypeConge` (tous les enregistrements `actif = true`).

```
Liste affichée :
  ● Congé Annuel         (CA)
  ● Congé Maladie        (CM)
  ● Congé Sans Solde     (CSS)
  ● Congé Paternité      (CP)
  ● Congé Maternité      (CM2)
  ● Congé Exceptionnel   (CE)
  ● Congé de Formation   (CF)
```

Le RH sélectionne **"Congé Annuel"** → l'application crée un `TypeConge` pré-rempli :

```
nom         ← "Congé Annuel"              (depuis catalogue.nom)
description ← "Congé payé annuel légal…"  (depuis catalogue.description)
code        ← CA                           (depuis catalogue.code)
joursParDefaut      ← 21                  (depuis catalogue.joursParDefautSuggere)
reportable          ← true                (depuis catalogue.reportableSuggere)
maxJoursReport      ← 10                  (depuis catalogue.maxJoursReportSuggere)
necessiteJustificatif ← false             (depuis catalogue.necessiteJustificatifSuggere)
```

Le RH ajuste : `joursParDefaut = 25` (accord d'entreprise), `maxJoursReport = 5` (politique interne) → sauvegarde.

---

**2. Deux entreprises, même type de congé, configurations différentes**

Les entreprises Acme et Beta ont toutes deux un Congé Annuel. Elles partagent la même entrée catalogue (`id = 1`, `nom = "Congé Annuel"`) mais ont chacune leur propre `TypeConge` :

| Champ | Acme | Beta |
|---|---|---|
| `catalogue` | id=1 (Congé Annuel) | id=1 (Congé Annuel) |
| `joursParDefaut` | 25 | 20 |
| `maxJoursReport` | 5 | 0 (reportable=false) |
| `delaiMinimumJours` | 14 jours | 7 jours |
| `couleur` | `#4CAF50` | `#00BCD4` |

Le catalogue assure que les deux s'appellent bien "Congé Annuel" et ont le code `CA` — les règles automatiques du service fonctionnent de façon identique pour les deux.

---

**3. Désactivation d'un type obsolète**

Une loi supprime le "Congé de Solidarité". L'administrateur passe `actif = false` sur l'entrée catalogue correspondante.

- L'entrée disparaît de la liste de sélection des RH → aucune nouvelle entreprise ne peut créer ce type.
- Les `TypeConge` déjà créés par les entreprises qui l'utilisaient **ne sont pas supprimés** → les demandes historiques restent consultables.
- Si une entreprise veut désactiver ce type chez elle, elle passe son propre `TypeConge.actif = false`.

---

**4. Flow de configuration initiale d'une nouvelle entreprise**

Quand une nouvelle entreprise rejoint la plateforme, le RH doit configurer ses types de congé avant que les employés puissent déposer des demandes. Le catalogue sert de **checklist guidée** : le RH passe en revue chaque entrée active et décide, type par type, si son entreprise l'adopte et comment.

**Étape 1 — L'application présente le catalogue**

Le RH voit la liste de tous les `CatalogueTypeConge` actifs. Pour chaque entrée, l'interface affiche le nom, la description officielle et les valeurs suggérées — c'est le point de départ de sa réflexion.

**Étape 2 — Pour chaque type, le RH prend ses décisions**

Le catalogue suggère, l'entreprise décide. Voici comment Acme (PME, 80 salariés) configure ses types à l'ouverture :

| Type (catalogue) | Adopté ? | Décisions de politique Acme |
|---|---|---|
| Congé Annuel (CA) | ✅ | `joursParDefaut = 25` (accord d'entreprise > légal), `reportable = true`, `maxJoursReport = 5`, `delaiMinimumJours = 14 JOURS_CALENDAIRES` |
| Congé Maladie (CM) | ✅ | `joursParDefaut = null` (pas de quota), `necessiteJustificatif = true`, `necessiteValidationRh = false` (manager seul suffit) |
| Congé Sans Solde (CSS) | ✅ | `sansSolde = true`, `necessiteValidationRh = true` (impact paie → RH obligatoire), `delaiMinimumJours = 30 JOURS_CALENDAIRES` |
| Congé Paternité (CP) | ✅ | `joursParDefaut = 11` (légal France), `ageMaximumEnfant = 6, uniteAge = MOIS`, `necessiteJustificatif = true` (acte de naissance) |
| Congé Maternité (CM2) | ✅ | `joursParDefaut = 112` (16 semaines), `necessiteAdresseConge = true`, `necessiteValidationRh = true` |
| Congé Exceptionnel (CE) | ✅ | `necessiteJustificatif = true`, `delaiMinimumJours = 0` (événements imprévus), `joursParDefaut = null` (variable selon l'événement) |
| Congé de Formation (CF) | ❌ | Acme n'a pas de budget formation cette année → type non créé, ignoré pour l'instant |

Pour chaque type adopté, un `TypeConge` est créé, lié à `CatalogueTypeConge` via `catalogue_id` et à l'entreprise via `entreprise_id`.

**Étape 3 — Initialisation des soldes**

Une fois les `TypeConge` créés, un batch initialise un `SoldeConge` par combinaison `(Employe, TypeConge)` en utilisant `joursParDefaut` comme solde de départ. Jean, embauché le 1er mars chez Acme, reçoit automatiquement :

```
SoldeConge : Jean × CA  → solde = 25 jours
SoldeConge : Jean × CM  → solde = null (pas de quota fixe)
SoldeConge : Jean × CSS → solde = 0 (sans solde, pas de capital de jours)
SoldeConge : Jean × CP  → solde = 11 jours
...
```

**Étape 4 — Évolution dans le temps**

La configuration n'est pas figée. Acme peut à tout moment :
- **Activer CF** quand un budget formation est débloqué → crée un nouveau `TypeConge` depuis le catalogue.
- **Modifier `joursParDefaut = 27`** suite à un nouvel accord d'entreprise → le batch de janvier suivant crédite 27 jours à chaque employé.
- **Désactiver un type** (`actif = false`) sans perdre l'historique des demandes passées.
- **Ajouter un type personnalisé** avec `code = AUTRE` pour un avantage spécifique à Acme non couvert par le catalogue (ex : "Congé Ancienneté 10 ans"). Dans ce cas `catalogue` reste `null`.

---

**5. Statistiques inter-entreprises**

Le tableau de bord administrateur veut afficher "combien de jours de Congé Annuel ont été posés cette année, toutes entreprises confondues ?".

Grâce au catalogue, la requête est simple :
```sql
SELECT SUM(d.nombreJours)
FROM demande_conge d
JOIN types_conge t ON d.type_conge_id = t.id
JOIN catalogue_types_conge c ON t.catalogue_id = c.id
WHERE c.code = 'CA'
  AND YEAR(d.dateDebut) = 2025
```

Sans catalogue, il faudrait maintenir une liste fragile de noms possibles (`"Congé Annuel"`, `"CA"`, `"Congé payé"`, ...) ce qui rendrait la requête peu fiable.

---

### Attributs

---

#### `id`
- **Rôle :** Identifiant technique auto-généré en base.
- **Utilité :** Clé primaire référencée par `TypeConge.catalogue_id` lors de la sélection.
- **Exemple :** `id = 1` pour le Congé Annuel, `id = 2` pour le Congé Maladie.

---

#### `nom`
- **Rôle :** Libellé officiel et standardisé affiché dans la liste de sélection du RH.
- **Utilité :** Garantit que toutes les entreprises utilisent la même dénomination, évite les variations ("congé annuel", "Congé Annuel", "CA"). Contrainte `unique = true` — un seul libellé par type dans le catalogue.
- **Exemple :** `"Congé Annuel"`, `"Congé Maladie"`, `"Congé Paternité"`.

---

#### `description`
- **Rôle :** Texte explicatif affiché automatiquement quand le RH sélectionne ce type.
- **Utilité :** Guide le RH dans sa configuration sans qu'il ait à chercher la définition légale ou réglementaire du type de congé.
- **Exemple :** Pour `CM` → `"Congé accordé en cas d'incapacité de travail médicalement constatée. Nécessite un certificat médical sous 48h."`.

---

#### `code`
- **Rôle :** Code métier court et unique, tiré de l'enum `CodeTypeConge` (CA, CM, CSS, CP, CM2, CE, CF, AUTRE).
- **Utilité :** Permet au service d'appliquer des règles métier automatiques selon le type. Contrainte `unique = true` — un seul code par entrée catalogue.

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

#### `joursParDefautSuggere`
- **Rôle :** Nombre de jours habituel pour ce type de congé, proposé comme valeur de départ.
- **Utilité :** Évite que chaque RH parte de zéro. Reste modifiable selon la politique de l'entreprise.
- **Exemple :** `joursParDefautSuggere = 21` pour le Congé Annuel (3 semaines légales). Une entreprise peut ensuite le passer à 25 dans son `TypeConge`.

---

#### `reportableSuggere`
- **Rôle :** Indique si ce type de congé est habituellement reportable d'une année sur l'autre.
- **Utilité :** Pré-coche l'option dans le formulaire du RH. Certains types ne se reportent jamais (CM, CP) ; d'autres presque toujours (CA).
- **Exemple :** `reportableSuggere = true` pour CA (les jours non pris peuvent passer en N+1). `reportableSuggere = false` pour CM2 (le congé maternité ne se reporte pas).

---

#### `maxJoursReportSuggere`
- **Rôle :** Nombre maximum de jours reportables suggéré.
- **Utilité :** Plafonne le report pour éviter l'accumulation excessive de jours. Valeur de départ que le RH peut ajuster.
- **Exemple :** `maxJoursReportSuggere = 10` pour CA — au-delà de 10 jours, les jours non pris expirent au 31 décembre.

---

#### `necessiteJustificatifSuggere`
- **Rôle :** Indique si ce type nécessite habituellement un document justificatif.
- **Utilité :** Pré-configure l'exigence documentaire. Certains types l'imposent toujours (CM → certificat médical) ; d'autres jamais (CA).
- **Exemple :** `necessiteJustificatifSuggere = true` pour CM. `necessiteJustificatifSuggere = false` pour CA.

---

#### `necessiteValidationRhSuggere`
- **Rôle :** Indique si ce type nécessite habituellement une validation RH en plus de celle du manager.
- **Utilité :** Pré-configure le circuit d'approbation. Certains types sensibles (CSS, formation) nécessitent toujours le RH ; d'autres passent par le manager seul.
- **Exemple :** `necessiteValidationRhSuggere = true` pour CF (Congé Formation) car il engage un budget formation. `necessiteValidationRhSuggere = false` pour CA courant.

---

#### `sansSoldeSuggere`
- **Rôle :** Indique si ce type n'est pas décompté du solde de jours (mais du salaire).
- **Utilité :** Distingue les congés payés des congés non rémunérés dès la sélection dans le catalogue.
- **Exemple :** `sansSoldeSuggere = true` pour CSS (Congé Sans Solde) — l'employé n'a plus de jours disponibles mais veut s'absenter ; le salaire est réduit. `sansSoldeSuggere = false` pour CA.

---

#### `actif`
- **Rôle :** Masque un type du catalogue sans le supprimer physiquement.
- **Utilité :** Permet de retirer un type obsolète ou réglementairement supprimé de la liste de sélection des RH, tout en conservant l'historique des `TypeConge` qui y sont déjà liés.
- **Valeur par défaut :** `true`.
- **Exemple :** Un type de congé spécifique à une ancienne loi est désactivé (`actif = false`) — il n'apparaît plus dans le formulaire de création, mais les entreprises qui l'utilisaient gardent leur configuration intacte.

---

---

## TypeConge

> Entité créée par le RH d'une entreprise. Elle représente la configuration effective d'un type de congé pour une entreprise donnée, initialisée depuis le catalogue mais personnalisable.

### Attributs

---

#### `id`
- **Rôle :** Identifiant technique auto-généré.
- **Utilité :** Référencé par `DemandeConge`, `SoldeConge`, `ReglePlageConge`.
- **Exemple :** Le `TypeConge` "Congé Annuel" de la société Acme a `id = 5`, celui de la société Beta a `id = 12` — deux instances distinctes du même type catalogue.

---

#### `catalogue`
- **Rôle :** Lien vers l'entrée `CatalogueTypeConge` depuis laquelle ce type a été créé (`@ManyToOne`, nullable).
- **Utilité :** Permet de retrouver l'origine du type, de resynchroniser nom/description si le catalogue est mis à jour, et de grouper les types de même nature entre entreprises (ex : stats globales sur tous les CA).
- **Exemple :** Le RH d'Acme sélectionne "Congé Annuel" dans le catalogue → `catalogue.id = 1`. S'il crée un type personnalisé sans catalogue, le champ est `null`.

---

#### `nom`
- **Rôle :** Libellé affiché aux employés et managers dans l'interface de l'entreprise.
- **Utilité :** Pré-rempli depuis `catalogue.nom` mais personnalisable. Contrainte `unique = true` — **attention**, cette unicité est globale, pas par entreprise (voir point de vigilance en bas de page).
- **Exemple :** Pré-rempli à `"Congé Annuel"`, le RH d'Acme le renomme `"Congé Payé Acme"` pour coller au vocabulaire interne.

---

#### `code`
- **Rôle :** Code métier du type, tiré de `CodeTypeConge`. Valeur par défaut : `AUTRE`.
- **Utilité :** Utilisé par les services pour déclencher des règles automatiques (vérification de genre pour CP/CM2, blocage de déduction de solde pour CSS).
- **Exemple :** `code = CP` → le service vérifie que l'employé est de genre masculin avant de valider la demande. `code = AUTRE` → aucune règle automatique, traitement générique.

---

#### `description`
- **Rôle :** Texte explicatif visible par l'employé lors de la création de sa demande.
- **Utilité :** Pré-rempli depuis `catalogue.description`, le RH peut l'adapter pour ajouter des précisions internes (procédure interne, contact RH, délai de dépôt).
- **Exemple :** `"Congé annuel payé. Chez Acme, toute demande doit être soumise au moins 15 jours à l'avance et validée par votre N+1."`.

---

#### `couleur`
- **Rôle :** Code couleur hexadécimal ou nom CSS pour l'affichage dans le calendrier de l'entreprise.
- **Utilité :** Différencier visuellement les types de congé dans un planning partagé. Choix purement visuel, propre à chaque entreprise.
- **Exemple :** `couleur = "#4CAF50"` (vert) pour CA, `couleur = "#F44336"` (rouge) pour CM, `couleur = "#FF9800"` (orange) pour CE.

---

#### `joursParDefaut`
- **Rôle :** Nombre de jours attribués à chaque employé pour ce type de congé lors de l'initialisation du solde annuel.
- **Utilité :** Valeur effective (contrairement à `joursParDefautSuggere` dans le catalogue). Utilisée par le batch de réinitialisation annuelle pour créditer les `SoldeConge`.
- **Exemple :** `joursParDefaut = 25` chez Acme (CA). Au 1er janvier, chaque employé se voit créditer 25 jours sur son `SoldeConge` de type CA.

---

#### `sansSolde`
- **Rôle :** Si `true`, la demande ne décompte pas de jours sur le `SoldeConge` de l'employé.
- **Utilité :** Différencie un congé payé (décompte du solde) d'un congé non rémunéré (décompte du salaire, pas du solde). Le service de déduction vérifie ce flag avant toute opération sur `SoldeConge`.
- **Exemple :** `sansSolde = true` pour CSS — un employé sans solde disponible peut quand même déposer une demande, mais son salaire du mois sera réduit proportionnellement.

---

#### `politiqueFinAnnee` + `reportable` + `maxJoursReport`

Ces champs contrôlent ensemble ce qu'il advient des jours non consommés au 31 décembre lors de la clôture de l'`ExerciceConge`.

> **Champ manquant à implémenter :** `politiqueFinAnnee` (enum `PolitiqueFinAnnee`) — les deux booléens actuels (`reportable`) ne couvrent que 2 des 3 cas possibles. Un enum est nécessaire pour le 3ème.

**Les 3 politiques possibles**

---

**`NOUVEAU_DEPART`** — les jours restants sont perdus, l'employé repart à zéro.

```
Solde CA au 31 décembre : 8 jours restants
→ Au 1er janvier : solde remis à 25 jours (joursParDefaut)
→ Les 8 jours sont perdus définitivement (→ ExerciceConge.joursExpires)
```

Cas typique : entreprises qui ne souhaitent pas gérer de report et veulent une comptabilité simple.

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

Si `maxJoursReport = null` avec `REPORTER` → aucun plafond, tous les jours sont reportés sans limite. À éviter sans politique claire — un employé peut accumuler des centaines de jours sur plusieurs années.

---

**`PAYER`** — les jours non pris sont **indemnisés financièrement** plutôt que reportés ou perdus.

```
Solde CA au 31 décembre : 8 jours restants
→ Au 1er janvier : solde remis à 25 jours (nouveau départ)
→ Les 8 jours sont transmis à la paie : l'employé reçoit 8 × (salaire journalier) en supplément
```

Cas typique : certaines conventions collectives (BTP, transport) imposent le rachat des congés non pris. Le RH configure ce type avec `PAYER` — à la clôture, le service génère une ligne de paie au lieu d'un report.

---

**Récapitulatif des 3 politiques**

| `politiqueFinAnnee` | `maxJoursReport` | Jours restants au 31 déc | Résultat |
|---|---|---|---|
| `NOUVEAU_DEPART` | (ignoré) | 8 jours | Perdus → `joursExpires = 8` |
| `REPORTER` | `5` | 8 jours | 5 reportés, 3 perdus |
| `REPORTER` | `null` | 8 jours | 8 reportés, aucun perdu |
| `PAYER` | (ignoré) | 8 jours | Indemnisés → ligne de paie |

> **Note de migration :** `reportable` (boolean actuel) correspond à `NOUVEAU_DEPART` quand `false` et `REPORTER` quand `true`. Lors de l'ajout de `politiqueFinAnnee`, migrer : `reportable=false` → `NOUVEAU_DEPART`, `reportable=true` → `REPORTER`.

---

#### `necessiteJustificatif`
- **Rôle :** Si `true`, l'employé doit joindre un document à sa demande (`PieceJointe`).
- **Utilité :** Déclenche une validation dans le service : une demande sans pièce jointe est rejetée automatiquement si ce flag est actif.
- **Exemple :** `necessiteJustificatif = true` pour CM → l'employé doit uploader un certificat médical. Sans ce fichier, la demande est bloquée avant même d'arriver au manager.

---

#### `necessiteValidationRh`
- **Rôle :** Si `true`, le circuit d'approbation inclut une étape RH après validation du manager.
- **Utilité :** Active la 3ème étape du workflow (manager → **RH** → approuvé). Si `false`, la validation manager suffit.
- **Exemple :** `necessiteValidationRh = true` pour CF — la formation engage un budget, le RH doit confirmer la disponibilité de fonds. `necessiteValidationRh = false` pour CA — le manager seul décide.

---

#### `necessiteAdresseConge`
- **Rôle :** Si `true`, l'employé doit renseigner une adresse de résidence pendant le congé.
- **Utilité :** Obligatoire dans certains secteurs (sécurité, santé) ou pour certains types d'absences longues où l'employeur doit pouvoir joindre l'employé.
- **Exemple :** `necessiteAdresseConge = true` pour les congés longue durée (CM2, CSS > 1 mois). L'adresse est stockée dans `DemandeConge.adresseConge`.

---

#### `delaiMinimumJours` + `uniteDelai`

Représentent le **délai de prévenance** : nombre minimum de jours entre la soumission de la demande et la date de début du congé. Empêche les demandes de dernière minute qui désorganisent l'équipe.

- `delaiMinimumJours` — la durée du délai.
- `uniteDelai` — `JOURS_CALENDAIRES` ou `JOURS_OUVRABLES` (les weekends comptent ou non).

**Exemple :** `delaiMinimumJours = 7`, `uniteDelai = JOURS_OUVRABLES`
- Demande soumise le **lundi 21 juillet** → début du congé au plus tôt le **lundi 28 juillet**.
- Demande soumise le lundi 21 pour un congé débutant le **mercredi 23** → rejetée automatiquement.

`delaiMinimumJours = 0` pour CM (urgence médicale, aucun préavis possible).

---

#### `ageMinimumEnfant` + `ageMaximumEnfant` + `uniteAge`

Définissent la **fenêtre d'âge de l'enfant** pendant laquelle ce type de congé peut être demandé. Vérifié dans le service à la soumission, uniquement pour les types liés à un enfant (CP, CM2, congé parental). Si les deux champs sont `null`, aucune vérification d'âge n'est effectuée (ex : CA, CM, CF).

- La borne basse (`ageMinimumEnfant`) s'applique quand un congé n'est disponible qu'après un âge minimum (ex : congé parental disponible seulement après 12 mois).
- La borne haute (`ageMaximumEnfant`) limite le délai après la naissance ou l'adoption.
- `uniteAge` — `MOIS` ou `ANS` pour une granularité adaptée aux premiers mois de vie.

| Type de congé | `ageMinimumEnfant` | `ageMaximumEnfant` | `uniteAge` | Signification |
|---|---|---|---|---|
| Congé paternité (CP) | 0 | 3 | MOIS | Enfant entre 0 et 3 mois |
| Congé maternité (CM2) | 0 | 6 | MOIS | Possible jusqu'aux 6 mois de l'enfant |
| Congé parental | 12 | 3 | ANS | Enfant entre 1 et 3 ans |
| Congé d'adoption | 0 | 6 | MOIS | Dans les 6 premiers mois après l'arrivée |

**Exemple concret — Congé paternité :**
```
ageMinimumEnfant = 0, ageMaximumEnfant = 3, uniteAge = MOIS
```
Un employé dont l'enfant a 4 mois → demande **rejetée automatiquement**.

---

#### `entreprise`
- **Rôle :** Lien vers l'`Entreprise` propriétaire de cette configuration (`@ManyToOne`, `nullable = false`).
- **Utilité :** Isole les configurations entre entreprises. Une requête `findByEntrepriseId()` ne retourne que les types de l'entreprise demandée. Empêche une entreprise de voir ou modifier les types d'une autre.
- **Exemple :** Acme (id=1) et Beta (id=2) ont toutes deux un CA, mais avec des configurations différentes (`joursParDefaut = 25` vs `20`). Chaque `TypeConge` pointe vers sa propre `Entreprise`.

---

#### `reglesPlage`
- **Rôle :** Liste des règles de plages mensuelles associées à ce type (`@OneToMany`, relation inverse de `ReglePlageConge.typeConge`). Définit les fenêtres dans lesquelles les employés peuvent ou doivent poser ce type de congé.
- **Utilité :** Permet au RH de restreindre les demandes à certaines périodes du mois ou de l'année, par secteur d'activité. La configuration est **entièrement optionnelle** : si aucune `ReglePlageConge` n'est définie pour un `TypeConge`, les employés peuvent poser ce congé à n'importe quelle date (sous réserve du délai de préavis).
- **Configuration par secteur :** Les plages sont définissables par catégorie d'employé ou secteur. Le RH n'est pas obligé de définir une plage pour tous les secteurs — un secteur sans plage définie est libre de toute contrainte calendaire pour ce type.

**Exemples de plages configurées :**

| Secteur | TypeConge | Plage définie | Signification |
|---|---|---|---|
| Production | CA | Du 1er au 31 juillet uniquement | Les opérateurs ne peuvent poser leur CA qu'en juillet (fermeture usine) |
| Administratif | CA | Aucune plage | Les administratifs posent leur CA librement toute l'année |
| Commercial | CA | Juillet + première semaine de janvier | Deux fenêtres autorisées, hors périodes de forte activité |
| Tous secteurs | CM | Aucune plage | Congé maladie : pas de restriction calendaire possible |

**Exemple concret :** Acme a deux secteurs — Production et Administratif. Le RH crée une `ReglePlageConge` pour le secteur Production sur le `TypeConge` CA : "du 15 juillet au 15 août". Un opérateur qui tente de poser un CA du 1er au 5 juin voit sa demande rejetée automatiquement. Un employé administratif sans plage définie peut poser la même demande sans problème.

---

#### `actif`
- **Rôle :** Désactive ce type de congé dans l'entreprise sans le supprimer.
- **Utilité :** Empêche les nouvelles demandes sur ce type tout en conservant l'historique des demandes passées. Un type inactif n'apparaît pas dans le formulaire de dépôt de l'employé.
- **Exemple :** L'entreprise Acme supprime son accord de congé de formation. Le `TypeConge` CF est passé à `actif = false` — les anciennes demandes CF restent visibles dans l'historique, mais personne ne peut en créer de nouvelle.

---

#### `dateCreation`- **Rôle :** Date et heure de création du type de congé, automatiquement remplie par `@CreationTimestamp`. Lecture seule (`@Setter(AccessLevel.NONE)`).
- **Utilité :** Audit et traçabilité. Permet de savoir depuis quand ce type est disponible dans l'entreprise.
- **Exemple :** `dateCreation = 2024-01-15T09:30:00` — le RH d'Acme a configuré son CA le 15 janvier 2024 au matin.

---

#### `dateModification`
- **Rôle :** Date et heure de la dernière modification, automatiquement mise à jour par `@UpdateTimestamp`. Lecture seule (`@Setter(AccessLevel.NONE)`).
- **Utilité :** Traçabilité des évolutions de politique RH. Permet de savoir quand `joursParDefaut`, `politiqueFinAnnee` ou tout autre paramètre a changé — essentiel pour les audits de paie.
- **Exemple :** Le RH d'Acme passe `joursParDefaut` de 25 à 27 le 1er mars suite à un nouvel accord d'entreprise → `dateModification = 2025-03-01T14:22:00`. En cas de litige sur un solde, on peut établir que la politique a changé après telle demande.

---

## Modifications apportées au modèle

### `TypeConge` — changements

---

#### `code` — supprimé comme champ, remplacé par méthode dérivée

**Avant :** `code` était un champ indépendant sur `TypeConge`, ce qui permettait à un RH de sélectionner "Congé Paternité" dans le catalogue et de changer le code en `AUTRE` — les règles automatiques du service (vérification de genre pour CP/CM2) ne se déclenchaient plus silencieusement.

**Après :** `code` est une méthode dérivée qui lit toujours depuis le catalogue :
```java
public CodeTypeConge getCode() {
    return catalogue != null ? catalogue.getCode() : CodeTypeConge.AUTRE;
}
```
Le code ne peut plus diverger. Pour un type personnalisé sans catalogue (`catalogue = null`), `AUTRE` est retourné automatiquement.

---

#### `reportable` — supprimé, remplacé par `politiqueFinAnnee`

**Avant :** `boolean reportable` — ne pouvait exprimer que 2 états (perdre ou reporter).

**Après :** `PolitiqueFinAnnee politiqueFinAnnee` — enum à 3 valeurs, valeur par défaut `NOUVEAU_DEPART` :

| Valeur | Comportement en fin d'année |
|---|---|
| `NOUVEAU_DEPART` | Jours non pris perdus → `ExerciceConge.joursExpires` |
| `REPORTER` | Jours transférés vers N+1, plafonnés par `maxJoursReport` |
| `PAYER` | Jours indemnisés financièrement au taux `tauxIndemnisation` |

**Migration données existantes :**
```sql
UPDATE types_conge SET politique_fin_annee =
  CASE WHEN reportable = true THEN 'REPORTER' ELSE 'NOUVEAU_DEPART' END;
```

---

#### `tauxIndemnisation` — ajouté

Utilisé uniquement quand `politiqueFinAnnee = PAYER`. Exprime le taux d'indemnisation des jours non pris sous forme décimale.

- `1.0` → 100% du salaire journalier (indemnisation complète)
- `0.5` → 50% du salaire journalier
- `null` si `politiqueFinAnnee != PAYER`

**Exemple :** Convention collective BTP → `politiqueFinAnnee = PAYER`, `tauxIndemnisation = 1.0`. Jean a 8 jours restants au 31 déc, salaire journalier = 150€ → il reçoit 8 × 150€ × 1.0 = **1 200€** sur sa paie de janvier.

---

#### Unicité de `nom` — contrainte composite

**Avant :** `@Column(unique = true)` sur `nom` — unicité globale sur toute la table, deux entreprises ne pouvaient pas avoir un type avec le même nom.

**Après :** Contrainte composite `(nom, entreprise_id)` — le même nom est autorisé dans deux entreprises différentes, le doublon reste interdit au sein d'une même entreprise :
```java
@Table(name = "types_conge", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"nom", "entreprise_id"})
})
```

---

### `CatalogueTypeConge` — changements

#### `reportableSuggere` — supprimé, remplacé par `politiqueFinAnneeSuggere`

Cohérence avec le changement sur `TypeConge`. Le catalogue suggère maintenant directement une `PolitiqueFinAnnee` :

```
politiqueFinAnneeSuggere = NOUVEAU_DEPART  →  pré-coche "Nouveau départ" dans le formulaire RH
politiqueFinAnneeSuggere = REPORTER        →  pré-coche "Reporter" + active le champ maxJoursReport
politiqueFinAnneeSuggere = PAYER           →  pré-coche "Payer" + active le champ tauxIndemnisation
```

#### `tauxIndemnisationSuggere` — ajouté

Valeur suggérée pour `tauxIndemnisation`, utilisée uniquement si `politiqueFinAnneeSuggere = PAYER`. Même logique que les autres `*Suggere` — le RH peut l'ajuster.

---

### `ReglePlageConge` — changements

#### `employes` → `departements`

**Avant :** `@ManyToMany Set<Employe>` — les règles de plage étaient assignées nominativement à chaque employé. Un nouvel embauché dans un secteur n'héritait d'aucune règle automatiquement, le RH devait l'ajouter manuellement à chaque règle.

**Après :** `@ManyToMany Set<Departement>` — les règles sont assignées à des départements entiers. Tout employé rattaché à un département hérite automatiquement de ses règles de plage.

- Table de jointure renommée : `regle_plage_employes` → `regle_plage_departements`
- Si `departements` est vide → la règle s'applique à **tous les départements** de l'entreprise

**Exemple :** La règle "CA uniquement en juillet" est assignée au département Production (id=3). Tout nouvel embauché dans ce département est automatiquement soumis à cette contrainte sans intervention du RH.

---

### Nouvel enum `PolitiqueFinAnnee`

Créé dans `com.example.demo.common.enums` :

```java
public enum PolitiqueFinAnnee {
    NOUVEAU_DEPART,  // jours non pris perdus définitivement au 31 déc
    REPORTER,        // jours transférés vers N+1, plafonnés par maxJoursReport
    PAYER            // jours indemnisés financièrement (taux défini par tauxIndemnisation)
}
```

---

### Point 7 — Resync catalogue (à implémenter)

La resync `catalogue → TypeConge` sera exposée via `POST /types-conge/{id}/resync-catalogue` dans `TypeCongeService` quand la couche service GDC sera créée. Elle écrasera `nom` et `description` depuis `catalogue.nom` / `catalogue.description` uniquement si le RH le déclenche explicitement.

---

## Vue d'ensemble des responsabilités

```
CatalogueTypeConge               TypeConge
────────────────────────         ──────────────────────────────────────────
nom standardisé             →    nom personnalisable (affiché aux employés)
code métier unique (source) →    getCode() dérivé du catalogue (lecture seule)
description officielle      →    description adaptée à l'entreprise
politiqueFinAnneeSuggere    →    politiqueFinAnnee (NOUVEAU_DEPART / REPORTER / PAYER)
maxJoursReportSuggere       →    maxJoursReport (si REPORTER)
tauxIndemnisationSuggere    →    tauxIndemnisation (si PAYER)
autres *Suggere             →    valeurs effectives (décision du RH)
                                 + couleur, délai, âge, entreprise
                                 + reglesPlage → par Departement (optionnel)
```