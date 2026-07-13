# Reference Complete Du Projet Patients-MD

Ce fichier est une fiche de revision complete du projet `Patients-MD`.

Il explique :

- le but de l'application.
- l'architecture globale.
- les packages Java.
- les entites JPA.
- les DTO, requests et responses.
- les repositories.
- les services.
- les controllers et endpoints.
- la security JWT Bearer.
- la configuration.
- le fonctionnement des erreurs.
- les points forts.
- les points a ameliorer pour un niveau professionnel.

## 1. Presentation Du Projet

`Patients-MD` est une API REST Spring Boot pour gerer un cabinet de nutrition.

L'application permet de gerer :

- les utilisateurs du systeme.
- les patients.
- les rendez-vous.
- les consultations.
- les plans alimentaires.
- l'authentification JWT.
- les roles et autorisations.

Le backend expose des endpoints REST sous `/api/...`.

Exemple :

```http
GET /api/patients
POST /api/auth/login
GET /api/appointments
```

## 2. Stack Technique

Le projet utilise :

| Technologie | Role |
|---|---|
| Java 21 | Langage principal |
| Spring Boot 4.1.0 | Framework backend |
| Spring Web MVC | Creation des APIs REST |
| Spring Data JPA | Acces base de donnees |
| Hibernate | ORM JPA |
| MySQL | Base de donnees |
| Spring Security | Securisation de l'application |
| OAuth2 Resource Server | Validation JWT Bearer |
| BCrypt | Hash des mots de passe |
| Spring Validation | Validation des donnees entrantes |
| Lombok | Reduction du boilerplate |
| Springdoc OpenAPI | Documentation Swagger |
| Maven | Build et gestion des dependances |

## 3. Structure Des Packages

Structure principale :

```text
src/main/java/com/bd/patientsmd
├── PatientsMdApplication.java
├── controllers
├── exceptions
├── models
│   ├── dtos
│   ├── entites
│   ├── enums
│   ├── mappers
│   ├── requests
│   └── responses
├── repository
├── security
└── services
```

Role de chaque package :

| Package | Role |
|---|---|
| `controllers` | Recoit les requetes HTTP |
| `services` | Contient la logique metier |
| `repository` | Accede a la base de donnees |
| `models.entites` | Entites JPA mappees vers les tables |
| `models.requests` | Objets recus dans les requetes |
| `models.dtos` | Objets retournes par l'API |
| `models.responses` | Reponses specialisees |
| `models.mappers` | Conversion entite vers DTO et inversement |
| `models.enums` | Enumerations metier |
| `exceptions` | Gestion des erreurs API |
| `security` | JWT, CORS, roles et Spring Security |

## 4. Point D'Entree De L'Application

Fichier :

```text
src/main/java/com/bd/patientsmd/PatientsMdApplication.java
```

Code principal :

```java
@SpringBootApplication
public class PatientsMdApplication {
    public static void main(String[] args) {
        SpringApplication.run(PatientsMdApplication.class, args);
    }
}
```

Role :

- demarrer Spring Boot.
- scanner les components Spring.
- charger la configuration.
- exposer les endpoints REST.

## 5. Configuration Globale

Fichier :

```text
src/main/resources/application.yml
```

### Application

```yaml
spring:
  application:
    name: Patients-MD
```

Le nom de l'application est `Patients-MD`.

### Base De Donnees

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/nutricab_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
  username: root
  password:
  driver-class-name: com.mysql.cj.jdbc.Driver
```

L'application se connecte a MySQL sur la base `nutricab_db`.

### JPA / Hibernate

```yaml
jpa:
  hibernate:
    ddl-auto: update
  show-sql: true
```

Explication :

- `ddl-auto: update` met a jour automatiquement le schema.
- `show-sql: true` affiche les requetes SQL.

Pour la production, il est recommande de remplacer :

```yaml
ddl-auto: update
```

par :

```yaml
ddl-auto: validate
```

et d'utiliser Flyway ou Liquibase.

### Port Serveur

```yaml
server:
  port: 8182
```

L'API tourne sur :

```text
http://localhost:8182
```

### Security

```yaml
app:
  security:
    cors:
      allowed-origins: http://localhost:4200
    jwt:
      issuer: patients-md
      secret: ${JWT_SECRET:change-me-change-me-change-me-change-me}
      expiration-minutes: 60
      refresh-expiration-days: 7
```

Explication :

- `allowed-origins`: frontend autorise par CORS.
- `issuer`: nom du generateur du token JWT.
- `secret`: cle secrete JWT.
- `expiration-minutes`: duree de vie du token.
- `refresh-expiration-days`: duree de vie du refresh token.

## 6. Entites JPA

Les entites representent les tables de la base de donnees.

### 6.1. Entite `Users`

Fichier :

```text
models/entites/Users.java
```

Table :

```text
users
```

Champs :

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant unique |
| `fullName` | `String` | Nom complet |
| `email` | `String` | Email unique |
| `password` | `String` | Mot de passe hashe |
| `role` | `UserRole` | Role utilisateur |
| `active` | `boolean` | Statut du compte |
| `patients` | `List<Patients>` | Patients lies a l'utilisateur |

Relation :

```text
Users 1 ---- * Patients
```

Un utilisateur peut avoir plusieurs patients.

### 6.2. Entite `Patients`

Fichier :

```text
models/entites/Patients.java
```

Table :

```text
patients
```

Champs :

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant unique |
| `fullName` | `String` | Nom complet du patient |
| `birthDate` | `LocalDate` | Date de naissance |
| `phone` | `String` | Telephone |
| `heightCm` | `Double` | Taille en cm |
| `initialWeightKg` | `Double` | Poids initial |
| `objective` | `String` | Objectif nutritionnel |
| `user` | `Users` | Utilisateur responsable |
| `appointments` | `List<Appointments>` | Rendez-vous |
| `consultations` | `List<Consultations>` | Consultations |
| `mealPlans` | `List<MealPlan>` | Plans alimentaires |

Relations :

```text
Patients * ---- 1 Users
Patients 1 ---- * Appointments
Patients 1 ---- * Consultations
Patients 1 ---- * MealPlan
```

### 6.3. Entite `Appointments`

Fichier :

```text
models/entites/Appointments.java
```

Table :

```text
appointments
```

Champs :

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant |
| `patient` | `Patients` | Patient concerne |
| `appointmentDate` | `LocalDateTime` | Date et heure du rendez-vous |
| `status` | `AppointmentStatus` | Statut du rendez-vous |
| `notes` | `String` | Notes |

### 6.4. Entite `Consultations`

Fichier :

```text
models/entites/Consultations.java
```

Table :

```text
consultations
```

Champs :

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant |
| `patient` | `Patients` | Patient consulte |
| `consultationDate` | `LocalDate` | Date de consultation |
| `weightKg` | `Double` | Poids |
| `waistCm` | `Double` | Tour de taille |
| `notes` | `String` | Notes |
| `recommendations` | `String` | Recommandations |

### 6.5. Entite `MealPlan`

Fichier :

```text
models/entites/MealPlan.java
```

Table :

```text
meal_plans
```

Champs :

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant |
| `patient` | `Patients` | Patient concerne |
| `title` | `String` | Titre du plan |
| `objective` | `String` | Objectif |
| `calories` | `Integer` | Calories |
| `content` | `String` | Contenu du plan alimentaire |

### 6.6. Entite `RefreshToken`

Fichier :

```text
models/entites/RefreshToken.java
```

Table :

```text
refresh_tokens
```

Champs :

| Champ | Type | Description |
|---|---|---|
| `id` | `Long` | Identifiant |
| `token` | `String` | Valeur aleatoire du refresh token |
| `user` | `Users` | Utilisateur proprietaire |
| `expiresAt` | `Instant` | Date d'expiration |
| `revoked` | `boolean` | Indique si le token est revoque |

Cette entite herite aussi de `Auditable`.

## 7. Enums

### `UserRole`

Fichier :

```text
models/enums/UserRole.java
```

Valeurs :

```java
ADMIN,
NUTRITIONIST,
SECRETARY,
PATIENT
```

Utilisation :

- definir les droits d'acces.
- proteger les routes.
- stocker le role dans le token JWT.

### `AppointmentStatus`

Fichier :

```text
models/enums/AppointmentStatus.java
```

Valeurs :

```java
PLANNED,
DONE,
CANCELLED
```

Utilisation :

- suivre l'etat d'un rendez-vous.
- filtrer les rendez-vous par statut.

## 8. Requests

Les classes dans `models.requests` representent les donnees envoyees par le client.

### `LoginRequest`

Utilisee pour le login.

Champs :

| Champ | Validation |
|---|---|
| `email` | obligatoire, email valide |
| `password` | obligatoire |

### `CreateUserRequest`

Utilisee pour creer ou modifier un utilisateur.

Champs :

| Champ | Validation |
|---|---|
| `fullName` | obligatoire |
| `email` | obligatoire, email valide |
| `password` | obligatoire, min 6 caracteres |
| `role` | obligatoire |

### `CreatePatientRequest`

Utilisee pour creer ou modifier un patient.

Champs :

| Champ | Validation |
|---|---|
| `userId` | optionnel |
| `fullName` | obligatoire |
| `birthDate` | obligatoire |
| `phone` | obligatoire |
| `heightCm` | obligatoire, positif |
| `initialWeightKg` | obligatoire, positif |
| `objective` | optionnel |

### `CreateAppointmentRequest`

Champs :

| Champ | Validation |
|---|---|
| `patientId` | obligatoire |
| `appointmentDate` | obligatoire, future |
| `notes` | optionnel |

### `CreateConsultationRequest`

Champs :

| Champ | Validation |
|---|---|
| `patientId` | obligatoire |
| `consultationDate` | obligatoire |
| `weightKg` | obligatoire, positif |
| `waistCm` | positif |
| `notes` | optionnel |
| `recommendations` | optionnel |

### `CreateMealPlanRequest`

Champs :

| Champ | Validation |
|---|---|
| `patientId` | obligatoire |
| `title` | obligatoire |
| `objective` | optionnel |
| `calories` | positif |
| `content` | optionnel |

### `ChangePasswordRequest`

Champs :

| Champ | Validation |
|---|---|
| `oldPassword` | obligatoire |
| `newPassword` | obligatoire, min 6 caracteres |

### `RefreshTokenRequest`

Utilisee pour refresh et logout.

Champs :

| Champ | Validation |
|---|---|
| `refreshToken` | obligatoire |

## 9. DTO Et Responses

Les DTO evitent d'exposer directement les entites JPA.

### DTO principaux

| DTO | Role |
|---|---|
| `UsersDto` | Retour utilisateur |
| `PatientDto` | Retour patient |
| `AppointmentDto` | Retour rendez-vous |
| `ConsultationsDto` | Retour consultation |
| `MealPlanDto` | Retour plan alimentaire |

Les DTO principaux contiennent aussi les champs d'audit :

| Champ | Description |
|---|---|
| `createdAt` | Date de creation |
| `updatedAt` | Date de derniere modification |
| `createdBy` | Utilisateur createur |
| `updatedBy` | Dernier utilisateur modificateur |

### Responses specialisees

| Response | Role |
|---|---|
| `AuthResponse` | Retour login avec access token et refresh token |
| `UserSummaryResponse` | Resume utilisateur |
| `PatientSummaryResponse` | Resume patient |
| `PatientResponse` | Detail patient |
| `AppointmentResponse` | Detail rendez-vous |
| `ConsultationResponse` | Detail consultation |
| `MealPlanResponse` | Detail plan alimentaire |

### `AuthResponse`

Retourne apres login :

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "4b4e4d9a-7c87-4f76-86fd-3cfd5e9a15cf",
  "fullName": "Admin",
  "role": "ADMIN"
}
```

## 10. Mappers

Package :

```text
models/mappers
```

Role :

- convertir une entite vers un DTO.
- convertir une request vers une entite.
- mettre a jour une entite existante.

Exemples :

| Mapper | Role |
|---|---|
| `UserMapper` | `Users` vers `UsersDto` |
| `PatientMapper` | `Patients` vers `PatientDto` |
| `AppointmentMapper` | `Appointments` vers `AppointmentDto` |
| `ConsultationMapper` | `Consultations` vers `ConsultationsDto` |
| `MealPlanMapper` | `MealPlan` vers `MealPlanDto` |

Interet :

- eviter de retourner directement les entites JPA.
- controler les donnees exposees.
- simplifier les controllers.

## 11. Repositories

Les repositories heritent de `JpaRepository`.

Ils fournissent deja :

- `save`
- `findById`
- `findAll`
- `deleteById`
- `existsById`

### `UsersRepository`

Methodes :

```java
Optional<Users> findByEmail(String email);
boolean existsByEmail(String email);
List<Users> findByRole(UserRole role);
List<Users> findByActive(boolean active);
List<Users> findByActiveAndRole(boolean active, UserRole role);
```

### `PatientRepository`

Methodes :

```java
List<Patients> findByFullNameContainingIgnoreCase(String keyword);
List<Patients> findByUserId(Long userId);
```

### `AppointmentRepository`

Methodes :

```java
List<Appointments> findByPatientId(Long patientId);
List<Appointments> findByStatus(AppointmentStatus status);
```

### `ConsultationRepository`

Methodes :

```java
List<Consultations> findByPatientId(Long patientId);
List<Consultations> findByPatientIdOrderByConsultationDateAsc(Long patientId);
List<Consultations> findTop10ByOrderByConsultationDateDesc();
```

### `MealPlanRepository`

Methodes :

```java
List<MealPlan> findByPatientId(Long patientId);
List<MealPlan> findByObjectiveContainingIgnoreCase(String objective);
List<MealPlan> findByCaloriesBetween(Integer minCalories, Integer maxCalories);
Optional<MealPlan> findFirstByPatientIdOrderByIdDesc(Long patientId);
```

### `RefreshTokenRepository`

Methodes :

```java
Optional<RefreshToken> findByToken(String token);
void deleteByExpiresAtBefore(Instant now);
void deleteByUser(Users user);
```

## 12. Services

Les services contiennent la logique metier.

Les controllers ne doivent pas contenir de logique complexe.

### `UsersServiceImpl`

Responsabilites :

- creer un utilisateur.
- verifier l'unicite de l'email.
- hasher les mots de passe avec BCrypt.
- connecter un utilisateur.
- generer un token JWT.
- changer un mot de passe.
- activer/desactiver un utilisateur.
- filtrer par role ou statut.
- recuperer les patients d'un utilisateur.

Points importants :

- le mot de passe n'est plus stocke en clair.
- le login incorrect retourne `401`.
- les anciens mots de passe en clair peuvent etre convertis automatiquement au premier login reussi.

### `PatientServiceImpl`

Responsabilites :

- creer un patient.
- modifier un patient.
- supprimer un patient.
- chercher par nom.
- associer un patient a un utilisateur si `userId` est fourni.

### `AppointmentServiceImpl`

Responsabilites :

- creer un rendez-vous.
- modifier un rendez-vous.
- supprimer un rendez-vous.
- rechercher par patient.
- rechercher par statut.

Regle importante :

- la date du rendez-vous doit etre dans le futur grace a `@Future`.

### `ConsultationsServiceImpl`

Responsabilites :

- creer une consultation.
- modifier une consultation.
- supprimer une consultation.
- recuperer les consultations d'un patient.
- recuperer l'historique du poids.
- recuperer les 10 consultations recentes.

### `MealPlanServiceImpl`

Responsabilites :

- creer un plan alimentaire.
- modifier un plan alimentaire.
- supprimer un plan alimentaire.
- chercher par patient.
- chercher par objectif.
- chercher par intervalle de calories.
- recuperer le dernier plan alimentaire d'un patient.

### `RefreshTokenService`

Responsabilites :

- creer un refresh token au login.
- supprimer les refresh tokens expires.
- verifier qu'un refresh token existe.
- verifier qu'il n'est pas expire.
- verifier qu'il n'est pas revoque.
- generer un nouveau JWT avec `/api/auth/refresh`.
- revoquer l'ancien refresh token apres refresh.
- revoquer un refresh token avec `/api/auth/logout`.

## 13. Controllers Et Endpoints

Les controllers exposent les routes REST.

### 13.1. AuthController

Base path :

```text
/api/auth
```

Endpoints :

| Methode | Route | Role |
|---|---|---|
| `POST` | `/api/auth/login` | Login |
| `POST` | `/api/auth/refresh` | Regenerer un access token |
| `POST` | `/api/auth/logout` | Revoquer un refresh token |

Exemple :

```http
POST /api/auth/login
Content-Type: application/json
```

Body :

```json
{
  "email": "amine.bennani@nutricab.ma",
  "password": "AdminNutri2026!"
}
```

### 13.2. UsersController

Base path :

```text
/api/users
```

Endpoints :

| Methode | Route | Description |
|---|---|---|
| `POST` | `/api/users` | Creer utilisateur |
| `GET` | `/api/users` | Lister utilisateurs, pagine |
| `GET` | `/api/users/{id}` | Recuperer utilisateur |
| `PUT` | `/api/users/{id}` | Modifier utilisateur |
| `DELETE` | `/api/users/{id}` | Supprimer utilisateur |
| `POST` | `/api/users/login` | Ancien endpoint login |
| `GET` | `/api/users/email/{email}` | Chercher par email |
| `GET` | `/api/users/role/{role}` | Chercher par role, pagine |
| `GET` | `/api/users/filter` | Filtrer par actif/role, pagine |
| `GET` | `/api/users/{userId}/patients` | Patients d'un utilisateur |
| `PATCH` | `/api/users/{id}/password` | Changer mot de passe |
| `PATCH` | `/api/users/{id}/status` | Activer/desactiver |

Security :

```text
/api/users/** -> ADMIN
```

Exception :

```text
POST /api/users/login -> public pour compatibilite
```

### 13.3. PatientController

Base path :

```text
/api/patients
```

Endpoints :

| Methode | Route | Description |
|---|---|---|
| `GET` | `/api/patients` | Lister patients, pagine |
| `POST` | `/api/patients` | Creer patient |
| `GET` | `/api/patients/{id}` | Detail patient |
| `PUT` | `/api/patients/{id}` | Modifier patient |
| `DELETE` | `/api/patients/{id}` | Supprimer patient |
| `GET` | `/api/patients/search?keyword=x` | Recherche par nom, paginee |

Security :

```text
/api/patients/** -> ADMIN, NUTRITIONIST, PATIENT avec controle metier
```

### 13.4. AppointmentsController

Base path :

```text
/api/appointments
```

Endpoints :

| Methode | Route | Description |
|---|---|---|
| `POST` | `/api/appointments` | Creer rendez-vous |
| `PUT` | `/api/appointments/{id}` | Modifier rendez-vous |
| `GET` | `/api/appointments` | Lister rendez-vous, pagine |
| `GET` | `/api/appointments/{id}` | Detail rendez-vous |
| `GET` | `/api/appointments/patient/{id}` | Rendez-vous d'un patient, pagine |
| `DELETE` | `/api/appointments/{id}` | Supprimer rendez-vous |
| `GET` | `/api/appointments/search?keyword=PLANNED` | Recherche par statut, paginee |

Security :

```text
/api/appointments/** -> ADMIN, NUTRITIONIST, SECRETARY, PATIENT avec controle metier
```

### 13.5. ConsultationsController

Base path :

```text
/api/consultations
```

Endpoints :

| Methode | Route | Description |
|---|---|---|
| `POST` | `/api/consultations` | Creer consultation |
| `GET` | `/api/consultations` | Lister consultations, pagine |
| `GET` | `/api/consultations/{id}` | Detail consultation |
| `PUT` | `/api/consultations/{id}` | Modifier consultation |
| `DELETE` | `/api/consultations/{id}` | Supprimer consultation |
| `GET` | `/api/consultations/patient/{patientId}` | Consultations par patient, paginees |
| `GET` | `/api/consultations/patient/{patientId}/weight-history` | Historique poids |
| `GET` | `/api/consultations/recent` | 10 consultations recentes |

Security :

```text
/api/consultations/** -> ADMIN, NUTRITIONIST, PATIENT avec controle metier
```

### 13.6. MealPlanController

Base path :

```text
/api/meal-plan
```

Endpoints :

| Methode | Route | Description |
|---|---|---|
| `POST` | `/api/meal-plan` | Creer plan |
| `GET` | `/api/meal-plan` | Lister plans, pagine |
| `GET` | `/api/meal-plan/{id}` | Detail plan |
| `PUT` | `/api/meal-plan/{id}` | Modifier plan |
| `DELETE` | `/api/meal-plan/{id}` | Supprimer plan |
| `GET` | `/api/meal-plan/patient/{patientId}` | Plans d'un patient, pagine |
| `GET` | `/api/meal-plan/search?objective=x` | Recherche par objectif, paginee |
| `GET` | `/api/meal-plan/calories?minCalories=1200&maxCalories=1800` | Recherche par calories, paginee |
| `GET` | `/api/meal-plan/patient/{patientId}/active` | Dernier plan du patient |

Security :

```text
/api/meal-plan/** -> ADMIN, NUTRITIONIST, PATIENT avec controle metier
```

## 14. Security JWT Bearer

La documentation detaillee de la security est dans :

```text
SECURITY.md
```

Resume :

1. l'utilisateur se connecte avec `/api/auth/login`.
2. le backend verifie email et password.
3. le backend genere un JWT et un refresh token.
4. le client garde ces tokens.
5. le client appelle les routes protegees avec :

```http
Authorization: Bearer <token>
```

Quand le JWT expire, le client appelle :

```http
POST /api/auth/refresh
```

avec :

```json
{
  "refreshToken": "4b4e4d9a-7c87-4f76-86fd-3cfd5e9a15cf"
}
```

Le refresh token est stocke en base dans la table `refresh_tokens` et il est revoque apres utilisation.

### Roles Et Permissions

| Route | Roles autorises |
|---|---|
| `/api/auth/login` | public |
| `/api/auth/refresh` | public |
| `/api/auth/logout` | public |
| `/api/users/login` | public compatibilite |
| `/api/users/**` | `ADMIN` |
| `/api/patients/**` | `ADMIN`, `NUTRITIONIST`, `PATIENT` avec controle metier |
| `/api/appointments/**` | `ADMIN`, `NUTRITIONIST`, `SECRETARY`, `PATIENT` avec controle metier |
| `/api/consultations/**` | `ADMIN`, `NUTRITIONIST`, `PATIENT` avec controle metier |
| `/api/meal-plan/**` | `ADMIN`, `NUTRITIONIST`, `PATIENT` avec controle metier |
| `/api/mealPlan/**` | meme regle, compatibilite security |
| autres routes | utilisateur authentifie |

### Classes Security

| Classe | Role |
|---|---|
| `SecurityConfig` | Configuration Spring Security |
| `JwtService` | Generation du JWT |
| `CurrentUserService` | Lecture du `userId` et du `role` depuis le JWT |
| `AuthorizationService` | Controle metier fin avec `@PreAuthorize` |
| `JwtAuthenticationEntryPoint` | Erreur `401` en JSON |
| `JwtAccessDeniedHandler` | Erreur `403` en JSON |
| `OpenApiSecurityConfig` | Configuration Swagger Bearer JWT |

### Security Metier Fine

Les routes ne sont plus protegees uniquement par role global.

Le projet verifie aussi si la ressource appartient au user connecte.

Regles :

- `ADMIN` peut tout voir et tout gerer.
- `NUTRITIONIST` voit et gere seulement les patients rattaches a son `userId`.
- `PATIENT` voit seulement ses propres patients/donnees.
- `SECRETARY` peut gerer les rendez-vous, mais pas les consultations ni les meal plans.

La verification se base sur :

```text
patients.user_id = userId du JWT
```

Exemples d'annotations :

```java
@PreAuthorize("@authorizationService.canAccessPatient(#id)")
@PreAuthorize("@authorizationService.canManageConsultation(#id)")
@PreAuthorize("@authorizationService.canCreateMealPlan(#request.patientId())")
```

Les listes sont filtrees dans les services pour eviter qu'un utilisateur voie toutes les lignes.

## 15. Gestion Des Erreurs

Package :

```text
exceptions
```

Classes :

| Classe | Role |
|---|---|
| `ErrorResponse` | Format JSON des erreurs |
| `ResourceNotFoundException` | Ressource inexistante |
| `InvalidCredentialsException` | Login incorrect |
| `GlobalExceptionHandler` | Handler global des erreurs |

Format d'erreur :

```json
{
  "timestamp": "2026-07-13T09:43:15",
  "status": 401,
  "error": "UNAUTHORIZED",
  "path": "/api/auth/login",
  "message": "Email ou mot de passe incorrect"
}
```

Codes HTTP importants :

| Cas | Code |
|---|---|
| Requete valide | `200` |
| Creation reussie | `200` actuellement |
| Donnees invalides | `400` |
| Login incorrect | `401` |
| Token absent/invalide/expire | `401` |
| Role insuffisant | `403` |
| Ressource introuvable | `404` |
| Erreur serveur | `500` |

## 16. Validation Des Donnees

Le projet utilise `jakarta.validation`.

Exemples :

```java
@NotBlank
@NotNull
@Email
@Size(min = 6)
@Positive
@Future
```

Ces validations sont appliquees sur les request records.

Si une validation echoue, `GlobalExceptionHandler` retourne une erreur `400`.

## 17. Flux Fonctionnels Importants

### 17.1. Flux Login

```text
Client -> AuthController -> UsersServiceImpl -> UsersRepository
       -> PasswordEncoder -> JwtService -> RefreshTokenService -> AuthResponse
```

Etapes :

1. le client envoie email/password.
2. l'utilisateur est cherche par email.
3. le compte actif est verifie.
4. le mot de passe est compare.
5. un JWT et un refresh token sont generes.
6. les tokens sont retournes.

### 17.2. Flux Creation Patient

```text
Client -> PatientController -> PatientServiceImpl -> UsersRepository
       -> PatientRepository -> PatientMapper -> PatientDto
```

Etapes :

1. le client envoie les donnees du patient.
2. si `userId` existe, l'utilisateur responsable est charge.
3. l'entite `Patients` est construite.
4. elle est sauvegardee.
5. un `PatientDto` est retourne.

### 17.3. Flux Creation Rendez-vous

```text
Client -> AppointmentsController -> AppointmentServiceImpl
       -> PatientRepository -> AppointmentRepository -> AppointmentDto
```

Etapes :

1. verifier que le patient existe.
2. creer l'entite `Appointments`.
3. sauvegarder.
4. retourner le DTO.

### 17.4. Flux Consultation

```text
Client -> ConsultationsController -> ConsultationsServiceImpl
       -> PatientRepository -> ConsultationRepository -> ConsultationsDto
```

Etapes :

1. verifier patient.
2. enregistrer poids, tour de taille, notes, recommandations.
3. retourner la consultation.

### 17.5. Flux Meal Plan

```text
Client -> MealPlanController -> MealPlanServiceImpl
       -> PatientRepository -> MealPlanRepository -> MealPlanDto
```

Etapes :

1. verifier patient.
2. creer plan alimentaire.
3. sauvegarder.
4. retourner le DTO.

## 18. Swagger / OpenAPI

Le projet utilise :

```xml
springdoc-openapi-starter-webmvc-ui
```

Swagger est accessible via :

```text
http://localhost:8182/swagger-ui.html
```

ou :

```text
http://localhost:8182/swagger-ui/index.html
```

La security Bearer est configuree par :

```text
OpenApiSecurityConfig.java
```

Utilisation :

1. faire un login.
2. copier le token.
3. cliquer sur `Authorize`.
4. coller le token.
5. tester les routes protegees.

## 19. Commandes Utiles

### Compiler sans tests

```bash
mvn -q -DskipTests package
```

### Lancer l'application

```bash
mvn spring-boot:run
```

### URL API

```text
http://localhost:8182
```

### Login

```http
POST http://localhost:8182/api/auth/login
```

Body :

```json
{
  "email": "amine.bennani@nutricab.ma",
  "password": "AdminNutri2026!"
}
```

### Appeler une route protegee

```http
GET http://localhost:8182/api/patients
Authorization: Bearer <token>
```

### Refresh token

```http
POST http://localhost:8182/api/auth/refresh
Content-Type: application/json
```

Body :

```json
{
  "refreshToken": "<refresh-token>"
}
```

### Logout

```http
POST http://localhost:8182/api/auth/logout
Content-Type: application/json
```

Body :

```json
{
  "refreshToken": "<refresh-token>"
}
```

## 20. Points Forts Du Projet

Le projet a deja plusieurs bons points :

- architecture en couches : controller, service, repository.
- DTO et mappers.
- validation des donnees avec `@Valid`.
- gestion globale des exceptions.
- securite JWT Bearer.
- refresh token avec rotation et revocation.
- roles `ADMIN`, `NUTRITIONIST`, `SECRETARY`, `PATIENT`.
- mots de passe hashes avec BCrypt.
- CORS centralise.
- pagination avec `Pageable`.
- audit automatique `createdAt`, `updatedAt`, `createdBy`, `updatedBy`.
- Swagger/OpenAPI.
- separation des entites metier.
- repositories Spring Data JPA propres.

## 21. Points A Ameliorer Pour Niveau Professionnel

### 21.1. Pagination

La pagination est maintenant en place sur les endpoints de liste et de recherche principaux.

Exemple :

```http
GET /api/patients?page=0&size=20&sort=fullName,asc
```

La reponse contient :

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "size": 20,
  "number": 0
}
```

### 21.2. Migrations Base De Donnees

Actuellement :

```yaml
ddl-auto: update
```

Pour un projet professionnel :

- ajouter Flyway ou Liquibase.
- utiliser `ddl-auto: validate`.
- versionner les scripts SQL.

### 21.3. Audit

L'audit est maintenant ajoute via `Auditable` et `JpaAuditingConfig`.

Champs ajoutes dans les entites :

```java
createdAt
updatedAt
createdBy
updatedBy
```

Cela permet de savoir :

- qui a cree une ressource.
- qui l'a modifiee.
- quand elle a ete creee.
- quand elle a ete modifiee.

Ces champs sont aussi exposes dans les DTO principaux.

### 21.4. Tests

Tests recommandes :

- tests unitaires des services.
- tests des mappers.
- tests des validations.
- tests login JWT.
- tests roles `401/403`.
- tests d'integration controller.

### 21.5. Refresh Token

Le refresh token est maintenant en place.

- access token court.
- refresh token plus long, persiste dans `refresh_tokens`.
- endpoint `/api/auth/refresh`.
- endpoint `/api/auth/logout`.
- rotation du refresh token a chaque refresh.

### 21.6. Security Metier Fine

Actuellement les roles protegent les routes.

Mais il manque des controles fins :

- un patient ne doit voir que ses propres donnees.
- un nutritionniste ne doit voir que ses patients.
- une secretaire peut gerer les rendez-vous mais pas les consultations.

Solution possible :

```java
@PreAuthorize(...)
```

ou verification dans les services.

### 21.7. Normalisation Des Routes

Route actuelle :

```text
/api/meal-plan
```

Route encore plus standard si on veut uniformiser au pluriel :

```text
/api/meal-plans
```

### 21.8. Erreurs Metier Plus Precises

Exemples a ajouter :

- email deja utilise -> `409 Conflict`.
- statut rendez-vous invalide -> `400`.
- patient deja supprime -> `404`.
- calories min > max -> `400`.

### 21.9. Docker

Ajouter :

- `Dockerfile`.
- `docker-compose.yml`.
- service MySQL.
- variables d'environnement.

### 21.10. Profils Spring

Ajouter :

```text
application-dev.yml
application-test.yml
application-prod.yml
```

Pour separer :

- configuration locale.
- configuration test.
- configuration production.

## 22. Questions De Revision Possibles

### Pourquoi utiliser DTO au lieu de retourner les entites ?

Pour eviter d'exposer directement le modele base de donnees, controler les champs retournes et eviter certains problemes de relations JPA.

### Pourquoi hasher les mots de passe ?

Pour ne jamais stocker le vrai mot de passe en base. BCrypt stocke un hash irreversible.

### Pourquoi JWT ?

JWT permet une authentification stateless : le serveur n'a pas besoin de garder une session.

### Pourquoi `SessionCreationPolicy.STATELESS` ?

Parce que chaque requete contient son token. Spring ne cree pas de session HTTP.

### Difference entre `401` et `403` ?

- `401`: utilisateur non authentifie ou token invalide.
- `403`: utilisateur authentifie mais role insuffisant.

### Pourquoi utiliser `@Transactional` dans les services ?

Pour executer les operations base de donnees dans une transaction.

### Pourquoi utiliser `JpaRepository` ?

Pour avoir directement les operations CRUD sans les implementer manuellement.

### Pourquoi utiliser `@Valid` ?

Pour valider automatiquement les donnees envoyees par le client.

### Pourquoi `@ManyToOne(fetch = FetchType.LAZY)` ?

Pour eviter de charger automatiquement toutes les relations et ameliorer les performances.

## 23. Resume Rapide Pour Soutenance

`Patients-MD` est une API REST Spring Boot pour gerer un cabinet de nutrition.

Elle gere les utilisateurs, patients, rendez-vous, consultations et plans alimentaires.

L'architecture est en couches :

```text
Controller -> Service -> Repository -> Database
```

La securite est basee sur JWT Bearer :

```text
Login -> JWT -> Authorization Bearer -> Verification des roles
```

Les mots de passe sont securises avec BCrypt.

Les routes sont protegees selon les roles :

- `ADMIN`.
- `NUTRITIONIST`.
- `SECRETARY`.
- `PATIENT`.

Les erreurs sont retournees en JSON structure.

Le projet est fonctionnel, mais pour un niveau production il faut encore ajouter :

- tests.
- migrations Flyway/Liquibase.
- Docker.
- profils dev/prod.
