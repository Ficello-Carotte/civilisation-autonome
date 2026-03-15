# Simulation de vie

Ce projet est une application web de simulation de vie, construite avec une architecture découplée frontend et backend.

## Architecture

- **Backend** : API REST et WebSocket avec Spring Boot.
- **Frontend** : Application React (avec Vite) et Phaser pour le rendu du jeu.
- **Services** : PostgreSQL pour la base de données, Redis pour le cache et les états temps réel, Keycloak pour l'authentification.
- **Déploiement** : L'ensemble des services est orchestré avec Docker Compose.

## Prérequis

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)

## Démarrage rapide

1.  Clonez le dépôt :
    ```bash
    git clone https://github.com/civilisation-autonome/civilisation-autonome.git
    cd civilisation-autonome
    ```

2.  Copiez le fichier d'environnement de développement :
    ```bash
    cp .env.development.example .env
    ```
    Ce fichier contient les configurations par défaut pour l'environnement de développement.

## Lancer l'application avec Docker

Le projet utilise des fichiers Docker Compose pour gérer les environnements de développement et de production.

### Mode Développement

Ce mode active le live-reloading pour le frontend et le backend, ce qui permet de voir les changements sans avoir à redémarrer manuellement les conteneurs.

1.  Pour lancer l'application en mode développement :
    ```bash
    docker compose -f docker-compose.yml -f docker-compose.dev.yml up --build -d
    ```

2.  Accédez aux services :
    - **Frontend (Vite)** : [http://localhost:5173](http://localhost:5173)
    - **Backend API** : [http://localhost:8080](http://localhost:8080)
    - **Keycloak (Admin)**: [http://localhost:8180](http://localhost:8180)

**Live-Reload :**
- **Frontend** : Toute modification dans le répertoire `app-frontend/src` est immédiatement visible dans votre navigateur.
- **Backend** : Après une modification dans `app-backend/src`, compilez les classes avec `mvn compile` (ou `mvn package`) dans le répertoire `app-backend`. Spring DevTools redémarrera automatiquement l'application.

### Mode Production

Ce mode est optimisé pour le déploiement. Il construit des images légères et configure les services pour être plus robustes.

1.  Assurez-vous d'avoir un fichier `.env` configuré pour la production (vous pouvez vous baser sur `.env.production.example`).

2.  Pour lancer l'application en mode production :
    ```bash
    docker compose -f docker-compose.yml -f docker-compose.prod.yml up --build -d
    ```

3.  Accédez à l'application :
    - **Frontend (Nginx)** : [http://localhost](http://localhost)

## Tableau des services

| Service       | Port Local (défaut) | Description                                       |
|---------------|---------------------|---------------------------------------------------|
| `app-frontend`| `80` (prod), `5173` (dev) | Interface utilisateur (React/Phaser)              |
| `app-backend` | `8080`              | API et logique métier (Spring Boot)             |
| `postgres`    | `5432`              | Base de données relationnelle                   |
| `redis`       | `6379`              | Cache et messagerie en temps réel               |
| `keycloak`    | `8180`              | Gestion des identités et des accès (OAuth2/OIDC) |

## Qualité du code

Des linters et formateurs sont configurés pour maintenir une base de code propre.

- **Backend** (depuis `app-backend`):
  - `mvn validate` : Vérifie le style (Checkstyle & Spotless).
  - `mvn spotless:apply` : Formate le code.

- **Frontend** (depuis `app-frontend`):
  - `npm run lint` : Vérifie le code avec ESLint.
  - `npm run format` : Formate le code avec Prettier.

Une GitHub Action valide le code à chaque `push`.
