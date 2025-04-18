# API de Gestion de Crèche

Cette application Android intègre une API RESTful pour la gestion d'une crèche, permettant de gérer les parents, les enfants, les disponibilités et les réservations.

## Fonctionnalités de l'API

L'API expose les ressources suivantes :

### Parents

- `GET /api/parents` - Récupérer tous les parents
- `GET /api/parents/{id}` - Récupérer un parent par son ID
- `POST /api/parents` - Créer un nouveau parent
- `PUT /api/parents/{id}` - Mettre à jour un parent
- `DELETE /api/parents/{id}` - Supprimer un parent

### Enfants

- `GET /api/enfants` - Récupérer tous les enfants
- `GET /api/enfants/{id}` - Récupérer un enfant par son ID
- `GET /api/enfants/parent/{parentId}` - Récupérer tous les enfants d'un parent
- `POST /api/enfants` - Créer un nouvel enfant
- `PUT /api/enfants/{id}` - Mettre à jour un enfant
- `DELETE /api/enfants/{id}` - Supprimer un enfant

### Disponibilités

- `GET /api/disponibilites` - Récupérer toutes les disponibilités
- `GET /api/disponibilites/{id}` - Récupérer une disponibilité par son ID
- `GET /api/disponibilites/date/{date}` - Récupérer les disponibilités pour une date donnée
- `POST /api/disponibilites` - Créer une nouvelle disponibilité
- `PUT /api/disponibilites/{id}` - Mettre à jour une disponibilité
- `DELETE /api/disponibilites/{id}` - Supprimer une disponibilité

### Réservations

- `GET /api/reservations` - Récupérer toutes les réservations
- `GET /api/reservations/{id}` - Récupérer une réservation par son ID
- `GET /api/reservations/enfant/{enfantId}` - Récupérer les réservations d'un enfant
- `GET /api/reservations/date/{date}` - Récupérer les réservations pour une date donnée
- `POST /api/reservations` - Créer une nouvelle réservation
- `PUT /api/reservations/{id}` - Mettre à jour une réservation
- `DELETE /api/reservations/{id}` - Supprimer une réservation

## Utilisation de l'API

L'API est accessible à l'adresse `http://localhost:8080` lorsque l'application est en cours d'exécution. Vous pouvez tester l'API directement depuis l'application en utilisant l'écran d'administration de l'API accessible depuis l'écran d'accueil.

### Exemples de requêtes

#### Récupérer tous les parents

```
GET http://localhost:8080/api/parents
```

#### Créer un nouveau parent

```
POST http://localhost:8080/api/parents
Content-Type: application/json

{
  "nom": "Dubois",
  "prenom": "Marie",
  "email": "marie.dubois@example.com",
  "telephone": "0612345678",
  "adresse": "123 Rue des Fleurs, 75003 Paris"
}
```

#### Récupérer les disponibilités pour une date

```
GET http://localhost:8080/api/disponibilites/date/2023-05-15
```

#### Créer une réservation

```
POST http://localhost:8080/api/reservations
Content-Type: application/json

{
  "enfantId": "1",
  "date": "2023-05-15",
  "heureDebut": "08:00",
  "heureFin": "12:00"
}
```

## Modèles de données

### Parent

```json
{
  "id": "string",
  "nom": "string",
  "prenom": "string",
  "email": "string",
  "telephone": "string",
  "adresse": "string",
  "enfants": ["string"]
}
```

### Enfant

```json
{
  "id": "string",
  "nom": "string",
  "prenom": "string",
  "dateNaissance": "string",
  "parentId": "string",
  "informationsSpecifiques": "string"
}
```

### Disponibilité

```json
{
  "id": "string",
  "date": "string",
  "heureDebut": "string",
  "heureFin": "string",
  "capaciteTotale": "number",
  "placesReservees": "number",
  "typeGarde": "REGULIER" | "OCCASIONNEL"
}
```

### Réservation

```json
{
  "id": "string",
  "enfantId": "string",
  "date": "string",
  "heureDebut": "string",
  "heureFin": "string",
  "statut": "EN_ATTENTE" | "CONFIRMEE" | "ANNULEE",
  "creeLe": "number"
}
```

## Notes techniques

- L'API est implémentée avec Ktor, un framework Kotlin pour les applications web et les API.
- Les données sont stockées en mémoire et sont réinitialisées à chaque redémarrage de l'application.
- L'API est accessible uniquement lorsque l'application est en cours d'exécution.