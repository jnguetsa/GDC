# JWT — Comment ça fonctionne pour l'authentification

## Qu'est-ce qu'un JWT ?

Un **JSON Web Token (JWT)** est un jeton signé numériquement qui permet de transmettre des informations de façon sécurisée entre deux parties. Il est utilisé pour **prouver l'identité d'un utilisateur** sans avoir à stocker de session côté serveur.

---

## Structure d'un JWT

Un JWT est une chaîne de caractères en 3 parties séparées par des points :

```
xxxxx.yyyyy.zzzzz
  │       │       │
Header  Payload  Signature
```

| Partie | Contenu | Exemple |
|---|---|---|
| **Header** | Algorithme de signature + type | `{ "alg": "HS256", "typ": "JWT" }` |
| **Payload** | Les claims (données utiles) | `{ "sub": "user@mail.com", "roles": ["ADMIN"] }` |
| **Signature** | HMAC(header + payload, secret) | Garantit l'intégrité du token |

Chaque partie est encodée en **Base64URL** (pas chiffrée — le payload est lisible, mais non modifiable sans invalider la signature).

---

## Flux d'authentification avec JWT

```
┌─────────┐                          ┌─────────────┐
│ Client  │                          │   Serveur   │
└────┬────┘                          └──────┬──────┘
     │                                      │
     │  1. POST /login {email, password}    │
     │─────────────────────────────────────>│
     │                                      │  Vérifie les credentials
     │                                      │  Génère le JWT (genererToken)
     │  2. 200 OK { token: "xxx.yyy.zzz" }  │
     │<─────────────────────────────────────│
     │                                      │
     │  3. GET /api/ressource               │
     │     Authorization: Bearer xxx.yyy.zzz│
     │─────────────────────────────────────>│
     │                                      │  Vérifie la signature
     │                                      │  Extrait l'email & les rôles
     │                                      │  Autorise ou refuse
     │  4. 200 OK { data... }               │
     │<─────────────────────────────────────│
```

### Étape par étape

1. **Login** : le client envoie email + mot de passe.
2. **Génération** : le serveur vérifie les credentials, puis appelle `genererToken()` pour créer un JWT signé contenant l'id, l'email et les rôles de l'utilisateur.
3. **Stockage côté client** : le client conserve le token (localStorage, cookie, mémoire).
4. **Requêtes protégées** : à chaque requête suivante, le client envoie le token dans le header `Authorization: Bearer <token>`.
5. **Vérification** : le serveur appelle `extraireTousClaims()` pour vérifier la signature et lire les données. Si le token est valide et non expiré, la requête est autorisée.

---

## Ce que contient le JWT dans ce projet

```json
{
  "sub": "user@example.com",
  "id": 42,
  "roles": ["ROLE_ADMIN", "ROLE_USER"],
  "iat": 1720000000,
  "exp": 1720086400
}
```

| Claim | Signification |
|---|---|
| `sub` | Email de l'utilisateur (subject) |
| `id` | Identifiant en base |
| `roles` | Liste des rôles |
| `iat` | Date d'émission (issued at) |
| `exp` | Date d'expiration |

---

## Pourquoi JWT et pas une session classique ?

| Session classique | JWT |
|---|---|
| Session stockée côté serveur | Aucune session serveur (stateless) |
| ID de session dans un cookie | Token envoyé dans le header |
| Difficile à scaler horizontalement | Scale facilement (n'importe quel serveur peut vérifier) |
| Révocation immédiate possible | Révocation complexe (durée de vie fixée à l'émission) |

---

## Sécurité — Points importants

- Le **secret** (`jwt.secret`) ne doit jamais être exposé. Il est lu depuis `application.properties` via `@Value`.
- Le token a une **durée de vie limitée** (`jwt.expiration`) : après expiration, il est rejeté.
- La **signature HMAC-SHA256** garantit que personne ne peut modifier le payload sans connaître le secret.
- Ne jamais stocker d'informations sensibles (mot de passe, carte bancaire) dans le payload — il est lisible par le client.