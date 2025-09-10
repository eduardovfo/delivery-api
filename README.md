# 🚀 Delivery API

> API REST para gerenciamento de entregas de pedidos
> **Stack**: Spring Boot 3 · Java 17 · PostgreSQL · OAuth2/Keycloak · Redis · Gradle · OpenAPI

[![Java](https://img.shields.io/badge/Java-17-blue)]()
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen)]()
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)]()
[![Redis](https://img.shields.io/badge/Redis-Cache-red)]()

## 📑 Sumário

* [Funcionalidades](#-funcionalidades)
* [Arquitetura](#-arquitetura)
* [Requisitos](#-requisitos)
* [Subir Local (3 passos)](#-subir-local-3-passos)
* [Endpoints e Exemplos](#-endpoints-e-exemplos)
* [Segurança (Keycloak)](#-segurança-keycloak)
* [Cache (Redis)](#-cache-redis)
* [Estrutura do Projeto](#-estrutura-do-projeto)
* [Testes e Qualidade](#-testes-e-qualidade)
* [Observações e Troubleshooting](#-observações-e-troubleshooting)

---

## ✅ Funcionalidades

* **Clientes**: CRUD com validações.
* **Produtos**: catálogo com preço.
* **Pedidos**: criação, consulta detalhada, **atualização de status** e **listagem com filtro**.
* **Persistência** em PostgreSQL, timestamps automáticos.
* **Documentação** via Swagger/OpenAPI.
* **Segurança** OAuth2 (Keycloak) com **escopos granulares**.
* **Cache** Redis para leituras rápidas e invalidação em escrita.

---

## 🧭 Arquitetura

**Clean Architecture** com camadas independentes:

```
📁 domain/          Entidades + Value Objects + Ports
📁 application/     Use cases + DTOs + Mappers (MapStruct)
📁 infrastructure/  Web (REST), Persistence (JPA), Security, Cache, Config
```

Padrões: **Repository Pattern**, **Use Case**, **DTO/Mapper**, **RFC 7807** para erros.

---

## 📋 Requisitos

* Java 17+
* Docker & Docker Compose
* Porta **8080** livre (API) e **8081** (Keycloak)
* Gradle Wrapper (`./gradlew`) já incluso

---

## ⚡ Subir Local (3 passos)

```bash
# 1) Subir serviços
docker-compose up -d   # postgres, redis, keycloak

# 2) Aguardar inicialização (≈30s p/ Keycloak)

# 3) Rodar a API
./gradlew bootRun
```

**Verificar**

* Health: `http://localhost:8080/actuator/health`
* Swagger: `http://localhost:8080/swagger-ui.html`

> Dicas úteis:
>
> * Logs: `docker-compose logs -f postgres|redis|keycloak`
> * Parar: `docker-compose down` | Reset total: `docker-compose down -v`
> * Testes: `./gradlew test`
> * Build: `./gradlew clean build`

---

## 🔗 Endpoints e Exemplos

### Clientes

* `POST /v1/customers` – Criar
* `GET /v1/customers/{id}` – Buscar por ID
* `GET /v1/customers?page=0&size=10` – Listar (paginado)

### Produtos

* `POST /v1/products` – Criar
* `GET /v1/products/{id}` – Buscar por ID
* `GET /v1/products?page=0&size=10` – Listar (paginado)

### Pedidos

* `POST /v1/orders` – Criar
* `GET /v1/orders/{id}` – Detalhar
* `GET /v1/orders?status=CREATED&page=0&size=10` – Listar (filtro)
* `PATCH /v1/orders/{id}/status` – Atualizar status

#### cURL — exemplo rápido (sem auth)

```bash
curl -X POST http://localhost:8080/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"João Silva","email":"joao@email.com","document":"12345678901"}'
```

Mais exemplos no Swagger.

---

## 🔐 Segurança (Keycloak)

* **Realm**: `delivery` (importado automaticamente pelo docker-compose)
* **Client**: `delivery-api`
* **Fluxos**: Authorization Code (recomendado) | ROPC (apenas DEV) | Client Credentials (M2M)

**Obter token (ROPC para DEV)**

```bash
curl -X POST http://localhost:8081/realms/delivery/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=delivery-api" \
  -d "client_secret=delivery-api-secret" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "scope=customers:read customers:write products:read products:write orders:read orders:write"
```

**Escopos**

* `customers:read|write`, `products:read|write`, `orders:read|write`

> **Dica:** Se ver `resolve_required_actions` no login, remova required actions do usuário de teste (ou verifique se o usuário pertence ao realm `delivery` e tem senha não temporária).

---

## ⚡ Cache (Redis)

* **@Cacheable** em consultas (`GET`)
* **@CacheEvict** em escritas
* TTL sugerido: **listas 5 min**, **detalhes 10 min**
* Serialização JSON

Arquivos-chave:

* `infrastructure/cache/RedisConfig.java`
* testes em `src/test/java/.../infrastructure/cache/`

---

## 🏗️ Estrutura do Projeto

```
src/
├── main/java/br/com/delivery/
│   ├── application/     # DTOs, mappers, use cases
│   ├── domain/          # entidades, ports, VOs
│   └── infrastructure/  # web, persistence, security, cache, config
└── test/                # unit/integration (Testcontainers)
```

---

```bash
./gradlew test
./gradlew jacocoTestReport
```

---

## 📝 Observações e Troubleshooting

* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
* **Execução simplificada (sem Keycloak/Redis)**: subir apenas Postgres no compose; a API roda com segurança limitada para testes locais.
* **Flyway**: caso desabilitado no DEV, o **Hibernate** pode gerir o schema (`ddl-auto`) — para produção, habilite **Flyway** com versão compatível.
* **Push para GitHub**: se o remoto tiver um README inicial, faça `git pull --rebase origin main` antes do `git push`.

---

> **Resumo Executivo**:
> Projeto demonstra **Clean Architecture**, **segurança robusta** (OAuth2/Keycloak), **cache** performático (Redis), **documentação** completa (Swagger) e **testes abrangentes** (Testcontainers), pronto para avaliação técnica e evolução para produção.
