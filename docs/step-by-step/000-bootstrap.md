# Bootstrap do projeto

## Etapas
1. Criado projeto Spring Boot 3 (Gradle Kotlin DSL) `delivery-api`.
2. Adicionadas dependências principais (Web, Validation, Data JPA, OAuth2 Resource Server, OpenAPI, Cache, Redis, Flyway, PostgreSQL, MapStruct, Lombok, Testcontainers, H2).
3. Configurados perfis `dev`, `test` e `prod` em `application.yml`.
4. Adicionado `docker-compose.yml` com Postgres, Redis e Keycloak com import do realm.
5. Criada migração inicial `V1__baseline.sql`.
6. Criadas configs básicas: `SecurityConfig`, `OpenApiConfig`, `CacheConfig`, `RedisConfig`.
7. Arquivos utilitários: `.editorconfig`, `.gitignore`, `README.md`, `Makefile`.
8. Teste de fumaça `SmokeApplicationTest` para validar contexto.

## Estrutura de pacotes
- `br.com.delivery.domain`
- `br.com.delivery.application`
- `br.com.delivery.infrastructure` (subpacotes: `web`, `persistence`, `config`, `security`)
- `br.com.delivery.shared`

## Como validar
- Subir dependências: `make up`
- Compilar e testar: `./gradlew clean build`
- Rodar em dev: `./gradlew bootRun --args='--spring.profiles.active=dev'`
