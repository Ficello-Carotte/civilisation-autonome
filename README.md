# Simulation de vie

Architecture du projet :

```
Docker Compose
├── PostgreSQL   persistance (joueurs, scores, historique)
├── Redis        état temps réel, positions, rooms
├── Keycloak     auth (OAuth2 / OIDC)
├── app-backend  Spring Boot + Dominion ECS
└── app-frontend Nginx + build React/Phaser/Vite/Zustand
```

## Environnements (dev vs prod)

- **Dev / local** : utilise `.env` dérivé de `.env.development.example` (valeurs pour travailler en local ou Docker en dev).
- **Prod / déploiement** : utilise des variables dérivées de `.env.production.example` (à définir sur le serveur ou l’orchestrateur, sans committer de vrais secrets).

En local, après avoir copié le bon fichier en `.env`, Compose lira automatiquement `.env` au lancement.

**Compose dev vs prod (même Dockerfiles de base) :**

| Fichier | Rôle |
|--------|------|
| `docker-compose.yml` | Base commune (tous les services). |
| `docker-compose.dev.yml` | Override **dev** : live reload (backend + frontend), profiler JFR, profil Spring `dev`, Actuator, SQL en log. |
| `docker-compose.prod.yml` | Override **prod** : `restart: unless-stopped`, profil Spring `prod`, limites mémoire, Keycloak en `start`. |

- **Un seul jeu de Dockerfiles** (front + back) : le backend a un stage **`dev`** en plus du stage par défaut (JRE). En dev on build avec `target: dev` pour avoir le JDK dans le conteneur (profiler, debug). En prod on utilise le stage par défaut (JRE, plus léger).

1. Copier les variables d’environnement (dev) :
   ```bash
   cp .env.development.example .env
   ```

2. Lancer la stack :
   - **Dev** : `docker compose -f docker-compose.yml -f docker-compose.dev.yml up -d --build`
   - **Prod** (ex. sur un VPS) : `docker compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build`
   - **Sans override** (base seule) : `docker compose up -d --build`

3. Accès au site et aux services :

| Mode | Commande Compose | URL du site |
|------|------------------|-------------|
| **Dev** (live reload) | `-f docker-compose.yml -f docker-compose.dev.yml` | **http://localhost:5173** (Vite) |
| **Base / Prod** | `up -d` sans override, ou avec `docker-compose.prod.yml` | **http://localhost** (port 80, Nginx) |

Autres URLs (tous modes) :
- Backend : http://localhost:8080 (ex. `/health`, `/actuator/health`)
- Keycloak : http://localhost:8180 (admin ; identifiants dans `.env` : `KEYCLOAK_ADMIN` / `KEYCLOAK_ADMIN_PASSWORD`)
- PostgreSQL : `localhost:5432`, Redis : `localhost:6379`

## Développement local

### Backend (Spring Boot)

- Prérequis : JDK 21, Maven, PostgreSQL et Redis en local (ou via `docker compose up postgres redis -d`).
- Depuis `app-backend` :
  ```bash
  mvn spring-boot:run
  ```
  (Ou ajouter le Maven Wrapper et utiliser `./mvnw spring-boot:run`.)

Structure standard Spring Boot (`app-backend/src/main/java/dev/simulation/`) :
- **controller** : REST (ex. `HealthController`)
- **service** : logique métier, `@Transactional` (ex. `JoueurService`)
- **repository** : Spring Data JPA, `JpaRepository` (ex. `JoueurRepository`, `ScoreRepository`)
- **entity** : entités JPA
- **config** : configuration (CORS, WebSocket, etc.)
- **websocket** : handler WebSocket
- **ecs** : monde Dominion ECS (stub pour l’instant)

### Frontend (Vite + React + Phaser + Zustand)

- Depuis `app-frontend` :
  ```bash
  npm install
  npm run dev
  ```
  Le proxy Vite redirige `/api` vers le backend (port 8080) et `/ws` en WebSocket.

Structure standard React / Vite (`app-frontend/src/`) :
- **components/** : composants React (ex. `GameView`)
- **hooks/** : hooks personnalisés (ex. `useWebSocket`)
- **utils/** : fonctions pures, helpers (ex. `getWebSocketUrl`)
- **store/** : état global Zustand (ex. `appStore`)
- **App.tsx**, **main.tsx** : point d’entrée
- Alias d’import **`@/`** → `src/` (ex. `@/components/GameView`)

Pour débugger le front : installer l’extension **React DevTools** dans le navigateur, puis ouvrir les outils développeur (F12) pour accéder aux onglets **Components** et **Profiler**.

### WebSocket

- **Backend** : endpoint `ws://.../ws/game` (Spring WebSocket), handler dans `app-backend/.../websocket/GameWebSocketHandler` (broadcast, à brancher sur l’ECS).
- **Frontend** : `useWebSocket()` (hook dans `src/hooks/useWebSocket.ts`) pour se connecter, envoyer et recevoir des messages. En prod, Nginx proxy `/ws/` vers le backend.

### Keycloak (auth)

- Service Keycloak 24 en mode `start-dev` (adapté au dev local).
- Console d’admin : http://localhost:8180 (créer un realm, des clients pour le front et le backend).
- L’intégration Spring Security OAuth2 (validation JWT) et le login côté front (Keycloak JS ou redirect) se feront au moment où tu protègeras les endpoints et le WebSocket.

### Live reload (mode dev Docker)

Avec `docker-compose.dev.yml`, le site est servi par Vite : **http://localhost:5173**.

- **Backend** : le volume `./app-backend/target/classes` est monté. Après une modif, lance `mvn compile` (ou `mvn package`) dans `app-backend` ; Spring DevTools redémarre le conteneur. Une fois avant la première utilisation : `mvn package` dans `app-backend` pour générer le JAR (PropertiesLauncher) et `target/classes`.
- **Frontend** : le code est monté, le serveur Vite tourne dans le conteneur ; la modification d’un fichier déclenche le HMR (hot reload) automatiquement.

### Profil dev et profiler (backend)

- Avec `docker-compose.dev.yml`, le backend utilise le profil Spring `dev` et l’image **dev** (JDK).
- `application-dev.yml` : SQL en log, Actuator (`/actuator/health`, `/actuator/metrics`), niveau DEBUG pour `dev.simulation`.
- **Java Flight Recorder (JFR)** est activé par défaut en dev : enregistrement continu dans le conteneur (`/tmp/recording.jfr`, max 100 Mo, 1 jour). Pour analyser : `docker cp simulation-backend:/tmp/recording.jfr .` puis ouvrir avec JDK Mission Control, IntelliJ (Run → Open Flight Recorder Snapshot) ou `jfr view recording.jfr`.

## Base saine dev / prod

- **`.dockerignore`** (backend + frontend) : exclut `target/`, `node_modules/`, `.git`, etc. pour des builds plus rapides et un contexte propre.
- **Backend** : conteneur tourne en **utilisateur non-root** (`appuser`) ; **profil `prod`** (`application-prod.yml`) : pas de SQL en log, Actuator limité à `/actuator/health`, pas de détails santé exposés.
- **Actuator** : en base seul `health` est exposé ; le profil `dev` ouvre `info` et `metrics`.
- **Nginx** : headers de sécurité (X-Frame-Options, X-Content-Type-Options, X-XSS-Protection, Referrer-Policy) ; cache long (1 an) pour les assets statiques hashés.
- **Compose** : healthcheck sur le backend (`/actuator/health`) ; le frontend ne démarre qu’une fois le backend healthy.
- **Prod** : `docker-compose.prod.yml` force le profil Spring `prod`, ajoute des **limites mémoire** (deploy.resources) pour éviter qu’un service sature la machine — prises en compte en mode Swarm ; en mode `docker compose` classique, seul le reste (restart, profil) s’applique.

## Lint et bonnes pratiques

À chaque **push / PR**, la GitHub Action (`.github/workflows/lint.yml`) lance :

- **backend-lint** : `mvn validate` (Checkstyle + Spotless) uniquement.
- **frontend-lint** : `npm run lint` (ESLint), `npm run format:check` (Prettier), puis `npm run build`.

Pour formater et vérifier en local avant de pousser :

- **Backend** (dans `app-backend`) :
  - Vérifier le style : `mvn validate`
  - Formater le code : `mvn spotless:apply`
- **Frontend** (dans `app-frontend`) :
  - Linter : `npm run lint` / `npm run lint:fix`
  - Format : `npm run format:check` / `npm run format`

Conseil : générer un `package-lock.json` côté frontend (`npm install` puis commit du lock) pour des builds CI reproductibles.