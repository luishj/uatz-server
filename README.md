# uatz-server

Backend do projeto UATZ construído com Quarkus.

## Objetivo do MVP

- Receber pedidos de orcamento originados do WhatsApp
- Cadastrar vendedores e produtos
- Distribuir pedidos para vendedores
- Consolidar respostas para retorno ao cliente

## Stack prevista

- Java 21
- Quarkus
- PostgreSQL
- JWT
- Docker Compose para desenvolvimento local

## Estrutura inicial

- `api`: camada de entrada REST
- `service`: regras de negocio com interfaces e implementacoes
- `repository`: acesso a dados com interfaces e implementacoes
- `model`: entidades e enums
- `src/main/resources/application.properties`
- `docker-compose.yml`

## Como rodar futuramente

Prerequisitos locais:

- JDK 21
- Maven 3.9+
- Docker Desktop

Com o ambiente instalado:

```powershell
mvn quarkus:dev
```

Banco local:

```powershell
docker compose up -d
```
