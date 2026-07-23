# Annotations de validation Jakarta

## Règle par type

| Type Java | Annotation | Ce qu'elle vérifie |
|---|---|---|
| `Long`, `Integer`, `Boolean`, objet | `@NotNull` | que le champ n'est pas null |
| `String` | `@NotNull` | que la chaîne n'est pas null |
| `String` | `@NotEmpty` | non null + non vide (`""` rejeté, `" "` accepté) |
| `String` | `@NotBlank` | non null + non vide + non que des espaces |
| `int`, `long` (primitif) | aucune (ne peut pas être null) | — |

## Quand utiliser quoi

- **`@NotNull`** — champ obligatoire dont la valeur peut être n'importe quoi (id, booléen, date)
- **`@NotBlank`** — champ texte obligatoire saisi par un utilisateur (nom, email, description)
- **`@NotEmpty`** — liste ou chaîne qui doit contenir au moins un élément

## Bug fréquent

`@NotBlank` ne fonctionne que sur `String`. L'utiliser sur `Long` ou `Boolean` ne lève aucune erreur de compilation mais ne valide rien.

```java
// INCORRECT
@NotBlank
private Long id;

@NotBlank
private Boolean active;

// CORRECT
@NotNull(message = "L'ID est requis")
private Long id;

@NotNull(message = "L'état est requis")
private Boolean active;
```

## Exemple appliqué (ActiveRole / ActivePermission)

```java
@NotNull(message = "L'ID est requis")
private Long id;

@NotNull(message = "L'état est requis")
private Boolean active;
```