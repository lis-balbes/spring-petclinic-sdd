# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

This project supports both Maven and Gradle (use wrappers). **Java 17+ required.**

### Maven
- **Build:** `./mvnw package`
- **Run:** `./mvnw spring-boot:run`
- **Run all tests:** `./mvnw test`
- **Run single test:** `./mvnw test -Dtest=OwnerControllerTests`
- **Run single test method:** `./mvnw test -Dtest=OwnerControllerTests#testInitCreationForm`
- **Format check:** `./mvnw spring-javaformat:validate`
- **Format apply:** `./mvnw spring-javaformat:apply`

### Gradle
- **Build:** `./gradlew build`
- **Run:** `./gradlew bootRun`
- **Run all tests:** `./gradlew test`
- **Run single test:** `./gradlew test --tests OwnerControllerTests`

## Architecture

Standard Spring Boot MVC application with Thymeleaf server-side rendering, organized by feature (not by layer):

- **`owner/`** — Owner, Pet, PetType, Visit entities + controllers + repositories
- **`vet/`** — Vet, Specialty entities + controller + repository
- **`model/`** — Base entity classes (BaseEntity, NamedEntity, Person)
- **`system/`** — Configuration (caching, web) and system controllers

### Key patterns
- **Spring Data JPA repositories** with `spring.jpa.open-in-view=false` — no lazy loading outside transactions
- **JCache caching** with Caffeine backend — vet list is cached (`@Cacheable("vets")`)
- **Database initialization** via SQL scripts in `src/main/resources/db/{h2|mysql|postgres}/`
- **No Hibernate DDL** — schema managed by SQL scripts (`ddl-auto=none`)
- **Snake-case column naming** via `PhysicalNamingStrategySnakeCaseImpl`

## Database Profiles

Default is H2 in-memory. Switch with `spring.profiles.active`:

| Profile    | Activate with                                  | Start DB                     |
|------------|------------------------------------------------|------------------------------|
| (default)  | no profile needed                              | H2 embedded, auto-starts     |
| `mysql`    | `-Dspring.profiles.active=mysql`               | `docker compose up mysql`    |
| `postgres` | `-Dspring.profiles.active=postgres`            | `docker compose up postgres` |

## Code Style

- **Spring Java Format** enforced in build — run `./mvnw spring-javaformat:apply` before committing
- **Checkstyle** with nohttp rules (no plain `http://` URLs in source)
- **EditorConfig**: tabs for Java/XML (width 4), spaces for HTML/SQL/Gradle (width 2)
- Checkstyle pinned to v12.x (v13 requires JDK 21)

## Testing

- **Controller tests** use `@WebMvcTest` with `@MockitoBean` for repositories
- **Service/repository tests** use `@DataJpaTest` with H2
- **Integration tests**: `PetClinicIntegrationTests` (H2), `MySqlIntegrationTests` (Testcontainers), `PostgresIntegrationTests` (Docker Compose)
- Assertions use AssertJ and Hamcrest matchers
