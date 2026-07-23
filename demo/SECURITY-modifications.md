# Documentation des modifications — Couche Sécurité

---

## Problème initial

Trois problèmes combinés empêchaient Spring Security de fonctionner correctement :

| # | Problème | Symptôme |
|---|---|---|
| 1 | `roles` en `LAZY` sur `Utilisateur` | `LazyInitializationException` dans `getAuthorities()` hors session Hibernate |
| 2 | `permissions` en `LAZY` sur `Role` | Même erreur en cascade lors du chargement des permissions |
| 3 | `JwtAuthenticationFilter` ne construisait pas l'`Authentication` | Toutes les requêtes traitées comme anonymes malgré un token valide |

---

## Corrections

### 1. UtilisateurRepository — requête JOIN FETCH

**Avant** : `findByEmail` chargeait l'utilisateur sans ses rôles ni ses permissions. Hibernate tentait de les charger plus tard, hors session → exception.

**Après** : Nouvelle méthode `findByEmailWithRolesAndPermissions` avec `JOIN FETCH` :

```java
@Query("SELECT u FROM Utilisateur u 
        LEFT JOIN FETCH u.roles r 
        LEFT JOIN FETCH r.permissions 
        WHERE u.email = :email")
Optional<Utilisateur> findByEmailWithRolesAndPermissions(@Param("email") String email);
```

**Résultat** : `roles` ET `permissions` chargés en **une seule requête SQL** au moment de l'authentification. `getAuthorities()` peut s'exécuter sans session Hibernate active.

**Pourquoi `roles` reste LAZY et non EAGER ?**
Le `EAGER` forcerait le chargement des rôles à chaque accès à un `Utilisateur`, même pour les endpoints qui n'en ont pas besoin (ex: simple lecture de profil). Le `JOIN FETCH` ciblé ne s'exécute que dans le filtre JWT — là où c'est vraiment nécessaire.

---

### 2. JwtAuthenticationFilter — construction de l'Authentication

**Avant** :
```java
// L'utilisateur était chargé mais jamais mis dans le SecurityContext
Utilisateur utilisateur = utilisateurRepository.findByEmail(email).orElse(null);
// Rien d'autre → Spring Security traitait la requête comme anonyme
```

**Après** :
```java
// Charge avec roles + permissions en une seule requête
Utilisateur utilisateur = utilisateurRepository
        .findByEmailWithRolesAndPermissions(email)
        .orElse(null);

if (utilisateur != null && utilisateur.isEnabled()) {
    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            utilisateur,
            null,
            utilisateur.getAuthorities() // données déjà en mémoire — pas de lazy
    );
    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(auth);
}
```

**Résultat** : L'utilisateur authentifié est reconnu par Spring Security sur toute la durée de la requête. Les annotations `@PreAuthorize`, `@Secured` et les règles `SecurityConfig` fonctionnent correctement.

---

### 3. Role — corrections de cohérence

**Avant** :
```java
private Boolean active;                      // Object nullable
private LocalDate dateCreation;              // incohérent avec le reste
private LocalDate dateModification;          // incohérent avec le reste
private Set<Employe> employes;               // mappedBy incorrect
@ManyToMany(fetch = FetchType.EAGER)         // chargement systématique
private Set<Permission> permissions;
```

**Après** :
```java
private boolean actif = true;               // primitive, jamais null
private LocalDateTime dateCreation;         // cohérent avec toutes les entités
private LocalDateTime dateModification;     // cohérent avec toutes les entités
private Set<Utilisateur> utilisateurs;      // mappedBy = "roles" pointe sur Utilisateur
@ManyToMany(fetch = FetchType.LAZY)         // chargé uniquement via JOIN FETCH
private Set<Permission> permissions;
```

---

### 4. Permission — corrections de cohérence

**Avant** :
```java
private Boolean active;
private LocalDate dateCreation;
private LocalDate dateModification;
```

**Après** :
```java
private boolean actif = true;
private LocalDateTime dateCreation;
private LocalDateTime dateModification;
```

---

### 5. Utilisateur — EAGER → LAZY sur roles

**Avant** :
```java
@ManyToMany(fetch = FetchType.EAGER)
private Set<Role> roles;
```

**Après** :
```java
@ManyToMany(fetch = FetchType.LAZY)
private Set<Role> roles;
```

Le chargement se fait désormais uniquement via `findByEmailWithRolesAndPermissions` dans le filtre JWT.

---

## Flux d'authentification corrigé

```
Requête HTTP avec header "Authorization: Bearer <token>"
        │
        ▼
JwtAuthenticationFilter.doFilterInternal()
        │
        ├── Extrait l'email du token JWT
        │
        ├── findByEmailWithRolesAndPermissions(email)
        │       └── SELECT u, roles, permissions en 1 requête SQL
        │
        ├── Vérifie utilisateur.isEnabled()
        │
        ├── Crée UsernamePasswordAuthenticationToken
        │       └── getAuthorities() → permissions déjà en mémoire ✓
        │
        └── SecurityContextHolder.setAuthentication(auth)
                └── Spring Security reconnaît l'utilisateur pour cette requête
```

---

## Fichiers modifiés

| Fichier | Modification |
|---|---|
| `UtilisateurRepository.java` | Ajout `findByEmailWithRolesAndPermissions` avec `JOIN FETCH` |
| `JwtAuthenticationFilter.java` | Construction de l'`Authentication` + utilisation de la nouvelle requête |
| `Role.java` | `Boolean active` → `boolean actif`, `LocalDate` → `LocalDateTime`, `Set<Employe>` → `Set<Utilisateur>`, `EAGER` → `LAZY` |
| `Permission.java` | `Boolean active` → `boolean actif`, `LocalDate` → `LocalDateTime` |
| `Utilisateur.java` | `EAGER` → `LAZY` sur `roles` |
