
# Plataforma de Orçamentos via WhatsApp

## 1. Visão Geral

Este projeto é uma plataforma que permite que usuários solicitem **orçamentos via WhatsApp** e recebam respostas de empresas da sua região.

Fluxo principal:

Cliente → WhatsApp → Sistema → Empresas → Respostas → Cliente

Exemplo:

Cliente envia no WhatsApp:

10 sacos de cimento
100 tijolos

O sistema:

1. Recebe a mensagem
2. Cria um pedido de orçamento
3. Envia para empresas cadastradas
4. Empresas respondem pelo painel web
5. Sistema retorna os melhores preços ao cliente

---

# 2. Objetivo do Produto

Criar um **marketplace de orçamentos regional** que:

- Conecte clientes a empresas
- Automatize pedidos de orçamento
- Armazene histórico de preços
- Permita automação futura com IA

---

# 3. Stack Tecnológica

## Backend

- Java
- Quarkus
- REST API
- JWT Authentication

## Frontend

- Angular
- WebSocket para tempo real

## Banco de dados

- PostgreSQL

## Infraestrutura

- Docker
- Redis (cache / filas)
- Nginx

## Integração

- WhatsApp Cloud API

---

# 4. Arquitetura do Sistema

Arquitetura inicial (MVP):

Angular (Frontend)
↓
Quarkus Backend
↓
PostgreSQL
↓
WhatsApp Cloud API

Arquitetura futura:

API Gateway
├ Auth Service
├ Chat Service
├ Budget Service
├ Vendor Service
└ Notification Service

---

# 5. Fluxo de Mensagens

1. Cliente envia mensagem no WhatsApp
2. WhatsApp envia webhook para backend
3. Sistema registra conversa
4. Sistema cria pedido de orçamento
5. Sistema identifica empresas da categoria
6. Empresas recebem pedido no painel
7. Empresas enviam preço
8. Sistema envia resumo ao cliente

---

# 6. Modelo de Dados (Resumo)

## Users

users
id
name
email
password_hash
role
created_at

roles:
ADMIN
OPERATOR
VENDOR

## Vendors

vendors
id
name
phone
email
city
state
active
created_at

## Clients

clients
id
phone
city
state
created_at

## Conversations

conversations
id
client_id
channel
created_at

## Messages

messages
id
conversation_id
direction
message
created_at

direction:
IN
OUT

## Categories

categories
id
name
parent_id

## Products

products
id
name
category_id

## Vendor Products

vendor_products
id
vendor_id
product_id

## Budget Requests

budget_requests
id
client_id
city
status
created_at

status:
OPEN
SENT_TO_VENDORS
WAITING_QUOTES
CLOSED

## Budget Items

budget_items
id
request_id
product_id
product_name
quantity
unit

## Vendor Quotes

vendor_quotes
id
request_id
vendor_id
total_price
message
created_at

---

# 7. Módulos do Backend

backend
 ├ auth
 ├ vendors
 ├ products
 ├ budgets
 ├ chat
 └ notifications

---

# 8. Estrutura Frontend

frontend
 ├ auth
 ├ dashboard
 ├ vendors
 ├ budgets
 ├ chat
 └ admin

---

# 9. Roadmap de Desenvolvimento

Fase 1 — MVP

- Integração WhatsApp
- Cadastro de vendedores
- Receber pedidos
- Vendedores responderem
- Enviar resposta ao cliente

Fase 2 — Beta

- Painel completo
- Histórico de preços
- Relatórios básicos

Fase 3 — Escala

- Multi cidades
- Multi categorias
- IA para interpretar mensagens
- Marketplace completo

---

# 10. Visão Futuro

A plataforma pode evoluir para:

- Marketplace regional
- Comparador de preços
- Sistema de geração de leads para empresas
- Automação de orçamento com IA
