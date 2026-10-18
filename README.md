# Azercell Cup Platform

A competitive-programming training platform used to prepare students for the Azercell Cup, built with Spring Boot and plain Java (no Lombok — entities and DTOs use hand-written constructors, getters/setters, and `equals`/`hashCode`/`toString`).

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

## Frontend build and the shared UI layer

`index.html` is the app shell. The nine pages in `frontend-src/*.html` (prep, week1-5, algo, scheme, python) are embedded in it as
base64 `srcdoc` iframes. After editing anything under `frontend-src/` (or the shell), regenerate and copy it with:

```bash
node tools/build-index.js
node --test tools/*.test.js
```

The build also injects the shared design layer, so the page sources themselves stay untouched:

| File | What it is | Injected into |
|---|---|---|
| `frontend-src/ui-polish.css` | Fonts, tokens, base typography, focus rings, toast/dialog styles, shared component polish, reduced-motion rules | the shell and every iframe page |
| `frontend-src/ui-kit.js` | `AzcupUI`: `toast`, `confirm`, `prompt`, `secret`, `swap`, back-to-top button (replaces native `alert/confirm/prompt`) | the shell and every iframe page |
| `frontend-src/ui-shell.css` | Sidebar, sliding tab indicator, login/language screens, panels, page transitions | the shell only |
| `frontend-src/problem-tools.js` | Editor, drafts, sample runs for the problem pages | the shell (attaches to the week iframes) |

Fonts (Inter and JetBrains Mono, SIL OFL 1.1) are self-hosted in `backend/src/main/resources/static/assets/fonts/`, with their licenses.
`StaticAssetsConfig` gives that folder a 30-day cache; Spring Security would otherwise send `no-store` for every font request.
Note that `index.html` uses CRLF line endings on Windows; the build script keeps them.
