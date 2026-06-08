
# ReadBrew

This repository is part of the website's backend. - [@ReadBrew (Front-end)](https://github.com/matiasmov/ReadBrew-frontend)

Readbrew is a social network for readers that dynamically recommends coffee/tea for each book they read. Users can register their books in their virtual room, rate them, and write reviews for their followers to see and be inspired by. The social network features: a customized profile picture in a cozy style, a user profile, mutual friends, and more. Another important aspect is that ReadBrew, even though it's a social network, is gamified and intelligent, recommending a specific type of tea/coffee for each book, making each experience unique.

## Technologies & Tools

This project was built using the following technologies:

* **Java 21:** Core programming language.
* **Spring Boot 3.x:** Primary framework for building the REST API.
* **Spring Data JPA / Hibernate:** ORM for database mapping and querying.
* **Spring Security & JWT:** Authentication and authorization control.
* **PostgreSQL:** Relational database management system.
* **Spring Cloud OpenFeign:** Declarative REST client for external API consumption.
* **Maven:** Dependency management and build tool.
* **Docker:** Containerization for the database environment.

## Authors

- [@matiasmov](https://www.github.com/matiasmov)

## Run Locally

Clone the project

```bash
  git clone https://github.com/matiasmov/ReadBrew.git
```

Go to the project directory

```bash
  cd ReadBrew
```

Install dependencies

```bash
  ./mvnw clean install

  OR
  
  .\mvnw.cmd clean install (if you are using Windows and the command above doesn't work...)

  OR

  mvn clean install (if you have Maven on your computer)
```

Start the server

```bash
  ./mvnw spring-boot:run
```

## API Endpoints

Below is the list of available routes in the ReadBrew API. The API follows `v1` versioning, and the base URL for the development environment is `http://localhost:8080`. 

All protected routes require a JWT token to be sent in the request header (`Authorization: Bearer <token>`).

### Authentication (`/api/v1/auth`)
Endpoints responsible for registering new users and issuing access tokens.

| Method | Endpoint | Description | Required Data / Params | Access |
| :--- | :--- | :--- | :--- | :--- |
| **POST** | `/api/v1/auth/login` | Authenticates a user and returns a JWT Token. Includes a temporary lockout after multiple failed attempts. | **Body (JSON):** `AuthenticationDTO` <br>*(email: String, password: String)* | Public |
| **POST** | `/api/v1/auth/register` | Registers a new user account in the system. | **Body (JSON):** `RegisterDTO` | Public |

### Public Catalog & Search
Endpoints for viewing the book collection and searching for new titles.

| Method | Endpoint | Description | Required Data / Params | Access |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/public/library` | Returns the public library with already mapped books. | **Query Params:** <br>`page` (int, default: 0)<br>`size` (int, default: 30) | Public |
| **GET** | `/api/v1/catalog/search` | Searches for external books using the Google Books API integration. | **Query Param:** <br>`title` (String, required) | Public |

### Reading Diary / My Room (`/api/v1/my-room`)
Core application endpoints for managing personal libraries, reading status, and statistics.

| Method | Endpoint | Description | Required Data / Params | Access |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/my-room` | Returns all reading diary entries for the authenticated user. | None | Private |
| **GET** | `/api/v1/my-room/my-books` | Alternative endpoint to list books in the user's diary. | None | Private |
| **POST** | `/api/v1/my-room/add` | Adds a new book to the personal diary. | **Body (JSON):** `BookResponseDTO` | Private |
| **PATCH** | `/api/v1/my-room/{diaryId}/start` | Changes a specific book's reading status to "Reading". | **Path Variable:** <br>`diaryId` (Long) | Private |
| **PATCH** | `/api/v1/my-room/{diaryId}/complete`| Finishes a reading. Allows adding reviews, ratings, and associating a coffee. | **Path Variable:** `diaryId` (Long) <br>**Body (JSON):** `CompleteReadingDTO` | Private |
| **DELETE**| `/api/v1/my-room/{diaryId}` | Removes a diary entry or marks a book as abandoned. | **Path Variable:** <br>`diaryId` (Long) | Private |
| **GET** | `/api/v1/my-room/stats` | Returns consolidated user statistics (e.g., total read books, accumulated XP). | None | Private |

### Coffee (`/api/v1/coffees`)
Endpoints to list and manage beverages available on the platform.

| Method | Endpoint | Description | Required Data / Params | Access |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/coffees` | Returns the complete catalog of registered coffees. | None | Public |
| **POST** | `/api/v1/coffees` | Registers a new coffee type in the system. | **Body (JSON):** `Coffee` object | ADMIN |

### Feed & Timeline (`/api/v1/feed`)
Social area to follow community activities and reviews.

| Method | Endpoint | Description | Required Data / Params | Access |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/feed` | Returns a personalized feed containing only the activities of followed users. | **Query Params:** <br>`page` (int, default: 0)<br>`size` (int, default: 10) | Private |
| **GET** | `/api/v1/feed/global` | Returns the global feed with recent activities from all platform members. | **Query Params:** <br>`page` (int, default: 0)<br>`size` (int, default: 10) | Private |

### Users & Social (`/api/v1/users`)
Endpoints for managing profiles, avatars, and the follower system.

| Method | Endpoint | Description | Required Data / Params | Access |
| :--- | :--- | :--- | :--- | :--- |
| **GET** | `/api/v1/users/me` | Returns the complete profile details of the logged-in user. | None | Private |
| **PATCH** | `/api/v1/users/me/avatar/{avatarId}`| Equips or updates the avatar used by the authenticated user. | **Path Variable:** <br>`avatarId` (Long) | Private |
| **GET** | `/api/v1/users/{id}` | Returns the public profile information of a specific user. | **Path Variable:** <br>`id` (Long) | Private |
| **GET** | `/api/v1/users/avatars` | Lists all available avatars on the platform. | None | Public |
| **GET** | `/api/v1/users/search` | Searches for registered users by their name. | **Query Param:** <br>`name` (String, required) | Private |
| **POST** | `/api/v1/users/{id}/follow` | Follows the user specified by the ID. | **Path Variable:** <br>`id` (Long) | Private |
| **DELETE**| `/api/v1/users/{id}/unfollow` | Unfollows the user specified by the ID. | **Path Variable:** <br>`id` (Long) | Private |
| **GET** | `/api/v1/users` | Lists all registered users in the system. | None | ADMIN |## Environment Variables

To run this project, you will need to add the following environment variables to your `.env` file or system environment. If not provided, the application will use the default values specified below.

* `DB_URL`
  Connection URL for the PostgreSQL database. (Default: `jdbc:postgresql://localhost:5432/readbrew_db`)

* `DB_USERNAME`
  Database authentication username. (Default: `postgres`)

* `DB_PASSWORD`
  Database authentication password.

* `DB_DDL_AUTO`
  Hibernate database schema initialization and update strategy. (Default: `update`)

* `DB_SHOW_SQL`
  Determines whether executed SQL statements should be printed in the console. (Default: `true`)

* `GOOGLE_BOOKS_API_KEY`
  Access key provided by the Google Cloud Console for the external Google Books API integration.

* `JWT_SECRET`
  Cryptographic secret key used to sign and validate JSON Web Tokens (JWT) for application security.

## Architecture Highlights

* **Layered Design:** The project strictly separates the presentation layer (`controller`), business logic (`service`), and data access (`repository`), preventing tight coupling and improving maintainability.
* **DTO Pattern:** The `dto` package is used extensively to transfer data between the client and server. This prevents exposing internal database models and secures the API against mass-assignment vulnerabilities.
* **External Integrations:** The `client` module isolates external HTTP calls (such as fetching book metadata from Google Books), making the main services cleaner and easier to test.
* **Stateless Security:** The `security` module implements a stateless authentication mechanism using JSON Web Tokens (JWT), which is ideal for modern RESTful APIs and decoupled frontend clients.


## Project Structure

The application follows a layered architecture pattern, ensuring a clear separation of concerns, scalability, and maintainability. Below is the primary directory structure of the backend:

```text
 src/main
 ┣  java/com/example/ReadBrew
 ┃ ┣  client       # External API clients (e.g., Google Books API via OpenFeign)
 ┃ ┣  config       # Application configurations (CORS, general Beans)
 ┃ ┣  controller   # REST API endpoints handling incoming HTTP requests
 ┃ ┣  dto          # Data Transfer Objects for decoupled request/response payloads
 ┃ ┣  model        # Domain entities and JPA database mappings
 ┃ ┣  repository   # Data access layer (Spring Data JPA interfaces)
 ┃ ┣  security     # Security configurations, JWT filters, and authentication logic
 ┃ ┣  service      # Core business logic and application rules
 ┃ ┗  ReadBrewApplication.java # Main application entry point
 ┗  resources
   ┣  static/images # Static assets (avatars and coffee images)
   ┗  application.properties # Environment and database properties
