# Documentation Frontend NutriCab

Ce document explique la partie frontend Angular de NutriCab et son lien avec le backend `Patients-MD` et la security JWT.

Objectif : garder une reference claire pour comprendre comment l'interface, les services HTTP, les routes, les guards et les roles fonctionnent ensemble.

## 1. Role Du Frontend

Le frontend `nutricab-front` est l'application Angular utilisee par le cabinet de nutrition.

Il permet de gerer :

- la connexion utilisateur.
- les patients.
- les rendez-vous.
- les consultations.
- les plans alimentaires.
- les utilisateurs systeme pour l'admin.
- la navigation selon le role connecte.

Le frontend ne decide pas seul des droits definitifs. Il cache ou bloque certaines pages pour l'experience utilisateur, mais le backend reste la source de verite avec Spring Security.

## 2. Stack Technique

| Element | Role |
|---|---|
| Angular | Framework frontend |
| Standalone components | Composants sans modules Angular classiques |
| Angular Router | Navigation entre pages |
| Reactive Forms | Formulaires de creation/modification |
| HttpClient | Appels REST vers le backend |
| Guards | Protection des routes cote frontend |
| Interceptor HTTP | Envoi automatique des cookies |
| Bootstrap / CSS | Mise en page et composants visuels |
| Font Awesome | Icones de menu, actions et mot de passe |

## 3. Structure Principale

```text
src/app
|-- app.component.*
|-- app.config.ts
|-- app.routes.ts
|-- core
|   |-- guards
|   |-- interceptors
|   |-- models
|   `-- services
|-- features
|   |-- login
|   |-- patients
|   |-- appointments
|   |-- consultations
|   |-- meal-planning
|   `-- users
`-- shared
    |-- layouts/main-layout
    |-- navbar
    |-- pagination
    `-- table-loading
```

Les dossiers ont des responsabilites separees :

| Dossier | Responsabilite |
|---|---|
| `core/services` | Communication HTTP avec le backend |
| `core/models` | Interfaces TypeScript des donnees |
| `core/guards` | Verification de connexion et role |
| `core/interceptors` | Configuration globale des requetes HTTP |
| `features` | Pages fonctionnelles de l'application |
| `shared` | Layout, navbar, pagination et etat de chargement des tableaux |

## 4. Demarrage Local

Backend :

```bash
cd Patients-MD
mvn spring-boot:run
```

Frontend :

```bash
cd nutricab-front
npm install
npm start
```

URLs principales :

| Application | URL |
|---|---|
| Frontend Angular | `http://localhost:4200` |
| Backend Spring Boot | `http://localhost:8182` |
| API REST | `http://localhost:8182/api/...` |

## 5. Routes Angular

Les routes sont definies dans :

```text
src/app/app.routes.ts
```

La route `/login` est placee en dehors du layout principal. Elle utilise `guestGuard` et n'affiche donc jamais la sidebar.

Toutes les pages metier sont des enfants de `MainLayoutComponent`. Ce layout contient la sidebar et un `router-outlet`, puis `authGuard` protege le parent et ses enfants.

```text
/patients
/appointments
/consultations
/meal-plans
/users
```

La route login est reservee aux visiteurs non authentifies :

```text
/login
```

Si un utilisateur deja connecte ouvre `/login`, `guestGuard` le renvoie vers le `returnUrl` valide ou vers `/patients`.

Les routes admin sont protegees en plus par `adminGuard` :

```text
/users
/users/new
/users/edit/:id
```

## 6. Lazy Loading

Les composants sont charges avec `loadComponent`.

Exemple :

```ts
{
  path: 'patients',
  loadComponent: () => import('./features/patients/patients.component')
    .then(m => m.PatientsComponent)
}
```

Avantages :

- le navigateur ne charge pas tout au demarrage.
- chaque page est chargee au moment ou l'utilisateur y accede.
- le build initial est plus leger.

## 7. Authentification Cote Front

Le service principal est :

```text
src/app/core/services/auth.service.ts
```

Il appelle :

| Methode front | Endpoint backend | Role |
|---|---|---|
| `login()` | `POST /api/auth/login` | Connecter l'utilisateur |
| `refresh()` | `POST /api/auth/refresh` | Regenerer la session |
| `logout()` | `POST /api/auth/logout` | Deconnecter l'utilisateur |

Le backend place l'access token et le refresh token dans des cookies `HttpOnly`. La reponse JSON ne contient aucun token :

```ts
export interface AuthResponse {
  fullName: string;
  role: string;
}
```

Il ne faut donc jamais utiliser `res.accessToken`, ni stocker un JWT dans `localStorage` ou `sessionStorage`.

Le frontend ne lit pas directement le JWT. Il garde seulement des informations simples dans `sessionStorage` :

```text
authenticated=true
role=ADMIN
fullName=Amine Bennani
```

Important : le role dans `sessionStorage` aide l'interface, mais la vraie autorisation est controlee par le backend.

## 8. Cookies Et Interceptor

L'interceptor est :

```text
src/app/core/interceptors/credentials.interceptor.ts
```

Il ajoute `withCredentials: true` a chaque requete HTTP, y compris login, refresh, logout et les appels proteges.

Sans cela, le navigateur ne transmet pas les cookies `access_token` et `refresh_token` au backend.

Flux simplifie :

```text
Angular HttpClient
-> credentialsInterceptor
-> requete avec cookies
-> Spring Security lit access_token
-> endpoint autorise ou refuse
```

Un second interceptor, `auth-refresh.interceptor.ts`, traite les `401` des routes protegees. Il ne traite pas `/auth/login`, `/auth/refresh` ou `/auth/logout`, ce qui evite les boucles de refresh.

Pendant le rendu SSR, cet interceptor laisse passer la requete sans tenter de refresh et sans naviguer. Le serveur Node ne possede pas automatiquement les cookies HttpOnly du navigateur. Le renouvellement est donc effectue apres hydratation, uniquement dans le navigateur.

## 9. Guards

### `authGuard`

Fichier :

```text
src/app/core/guards/auth.guard.ts
```

Role :

- autoriser si l'utilisateur est deja authentifie.
- sinon appeler `/api/auth/refresh`.
- si le refresh reussit, laisser entrer.
- sinon rediriger vers `/login?returnUrl=...`.
- retourner un `UrlTree` au Router, sans appeler `router.navigate()` dans le guard.

Ce comportement evite de perdre la session apres un rafraichissement de page.

`AuthService.refresh()` partage une requete deja en cours avec `shareReplay`. Plusieurs guards ou appels HTTP ne peuvent donc pas lancer deux rotations concurrentes du meme refresh token.

### `guestGuard`

Fichier :

```text
src/app/core/guards/guest.guard.ts
```

Role :

- laisser afficher `/login` si le refresh cookie est absent ou invalide.
- restaurer la session si le cookie est encore valide.
- renvoyer un utilisateur authentifie vers le `returnUrl`.
- refuser les `returnUrl` externes, doubles (`//...`) ou pointant vers `/login`.

### `adminGuard`

Fichier :

```text
src/app/core/guards/admin.guard.ts
```

Role :

- autoriser seulement si `role === 'ADMIN'`.
- rediriger vers `/patients` si l'utilisateur n'est pas admin.

Le backend applique aussi la regle :

```text
/api/users/** -> ADMIN
```

Donc meme si quelqu'un force l'URL dans le navigateur, le backend bloque les actions non autorisees.

## 10. Navbar Et Login

La navbar/sidebar est rendue par `MainLayoutComponent`, uniquement autour des pages protegees.

Fichiers :

```text
src/app/shared/layouts/main-layout/main-layout.component.*
src/app/shared/navbar/navbar.component.*
```

Le lien `Users` est affiche seulement pour l'admin.

Le bouton `Deconnecter` appelle :

```text
POST /api/auth/logout
```

Puis redirige vers :

```text
/login
```

### Role Admin Avant Le Premier Affichage

Pendant le SSR, le serveur ne peut pas lire `sessionStorage`. Pour eviter que le lien `Users` soit absent au premier affichage puis apparaisse apres hydratation, `index.html` lit uniquement la valeur non sensible `sessionStorage.role` avant le rendu visuel.

Si cette valeur vaut `ADMIN`, la classe `auth-role-admin` est ajoutee a l'element `<html>`. Le CSS de la navbar peut alors afficher le lien `Users` des la premiere frame, avant le demarrage d'Angular.

`AuthService` synchronise ensuite cette classe apres login, refresh et logout. Le lien existe dans la structure SSR, mais reste masque pour les roles non admin. `adminGuard` et la protection backend `/api/users/**` restent les vraies protections d'acces.

### Role Admin Avant Le Premier Affichage

Pendant le SSR, le serveur ne peut pas lire `sessionStorage`. Pour eviter que le lien `Users` soit absent au premier affichage puis apparaisse apres hydratation, `index.html` lit uniquement la valeur non sensible `sessionStorage.role` avant le rendu visuel.

Si cette valeur vaut `ADMIN`, la classe `auth-role-admin` est ajoutee a l'element `<html>`. Le CSS de la navbar peut alors afficher le lien `Users` des la premiere frame, avant le demarrage d'Angular.

`AuthService` synchronise ensuite cette classe apres login, refresh et logout. Le lien existe dans la structure SSR, mais reste masque pour les roles non admin. `adminGuard` et la protection backend `/api/users/**` restent les vraies protections d'acces.

## 11. Services Front Et Endpoints Back

Chaque service Angular correspond a un groupe d'endpoints backend.

| Service Angular | Endpoint backend | Fonction |
|---|---|---|
| `AuthService` | `/api/auth` | login, refresh, logout |
| `PatientsService` | `/api/patients` | CRUD patients |
| `AppointmentsService` | `/api/appointments` | CRUD rendez-vous |
| `ConsultationsService` | `/api/consultations` | CRUD consultations |
| `MealplanningService` | `/api/meal-plan` | CRUD plans alimentaires |
| `UserService` | `/api/users` | CRUD utilisateurs, statut, filtre, role |

## 12. Liaison Fonctionnelle Front / Back / Security

### Connexion admin

```text
1. L'utilisateur ouvre /login.
2. Angular envoie POST /api/auth/login.
3. Le backend verifie email, mot de passe, active et role.
4. Le backend genere access_token et refresh_token.
5. Les tokens sont places dans des cookies HttpOnly.
6. Angular stocke authenticated, role et fullName dans sessionStorage.
7. Angular redirige vers /patients ou returnUrl.
8. La navbar affiche les menus selon le role.
```

### Consultation d'une page protegee

```text
1. L'utilisateur va sur /patients.
2. authGuard verifie la session frontend.
3. Si besoin, authGuard appelle /api/auth/refresh.
4. Le composant appelle PatientsService.getAllPatients().
5. credentialsInterceptor envoie les cookies.
6. Spring Security valide le JWT.
7. Le controller backend retourne les donnees autorisees.
```

### Creation d'un utilisateur

```text
1. L'admin ouvre /users/new.
2. adminGuard verifie role ADMIN cote frontend.
3. Le formulaire envoie POST /api/users.
4. Spring Security verifie ROLE_ADMIN cote backend.
5. Le backend hash le mot de passe avec BCrypt.
6. Le nouvel utilisateur est sauvegarde en base.
```

### Erreur `403 Forbidden`

Un `403` signifie :

```text
L'utilisateur est connecte, mais son role ne permet pas cette action.
```

Exemples :

- `SECRETARY` appelle `/api/patients`.
- `NUTRITIONIST` appelle `/api/users`.
- utilisateur non admin essaie de creer un user.

## 13. Correspondance Des Roles

| Role | Acces principal |
|---|---|
| `ADMIN` | Gestion globale, utilisateurs inclus |
| `NUTRITIONIST` | Patients, consultations, plans alimentaires autorises |
| `SECRETARY` | Rendez-vous principalement |
| `PATIENT` | Ses donnees autorisees |

Le frontend peut masquer des menus, mais le backend controle toujours les vrais acces.

## 14. Formulaires Importants

### Users

Creation :

- `fullName`
- `email`
- `password`
- `role`
- `active`

Modification :

- `fullName`
- `email`
- `role`
- `active`

Le mot de passe n'est pas affiche dans le formulaire de modification. Le changement de mot de passe passe par une route separee :

```text
PATCH /api/users/{id}/password
```

### Patients

Le formulaire patient envoie les informations medicales de base :

- nom complet.
- date de naissance.
- telephone.
- taille.
- poids initial.
- objectif.

Selon le role connecte, le backend peut rattacher ou filtrer les donnees.

## 15. Points A Verifier En Cas De Probleme

### La page revient vers login au rafraichissement

Verifier :

- `authGuard` appelle bien `refresh()`.
- le cookie `refresh_token` existe dans le navigateur.
- `withCredentials: true` est active.
- le backend autorise CORS avec `http://localhost:4200`.
- `auth-refresh.interceptor.ts` ne redirige pas pendant le rendu SSR.
- le frontend est ouvert avec le meme hostname que celui autorise (`localhost`, pas un melange avec `127.0.0.1`).

### Une action retourne `403`

Verifier :

- le role de l'utilisateur connecte.
- la route backend concernee dans `SecurityConfig`.
- les annotations `@PreAuthorize`.
- le menu frontend ne suffit pas a donner le droit.

### Une requete retourne `401`

Verifier :

- l'utilisateur est connecte.
- le cookie `access_token` n'est pas expire.
- le refresh token est valide.
- la requete Angular envoie bien les cookies.

## 16. Fichiers A Connaitre

| Fichier | Pourquoi il est important |
|---|---|
| `src/app/app.routes.ts` | Routes, lazy loading et guards |
| `src/app/app.config.ts` | Router, HttpClient et interceptor |
| `src/app/core/services/auth.service.ts` | Login, refresh, logout |
| `src/app/core/guards/auth.guard.ts` | Protection des pages connectees |
| `src/app/core/guards/guest.guard.ts` | Empêche un utilisateur connecte d'afficher login |
| `src/app/core/guards/admin.guard.ts` | Protection des pages admin |
| `src/app/core/interceptors/credentials.interceptor.ts` | Envoi des cookies |
| `src/app/core/interceptors/auth-refresh.interceptor.ts` | Refresh global des requetes en `401` |
| `src/app/shared/layouts/main-layout/main-layout.component.*` | Sidebar des pages protegees |
| `src/app/shared/navbar/navbar.component.*` | Menu, role admin, logout |
| `src/app/shared/table-loading/table-loading.component.*` | Spinner reutilisable dans les tableaux |
| `src/app/features/users` | Gestion des utilisateurs |

## 17. Resume Pour Soutenance

Le frontend Angular communique avec le backend Spring Boot par des services HTTP.

La connexion passe par `/api/auth/login`. Le backend met les tokens JWT dans des cookies `HttpOnly`, puis Angular garde seulement l'etat minimal de session dans `sessionStorage`.

Toutes les pages metier sont protegees par `authGuard`. Les pages utilisateurs sont protegees par `adminGuard`.

Chaque requete Angular envoie automatiquement les cookies grace a `credentialsInterceptor`. Spring Security valide ensuite le JWT et le role avant d'autoriser l'acces au controller.

La securite reelle est donc cote backend, tandis que le frontend ameliore la navigation et evite d'afficher des actions impossibles pour le role connecte.

## 18. Ameliorations Ajoutees Pour Un Niveau Plus Professionnel

### 18.1. Configuration API Centralisee

Avant, chaque service Angular contenait directement l'URL du backend.

Maintenant, l'URL de base est centralisee dans :

```text
src/app/core/config/api.config.ts
```

Les services utilisent `API_BASE_URL` :

```text
src/app/core/services/auth.service.ts
src/app/core/services/patients.service.ts
src/app/core/services/appointments.service.ts
src/app/core/services/consultations.service.ts
src/app/core/services/mealplanning.service.ts
src/app/core/services/user.service.ts
```

Cela permet de changer l'URL API a un seul endroit et evite les duplications.

### 18.2. Refresh Token Global Cote Frontend

Le refresh global est gere par :

```text
src/app/core/interceptors/auth-refresh.interceptor.ts
```

Fonctionnement :

1. une requete API retourne `401 Unauthorized`.
2. l'interceptor appelle `POST /api/auth/refresh`.
3. si le refresh reussit, la requete initiale est rejouee.
4. si le refresh echoue, la session locale est nettoyee et l'utilisateur revient vers `/login`.

L'interceptor est branche dans :

```text
src/app/app.config.ts
```

### 18.3. Pagination UI

Le composant reutilisable de pagination est :

```text
src/app/shared/pagination/pagination.component.ts
```

Il utilise la reponse `Page<T>` retournee par Spring Data :

```text
content
totalElements
totalPages
size
number
first
last
empty
```

Les pages suivantes affichent maintenant une pagination :

```text
src/app/features/patients/patients.component.*
src/app/features/appointments/appointments.component.*
src/app/features/consultations/consultations.component.*
src/app/features/meal-planning/meal-planning.component.*
src/app/features/users/users.component.*
```

Les services retournent maintenant `Page<T>` pour les listes, recherches et filtres.

### 18.4. Spinner Reutilisable Pour Les Tableaux

Le composant commun est :

```text
src/app/shared/table-loading/table-loading.component.*
```

Il s'utilise comme une vraie ligne de tableau :

```html
<tr appTableLoading [loading]="loading" [colspan]="8"></tr>
```

Le composant :

- affiche un spinner Bootstrap centre dans une ligne de hauteur stable.
- adapte la cellule au nombre de colonnes avec `colspan`.
- expose `aria-busy` et un texte masque pour les lecteurs d'ecran.
- masque completement sa ligne quand `loading` devient faux.

Dans chaque tableau, les lignes de donnees et le bloc `@empty` sont places dans `@if (!loading)`. Le message vide ne peut donc apparaitre qu'apres la fin de la requete.

Les cinq pages concernees sont :

```text
/patients
/appointments
/consultations
/meal-plans
/users
```

### 18.5. Chargement Compatible SSR Et Hydratation

Chaque page de liste initialise `loading = true`. Son `ngOnInit()` lance les appels HTTP uniquement lorsque `isPlatformBrowser(PLATFORM_ID)` retourne `true`.

Le HTML SSR contient donc directement le tableau et son spinner, jamais un faux message `Aucune donnee trouvee`. Apres hydratation :

```text
1. le navigateur affiche le spinner prerendu.
2. `ngOnInit()` cote navigateur lance la requete API avec les cookies.
3. loading reste vrai pendant la requete.
4. au succes, les vraies lignes remplacent le spinner.
5. si la liste est reellement vide, le message @empty apparait seulement maintenant.
```

Le meme cycle est reutilise pour la recherche, les filtres et la pagination.

`afterNextRender()` n'est pas utilise pour charger les donnees. Angular precise qu'un composant n'est pas garanti d'etre hydrate lorsque ce callback s'execute. Une reponse HTTP rapide pouvait donc modifier l'etat avant la fin de l'hydratation, puis laisser le DOM SSR bloque sur le spinner.

### 18.6. Verification Des Corrections

La commande suivante compile les bundles navigateur et serveur et prerend les routes :

```bash
npm run build
```

La verification attendue pour chaque route de tableau est :

| Etat | Spinner | Lignes | Message vide |
|---|---:|---:|---:|
| SSR / requete en cours | oui | non | non |
| Reponse avec donnees | non | oui | non |
| Reponse vide | non | non | oui |
