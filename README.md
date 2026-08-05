# uatz-server

Backend do projeto UATZ construído com Quarkus. É a API REST da plataforma.

## Objetivo do MVP

- Receber pedidos de orcamento originados do WhatsApp
- Cadastrar vendedores e produtos
- Distribuir pedidos para vendedores
- Consolidar respostas para retorno ao cliente

## Stack

- Java 21
- Quarkus 3.15.1
- PostgreSQL
- JWT
- Docker Compose para desenvolvimento local

## Projetos da plataforma

A plataforma é dividida em três projetos:

| Projeto | Pasta | Responsabilidade |
|---|---|---|
| `uatz-server` | este | API REST |
| `uatz-model` | `../uatz-model` | entidades JPA (`br.com.uatz.model`) |
| `uatz-migration` | `../uatz-migration` | schema do banco (Liquibase) |

Este projeto **não** gera schema (`quarkus.hibernate-orm.database.generation=none`) e **não** tem
mais Flyway: o banco é versionado pelo `uatz-migration`.

## Estrutura

```
src/main/java/br/com/uatz/server/
  api/            interfaces dos controllers (rotas JAX-RS + @Valid)
  api/impl/       implementações (@Inject dos services + @RolesAllowed)
  service/        regras de negócio (interface + impl/)
  repository/     acesso a dados (interface + impl/, sobre GenericRepository)
  dto/            records de request/response por domínio
  mapping/        conversão entidade <-> DTO
  vo/             objetos de valor de consulta
  exception/      CloudMessage, MessageBuilder, BusinessServerException, ErrorMapper
  enumerador/     enums da aplicação
  constante/      constantes (Perfil)
  util/           QueryUtil, PropertyUtil, StringUtil
  env/            Enviroment
  startup/impl/   StartupImpl (@QuarkusMain)
src/main/resources/
  application.properties
  messages_pt_BR.properties   mensagens de negócio (chaves do CloudMessage)
```

## Como rodar

Prerequisitos locais:

- JDK 21
- Docker Desktop

Banco local:

```powershell
docker compose up -d
```

Aplicar as migrations (primeira vez e a cada nova entrega de schema):

```powershell
cd ..\uatz-migration; .\mvnw quarkus:dev
```

Publicar o model no repositório local (primeira vez e a cada alteração de entidade):

```powershell
cd ..\uatz-model; .\mvnw clean install
```

Subir a API (porta 8081):

```powershell
.\mvnw quarkus:dev
```

- Swagger UI: http://localhost:8081/q/swagger-ui
- Health: http://localhost:8081/q/health
- Status: http://localhost:8081/api/status

Variáveis de ambiente: `BASE` (host:porta/base), `USUARIO`, `SENHA`.
