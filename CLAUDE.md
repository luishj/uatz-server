# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Quarkus 3.15.1 microservice (Java 21) — the REST API of the UATZ platform. It receives budget
requests coming from WhatsApp, distributes them to vendors and consolidates the quotes.

Endpoints are exposed under `/api/...` on port `8081`.

## Architecture

### Layer Structure

Everything lives under `src/main/java/br/com/uatz/server/`:

```
api/            → REST controller INTERFACES (@Path + JAX-RS annotations)
api/impl/       → controller implementations (@Inject services, security annotations)
service/        → business logic (interface + impl/)
repository/     → data access (interface + impl/, extends GenericRepository)
client/         → REST clients for external APIs (@RegisterRestClient)
dto/            → request/response records, grouped by domain (auth/, budget/, vendor/, ...)
mapping/        → entity <-> DTO conversion
vo/             → value objects for queries (create when needed)
exception/      → CloudMessage, MessageBuilder, BusinessServerException, ErrorMapper
enumerador/     → application enums (ProfileEnum, PropertyEnum) — domain enums live in uatz-model
constante/      → constants (Perfil: ADMIN/OPERATOR/VENDOR)
util/           → QueryUtil, PropertyUtil, StringUtil
env/            → Enviroment (@ConfigProperty holder)
startup/impl/   → StartupImpl, the @QuarkusMain entry point
```

### Key Base Classes

- **`GenericRepository<T, ID>`** — extends `PanacheRepositoryBase`; adds `salvar()` and
  `obterDataHoraAtual()`. Every repository interface extends it and every impl extends
  `GenericRepositoryImpl<T, ID>`.

### Controller Pattern

Interface + implementation split:

- interface in `api/` carries the **routing**: `@Path`, `@GET/@POST/...`, `@Consumes`, `@Produces`,
  `@PathParam`, `@RegisterForReflection`, and **`@Valid`** on the request parameters
- implementation in `api/impl/` carries the **security**: `@RolesAllowed`/`@PermitAll` on the class
  and, when a method differs, on the method

Why the split matters (both verified at runtime):

- `@RolesAllowed` is a CDI interceptor binding — on the interface it would NOT be applied, so it
  must be on the impl (an unprotected endpoint would silently return 200)
- `@Valid` must be declared in exactly ONE place, and it has to be the interface: Bean Validation
  forbids an overriding method from adding parameter constraints
  (`ConstraintDeclarationException: HV000151`) — declaring it on the impl breaks startup

Controllers use field `@Inject`, not constructor injection.

### Service Pattern

- `@ApplicationScoped`, interface in `service/` + impl in `service/impl/`
- `@Transactional` on methods that write

### Exception Handling

- **`CloudMessage`** — enum with the business message keys; each key must exist in
  `src/main/resources/messages_pt_BR.properties`
- **`MessageBuilder`** — builds the exception: `MessageBuilder.build(CloudMessage.X, Status.Y)`
  wraps a `BusinessServerException` (code + message + HTTP status) in a `WebApplicationException`
- **`ErrorMapper`** — global `ExceptionMapper<Exception>`; serializes everything as
  `ApiErrorResponse` (`status`, `message`, `timestamp`, `errors[]`)

Never throw `WebApplicationException` with a literal message — add a key to `CloudMessage` +
`messages_pt_BR.properties` and pass the HTTP status:

```java
throw MessageBuilder.build(CloudMessage.PEDIDO_NAO_ENCONTRADO, Status.NOT_FOUND);

// com parâmetro na mensagem: PEDIDO_X_NAO_ENCONTRADO=PEDIDO {0} NÃO ENCONTRADO
throw MessageBuilder.build(CloudMessage.PEDIDO_X_NAO_ENCONTRADO, Status.NOT_FOUND, Map.of("{0}", id.toString()));
```

Note: bean-validation failures on endpoints are handled by Quarkus' own built-in mapper (response
`{"title":"Constraint Violation","violations":[...]}`), which takes precedence over `ErrorMapper`.

## Configuration

`src/main/resources/application.properties`. Env vars: `BASE` (host:porta/base), `USUARIO`, `SENHA`
— defaults point to the local `localhost:5432/uatz`.

WhatsApp Cloud API: `WHATSAPP_ENABLED`, `WHATSAPP_VERIFY_TOKEN`, `WHATSAPP_APP_SECRET`,
`WHATSAPP_ACCESS_TOKEN`, `WHATSAPP_PHONE_NUMBER_ID`, `WHATSAPP_API_URL` — all read through
`Enviroment`. With `WHATSAPP_ENABLED=false` (default) outbound messages only go to the log.

The webhook (`/api/whatsapp/webhook`, `@PermitAll`) takes the POST body as a raw `String`, not a
DTO: the `X-Hub-Signature-256` HMAC is computed over the exact bytes, so the JSON is only parsed
after the signature checks out.

## Internal Dependencies

- `uatz-model` — JPA entities, package `br.com.uatz.model` (`../uatz-model/` folder). Run
  `mvnw install` there after changing an entity
- `uatz-migration` — Liquibase changelogs, the schema owner (`../uatz-migration/` folder)

This project never generates schema (`quarkus.hibernate-orm.database.generation=none`): any entity
change needs a matching changelog in `uatz-migration`.

## Coding rules

- Use JAVA conventions
- camelCase for variables
- SNAKE_CASE for static variables/enums
- Structured `if` statements (with `if (condition) { ... }`)
- Use DTOs to receive and send data on API endpoints. Never receive or send an entity
- Use `mapping/` classes to transform DTO -> entity -> DTO. They are static helper classes over
  records (MapStruct is available in the pom if a mapper gets big enough to justify it)
- `Perfil.ADMIN`/`OPERATOR`/`VENDOR` instead of string literals in `@RolesAllowed`
- `StringUtil.isNullOrEmpty` instead of `x == null || x.isBlank()`

## Repository / SQL rules

- Prefer Panache finders; for native SQL use `QueryUtil` and never concatenate values — bind with
  `:parameter` to prevent SQL injection
- DTO/VO classes filled by `QueryUtil` need `@RegisterForReflection`

## Build

```powershell
.\mvnw quarkus:dev       # porta 8081
.\mvnw clean package
```
