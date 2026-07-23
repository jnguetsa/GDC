# GDC — Ordre d'implémentation

> Pour chaque ressource, l'ordre est toujours : DTO → Repository → Mapper → Service → Controller
> L'ordre entre ressources suit les dépendances JPA — on implémente d'abord ce qui est référencé par les autres.

---

## Ordre global

```
1. CatalogueTypeConge
2. TypeConge
3. JourFerie
4. ReglePlageConge
5. SoldeConge
6. ExerciceConge
7. DemandeConge
8. RetourConge  ←┐ dépendance croisée
9. PieceJointe  ─┘
10. Notification
11. HistoriqueAction
```

---

## 1. CatalogueTypeConge

**Dépendances :** aucune dans GDC

**Fichiers à créer :**
```
dto/catalogueTypeConge/
  ├── CatalogueTypeCongeRequest.java     ✅ fait
  └── CatalogueTypeCongeResponse.java    ✅ fait

repository/
  └── CatalogueTypeCongeRepository.java

mapper/
  └── CatalogueTypeCongeMapper.java

service/
  ├── ICatalogueTypeCongeService.java
  └── CatalogueTypeCongeService.java

controller/
  └── CatalogueTypeCongeController.java
```

**Endpoints :**
```
GET    /api/catalogue-types-conge
GET    /api/catalogue-types-conge/actifs
GET    /api/catalogue-types-conge/{id}
POST   /api/catalogue-types-conge
PUT    /api/catalogue-types-conge/{id}
PATCH  /api/catalogue-types-conge/{id}/activation
```

---

## 2. TypeConge

**Dépendances :** CatalogueTypeConge

**Fichiers à créer :**
```
dto/typeConge/
  ├── TypeCongeRequest.java
  └── TypeCongeResponse.java

repository/
  └── TypeCongeRepository.java

mapper/
  └── TypeCongeMapper.java

service/
  ├── ITypeCongeService.java
  └── TypeCongeService.java

controller/
  └── TypeCongeController.java
```

**Endpoints :**
```
GET    /api/types-conge
GET    /api/types-conge/actifs
GET    /api/types-conge/{id}
POST   /api/types-conge
PUT    /api/types-conge/{id}
PATCH  /api/types-conge/{id}/activation
POST   /api/types-conge/{id}/resync-catalogue
```

---

## 3. JourFerie

**Dépendances :** aucune dans GDC

**Fichiers à créer :**
```
dto/jourFerie/
  ├── JourFerieRequest.java
  └── JourFerieResponse.java

repository/
  └── JourFerieRepository.java

mapper/
  └── JourFerieMapper.java

service/
  ├── IJourFerieService.java
  └── JourFerieService.java

controller/
  └── JourFerieController.java
```

**Endpoints :**
```
GET    /api/jours-feries
GET    /api/jours-feries/{annee}
POST   /api/jours-feries
PUT    /api/jours-feries/{id}
DELETE /api/jours-feries/{id}
```

---

## 4. ReglePlageConge

**Dépendances :** TypeConge, Departement (GDO)

**Fichiers à créer :**
```
dto/reglePlageConge/
  ├── ReglePlageCongeRequest.java
  └── ReglePlageCongeResponse.java

repository/
  └── ReglePlageCongeRepository.java

mapper/
  └── ReglePlageCongeMapper.java

service/
  ├── IReglePlageCongeService.java
  └── ReglePlageCongeService.java

controller/
  └── ReglePlageCongeController.java
```

**Endpoints :**
```
GET    /api/regles-plage
GET    /api/regles-plage/type-conge/{typeCongeId}
POST   /api/regles-plage
PUT    /api/regles-plage/{id}
PATCH  /api/regles-plage/{id}/activation
DELETE /api/regles-plage/{id}
```

---

## 5. SoldeConge

**Dépendances :** TypeConge

**Fichiers à créer :**
```
dto/soldeConge/
  ├── SoldeCongeResponse.java
  └── AjustementSoldeRequest.java

repository/
  └── SoldeCongeRepository.java

mapper/
  └── SoldeCongeMapper.java

service/
  ├── ISoldeCongeService.java
  └── SoldeCongeService.java

controller/
  └── SoldeCongeController.java
```

**Endpoints :**
```
GET    /api/soldes
GET    /api/soldes/employe/{employeId}
GET    /api/soldes/employe/{employeId}/annee/{annee}
POST   /api/soldes/initialiser
PATCH  /api/soldes/{id}/ajustement
```

---

## 6. ExerciceConge

**Dépendances :** TypeConge, SoldeConge

**Fichiers à créer :**
```
dto/exerciceConge/
  ├── ExerciceCongeResponse.java
  ├── ClotureAnnuelleRequest.java
  ├── CloturePreviewResponse.java
  └── ExercicePreviewItem.java

repository/
  └── ExerciceCongeRepository.java

mapper/
  └── ExerciceCongeMapper.java

service/
  ├── IExerciceCongeService.java
  └── ExerciceCongeService.java

controller/
  └── ExerciceCongeController.java
```

**Endpoints :**
```
GET    /api/exercices
GET    /api/exercices/employe/{employeId}
GET    /api/exercices/{id}
GET    /api/exercices/entreprise/{annee}
GET    /api/exercices/cloture-annuelle/preview
POST   /api/exercices/cloture-annuelle
```

---

## 7. DemandeConge

**Dépendances :** TypeConge, ExerciceConge, SoldeConge, JourFerie, ReglePlageConge

**Fichiers à créer :**
```
dto/demandeConge/
  ├── DemandeCongeRequest.java
  ├── DemandeCongeResponse.java
  ├── ValidationManagerRequest.java
  ├── ValidationRhRequest.java
  └── AnnulationRequest.java

repository/
  └── DemandeCongeRepository.java

mapper/
  └── DemandeCongeMapper.java

service/
  ├── IDemandeCongeService.java
  └── DemandeCongeService.java

controller/
  └── DemandeCongeController.java
```

**Endpoints :**
```
GET    /api/demandes
GET    /api/demandes/{id}
GET    /api/demandes/equipe
GET    /api/demandes/entreprise
GET    /api/demandes/a-valider
GET    /api/demandes/a-valider-rh
POST   /api/demandes
PATCH  /api/demandes/{id}/annulation
PATCH  /api/demandes/{id}/validation-manager
PATCH  /api/demandes/{id}/validation-rh
```

---

## 8 & 9. RetourConge + PieceJointe

**Dépendance croisée** — écrire dans cet ordre :
1. `RetourCongeResponse` sans `List<PieceJointeResponse>`
2. Tout `PieceJointe`
3. Compléter `RetourCongeResponse` avec `List<PieceJointeResponse>`

**Dépendances :** DemandeConge (pour les deux)

**Fichiers à créer :**
```
dto/retourConge/
  ├── RetourCongeResponse.java
  ├── EnregistrerRetourRequest.java
  └── DecisionRetourRequest.java

dto/pieceJointe/
  └── PieceJointeResponse.java

repository/
  ├── RetourCongeRepository.java
  └── PieceJointeRepository.java

mapper/
  ├── RetourCongeMapper.java
  └── PieceJointeMapper.java

service/
  ├── IRetourCongeService.java
  ├── RetourCongeService.java
  ├── IPieceJointeService.java
  └── PieceJointeService.java

controller/
  ├── RetourCongeController.java
  └── PieceJointeController.java
```

**Endpoints RetourConge :**
```
GET    /api/retours
GET    /api/retours/{id}
GET    /api/retours/entreprise
GET    /api/retours/en-attente
PATCH  /api/retours/{id}
PATCH  /api/retours/{id}/decision
```

**Endpoints PieceJointe :**
```
POST   /api/demandes/{id}/pieces-jointes
POST   /api/retours/{id}/pieces-jointes
GET    /api/pieces-jointes/{id}/telecharger
DELETE /api/pieces-jointes/{id}
```

---

## 10. Notification

**Dépendances :** DemandeConge, RetourConge

**Fichiers à créer :**
```
dto/notification/
  └── NotificationResponse.java

repository/
  └── NotificationRepository.java

mapper/
  └── NotificationMapper.java

service/
  ├── INotificationService.java
  └── NotificationService.java    // creer() appelé en interne par les autres services

controller/
  └── NotificationController.java
```

**Endpoints :**
```
GET    /api/notifications
GET    /api/notifications/non-lues
GET    /api/notifications/count
PATCH  /api/notifications/{id}/lue
PATCH  /api/notifications/tout-lire
```

---

## 11. HistoriqueAction

**Dépendances :** aucune dans GDC (juste Employe)

**Fichiers à créer :**
```
dto/historiqueAction/
  └── HistoriqueActionResponse.java

repository/
  └── HistoriqueActionRepository.java

mapper/
  └── HistoriqueActionMapper.java

service/
  ├── IHistoriqueActionService.java
  └── HistoriqueActionService.java  // logger() appelé en interne par les autres services

controller/
  └── HistoriqueActionController.java
```

**Endpoints :**
```
GET    /api/historique
GET    /api/historique/employe/{employeId}
GET    /api/historique/demande/{demandeId}
GET    /api/historique/entreprise
```

---

## Récapitulatif des fichiers à créer

| Ressource | DTOs | Repo | Mapper | Service | Controller | Total |
|---|---|---|---|---|---|---|
| CatalogueTypeConge | 2 ✅ | 1 | 1 | 2 | 1 | 7 |
| TypeConge | 2 | 1 | 1 | 2 | 1 | 7 |
| JourFerie | 2 | 1 | 1 | 2 | 1 | 7 |
| ReglePlageConge | 2 | 1 | 1 | 2 | 1 | 7 |
| SoldeConge | 2 | 1 | 1 | 2 | 1 | 7 |
| ExerciceConge | 4 | 1 | 1 | 2 | 1 | 9 |
| DemandeConge | 5 | 1 | 1 | 2 | 1 | 10 |
| RetourConge | 3 | 1 | 1 | 2 | 1 | 8 |
| PieceJointe | 1 | 1 | 1 | 2 | 1 | 6 |
| Notification | 1 | 1 | 1 | 2 | 1 | 6 |
| HistoriqueAction | 1 | 1 | 1 | 2 | 1 | 6 |
| **Total** | **25** | **11** | **11** | **22** | **11** | **80** |
