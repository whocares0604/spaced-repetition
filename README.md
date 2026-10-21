# Spaced Repetition

A backend for learning with flashcards and spaced repetition.

You create decks, add cards to them, and review the cards that are due today.
After each review the application decides when to show the card again,
using a simplified SM-2 algorithm.

This is a learning pet-project. It evolves together with a Java Backend roadmap.

## Tech stack

- Java 21
- Spring Boot 4.1 (Spring MVC, Bean Validation, JdbcClient)
- PostgreSQL 18
- Flyway (database migrations, run on application startup)
- Maven
- Docker Compose (local database)
- JUnit 6, AssertJ, MockMvc, RestTestClient, Testcontainers 2

## Running locally

Requirements: JDK 21+, Maven, Docker.

1. Start PostgreSQL:

   ```bash
   docker compose up -d
   ```

   The database is available on `localhost:5433`
   (port 5433 is used so it does not conflict with a locally installed PostgreSQL on 5432).

2. Start the application:

   ```bash
   mvn spring-boot:run
   ```

   Flyway applies the migrations on startup. The API is available on `http://localhost:8080`.

   Or build and run the jar:

   ```bash
   mvn package
   java -jar target/spaced-repetition-1.0-SNAPSHOT.jar
   ```

3. Stop the database when you are done:

   ```bash
   docker compose down
   ```

   `docker compose down -v` also deletes the database data.

### Configuration

The application and Docker Compose read the same variables.
The defaults work with `compose.yaml` out of the box, so no configuration is needed for local development.

| Variable      | Default                                               |
|---------------|-------------------------------------------------------|
| `DB_URL`      | `jdbc:postgresql://localhost:5433/spaced_repetition`  |
| `DB_NAME`     | `spaced_repetition`                                   |
| `DB_USER`     | `spaced`                                              |
| `DB_PASSWORD` | `spaced`                                              |

To change them, copy `.env.example` to `.env` and edit the values.
`.env` is ignored by Git. PostgreSQL sets the password only when the database volume is created,
so after changing `DB_PASSWORD` recreate the volume with `docker compose down -v`.

## Running tests

```bash
mvn verify
```

Docker must be running: integration tests start their own PostgreSQL container with Testcontainers.

## API

| Method   | Path                            | Description                                     |
|----------|---------------------------------|-------------------------------------------------|
| `POST`   | `/api/decks`                    | Create a deck                                   |
| `GET`    | `/api/decks`                    | List decks                                      |
| `GET`    | `/api/decks/{id}`               | Get a deck                                      |
| `PUT`    | `/api/decks/{id}`               | Rename a deck                                   |
| `DELETE` | `/api/decks/{id}`               | Delete a deck together with its cards           |
| `POST`   | `/api/decks/{deckId}/cards`     | Add a card to a deck                            |
| `GET`    | `/api/decks/{deckId}/cards`     | List cards of a deck                            |
| `GET`    | `/api/cards/{id}`               | Get a card                                      |
| `PUT`    | `/api/cards/{id}`               | Edit a card                                     |
| `DELETE` | `/api/cards/{id}`               | Delete a card                                   |
| `GET`    | `/api/decks/{deckId}/cards/due` | Cards of a deck that are due for review         |
| `POST`   | `/api/cards/{id}/review`        | Review a card: `AGAIN`, `HARD`, `GOOD` or `EASY` |

Status codes: `201` for created resources (with a `Location` header), `204` for deletes,
`400` for invalid requests and `404` for unknown decks or cards.

Errors are returned as [ProblemDetail](https://www.rfc-editor.org/rfc/rfc9457) (`application/problem+json`):

```json
{
  "title": "Bad Request",
  "status": 400,
  "detail": "Request validation failed",
  "instance": "/api/decks",
  "errors": {
    "name": "must not be blank"
  }
}
```

### Examples (bash)

```bash
# Create a deck
curl -X POST http://localhost:8080/api/decks \
  -H "Content-Type: application/json" \
  -d '{"name": "Spanish"}'

# Add a card to deck 1
curl -X POST http://localhost:8080/api/decks/1/cards \
  -H "Content-Type: application/json" \
  -d '{"front": "hola", "back": "hello"}'

# Get cards that are due
curl http://localhost:8080/api/decks/1/cards/due

# Review card 1
curl -X POST http://localhost:8080/api/cards/1/review \
  -H "Content-Type: application/json" \
  -d '{"grade": "GOOD"}'
```

### Examples (PowerShell)

In Windows PowerShell `curl` is an alias for `Invoke-WebRequest`, and passing JSON to `curl.exe` needs extra escaping,
so the examples use `Invoke-RestMethod`:

```powershell
# Create a deck
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/decks `
  -ContentType "application/json" -Body '{"name": "Spanish"}'

# Add a card to deck 1
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/decks/1/cards `
  -ContentType "application/json" -Body '{"front": "hola", "back": "hello"}'

# Get cards that are due
Invoke-RestMethod http://localhost:8080/api/decks/1/cards/due

# Review card 1
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/cards/1/review `
  -ContentType "application/json" -Body '{"grade": "GOOD"}'
```

## Review algorithm

Each card stores:

- `repetitions`: how many times in a row it was remembered;
- `intervalDays`: days until the next review;
- `easeFactor`: how fast the interval grows (starts at 2.5, never below 1.3);
- `dueAt`: when the card should be reviewed next.

A new card is due immediately. After a review the new state is calculated
by `Sm2Scheduler` (a plain Java class), and `dueAt` becomes now + `intervalDays`.

| Grade   | Meaning                 | Repetitions | Next interval                                              | Ease factor |
|---------|-------------------------|-------------|------------------------------------------------------------|-------------|
| `AGAIN` | forgot the card         | reset to 0  | 1 day                                                      | −0.20       |
| `HARD`  | remembered with effort  | +1          | 1 day first, then interval × 1.2 (at least +1 day)         | −0.15       |
| `GOOD`  | remembered              | +1          | 1 day, then 6 days, then interval × ease factor            | unchanged   |
| `EASY`  | remembered easily       | +1          | `GOOD` interval × 1.3 (at least +1 day)                    | +0.15       |

For example, answering `GOOD` three times gives intervals of 1, 6 and 15 days.

This is a simplified version of [SM-2](https://super-memory.com/english/ol/sm2.htm):
the original uses grades from 0 to 5 and a formula for the ease factor,
this project uses four grades (like Anki) and fixed ease factor changes.

## Project structure

```
src/main/java/io/github/whocares0604/spacedrepetition/
├── api/      global error handling (ProblemDetail)
├── deck/     deck API: controller, service, repository, DTOs
├── card/     card API: controller, service, repository, DTOs
└── review/   SM-2 scheduler, due cards and review API
src/main/resources/
├── application.yml
└── db/migration/   Flyway migrations V1–V4
```

Each feature follows the same layers: controller (HTTP) → service (logic) → repository (SQL with JdbcClient).
