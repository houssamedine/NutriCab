# Documentation Security JWT Bearer

Ce document explique la partie security ajoutee dans le projet `Patients-MD`.

L'objectif est de securiser l'API avec un token JWT utilise dans le header HTTP :

```http
Authorization: Bearer <token>
```

## 1. Dependances ajoutees

Dans `pom.xml`, deux dependances Spring Security sont utilisees :

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

Role des dependances :

- `spring-boot-starter-security` active Spring Security dans l'application.
- `spring-boot-starter-oauth2-resource-server` permet a Spring de lire et valider les tokens JWT Bearer.

## 2. Configuration dans `application.yml`

La configuration JWT est dans `src/main/resources/application.yml` :

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

- `issuer`: nom de l'application qui genere le token.
- `secret`: cle secrete utilisee pour signer et verifier le JWT.
- `expiration-minutes`: duree de validite du token.
- `allowed-origins`: URL du frontend autorisee par CORS.
- `refresh-expiration-days`: duree de validite du refresh token.

En local, une valeur par defaut est disponible :

```yaml
change-me-change-me-change-me-change-me
```

En production, il faut utiliser une vraie variable d'environnement :

```bash
JWT_SECRET=une-cle-tres-longue-et-secrete-au-moins-32-caracteres
```

La cle doit avoir au minimum 32 caracteres parce que l'application signe les tokens avec `HS256`.

## 2.1. Configuration CORS

Le CORS est configure dans `SecurityConfig`, pas dans les controllers.

Actuellement le frontend autorise est :

```yaml
app:
  security:
    cors:
      allowed-origins: http://localhost:4200
```

Cela autorise Angular en local a appeler l'API Spring Boot.

Pour ajouter plusieurs frontends :

```yaml
allowed-origins: http://localhost:4200,https://app.nutricab.ma
```

## 3. Classe `SecurityConfig`

Fichier :

```text
src/main/java/com/bd/patientsmd/security/SecurityConfig.java
```

Cette classe est la configuration principale de Spring Security.

### Annotations

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
}
```

Role :

- `@Configuration`: indique que la classe contient des beans Spring.
- `@EnableWebSecurity`: active la securite web Spring Security.
- `@EnableMethodSecurity`: permet plus tard d'utiliser des annotations comme `@PreAuthorize` si besoin.

### Bean `SecurityFilterChain`

La methode principale est :

```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
```

Elle configure les regles de securite HTTP.

### CSRF desactive

```java
.csrf(AbstractHttpConfigurer::disable)
```

On desactive CSRF parce que l'application est une API REST stateless. Le client s'authentifie avec un token JWT, pas avec une session serveur classique.

### Session stateless

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

Cela veut dire que Spring ne garde pas de session utilisateur cote serveur.

A chaque requete protegee, le client doit envoyer le token :

```http
Authorization: Bearer <token>
```

### Routes publiques

```java
.requestMatchers(
        HttpMethod.POST,
        "/api/auth/login",
        "/api/auth/refresh",
        "/api/auth/logout",
        "/api/users/login"
).permitAll()
```

Ces routes sont publiques :

- `POST /api/auth/login`: authentification principale.
- `POST /api/auth/refresh`: regeneration du JWT avec refresh token.
- `POST /api/auth/logout`: revocation du refresh token.
- `POST /api/users/login`: authentification.

Important : `POST /api/users` n'est plus public. La creation d'utilisateur est reservee a `ADMIN`.

`POST /api/users/login` reste disponible pour compatibilite, mais le endpoint recommande est maintenant :

```http
POST /api/auth/login
```

Swagger est aussi public :

```java
.requestMatchers(
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
).permitAll()
```

### Routes protegees par role

Les routes sont protegees comme ceci :

```java
.requestMatchers("/api/users/**").hasRole("ADMIN")
.requestMatchers("/api/patients/**").hasAnyRole("ADMIN", "NUTRITIONIST", "PATIENT")
.requestMatchers("/api/appointments/**").hasAnyRole("ADMIN", "NUTRITIONIST", "SECRETARY", "PATIENT")
.requestMatchers("/api/consultations/**").hasAnyRole("ADMIN", "NUTRITIONIST", "PATIENT")
.requestMatchers("/api/mealPlan/**", "/api/meal-plan/**").hasAnyRole("ADMIN", "NUTRITIONIST", "PATIENT")
.anyRequest().authenticated()
```

Donc :

| Route | Roles autorises |
|---|---|
| `/api/users/**` | `ADMIN` |
| `/api/patients/**` | `ADMIN`, `NUTRITIONIST`, `PATIENT` avec controle metier |
| `/api/appointments/**` | `ADMIN`, `NUTRITIONIST`, `SECRETARY`, `PATIENT` avec controle metier |
| `/api/consultations/**` | `ADMIN`, `NUTRITIONIST`, `PATIENT` avec controle metier |
| `/api/meal-plan/**` | `ADMIN`, `NUTRITIONIST`, `PATIENT` avec controle metier |
| `/api/mealPlan/**` | meme regle, compatibilite security |
| Autres routes | utilisateur connecte |

### Validation JWT

```java
.oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
)
```

Cela indique a Spring Security :

1. de lire le header `Authorization`.
2. de verifier que le token est un Bearer token.
3. de decoder le JWT.
4. de verifier sa signature.
5. de creer l'utilisateur authentifie dans le contexte Spring Security.

## 4. Beans JWT

Dans `SecurityConfig`, deux beans sont crees :

```java
@Bean
public JwtEncoder jwtEncoder(...)
```

et :

```java
@Bean
public JwtDecoder jwtDecoder(...)
```

### `JwtEncoder`

Le `JwtEncoder` sert a generer/signer les tokens JWT au moment du login.

Il utilise la cle secrete configuree dans `application.yml`.

### `JwtDecoder`

Le `JwtDecoder` sert a verifier les tokens JWT recus dans les requetes.

Quand un client appelle une route protegee, Spring utilise ce decoder pour verifier :

- que le token est valide.
- que la signature est correcte.
- que le token n'est pas expire.

## 5. Conversion du role JWT vers Spring Security

Dans le token, on met le role comme ceci :

```json
{
  "role": "ADMIN"
}
```

Mais Spring Security attend les roles sous cette forme :

```text
ROLE_ADMIN
```

La methode `jwtAuthenticationConverter()` fait cette conversion :

```java
String role = jwt.getClaimAsString("role");

if (role != null && !role.isBlank()) {
    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
}
```

Exemples :

| Role dans JWT | Role Spring Security |
|---|---|
| `ADMIN` | `ROLE_ADMIN` |
| `NUTRITIONIST` | `ROLE_NUTRITIONIST` |
| `SECRETARY` | `ROLE_SECRETARY` |
| `PATIENT` | `ROLE_PATIENT` |

C'est pour cela que cette regle fonctionne :

```java
.requestMatchers("/api/users/**").hasRole("ADMIN")
```

Avec `hasRole("ADMIN")`, Spring cherche automatiquement l'autorite `ROLE_ADMIN`.

## 5.1. Security metier fine

En plus des roles par route, le projet applique maintenant des controles metier avec `@PreAuthorize`.

Objectif :

- un `PATIENT` ne voit que ses propres donnees.
- un `NUTRITIONIST` ne voit que les patients qui lui sont rattaches.
- une `SECRETARY` peut gerer les rendez-vous, mais pas les consultations ni les plans alimentaires.
- un `ADMIN` garde l'acces global.

La verification utilise la relation :

```text
patients.user_id = users.id
```

Donc une ressource appartient a un utilisateur si elle est liee a un patient dont `user_id` correspond au `userId` du JWT.

Classes utilisees :

| Classe | Role |
|---|---|
| `CurrentUserService` | Lit `userId` et `role` depuis le JWT courant |
| `AuthorizationService` | Verifie les droits metier par patient, rendez-vous, consultation et meal plan |

Exemples :

```java
@PreAuthorize("@authorizationService.canAccessPatient(#id)")
```

```java
@PreAuthorize("@authorizationService.canManageConsultation(#id)")
```

```java
@PreAuthorize("@authorizationService.canCreateMealPlan(#request.patientId())")
```

Les endpoints de liste sont aussi filtres dans les services :

- `ADMIN` voit tout.
- `SECRETARY` voit tous les rendez-vous.
- `NUTRITIONIST` et `PATIENT` voient seulement les donnees liees a leurs patients.

## 6. Classe `JwtService`

Fichier :

```text
src/main/java/com/bd/patientsmd/security/JwtService.java
```

Cette classe genere le token JWT apres un login reussi.

Methode principale :

```java
public String generateToken(Users user)
```

Elle cree un token avec ces informations :

```java
JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer(issuer)
        .issuedAt(now)
        .expiresAt(now.plus(expirationMinutes, ChronoUnit.MINUTES))
        .subject(user.getEmail())
        .claim("userId", user.getId())
        .claim("fullName", user.getFullName())
        .claim("role", user.getRole().name())
        .build();
```

Claims importants :

| Claim | Description |
|---|---|
| `iss` | application qui a cree le token |
| `iat` | date de creation du token |
| `exp` | date d'expiration du token |
| `sub` | email de l'utilisateur |
| `userId` | id de l'utilisateur |
| `fullName` | nom complet |
| `role` | role applicatif |

Le token est signe avec `HS256` :

```java
JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();
```

Puis encode :

```java
return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
```

## 7. Classe `OpenApiSecurityConfig`

Fichier :

```text
src/main/java/com/bd/patientsmd/security/OpenApiSecurityConfig.java
```

Cette classe configure Swagger/OpenAPI pour accepter un token Bearer.

```java
@OpenAPIDefinition(security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
```

Grace a cette configuration, Swagger affiche un bouton `Authorize`.

Dans Swagger :

1. cliquer sur `Authorize`.
2. coller le token JWT.
3. appeler les routes protegees.

Selon la configuration Swagger, il faut parfois coller seulement le token, et parfois `Bearer <token>`.

## 7.1. Classes d'erreurs Security

Deux classes gerent les erreurs qui arrivent avant les controllers :

```text
src/main/java/com/bd/patientsmd/security/JwtAuthenticationEntryPoint.java
src/main/java/com/bd/patientsmd/security/JwtAccessDeniedHandler.java
```

### `JwtAuthenticationEntryPoint`

Cette classe est appelee quand l'utilisateur n'est pas authentifie.

Exemples :

- token absent.
- token invalide.
- token expire.

Elle retourne :

```http
401 Unauthorized
```

Avec un body JSON propre.

### `JwtAccessDeniedHandler`

Cette classe est appelee quand l'utilisateur est authentifie, mais son role n'est pas autorise.

Exemple :

- un `SECRETARY` essaie d'appeler `/api/patients`.

Elle retourne :

```http
403 Forbidden
```

Avec un body JSON propre.

## 7.2. Classes Refresh Token

Le refresh token utilise ces classes :

| Classe | Role |
|---|---|
| `RefreshToken` | Entite JPA stockee dans `refresh_tokens` |
| `RefreshTokenRepository` | Recherche, sauvegarde et suppression des refresh tokens |
| `RefreshTokenService` | Creation, validation, rotation et revocation |
| `RefreshTokenRequest` | Body de `/api/auth/refresh` et `/api/auth/logout` |

Le refresh token n'est pas un JWT. C'est une valeur aleatoire UUID stockee en base.

Quand `/api/auth/refresh` est appele, l'ancien refresh token est revoque et un nouveau refresh token est genere.

## 8. Modifications dans `UsersServiceImpl`

Fichier :

```text
src/main/java/com/bd/patientsmd/services/UsersServiceImpl.java
```

Cette classe utilise maintenant :

```java
private final PasswordEncoder passwordEncoder;
private final JwtService jwtService;
```

### Creation d'utilisateur

Avant, le mot de passe etait sauvegarde en clair.

Maintenant, il est hashe avec BCrypt :

```java
user.setPassword(passwordEncoder.encode(request.password()));
```

Donc dans la base de donnees, le password ressemble a :

```text
$2a$10$...
```

Le vrai mot de passe n'est plus stocke.

### Login

Pendant le login :

1. on cherche l'utilisateur par email.
2. on verifie que le compte est actif.
3. on verifie que le password existe.
4. on verifie que le role existe.
5. on compare le mot de passe avec BCrypt.
6. on genere le JWT.
7. on genere un refresh token.
8. on retourne les deux tokens.

Code important :

```java
if (!passwordMatches(request.password(), user)) {
    throw new InvalidCredentialsException("Email ou mot de passe incorrect");
}

return new AuthResponse(
        jwtService.generateToken(user),
        refreshTokenService.createRefreshToken(user),
        user.getFullName(),
        user.getRole().name()
);
```

### Migration douce des anciens mots de passe

Comme des utilisateurs existaient peut-etre deja avec des mots de passe en clair, cette methode accepte temporairement les anciens mots de passe :

```java
private boolean passwordMatches(String rawPassword, Users user)
```

Si le mot de passe en base est deja un hash BCrypt, on utilise :

```java
passwordEncoder.matches(rawPassword, savedPassword)
```

Si le mot de passe en base est encore en clair et qu'il correspond, il est automatiquement remplace par un hash BCrypt :

```java
user.setPassword(passwordEncoder.encode(rawPassword));
```

Donc apres le premier login reussi, l'ancien mot de passe en clair est converti en mot de passe hashe.

## 9. Flux complet d'authentification

### Etape 1 : login

Le client envoie :

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

### Etape 2 : reponse

Si les identifiants sont corrects, l'API retourne :

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "4b4e4d9a-7c87-4f76-86fd-3cfd5e9a15cf",
  "fullName": "Amine Bennani",
  "role": "ADMIN"
}
```

### Etape 3 : appel d'une route protegee

Le client doit envoyer le token dans le header :

```http
GET /api/patients
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Etape 4 : verification par Spring Security

Spring Security :

1. recupere le token.
2. verifie la signature avec `JWT_SECRET`.
3. verifie l'expiration.
4. lit le claim `role`.
5. transforme `ADMIN` en `ROLE_ADMIN`.
6. autorise ou refuse la route.

### Etape 5 : refresh token

Quand le token JWT expire, le client peut demander un nouveau couple access token + refresh token :

```http
POST /api/auth/refresh
Content-Type: application/json
```

Body :

```json
{
  "refreshToken": "4b4e4d9a-7c87-4f76-86fd-3cfd5e9a15cf"
}
```

Le refresh token est persiste dans la table `refresh_tokens`.

Au refresh :

1. l'ancien refresh token est verifie.
2. s'il est valide, il est revoque.
3. un nouveau JWT est genere.
4. un nouveau refresh token est genere.

### Logout

Le logout revoque le refresh token :

```http
POST /api/auth/logout
Content-Type: application/json
```

Body :

```json
{
  "refreshToken": "4b4e4d9a-7c87-4f76-86fd-3cfd5e9a15cf"
}
```

## 10. Codes HTTP attendus

| Situation | Code HTTP |
|---|---|
| Pas de token | `401 Unauthorized` |
| Token invalide | `401 Unauthorized` |
| Token expire | `401 Unauthorized` |
| Token valide mais role insuffisant | `403 Forbidden` |
| Login incorrect | `401 Unauthorized` |
| Login correct | `200 OK` |

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

## 11. Tests rapides avec Postman

### Test login

```http
POST http://localhost:8182/api/auth/login
Content-Type: application/json
```

Body :

```json
{
  "email": "amine.bennani@nutricab.ma",
  "password": "AdminNutri2026!"
}
```

### Test route protegee

```http
GET http://localhost:8182/api/patients
Authorization: Bearer <token>
```

### Test pagination

Les endpoints de liste acceptent maintenant :

```http
GET http://localhost:8182/api/patients?page=0&size=10&sort=id,desc
Authorization: Bearer <token>
```

La reponse contient notamment :

```json
{
  "content": [],
  "totalElements": 0,
  "totalPages": 0,
  "size": 10,
  "number": 0
}
```

### Test sans token

```http
GET http://localhost:8182/api/patients
```

Resultat attendu :

```http
401 Unauthorized
```

### Test role insuffisant

Exemple : un utilisateur `SECRETARY` appelle :

```http
GET http://localhost:8182/api/patients
Authorization: Bearer <token-secretary>
```

Resultat attendu :

```http
403 Forbidden
```

## 12. Points importants a retenir

- Le token JWT est genere seulement apres un login correct.
- Le refresh token permet de regenerer un access token sans refaire le login.
- Le backend ne stocke pas les sessions.
- Chaque requete protegee doit envoyer `Authorization: Bearer <token>`.
- Les mots de passe doivent etre stockes avec BCrypt, jamais en clair.
- Le role est stocke dans le JWT avec le claim `role`.
- Le backend convertit `ADMIN` en `ROLE_ADMIN` pour Spring Security.
- `hasRole("ADMIN")` correspond a `ROLE_ADMIN`.
- `hasAnyRole("ADMIN", "NUTRITIONIST")` accepte plusieurs roles.

## 13. Ameliorations possibles

Plus tard, tu peux ameliorer la security avec :

- blacklist/revocation avancee des access tokens si necessaire.
- expiration plus courte du access token.
- politique de mot de passe plus stricte.
- roles plus fins par methode avec `@PreAuthorize`.
