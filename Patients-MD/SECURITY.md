# Documentation Security JWT Par Cookies HttpOnly

Ce document explique la partie security ajoutee dans le projet `Patients-MD`.

L'objectif est de securiser l'API avec un JWT place dans un cookie `HttpOnly`. Le `BearerTokenResolver` accepte aussi le header suivant pour les outils techniques comme Swagger ou Postman :

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

CSRF est actuellement desactive. L'API est stateless, mais les JWT sont transportes par cookies : en production, `SameSite` et la liste CORS reduisent le risque sans remplacer une protection CSRF complete. Une strategie CSRF explicite est donc recommandee si le frontend et l'API deviennent cross-site.

### Session stateless

```java
.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
```

Cela veut dire que Spring ne garde pas de session utilisateur cote serveur.

A chaque requete protegee, le navigateur envoie normalement :

```http
Cookie: access_token=<jwt>
```

Le header `Authorization: Bearer <token>` reste disponible comme fallback pour Swagger et les clients non navigateur.

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

1. de demander le token au `BearerTokenResolver` personnalise.
2. de lire en priorite le cookie `access_token`.
3. de conserver le header `Authorization: Bearer` comme fallback.
4. de decoder le JWT et verifier sa signature.
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
| `RefreshTokenRequest` | Ancien body encore accepte en compatibilite par refresh et logout |

Le refresh token n'est pas un JWT. C'est une valeur aleatoire UUID stockee en base et envoyee au navigateur dans le cookie `refresh_token`.

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
8. on retourne un `AuthSession` interne au controller.

Code important :

```java
if (!passwordMatches(request.password(), user)) {
    throw new InvalidCredentialsException("Email ou mot de passe incorrect");
}

return new AuthSession(
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

Si les identifiants sont corrects, l'API pose deux headers `Set-Cookie` :

```text
access_token=<jwt>; HttpOnly; Path=/; Max-Age=...
refresh_token=<uuid>; HttpOnly; Path=/api/auth; Max-Age=...
```

Le body JSON contient uniquement les informations utiles a l'interface :

```json
{
  "fullName": "Amine Bennani",
  "role": "ADMIN"
}
```

### Etape 3 : appel d'une route protegee

Le navigateur envoie automatiquement le cookie `access_token` lorsque le frontend utilise `withCredentials: true` :

```http
GET /api/patients
Cookie: access_token=eyJhbGciOiJIUzI1NiJ9...
```

### Etape 4 : verification par Spring Security

Spring Security :

1. cherche d'abord `access_token` dans les cookies.
2. utilise le header Bearer en fallback si aucun cookie n'est disponible.
3. verifie la signature avec `JWT_SECRET`.
4. verifie l'expiration.
5. lit le claim `role`.
6. transforme `ADMIN` en `ROLE_ADMIN`.
7. autorise ou refuse la route.

### Etape 5 : refresh token

Quand le token JWT expire, le client demande une nouvelle session. Le cookie `refresh_token` est envoye automatiquement :

```http
POST /api/auth/refresh
Content-Type: application/json
```

Le body peut etre vide (`{}`). Le body historique avec `refreshToken` reste accepte uniquement pour compatibilite.

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

Le body peut etre vide. Le backend lit le cookie, revoque le token en base, puis expire `access_token` et `refresh_token`.

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
```

Postman doit conserver les cookies recus pendant le login. Pour un test sans cookie jar, le header `Authorization: Bearer <token>` reste supporte.

### Test pagination

Les endpoints de liste acceptent maintenant :

```http
GET http://localhost:8182/api/patients?page=0&size=10&sort=id,desc
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
- Chaque requete Angular protegee doit utiliser `withCredentials: true` pour envoyer `access_token`.
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

## 14. Liaison Security Avec Le Frontend

Le frontend Angular est documente dans :

```text
../nutricab-front/FRONTEND.md
```

Cette section explique comment la security backend est utilisee par Angular.

### 14.1. Login Depuis Angular

Flux complet :

```text
1. L'utilisateur ouvre /login.
2. Angular appelle POST /api/auth/login.
3. Le backend verifie email, mot de passe, active et role.
4. Le backend genere un access token JWT et un refresh token.
5. Le backend place les tokens dans des cookies HttpOnly.
6. Angular stocke seulement authenticated, role et fullName dans sessionStorage.
7. Angular redirige vers /patients ou vers returnUrl.
```

Les cookies HttpOnly ne sont pas lisibles par JavaScript. C'est voulu : cela limite l'exposition du JWT cote navigateur.

`AuthResponse` ne contient donc que :

```json
{
  "fullName": "Amine Bennani",
  "role": "ADMIN"
}
```

Angular n'utilise jamais `res.accessToken` et ne stocke aucun JWT dans le stockage web.

### 14.2. Envoi Des Cookies

Angular utilise un interceptor :

```text
src/app/core/interceptors/credentials.interceptor.ts
```

Son role est d'ajouter :

```ts
withCredentials: true
```

Sans cette option, le navigateur n'envoie pas les cookies au backend, et Spring Security ne peut pas authentifier la requete.

### 14.3. Refresh Au Rechargement De Page

Au refresh navigateur, `sessionStorage` peut ne pas suffire pour reconstruire l'etat complet.

Le guard Angular fait donc :

```text
1. Si authenticated=true, il laisse passer.
2. Sinon, il appelle POST /api/auth/refresh.
3. Si le refresh token est valide, Angular restaure la session.
4. Sinon, Angular redirige vers /login?returnUrl=...
```

Cela evite l'effet ou la page login apparait inutilement avant de revenir sur la page recente.

Les guards sont des `CanActivateFn` et retournent un `UrlTree` en cas de refus. Ils ne lancent pas directement `router.navigate()`.

Pendant le rendu SSR, le guard laisse le rendu initial continuer. Le refresh et les redirections d'authentification sont executes apres hydratation dans le navigateur, car le processus Node ne possede pas automatiquement les cookies HttpOnly du navigateur.

### 14.4. Difference Entre Guard Front Et Security Back

| Niveau | Fichier | Role |
|---|---|---|
| Frontend | `auth.guard.ts` | bloque les pages si l'utilisateur n'est pas connecte |
| Frontend | `guest.guard.ts` | empeche un utilisateur connecte d'afficher `/login` |
| Frontend | `admin.guard.ts` | bloque les pages `/users` si le role n'est pas `ADMIN` |
| Backend | `SecurityConfig.java` | protege les endpoints REST |
| Backend | `@PreAuthorize` | protege les methodes controller avec des regles metier |
| Backend | `AuthorizationService` | verifie les droits fins selon les donnees |

Le guard frontend ameliore l'experience utilisateur. Spring Security protege reellement les donnees.

### 14.5. Exemple `403 Forbidden`

Cas :

```text
POST http://localhost:8182/api/users
```

Regle backend :

```text
/api/users/** -> ADMIN
```

Si l'utilisateur connecte n'a pas le role `ADMIN`, le backend retourne :

```text
403 Forbidden
```

Ce comportement est normal. Il signifie que le token est reconnu, mais que le role est insuffisant.

### 14.6. Exemple `401 Unauthorized`

Un `401` arrive si :

- aucun cookie `access_token` n'est envoye.
- le token est expire.
- le token est invalide.
- le refresh token est invalide ou absent.

Dans ce cas, Angular doit rediriger vers `/login`.

### 14.7. Compte Admin Local

Compte de test documente :

```json
{
  "email": "amine.bennani@nutricab.ma",
  "password": "AdminNutri2026!"
}
```

Ce compte doit exister dans la table `users` avec :

```text
role = ADMIN
active = true
```

Sinon, la connexion admin ne peut pas fonctionner et la creation d'autres utilisateurs restera bloquee.

## 15. Refresh Global Cote Frontend

Le frontend gere maintenant automatiquement certains `401 Unauthorized`.

Fichier principal :

```text
../nutricab-front/src/app/core/interceptors/auth-refresh.interceptor.ts
```

Flux :

1. une requete protegee retourne `401`.
2. Angular appelle `POST /api/auth/refresh`.
3. les nouveaux cookies sont poses par le backend.
4. Angular rejoue la requete initiale.
5. si le refresh echoue, Angular nettoie la session et redirige vers `/login`.

Cet interceptor complete :

```text
../nutricab-front/src/app/core/interceptors/credentials.interceptor.ts
```

`credentials.interceptor.ts` envoie les cookies, tandis que `auth-refresh.interceptor.ts` gere le renouvellement de session quand l'access token expire.

Protections supplementaires :

- les endpoints login, refresh et logout sont exclus du retry automatique.
- l'interceptor de refresh est inactif pendant le SSR.
- `AuthService.refresh()` partage la requete en cours avec `shareReplay`.
- une seule rotation du refresh token peut donc etre lancee a la fois dans le navigateur.
- le `returnUrl` est conserve pour revenir a la page demandee apres reconnexion.
- le role non sensible stocke dans `sessionStorage` permet d'afficher le menu admin avant l'hydratation, sans lire le cookie JWT HttpOnly.
- `adminGuard` et `/api/users/**` restent obligatoires : la classe CSS de sidebar n'est qu'un traitement visuel.
