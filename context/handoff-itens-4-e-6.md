# Handoff — itens 4 e 6 (precisam de banco)

Escrito em 06/08/2026, na máquina de desenvolvimento onde **não há Docker** e o Postgres local
rejeita as credenciais `uatz/uatz`. Os itens 1, 2, 3, 5 e 7 do plano de 30 dias
(`avaliacao-tecnica.txt`) foram concluídos e estão commitados. Sobraram dois, e os dois travam no
mesmo lugar: precisam de um banco de pé.

Este documento é para continuar o trabalho na máquina que tem o banco.

---

## 0. Antes de qualquer coisa: gerar as chaves do JWT

**A aplicação não sobe sem isso, e o Git não traz as chaves.** No item 2 o par foi rotacionado e
movido para `.keys/`, que está no `.gitignore` de propósito — chave privada não se versiona.

```powershell
cd uatz-server
mkdir .keys
openssl genpkey -algorithm RSA -pkeyopt rsa_keygen_bits:2048 -out .keys\privateKey.pem
openssl rsa -pubout -in .keys\privateKey.pem -out .keys\publicKey.pem
```

O par pode ser diferente do desta máquina — só não pode faltar. Se faltar, o
`ValidacaoConfiguracaoImpl` derruba o startup com uma mensagem explicando o que fazer.

> Se preferir usar exatamente o mesmo par das duas máquinas, copie os dois `.pem` por um canal
> fora do Git (pendrive, gerenciador de senhas). Nunca por commit, e-mail ou chat.

---

## 1. Onde está o código

Três repositórios, todos na branch **`seguranca-indices-e-docs`**:

| Repo | Caminho nesta máquina | O que mudou |
|---|---|---|
| `uatz-server` | `D:\dsv-git\dsv-quarkus\uatz-server` | escopo das cotações, chaves, validação de startup, docs |
| `uatz-migration` | `D:\dsv-git\dsv-quarkus\uatz-migration` | `db.changelog-10000008.xml` (7 índices) |
| `uatz-web` | `D:\dsv-git\dsv-java\uatz-web` | tela de detalhe não pede mais o resumo quando é fornecedor |

Atenção: o `uatz-web` fica em **outra raiz** (`dsv-java`, não `dsv-quarkus`).

```bash
git fetch origin
git checkout seguranca-indices-e-docs
```

---

## 2. Preparar o banco

```powershell
psql -U postgres -c "CREATE USER uatz WITH PASSWORD 'uatz' CREATEDB;"
psql -U postgres -c "CREATE DATABASE uatz OWNER uatz;"

cd uatz-migration
.\mvnw quarkus:dev     # aplica os changelogs, incluindo o 10000008
```

Confirmar que os 7 índices novos entraram:

```sql
SELECT indexname FROM pg_indexes
 WHERE indexname LIKE '%_idx' OR indexname = 'vendors_email_uk'
 ORDER BY indexname;
```

Esperado: `budget_items_request_id_idx`, `budget_request_vendors_vendor_id_idx`,
`budget_requests_client_id_idx`, `conversations_client_id_idx`,
`vendor_quote_items_quote_id_idx`, `vendor_quotes_vendor_id_idx`, `vendors_email_uk`.

O DDL foi validado com `liquibase updateSQL` em modo offline, mas **nunca foi aplicado a um banco
real**. Se `vendors_email_uk` falhar, existem e-mails de fornecedor duplicados em base — resolver o
dado antes de seguir.

---

## 3. Item 4 — idempotência do webhook (F3.7)

### O problema

A Meta reenvia o webhook sempre que não recebe `200` rápido. Hoje cada POST reprocessa tudo:
**cada reenvio cria um pedido de orçamento novo**, duplicado na frente do lojista. O próprio
Javadoc de `WhatsAppWebhookServiceImpl.handleMessage` já reconhece o reenvio, mas só trata o caso
de uma mensagem falhar no meio do lote — não o caso do lote inteiro voltar.

É o último item de código da etapa 3 e o mais importante dela.

### Desenho recomendado: tabela dedicada, insert como trava

Criar `whatsapp_inbound_messages`:

| coluna | tipo | |
|---|---|---|
| `id` | BIGINT | PK, autoincrement |
| `message_id` | VARCHAR(150) | NOT NULL, **UNIQUE** — o `id` que a Meta manda |
| `received_at` | TIMESTAMP | NOT NULL |

Em `handleMessage`, **antes** de processar:

1. tenta inserir `message_id` numa transação própria (`REQUIRES_NEW`);
2. se a unique estourar → já foi processada (ou está sendo agora) → ignora e segue o lote;
3. se inseriu → processa normalmente.

**Por que inserir antes e não checar antes:** um `exists()` seguido de processamento tem janela de
corrida. Dois reenviosativos concorrentes passariam os dois no `exists` e criariam dois pedidos. A
unique do banco é a única trava confiável aqui.

**Por que tabela nova e não uma coluna em `messages`:** a mensagem só é persistida depois de
resolver cliente e conversa, ou seja, depois de metade do trabalho. Para a trava valer, ela precisa
acontecer antes de tudo.

### A decisão que precisa ser tomada

Com o insert antes do processamento, se o processamento falhar a mensagem fica marcada como vista e
**nunca mais é processada** (at-most-once). O contrário — marcar depois — arrisca duplicar
(at-least-once).

Para este domínio, at-most-once é o certo: um pedido duplicado na frente do lojista queima a loja
que levou uma semana para conquistar; uma mensagem perdida o cliente reenvia. Mas o erro tem que
gritar no log, não sumir. Hoje `handleMessage` engole a exceção com `logger.errorf` — mantenha, e
considere um contador/alerta depois.

### Arquivos

- `uatz-migration`: novo `db.changelog-10000009.xml` + `<include>` em `db/10000000.xml`
- `uatz-model`: entidade nova (`WhatsAppInboundMessage`), depois `mvnw install`
- `uatz-server`: repositório (interface + impl estendendo `GenericRepository`) e o gancho em
  `WhatsAppWebhookServiceImpl.handleMessage`

Seguir os padrões do `CLAUDE.md`: interface em `repository/`, impl em `repository/impl/`, mensagem
de negócio em `CloudMessage` + `messages_pt_BR.properties` se precisar de erro novo.

### Como validar

Postar o **mesmo payload duas vezes** em `/api/whatsapp/webhook` (com assinatura válida) e conferir
que `budget_requests` ganhou **uma** linha só. A collection do Postman já tem o webhook.

---

## 4. Item 6 — os três testes

`quarkus-junit5`, `rest-assured`, `surefire` e `failsafe` já estão no `pom.xml`. Não existe um único
arquivo em `src/test`. Não é para buscar cobertura — é para cobrir os três lugares onde o dinheiro
passa.

### Configuração de teste

Com Docker instalado, o DevServices sobe o Postgres sozinho e não precisa de nada. Sem Docker,
aponte o profile de teste para um banco real:

```properties
%test.quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/uatz_test
%test.quarkus.datasource.username=uatz
%test.quarkus.datasource.password=uatz
```

Lembre que `quarkus.hibernate-orm.database.generation=none`: o banco de teste precisa das migrations
aplicadas antes.

### Teste 1 — autorização das cotações (o mais importante)

É o teste que teria pego os dois furos do item 1 em cinco minutos. Com dois fornecedores (A e B)
atribuídos ao mesmo pedido, autenticado como A:

| ação | esperado |
|---|---|
| `GET /api/vendor-quotes/request/{id}` | **403** (só ADMIN/OPERATOR) |
| `GET /api/vendor-quotes/request/{id}/summary` | **403** |
| `GET /api/vendor-quotes/vendor/{idDoB}` | **403** |
| `GET /api/vendor-quotes/{idDaCotacaoDoB}` | **404** |
| `GET /api/vendor-quotes/request/{id}/me` | **200**, a cotação de A |
| `POST /api/vendor-quotes` com `vendorId` = B | **403** |
| `POST /api/vendor-quotes` com `vendorId` = A | **201** |

Esse quadro é a especificação do comportamento — se algum dia alguém reabrir a classe para VENDOR,
o teste avisa.

### Teste 2 — parser de itens

Puro, sem HTTP. `"10 sacos de cimento\n100 tijolos"` deve virar dois itens:
`(10, "sacos de cimento", unidade "saco")` e `(100, "tijolos", unidade "un")`.

Vale cobrir também: vírgula decimal (`1,5 metros`), mensagem sem quantidade (cai em `1 un`) e
mensagem vazia (item único com o texto cru).

`parseWhatsAppItems` é privado hoje — extrair para uma classe utilitária testável é aceitável e
melhora o desenho.

### Teste 3 — webhook

- assinatura inválida → **403** (com `whatsapp.app-secret` setado no profile de teste)
- assinatura válida → **200** e um pedido criado
- **mesmo payload duas vezes → um pedido só** (é a validação do item 4)

---

## 5. Ordem sugerida

1. chaves (seção 0) e banco (seção 2) — sem isso nada roda
2. **teste 1**, contra o código que já está pronto — trava o item 1 antes de mexer em qualquer coisa
3. item 4 (idempotência)
4. testes 2 e 3

Escrever o teste 1 antes do item 4 é de propósito: ele valida trabalho já feito, custa pouco e
deixa a rede de segurança no lugar antes de a próxima mudança entrar.

---

## 6. O que ficou pendente de decisão sua

**F5.5 — purgar as chaves antigas do histórico do Git.** O par foi rotacionado, então a chave que
está nos commits antigos não assina mais nada. Mas ela continua publicada no histórico dos três
repositórios. `git filter-repo` resolve, reescreve todos os SHAs e exige `push --force`. Com um
único autor e sem forks, o custo é baixo agora e cresce a cada colaborador novo.

---

## 7. Ambiente de build (referência desta máquina)

Pode ser diferente na outra. Aqui, o `mvn`/`java` do PATH são Maven 3.5 + Java 8, que não servem
para Quarkus 3.15:

```bash
export JAVA_HOME="/d/dsv/graalvm-jdk-21_windows-x64_bin/graalvm-jdk-21+35.1"
export M2_HOME=; export MAVEN_HOME=
./mvnw -B clean install -DskipTests
```

Ordem de build: `uatz-model` (install) → `uatz-migration` → `uatz-server`.
Front: `npx --no-install ng build --configuration development` (~7s).

A porta 8081 está ocupada por outra aplicação nesta máquina — use `-Dquarkus.http.port=<outra>`.
