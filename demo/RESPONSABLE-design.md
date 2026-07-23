# Décision de conception — Champ `responsable` sur Employe

## Contexte

Lors du développement du module GDU, la question s'est posée : comment savoir si un employé
est responsable d'un département ? Deux approches ont été envisagées.

---

## Approche A — `Departement.responsable` (retenue)

Le département porte une référence directe vers son responsable :

```java
// Departement.java
@ManyToOne
@JoinColumn(name = "responsable_id")
private Employe responsable;
```

Le champ `responsable` dans `EmployeResponse` est **calculé** dans le service, pas stocké en base :

```java
// EmployeService.java
private EmployeResponse toResponse(Employe employe) {
    EmployeResponse response = employerMapper.toDto(employe);
    response.setResponsable(
        employe.getDepartement() != null &&
        employe.getDepartement().getResponsable() != null &&
        employe.getDepartement().getResponsable().getId().equals(employe.getId())
    );
    return response;
}
```

Le mapper ignore le champ (il n'existe pas en base) :

```java
// EmployeMapper.java
@Mapping(target = "responsable", ignore = true)
EmployeResponse toDto(Employe employe);
```

---

## Approche B — `Employe.responsable` (boolean) — rejetée

Ajouter un champ `boolean responsable = false` directement sur `Employe` :

```java
// Employe.java
private boolean responsable = false;
```

### Pourquoi rejetée ?

1. **Redondance** — `Departement.responsable` existe déjà. Deux sources de vérité peuvent
   se désynchroniser silencieusement :
   ```
   Departement.responsable = Employe(id=5)   ✅
   Employe(id=5).responsable = false          ❌  bug invisible
   ```

2. **Aucune contrainte d'unicité** — rien n'empêche deux employés du même département
   d'avoir `responsable = true` simultanément.

3. **Couplage département perdu** — le boolean dit "je suis responsable" mais pas
   **de quel département**. Si l'employé change de département, le boolean reste `true`
   par erreur.

4. **Maintenance double** — chaque changement de responsable nécessite de mettre à jour
   deux endroits au lieu d'un.

---

## Gestion du changement de département

Puisque `Departement.responsable` est la source de vérité, quand un employé change de
département il faut libérer son ancien poste de responsable. Cela est géré dans
`EmployeService.updateEmployer()` :

```java
if (request.getDepartementId() != null) {
    Departement ancienDept = employe.getDepartement();
    boolean changeDepartement = ancienDept != null &&
        !ancienDept.getId().equals(request.getDepartementId());

    if (changeDepartement &&
        ancienDept.getResponsable() != null &&
        ancienDept.getResponsable().getId().equals(employe.getId())) {
        ancienDept.setResponsable(null);   // libère le poste
        departementRepository.save(ancienDept);
    }

    Departement nouveauDept = departementRepository.findById(request.getDepartementId())
        .orElseThrow(() -> new DepartementNotFoundException(...));
    employe.setDepartement(nouveauDept);
}
```

---

## Récupérer les responsables d'un département

La query JPQL dans `EmployeRepository` exploite directement la relation :

```java
@Query("SELECT e FROM Employe e JOIN Departement d ON d.responsable = e WHERE d.id = :departementId")
List<Employe> findResponsablesByDepartementId(@Param("departementId") Long departementId);
```

---

## Règle à retenir

> `responsable` n'est **jamais stocké** sur `Employe` en base.
> C'est toujours `Departement.responsable` qui fait foi.
> Le boolean dans `EmployeResponse` est uniquement une commodité pour le client API,
> calculée à la volée dans `toResponse()`.