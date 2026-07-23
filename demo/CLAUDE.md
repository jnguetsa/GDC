# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.4.1 REST API for HR and Leave Management (GDC — Gestion des Congés). Java 17, PostgreSQL, JWT auth. All entity/field names are in French.

## Commands

```bash
./mvnw spring-boot:run        # Start dev server on port 8080
./mvnw clean package          # Build JAR
./mvnw test                   # Run all tests
./mvnw test -Dtest=ClassName  # Run a single test class
```

Swagger UI: `http://localhost:8080/swagger-ui.html`

## Database

PostgreSQL at `localhost:5432/demo` (user: `postgres`, pass: `root`).  
**`spring.jpa.hibernate.ddl-auto=create`** — the schema is recreated on every startup in dev. Change to `update` or `validate` for production.

## Architecture

Three business modules under `com.example.demo`, each with `entity/`, `dto/`, `mapper/`, `repository/`, `service/`, and `controller/` sub-packages:

### GDU — User & Auth Management
Handles authentication, employees, roles, and permissions.

- **Entities:** `Utilisateur` (login), `Employe` (HR data), `Role`, `Permission`, `RefreshToken`
- **Security:** JWT via JJWT. `JwtAuthenticationFilter` validates Bearer tokens, loads the user with roles/permissions using a JOIN FETCH query (`findByEmailWithRolesAndPermissions`), and populates the `SecurityContext`. The JOIN FETCH is intentional — it prevents `LazyInitializationException` outside a transaction (documented in `SECURITY-modifications.md`).
- **Auth endpoints:** register, login, refresh token

### GDC — Leave Management
Core business logic. `DemandeConge` is the central entity.

- **Entities:** `DemandeConge`, `TypeConge`, `CatalogueTypeConge`, `SoldeConge`, `ExerciceConge`, `RetourConge`, `JourFerie`, `ReglePlageConge`, `PieceJointe`, `HistoriqueAction`, `Notification`
- **Workflow:** 3-stage approval — employee submits → manager approves → HR approves. Each stage records `commentaire`, `dateValidation`, and `validatedBy`.
- **Return tracking:** `RetourConge` has `@PreUpdate` logic that auto-calculates `joursEcart` and `joursNonPrisRecuperables` from `dateRetourEffective`.
- **Attachments:** `PieceJointe` stores files under `uploads/pieces-jointes/` (max 10 MB).

### GDO — Company & Department Management
Thin module: `Entreprise` and `Departement` entities with DTOs and mappers. Note: `DepartementRepository` has a typo in the file name (`DepartementReepository`).

### Common Layer
`com.example.demo.common/enums/` — 10 shared enums: `StatutDemande`, `CodeTypeConge`, `StatutRetour`, `TypeAction`, `TypeNotification`, `StatutEmploi`, `StatutEntreprise`, `TypeContrat`, `UniteSolde`, `UniteTemps`.

## Key Conventions

- **Lombok:** `@Getter`/`@Setter` with `AccessLevel` for controlled access. `id`, `dateCreation`, `dateModification` fields are read-only (no public setter).
- **Timestamps:** `@CreationTimestamp` / `@UpdateTimestamp` on `LocalDateTime` fields — never `LocalDate`.
- **Mapping:** MapStruct interfaces per entity (`SomeMapper`). Never map manually in service code.
- **Services:** Interface + Impl pattern (`ISomethingService` / `SomethingServiceImpl`).
- **Exceptions:** Custom `*NotFoundException` classes per entity; throw these from services, not controllers.
- **Audit:** All state changes write a `HistoriqueAction` record with the actor, action type, and client IP.