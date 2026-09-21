# PROJECT_CONTEXT

## Project

Spaced-repetition application (working name).

A single Java Backend pet-project that evolves gradually alongside the Java Backend roadmap.

The long-term goal is to build an application for creating decks, studying flashcards and scheduling reviews, with optional AI integration in later stages.

## Current stage

- Completed: DB Migrations (Flyway).
- Next: JDBC basics.
- The project currently contains only its database foundation.
- Spring Boot, REST API, JPA, authentication and AI features have not been implemented.

## Technologies

- Java: JDK 25, target compatibility Java 21.
- Build tool: Maven.
- Database: PostgreSQL 18.x.
- Database migrations: Flyway 13.7.0.
- PostgreSQL JDBC driver: configured for the Flyway Maven plugin.
- Version control: Git and GitHub.
- Development environment: Windows, IntelliJ IDEA, PowerShell.
- Database inspection: IntelliJ Database / DBeaver.

## Implemented

### Database schema

Table: deck
- id: BIGINT, identity, primary key
- name: VARCHAR(255), NOT NULL
- created_at: TIMESTAMPTZ, NOT NULL, default CURRENT_TIMESTAMP

Table: card
- id: BIGINT, identity, primary key
- deck_id: BIGINT, NOT NULL, references deck(id)
- front: TEXT, NOT NULL
- back: TEXT, NOT NULL
- created_at: TIMESTAMPTZ, NOT NULL, default CURRENT_TIMESTAMP
- due_at: TIMESTAMPTZ, NOT NULL, default CURRENT_TIMESTAMP
- interval_days: INTEGER, NOT NULL, default 0

Index:
- idx_card_due_at on card(due_at)

### Migrations

Location: src/main/resources/db/migration/

- V1__create_decks_and_cards.sql
- V2__add_scheduling_fields.sql
- V3__add_due_at_index.sql

All three migrations have been successfully applied to the local database.

The schema was also successfully reproduced from scratch in a separate empty PostgreSQL database using Flyway.

### Repository

The project contains:
- pom.xml
- README.md
- .gitignore
- SQL migration files

The migration files are tracked in Git.

## Important decisions

- Flyway is the only database migration tool used in the project.
- Applied migrations must not be modified for new schema changes.
- New schema requirements are implemented through new versioned migrations.
- Database schema changes are tracked in Git.
- Database passwords and other secrets must not be committed.
- PostgreSQL is currently used locally.
- The initial database model contains only decks and cards.
- Additional functionality will be introduced progressively as the roadmap advances.

## Known issues / TODO

- No Java application code has been implemented yet.
- Database access from Java will be introduced in the JDBC block.
- The scheduling algorithm has not been implemented.
- User management, review history, REST API and AI integration remain future features.