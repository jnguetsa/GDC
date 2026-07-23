# Verrouillage des Getters et Setters avec Lombok dans Spring Boot

## 1. Problème de départ

Par défaut, quand on annote une classe avec `@Getter` et `@Setter`, **tous les champs** deviennent accessibles et modifiables de l'extérieur :

```java
@Getter
@Setter
@Entity
public class Employe {
    private Long id;
    private String nom;
    private LocalDateTime dateCreation;
    private StatutEmploi statut;
}
```

Ce code génère automatiquement `getId()`, `setId()`, `getNom()`, `setNom()`, `setDateCreation()`... même pour des champs qui **ne devraient jamais être modifiés manuellement** (comme `id` ou `dateCreation`).

---

## 2. La solution : `AccessLevel`

Lombok fournit l'enum `AccessLevel` qui permet de contrôler précisément la visibilité de chaque getter/setter.

### Import nécessaire

```java
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
```

---

## 3. Les niveaux d'accès disponibles

| Niveau | Visibilité | Cas d'usage |
|---|---|---|
| `PUBLIC` | Tout le monde | Défaut — accès libre |
| `PROTECTED` | Classe + sous-classes | Héritage, méthodes internes |
| `PACKAGE` | Classes du même package | Accès limité au module |
| `PRIVATE` | La classe elle-même | Modification interne uniquement |
| `NONE` | Personne | Aucun getter/setter généré |

---

## 4. Utilisation champ par champ

On peut **écraser** le comportement global sur un champ précis :

```java
@Getter
@Setter
@Entity
public class Employe {

    @Id
    @Setter(AccessLevel.NONE)       // Hibernate gère l'id, on ne le modifie jamais
    private Long id;

    private String nom;             // getter + setter public (défaut)

    @Setter(AccessLevel.NONE)       // géré par @CreationTimestamp, jamais modifié manuellement
    private LocalDateTime dateCreation;

    @Setter(AccessLevel.PROTECTED)  // modifiable uniquement par une méthode interne ou sous-classe
    private StatutEmploi statut;

    @Getter(AccessLevel.PRIVATE)    // le mot de passe ne doit pas être lu de l'extérieur
    private String motDePasse;
}
```

---

## 5. Cas concrets dans un projet Spring Boot

### 5.1 Champs gérés par Hibernate

Les champs annotés `@CreationTimestamp` ou `@UpdateTimestamp` sont remplis automatiquement par Hibernate. Exposer leur setter est inutile et dangereux.

```java
@Setter(AccessLevel.NONE)
@CreationTimestamp
private LocalDateTime dateCreation;

@Setter(AccessLevel.NONE)
@UpdateTimestamp
private LocalDateTime dateModification;
```

### 5.2 Identifiant technique

L'`id` est généré par la base de données. Le modifier manuellement casserait la cohérence.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Setter(AccessLevel.NONE)
private Long id;
```

### 5.3 Statut avec règles métier

Le statut d'une entité suit souvent un workflow précis (ex : BROUILLON → EN_ATTENTE → VALIDE). On force son changement à passer par une méthode dédiée :

```java
@Setter(AccessLevel.NONE)
private StatutDemande statut;

// Méthode métier qui contrôle la transition
public void valider(Employe validateur) {
    if (this.statut != StatutDemande.EN_ATTENTE) {
        throw new IllegalStateException("Seule une demande en attente peut être validée.");
    }
    this.statut = StatutDemande.VALIDEE;
    this.valideParRh = validateur;
    this.dateValidationRh = LocalDateTime.now();
}
```

### 5.4 Champ sensible (mot de passe)

On veut hasher le mot de passe avant de l'utiliser, jamais l'exposer en clair :

```java
@Getter(AccessLevel.NONE)  // aucun getMotDePasse() généré
private String motDePasse;
```

---

## 6. Désactiver complètement getter ou setter avec `NONE`

`AccessLevel.NONE` supprime la génération du getter ou du setter, même si la classe entière a `@Getter`/`@Setter`.

```java
@Getter
@Setter
public class MaClasse {

    private String champNormal;         // getter + setter générés

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private String champBloque;         // aucun getter ni setter généré
}
```

---

## 7. Récapitulatif — Bonnes pratiques

| Champ | Recommandation |
|---|---|
| `id` | `@Setter(AccessLevel.NONE)` |
| `dateCreation` / `dateModification` | `@Setter(AccessLevel.NONE)` |
| `statut` avec workflow | `@Setter(AccessLevel.NONE)` + méthode métier |
| mot de passe / données sensibles | `@Getter(AccessLevel.NONE)` |
| champs calculés en lecture seule | `@Setter(AccessLevel.NONE)` |

---

## 8. Résumé

Verrouiller les getters et setters permet de :

1. **Protéger les invariants métier** : un statut ne change qu'en respectant le workflow.
2. **Éviter les bugs** : on ne peut pas modifier accidentellement un `id` ou une `dateCreation`.
3. **Sécuriser les données sensibles** : un mot de passe ne doit pas être lisible librement.
4. **Clarifier les intentions** : un champ sans setter indique clairement qu'il est en lecture seule.

> La règle d'or : **rendre accessible uniquement ce qui doit l'être**, pas plus.
