# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

"Spendly" — a server-rendered expense tracker. Spring Boot 4.1.0 / Java 21 / Thymeleaf / MySQL.
The Maven artifact is `speendlyclaude`; the user-facing product name is "Spendly".

## Commands

Use the wrapper (`mvnw.cmd` on Windows PowerShell, `./mvnw` under the Bash tool).

```powershell
.\mvnw.cmd spring-boot:run                              # run on :8080 (devtools restart enabled)
.\mvnw.cmd clean package                                # build jar into target/
.\mvnw.cmd test                                         # all tests
.\mvnw.cmd test -Dtest=SpeendlyclaudeApplicationTests   # one test class
.\mvnw.cmd test -Dtest='ClassName#methodName'           # one test method
```

Tests currently boot the full context (`@SpringBootTest`), so **a reachable MySQL instance is
required for `test` to pass** — there is no embedded/H2 profile. Same for `spring-boot:run`.

### Database

MySQL schema `speendly`, read from env vars with dev defaults baked into
`src/main/resources/application.properties`: `DATABASE_HOST`, `DATABASE_PORT`, `DATABASE_NAME`,
`DATABASE_USERNAME`, `DATABASE_PASSWORD`. Schema is managed by Hibernate `ddl-auto=update` —
there are no migration files, so entity changes are the schema changes.

## Architecture

Classic three-layer Spring MVC, no service layer: controllers talk to Spring Data repositories
directly and return Thymeleaf view names. All packages live under `com.example.speendlyclaude`.

- `config/ProjectConfig.java` — the whole security setup in one file: the filter chain
  (public routes are `/`, `/login`, `/register`, `/terms`, `/privacy`, `/css/**`, `/js/**`;
  everything else authenticated), the delegating `PasswordEncoder`, and a lambda
  `UserDetailsService` that adapts `User` → Spring Security's `User` with a single `USER`
  authority. Login uses **`email` as the username parameter**, not `username`.
- `controller/UserController.java` — landing/login/register/terms/privacy pages plus the
  registration POST (duplicate-email check, encode, save, redirect to `/login`).
- `controller/ExpenseController.java` — everything under `/expenses`. Resolves the current
  user via `userRepository.findByEmail(authentication.getName())`; the total is summed in
  Java from the fetched list. Deletion is ownership-checked by filtering on the owner's id
  before delete — preserve that pattern for any new per-expense mutation.
- `model/` — `User` (table `users`) and `Expenses` (table `Expenses`, `@ManyToOne` lazy to
  `User`, column `expense_date` mapped to the field `date`). Lombok `@Getter/@Setter` only;
  no builders.

### Templates

Thymeleaf uses **inclusion, not inheritance** — `fragments/layout.html` exposes `head(pageTitle)`,
`navbar`, `footer`, and `scripts` fragments, and each page is a standalone HTML document that
pulls them in with `th:replace="~{fragments/layout :: ...}"`. There is no base template to
extend; a new page copies that four-line skeleton. `layout.html` itself is never rendered.
Auth-conditional markup uses `sec:authorize` (thymeleaf-extras-springsecurity6).

Spring Security CSRF is on by default and forms rely on Thymeleaf injecting the hidden token —
so **POST forms must use `th:action="@{/...}"`**, never a plain `action="..."`, or they 403.

`static/css/style.css` is a single hand-written stylesheet driven by CSS custom properties on
`:root` (`--bg`, `--ink`, `--brand`, …); use those tokens rather than literal colors.
`static/js/main.js` is one IIFE for the landing page's "how it works" video modal, written to
no-op when its elements are absent.

## Dependency note

This is Spring Boot **4.x**, where starter artifact ids differ from the Boot 3 names people
reach for by habit: it uses `spring-boot-starter-webmvc` (not `-web`) and per-slice test
starters (`spring-boot-starter-webmvc-test`, `-data-jpa-test`, `-jdbc-test`, `-security-test`)
instead of `spring-boot-starter-test`. Match the existing style when adding dependencies.
Lombok is wired through explicit `annotationProcessorPaths` in both compiler executions.

`HELP.md` is git-ignored Spring Initializr boilerplate; don't treat it as project documentation.
