# Tests API - Employer & Role

Base URL : `http://localhost:8080`

---

## Employer

### 1. Créer un employé
**POST** `/api/employes`

```json
{
  "id": 1,
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@example.com",
  "telephone": "0612345678",
  "adresse": "12 rue de la Paix, Paris",
  "dateEmbauche": "2024-01-15",
  "dateDebut": "2024-01-15",
  "dateFin": "2025-01-15"
}
```
Réponse attendue : `201 Created`

---

### 2. Modifier un employé
**PUT** `/api/employes/1`

```json
{
  "id": 1,
  "nom": "Dupont",
  "prenom": "Pierre",
  "email": "pierre.dupont@example.com",
  "telephone": "0698765432",
  "adresse": "5 avenue Victor Hugo, Lyon",
  "dateEmbauche": "2024-01-15",
  "dateDebut": "2024-01-15",
  "dateFin": "2026-01-15"
}
```
Réponse attendue : `200 OK`

---

### 3. Lister tous les employés
**GET** `/api/employes`

Réponse attendue : `200 OK`

---

### 4. Ajouter un rôle à un employé
**POST** `/api/employes/1/roles/1`

> Remplacer `1` par l'id de l'employé et `1` par l'id du rôle

Réponse attendue : `200 OK`

---

## Role

### 5. Lister tous les rôles
**GET** `/api/roles`

Réponse attendue : `200 OK`

---

### 6. Lister les rôles actifs
**GET** `/api/roles/active`

Réponse attendue : `200 OK`

---

## Cas d'erreur

### Employé introuvable
**PUT** `/api/employes/999`
```json
{
  "id": 999,
  "nom": "Test",
  "prenom": "Test",
  "email": "test@test.com",
  "telephone": "0600000000",
  "adresse": "adresse test",
  "dateEmbauche": "2024-01-01",
  "dateDebut": "2024-01-01",
  "dateFin": "2025-01-01"
}
```
Réponse attendue : `404 Not Found` ou `500` (selon gestion des exceptions)

---

### Email déjà existant
**POST** `/api/employes` avec le même email qu'un employé existant

Réponse attendue : `500` (selon gestion des exceptions)
