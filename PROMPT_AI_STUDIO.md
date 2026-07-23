# Prompt — Prototype UI pour Google AI Studio

---

## Prompt à copier-coller dans Google AI Studio

---

Tu es un expert en design d'interfaces web modernes. Génère un prototype HTML/CSS/JS complet en **mode dark**, **épuré** et **ergonomique** pour une application RH de gestion des congés (GDC — Gestion Des Congés).

### Contraintes de design

- **Palette dark** : fond principal `#0f1117`, surface cards `#1a1d27`, accents bleu `#4f8ef7`, succès vert `#22c55e`, warning orange `#f59e0b`, danger rouge `#ef4444`, texte primaire `#e2e8f0`, texte secondaire `#64748b`
- **Typographie** : Inter ou Roboto (Google Fonts), taille base 14px
- **Radius** : 8–12px sur les cards et boutons
- **Animations** : transitions légères 200ms, hover subtil
- **Layout** : sidebar fixe à gauche (240px), header fixe en haut, zone contenu scrollable
- **No framework CSS** : tout en CSS vanilla + JS vanilla, 0 dépendance externe sauf Google Fonts

### Contenu à générer — 5 vues dans un seul fichier HTML avec navigation

Utilise une **Single Page** avec un menu latéral qui affiche/masque les sections. Chaque section simule une page avec des données mockées.

---

#### Vue 1 — Dashboard (accueil)

- Cartes KPI en haut : **Congés en attente**, **Congés approuvés ce mois**, **Employés en congé aujourd'hui**, **Solde moyen restant**
- Graphique en barres simple (JS canvas ou SVG) : congés par mois sur l'année
- Tableau "Demandes récentes" : colonnes Employé, Type, Du, Au, Statut (badge coloré), Actions
- Notifications récentes (panneau latéral droit)

---

#### Vue 2 — Demandes de congé

Table paginée avec filtres :
- Filtres : Statut (`EN_ATTENTE` / `APPROUVEE` / `REJETEE` / `ANNULEE`), Type de congé, Période
- Colonnes : Employé (avatar initiales + nom), Type, Début, Fin, Nb jours, Statut (badge), Actions (Voir / Approuver / Rejeter)
- Modal "Détail demande" avec toutes les infos : employé, type congé, dates, motif, pièces jointes, historique de validation (timeline), boutons Approuver/Rejeter avec champ commentaire
- Modal "Nouvelle demande" : formulaire avec les champs de `DemandeCongeRequest` (type congé select, dates, demi-journée toggle, motif, adresse, contact urgence)

---

#### Vue 3 — Employés

- Barre de recherche + filtre par département
- Grille de cartes employés : photo (initiales colorées), nom, poste, département, badge "Responsable" si applicable, solde restant
- Panel latéral "détail employé" qui s'ouvre au clic : toutes les infos `EmployeResponse`, ses soldes de congé par type, ses dernières demandes

---

#### Vue 4 — Soldes & Exercices

- Sélecteur année en haut
- Table des soldes par employé et par type de congé
- Colonnes : Employé, Type, Solde initial, Pris, En attente, Restant (barre de progression colorée), Expiré le
- Section "Exercice annuel" : rapport groupé par département avec totaux

---

#### Vue 5 — Configuration

Tabs horizontaux :
- **Types de congé** : liste avec couleur (dot), code, politique fin d'année, jours/défaut, toggle actif/inactif
- **Jours fériés** : calendrier annuel simple + liste avec toggle récurrent
- **Règles de plage** : cards par règle avec mois autorisés, catégories, départements concernés
- **Entreprise & Départements** : card entreprise + liste départements avec responsable et nb employés

---

### Données mockées à inclure

```js
const EMPLOYES = [
  { id: 1, matricule: "EMP001", nom: "Dupont", prenom: "Marie", poste: "Chef de projet", departement: "IT", responsable: true, photoProfil: null },
  { id: 2, matricule: "EMP002", nom: "Martin", prenom: "Paul", poste: "Développeur", departement: "IT", responsable: false, photoProfil: null },
  { id: 3, matricule: "EMP003", nom: "Bernard", prenom: "Sophie", poste: "RH Manager", departement: "RH", responsable: true, photoProfil: null },
  { id: 4, matricule: "EMP004", nom: "Leroy", prenom: "Thomas", poste: "Comptable", departement: "Finance", responsable: false, photoProfil: null },
  { id: 5, matricule: "EMP005", nom: "Moreau", prenom: "Emma", poste: "Stagiaire", departement: "IT", responsable: false, photoProfil: null }
]

const DEMANDES = [
  { id: 1, employe: EMPLOYES[0], type: "Congé Annuel", debut: "2026-08-01", fin: "2026-08-15", jours: 10, statut: "EN_ATTENTE", motif: "Vacances été" },
  { id: 2, employe: EMPLOYES[1], type: "Congé Maladie", debut: "2026-07-20", fin: "2026-07-22", jours: 3, statut: "APPROUVEE", motif: "Grippe" },
  { id: 3, employe: EMPLOYES[3], type: "Congé Annuel", debut: "2026-07-10", fin: "2026-07-12", jours: 2, statut: "REJETEE", motif: "Personnel" },
  { id: 4, employe: EMPLOYES[2], type: "Congé Exceptionnel", debut: "2026-07-25", fin: "2026-07-25", jours: 1, statut: "EN_ATTENTE", motif: "Mariage" }
]

const SOLDES = [
  { employe: EMPLOYES[0], type: "Congé Annuel", initial: 25, pris: 8, enAttente: 10, restant: 7, expiration: "2026-12-31" },
  { employe: EMPLOYES[1], type: "Congé Annuel", initial: 25, pris: 5, enAttente: 0, restant: 20, expiration: "2026-12-31" },
  { employe: EMPLOYES[2], type: "Congé Annuel", initial: 30, pris: 12, enAttente: 0, restant: 18, expiration: "2026-12-31" },
  { employe: EMPLOYES[3], type: "Congé Annuel", initial: 25, pris: 2, enAttente: 2, restant: 21, expiration: "2026-12-31" }
]
```

---

### Composants UI à réutiliser

- **Badge statut** : pill colorée selon statut (EN_ATTENTE=orange, APPROUVEE=vert, REJETEE=rouge, ANNULEE=gris)
- **Avatar** : cercle avec 2 initiales, couleur générée à partir du nom
- **Barre de progression** : rouge si restant < 20%, orange < 50%, vert sinon
- **Modal** : fond semi-transparent, card centrée, animation fade+scale, touche Escape ferme
- **Toast notification** : coin bas-droit, auto-disparition 3s
- **Sidebar** : liens avec icône SVG inline + label, item actif surligné, sous-menu collapse pour Configuration

---

### Résultat attendu

Un seul fichier `prototype.html` autonome, prêt à ouvrir dans un navigateur, sans installation. Le code doit être propre, commenté par section, et le rendu doit donner l'impression d'une vraie application professionnelle dark-mode.