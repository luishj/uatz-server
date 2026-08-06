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
  client/         clientes REST de APIs externas (@RegisterRestClient)
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

## Integração com o WhatsApp

O envio e o recebimento passam pela WhatsApp Cloud API. Com `whatsapp.enabled=false` (padrão) o
envio só vai para o log, o que permite exercitar o fluxo inteiro sem número verificado.

| Variável | Para que serve |
|---|---|
| `WHATSAPP_ENABLED` | `true` liga o envio pela Cloud API |
| `WHATSAPP_VERIFY_TOKEN` | token combinado com a Meta no cadastro do webhook |
| `WHATSAPP_APP_SECRET` | confere a assinatura das notificações recebidas |
| `WHATSAPP_ACCESS_TOKEN` | token de acesso usado no envio |
| `WHATSAPP_PHONE_NUMBER_ID` | id do número remetente |
| `WHATSAPP_API_URL` | base da Graph API (padrão `https://graph.facebook.com/v21.0`) |

Endpoints do webhook (públicos — quem chama é a Meta):

- `GET /api/whatsapp/webhook` — verificação do cadastro da URL; devolve `hub.challenge` quando
  `hub.verify_token` bate com `WHATSAPP_VERIFY_TOKEN`
- `POST /api/whatsapp/webhook` — recebe as mensagens; exige a assinatura `X-Hub-Signature-256`
  quando `WHATSAPP_APP_SECRET` está configurado

Como a mensagem recebida é interpretada:

- número isolado (`2`) **e** existe pedido do cliente com opções já enviadas e ainda aberto →
  escolha da cotação, fechando o pedido
- qualquer outro texto → novo pedido de orçamento, com os itens quebrados a partir das linhas

Sem `WHATSAPP_APP_SECRET` a assinatura não é conferida e qualquer um consegue postar no webhook —
é aceitável em desenvolvimento, não em produção. Cada notificação aceita sem conferência registra
um aviso no log.

Sem expor a aplicação na internet, o `POST /api/whatsapp/simulations` (perfis ADMIN/OPERATOR)
injeta uma mensagem pelo mesmo caminho de negócio.
