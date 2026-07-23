# GDC — Phase 5 : Transversaux

> Notification et HistoriqueAction ne sont jamais créés directement par un endpoint client.
> Ils sont produits automatiquement par les services des phases 1 à 4.
> Les endpoints exposés ici sont uniquement en lecture (+ marquage "lu" pour les notifications).

---

## 12. Notification

### Rôle
Alerte in-app produite automatiquement à chaque transition d'état significative. Peut aussi déclencher un email (champ `envoyeeParEmail`). L'employé consulte ses notifications depuis l'interface — il ne les crée jamais.

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/notifications` | Notifications de l'employé connecté |
| `GET` | `/api/notifications/non-lues` | Uniquement les non lues |
| `GET` | `/api/notifications/count` | Nombre de notifications non lues |
| `PATCH` | `/api/notifications/{id}/lue` | Marquer une notification comme lue |
| `PATCH` | `/api/notifications/tout-lire` | Marquer toutes comme lues |

### DTOs

**Response (`NotificationResponse`)** :
```java
Long id
TypeNotification type
String titre
String message
String lienAction          // URL vers la ressource concernée
boolean lue
LocalDateTime dateLecture
Long demandeCongeId        // null si non lié
Long retourCongeId         // null si non lié
LocalDateTime dateCreation
```

### Logique service

```
getNotifications(employeId) :
  SELECT * FROM notifications WHERE destinataire_id = :employeId ORDER BY dateCreation DESC

getNonLues(employeId) :
  SELECT * FROM notifications WHERE destinataire_id = :employeId AND lue = false

countNonLues(employeId) :
  SELECT COUNT(*) FROM notifications WHERE destinataire_id = :employeId AND lue = false

marquerLue(id, employeId) :
  Vérifier destinataire = employeId
  notification.lue = true
  notification.dateLecture = now()

marquerToutLu(employeId) :
  UPDATE notifications SET lue = true, dateLecture = now()
  WHERE destinataire_id = :employeId AND lue = false
```

### Méthode interne de création (utilisée par tous les services)

```java
// NotificationService.creer() — appelée par les autres services, pas exposée en API
creer(destinataire, type, titre, message, demandeConge, retourConge, lienAction) :
  Créer Notification et sauvegarder
  Si typeNecessiteEmail(type) :
    envoyerEmail(destinataire.email, titre, message)
    notification.envoyeeParEmail = true
    notification.dateEnvoiEmail = now()
```

### Types nécessitant un email

```java
typeNecessiteEmail(type) :
  return type IN [
    DEMANDE_APPROUVEE_RH,
    DEMANDE_REJETEE,
    RETOUR_EN_RETARD,
    ABSENT_INJUSTIFIE,
    SOLDE_FAIBLE,
    EXERCICE_CLOTURE
  ]
```

---

## 13. HistoriqueAction

### Rôle
Audit trail immuable. Enregistre qui a fait quoi, sur quelle entité, depuis quelle IP. Jamais modifiable ni supprimable. Lecture seule depuis l'API.

### Endpoints

| Méthode | URL | Description |
|---|---|---|
| `GET` | `/api/historique` | Actions de l'employé connecté |
| `GET` | `/api/historique/employe/{employeId}` | Actions d'un employé (RH) |
| `GET` | `/api/historique/demande/{demandeId}` | Toutes les actions sur une demande |
| `GET` | `/api/historique/entreprise` | Toutes les actions de l'entreprise (RH) |

### DTOs

**Response (`HistoriqueActionResponse`)** :
```java
Long id
Long employeId
String nomEmploye, prenomEmploye
TypeAction action
String entiteType
Long entiteId
String details
String adresseIp
LocalDateTime dateAction
```

### Logique service

```
getByDemande(demandeId) :
  SELECT h FROM historique_actions h
  WHERE h.entite_type = 'DemandeConge'
  AND h.entite_id = :demandeId
  ORDER BY h.date_action ASC

getByEmploye(employeId) :
  SELECT h FROM historique_actions h
  WHERE h.employe_id = :employeId
  ORDER BY h.date_action DESC
```

### Méthode interne de création (utilisée par tous les services)

```java
// HistoriqueService.logger() — appelée par les autres services, pas exposée en API
logger(employe, action, entiteType, entiteId, details, adresseIp, userAgent) :
  Créer HistoriqueAction et sauvegarder
```

---

## Tableau récapitulatif — qui crée quoi

| Action métier | Notification créée | HistoriqueAction créée |
|---|---|---|
| Demande soumise | DEMANDE_SOUMISE → manager | DEMANDE_CREATION |
| Approuvée manager (sans RH) | DEMANDE_APPROUVEE_RH → employé | DEMANDE_APPROBATION_MANAGER |
| Approuvée manager (avec RH) | APPROUVEE_MANAGER → RH | DEMANDE_APPROBATION_MANAGER |
| Approuvée RH | DEMANDE_APPROUVEE_RH → employé | DEMANDE_APPROBATION_RH |
| Rejetée manager | DEMANDE_REJETEE → employé | DEMANDE_REJET_MANAGER |
| Rejetée RH | DEMANDE_REJETEE → employé | DEMANDE_REJET_RH |
| Annulée employé | DEMANDE_ANNULEE → manager | DEMANDE_ANNULATION |
| Retour enregistré | RETOUR_* → employé | RETOUR_ENREGISTREMENT |
| Décision retour | — | RETOUR_MODIFICATION |
| Solde ajusté | SOLDE_AJUSTE → employé | SOLDE_AJUSTEMENT |
| Clôture annuelle | JOURS_REPORTES/PERDUS/SOLDE_AJUSTE → employé | EXERCICE_CLOTURE |
| Connexion | — | CONNEXION |
| Déconnexion | — | DECONNEXION |

---

## SoldeConge — alerte solde faible (optionnel)

Un scheduler peut notifier l'employé quand son solde devient faible :

```java
@Scheduled(cron = "0 0 8 * * MON")   // chaque lundi à 8h
verifierSoldesFaibles() :
  SELECT s FROM soldes_conge s
  WHERE s.annee = anneeEnCours
  AND s.soldeRestant <= 3
  AND s.estExpire = false
  Pour chaque solde :
    Si pas de notification SOLDE_FAIBLE envoyée dans les 7 derniers jours :
      Notification(SOLDE_FAIBLE, destinataire = solde.employe)
```