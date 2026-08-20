# Azercell Cup Platform

A **Java** rewrite of the [azercell-cup-platform](https://github.com/ismayilqarayev/azercell-cup-platform) backend — a competitive-programming training platform used to prepare students for the Azercell Cup. Same architecture, same API, same data — the entire Spring Boot service is implemented in plain Java.

## What it is

A judge-style learning platform: students work through topics ("Ay 1: Massivlər", "Qraflar", etc.), solve C++ problems, and get their submissions compiled and run automatically against sample I/O. Teachers can monitor student progress; admins manage the full problem/topic catalog and user accounts.

- **16 topics, ~680 problems**, seeded from the original dataset on first boot
- **Live C++ judging** — submissions are compiled with `g++` and run against sample input/output, with compile/runtime/time-limit error handling
- **JWT authentication**, three roles (`STUDENT` / `TEACHER` / `ADMIN`) with an admin-approval flow for teacher accounts
- **Admin panel API** for full topic/problem CRUD and user management

## Stack

| | |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1 (Web MVC, Security, Data JPA, Validation) |
| Database | PostgreSQL (H2 for tests) |
| Auth | JWT (jjwt) + BCrypt |
| Build | Maven |

## Why this exists

The [original project](https://github.com/ismayilqarayev/azercell-cup-platform) is written in Java with Lombok. This repo is a 1:1 functional port without Lombok — `@Builder`/`@Getter`/`@Setter` replaced by hand-written constructors/getters/setters on JPA entities, immutable DTOs implemented as Java `record`s — while keeping the exact same REST contract, database schema, and business rules. Verified with the full test suite plus a manual pass over every endpoint (auth, topics, problems, submissions, admin CRUD, user management).

## Running it

```bash
cd backend
./mvnw spring-boot:run
```

Requires a local PostgreSQL database (`azcup`/`azcup` by default, see `backend/src/main/resources/application.yml`) and these environment variables for production use:

```bash
JWT_SECRET=<a real 256-bit random value>
ADMIN_EMAIL=<bootstrap admin email>
ADMIN_PASSWORD=<bootstrap admin password>
```

Run the tests:

```bash
./mvnw test
```

## Project layout

```
backend/          Spring Boot + Java API (entities, services, controllers, JWT security, JudgeService)
frontend-src/      Legacy static frontend pages (carried over unchanged)
tools/             Build scripts used to generate the static site / seed data
index.html         Legacy static landing page (carried over unchanged)
```
