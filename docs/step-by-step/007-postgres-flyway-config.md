# Configuração PostgreSQL + Flyway - Passo a Passo

## Mudanças Realizadas

### 1. Configuração do Banco de Dados

**Antes (H2 em memória):**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  
  jpa:
    hibernate:
      ddl-auto: create-drop
  
  flyway:
    enabled: false
```

**Depois (PostgreSQL):**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/delivery
    username: delivery
    password: delivery
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: validate
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

### 2. Mudanças Importantes

#### **Hibernate DDL:**
- **Antes**: `create-drop` (recria schema a cada execução)
- **Depois**: `validate` (apenas valida se schema está correto)

#### **Flyway:**
- **Antes**: Desativado
- **Depois**: Ativado com `baseline-on-migrate: true`

#### **Dialeto:**
- **Antes**: `H2Dialect`
- **Depois**: `PostgreSQLDialect`

### 3. Migração do Flyway

**Arquivo**: `src/main/resources/db/migration/V1__init.sql`

Contém:
- ✅ Criação das tabelas: `customers`, `products`, `orders`, `order_items`
- ✅ Constraints e foreign keys
- ✅ Índices para performance
- ✅ Triggers para `updated_at` automático
- ✅ Função PostgreSQL para timestamps

### 4. Docker Compose

**PostgreSQL configurado:**
```yaml
postgres:
  image: postgres:16
  container_name: pg_delivery
  environment:
    POSTGRES_DB: delivery
    POSTGRES_USER: delivery
    POSTGRES_PASSWORD: delivery
  ports:
    - '5432:5432'
  volumes:
    - pgdata:/var/lib/postgresql/data
```

## Como Executar

### Opção 1: Script Automatizado
```bash
# Windows
start-dev.bat

# Ou manualmente:
```

### Opção 2: Passo a Passo Manual
```bash
# 1. Iniciar PostgreSQL
docker-compose up -d postgres

# 2. Verificar se está rodando
docker ps

# 3. Aguardar inicialização (10-15 segundos)

# 4. Executar aplicação
./gradlew bootRun
```

### Opção 3: Logs Detalhados
```bash
# Ver logs do PostgreSQL
docker-compose logs -f postgres

# Executar app com logs do Flyway
./gradlew bootRun --info
```

## Verificações

### 1. PostgreSQL Funcionando
```bash
# Conectar no banco
docker exec -it pg_delivery psql -U delivery -d delivery

# Listar tabelas
\dt

# Verificar migração
SELECT * FROM flyway_schema_history;
```

### 2. Aplicação Funcionando
```bash
# Health check
curl http://localhost:8080/actuator/health

# Swagger UI
http://localhost:8080/swagger-ui.html
```

## Vantagens para Desafio Técnico

### 1. **Demonstra Conhecimento Real**
- PostgreSQL é usado em produção
- Flyway é padrão da indústria
- Schema versionado e controlado

### 2. **Melhor para Testes**
- Dados persistem entre execuções
- Comportamento idêntico à produção
- Fácil inspeção do banco

### 3. **Recursos Avançados**
- Triggers e funções PostgreSQL
- Constraints e índices otimizados
- Tipos de dados específicos

## Troubleshooting

### Erro: "Connection refused"
```bash
# Verificar se PostgreSQL está rodando
docker ps

# Iniciar se necessário
docker-compose up -d postgres
```

### Erro: "Database does not exist"
```bash
# Recriar container
docker-compose down
docker-compose up -d postgres
```

### Erro: Flyway migration failed
```bash
# Limpar e recriar banco
docker-compose down -v
docker-compose up -d postgres
```

## Próximos Passos

1. ✅ PostgreSQL configurado
2. ✅ Flyway ativado
3. ⏳ Testar aplicação
4. ⏳ Adicionar dados de exemplo (opcional)
5. ⏳ Configurar Redis (opcional)
6. ⏳ Configurar Keycloak (opcional)

## Observações

- **H2 removido**: Projeto agora usa apenas PostgreSQL
- **Schema controlado**: Flyway gerencia todas as mudanças
- **Pronto para demonstração**: Configuração profissional para entrevistas
