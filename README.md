# Backend Support - Gestion des Tickets

## Description
Ce projet est une API REST pour la gestion des tickets et des messages associés, développée avec Spring Boot. Elle permet la création, la modification et la consultation des tickets, ainsi que l'envoi de messages avec ou sans fichiers. De plus, un système de gestion de rôle est intégré via un service JWT.

## Routes

### TicketController

#### Récupérer les tickets par rôle et email
- **Méthode** : `GET`
- **Endpoint** : `/ticket/list/{role}/{mail}`
- **Description** : Récupère la liste des tickets associés à un rôle et un email spécifique.
- **Paramètres** :
  - `role` : Le rôle de l'utilisateur (`support`, `user`).
  - `mail` : L'email de l'utilisateur.
  
#### Mettre à jour l'état d'un ticket
- **Méthode** : `GET`
- **Endpoint** : `/ticket/{id}/{etatId}`
- **Description** : Met à jour l'état d'un ticket en fonction de l'ID du ticket et de l'ID de l'état.
- **Paramètres** :
  - `id` : L'ID du ticket.
  - `etatId` : L'ID de l'état à appliquer.

#### Ajouter un nouveau ticket
- **Méthode** : `POST`
- **Endpoint** : `/ticket`
- **Description** : Ajoute un nouveau ticket.
- **Corps de la requête** :
  - `title` : Le titre du ticket.
  - `creerPar` : L'email ou le nom de la personne créant le ticket.

### MessageController

#### Récupérer tous les messages
- **Méthode** : `GET`
- **Endpoint** : `/ticket/messages`
- **Description** : Récupère la liste de tous les messages.

#### Récupérer un message par ID
- **Méthode** : `GET`
- **Endpoint** : `/ticket/messages/{id}`
- **Description** : Récupère un message spécifique par son ID.
- **Paramètres** :
  - `id` : L'ID du message.

#### Récupérer les messages d'un ticket
- **Méthode** : `GET`
- **Endpoint** : `/ticket/messages/ticket/{id}`
- **Description** : Récupère la liste des messages associés à un ticket spécifique.
- **Paramètres** :
  - `id` : L'ID du ticket.

#### Télécharger un fichier joint à un message
- **Méthode** : `GET`
- **Endpoint** : `/ticket/messages/file/{id}`
- **Description** : Télécharge un fichier joint à un message en fonction de l'ID du fichier.
- **Paramètres** :
  - `id` : L'ID du fichier.

#### Visualiser un fichier joint à un message
- **Méthode** : `GET`
- **Endpoint** : `/ticket/messages/view/{id}`
- **Description** : Permet de visualiser un fichier joint à un message en fonction de l'ID du fichier.
- **Paramètres** :
  - `id` : L'ID du fichier.

#### Ajouter un message avec fichiers joints
- **Méthode** : `POST`
- **Endpoint** : `/ticket/messages`
- **Description** : Ajoute un nouveau message avec des fichiers optionnels.
- **Paramètres** :
  - `content` : Contenu du message (facultatif).
  - `envoyePar` : L'email de l'utilisateur envoyant le message.
  - `ticketId` : L'ID du ticket associé.
  - `files` : Liste de fichiers joints (facultatif).

### JwtController

#### Décoder un token JWT
- **Méthode** : `POST`
- **Endpoint** : `/ticket/decode-token`
- **Description** : Décode un token JWT pour récupérer l'email et le rôle de l'utilisateur.
- **Paramètres** :
  - `token` : Le token JWT à décoder.

## Services

### TicketService
- Contient la logique métier pour la gestion des tickets, y compris la récupération, l'ajout et la mise à jour des états des tickets.

### MessageService
- Gère la logique pour la récupération et l'ajout de messages, ainsi que la gestion des fichiers joints.

### JwtService
- Permet l'extraction des informations utilisateur à partir d'un token JWT.

## Installation

1. Clonez ce dépôt.
   ```bash
   git clone https://github.com/FabioLousado/app_support.git
2. Accédez au dossier du projet :
    ```bash
    cd app_support

3. Installez Eclipse :
    ```bash
    mvn install

## Utilisation

1. Créez un fichier .env à la racine du projet avec les variables suivantes :

    ```env
    SERVER_PORT=8083
    DATABASE_HOST=localhost
    DATABASE_PORT=5432
    DATABASE_NAME=support_db

2. Démarrez le serveur :
    ```bash
    mvn spring-boot:run

Le front doit être démarré pour le bon fonctionnement de l'application : [support_pst](https://github.com/RomainZabeth/support-pst)



