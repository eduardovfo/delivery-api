# Teste dos Requisitos - Sistema de Delivery

## Visão Geral
Este documento detalha como testar todos os requisitos funcionais e técnicos do sistema de delivery usando Postman e scripts automatizados.

## 📋 Requisitos a Testar

### Requisitos Funcionais
- ✅ **RF01**: Cadastro e consulta de clientes
- ✅ **RF02**: Cadastro e consulta de produtos  
- ✅ **RF03**: Cadastro de pedidos vinculados a clientes e produtos
- ✅ **RF04**: Atualização do status de um pedido
- ✅ **RF05**: Listagem de pedidos com filtro por status
- ✅ **RF06**: Consulta de pedido por ID com informações completas
- ✅ **RF07**: Data/hora automática de criação
- ✅ **RF08**: Banco de dados relacional (PostgreSQL)

### Requisitos Técnicos
- ✅ **RT01**: Java 17+ e Spring Boot
- ✅ **RT02**: Spring Data JPA e Validation
- ✅ **RT03**: Gradle como build tool
- ✅ **RT04**: Documentação OpenAPI/Swagger
- ✅ **RT05**: Autenticação OAuth2 com Keycloak
- ✅ **RT06**: Cache Redis

## 🚀 Como Executar os Testes

### Pré-requisitos
1. **Iniciar serviços necessários:**
   ```bash
   # PostgreSQL
   docker-compose up -d postgres
   
   # Redis
   docker-compose up -d redis
   
   # Keycloak
   docker-compose up -d keycloak
   
   # Aguardar 30 segundos para Keycloak inicializar
   ```

2. **Iniciar aplicação:**
   ```bash
   ./gradlew bootRun
   ```

### Opção 1: Teste Automatizado (Recomendado)
```bash
# Dar permissão de execução
chmod +x scripts/test-all-requirements.sh

# Executar todos os testes
./scripts/test-all-requirements.sh
```

### Opção 2: Teste com Postman
1. **Importar coleção:**
   - Abra o Postman
   - Importe: `postman/Delivery-API-Tests.postman_collection.json`
   - Importe: `postman/Delivery-API-Environment.postman_environment.json`

2. **Configurar ambiente:**
   - Selecione o ambiente "Delivery API - Ambiente Local"
   - Verifique se as URLs estão corretas

3. **Executar testes:**
   - Execute primeiro: "Obter Token de Acesso"
   - Execute a coleção "Clientes (RF01)"
   - Execute a coleção "Produtos (RF02)"
   - Execute a coleção "Pedidos (RF03-RF06)"
   - Execute a coleção "Testes de Cache Redis"
   - Execute a coleção "Testes de Segurança"

### Opção 3: Teste de Cache Redis Específico
```bash
# Dar permissão de execução
chmod +x scripts/test-redis-cache.sh

# Executar teste de cache
./scripts/test-redis-cache.sh
```

## 📊 Validação dos Requisitos

### RF01: Cadastro e Consulta de Clientes
- **POST /v1/customers** - Criar cliente
- **GET /v1/customers/{id}** - Buscar cliente por ID
- **GET /v1/customers** - Listar clientes (paginado)

**Validações:**
- Status 201 na criação
- Status 200 na consulta
- Dados corretos retornados
- Paginação funcionando

### RF02: Cadastro e Consulta de Produtos
- **POST /v1/products** - Criar produto
- **GET /v1/products/{id}** - Buscar produto por ID
- **GET /v1/products** - Listar produtos (paginado)

**Validações:**
- Status 201 na criação
- Status 200 na consulta
- Dados corretos retornados
- Paginação funcionando

### RF03: Cadastro de Pedidos
- **POST /v1/orders** - Criar pedido vinculado a cliente e produtos

**Validações:**
- Status 201 na criação
- Pedido vinculado ao cliente correto
- Itens vinculados aos produtos corretos
- Cálculo de total correto

### RF04: Atualização de Status
- **PATCH /v1/orders/{id}/status** - Atualizar status do pedido

**Validações:**
- Status 200 na atualização
- Status atualizado corretamente
- Transições de status válidas

### RF05: Listagem com Filtro
- **GET /v1/orders** - Listar pedidos
- **GET /v1/orders?status=CREATED** - Filtrar por status

**Validações:**
- Status 200 na listagem
- Filtro por status funcionando
- Paginação funcionando

### RF06: Consulta Detalhada
- **GET /v1/orders/{id}** - Buscar pedido com informações completas

**Validações:**
- Status 200 na consulta
- Informações do cliente incluídas
- Informações dos produtos incluídas
- Dados completos do pedido

### RF07: Data/Hora Automática
- Verificar campo `createdAt` em pedidos

**Validações:**
- Campo `createdAt` presente
- Data/hora atual registrada
- Formato ISO correto

### RT01-RT03: Requisitos Técnicos Básicos
- Java 17+ (verificar build.gradle.kts)
- Spring Boot 3.3.3
- Spring Data JPA
- Spring Validation
- Gradle

### RT04: Documentação OpenAPI
- **GET /swagger-ui.html** - Interface Swagger
- **GET /v3/api-docs** - Documentação JSON

**Validações:**
- Swagger UI acessível
- OpenAPI JSON válido
- Documentação completa

### RT05: Autenticação OAuth2
- Obter token do Keycloak
- Acessar endpoints protegidos
- Validação de scopes

**Validações:**
- Token obtido com sucesso
- Acesso negado sem token (401)
- Acesso permitido com token válido
- Scopes funcionando corretamente

### RT06: Cache Redis
- Primeira consulta (banco)
- Segunda consulta (cache)
- Comparação de tempos

**Validações:**
- Segunda consulta mais rápida
- Chaves no Redis
- Logs de cache

## 🔧 Troubleshooting

### Erro de Conexão com Banco
```bash
# Verificar se PostgreSQL está rodando
docker ps | grep pg_delivery

# Se não estiver, iniciar
docker-compose up -d postgres
```

### Erro de Conexão com Redis
```bash
# Verificar se Redis está rodando
docker ps | grep redis_delivery

# Se não estiver, iniciar
docker-compose up -d redis
```

### Erro de Conexão com Keycloak
```bash
# Verificar se Keycloak está rodando
docker ps | grep keycloak_delivery

# Se não estiver, iniciar
docker-compose up -d keycloak

# Aguardar 30 segundos para inicializar
```

### Token OAuth2 Inválido
- Verificar se Keycloak está rodando
- Verificar credenciais (admin/admin123)
- Verificar se o realm "delivery" existe
- Verificar se o client "delivery-api" existe

### Cache Não Funcionando
- Verificar se Redis está rodando
- Verificar configuração de cache no application.yml
- Verificar logs da aplicação
- Em ambiente de teste, pode usar cache simples

## 📁 Arquivos Criados

### Coleção Postman
- `postman/Delivery-API-Tests.postman_collection.json` - Coleção completa
- `postman/Delivery-API-Environment.postman_environment.json` - Ambiente

### Scripts de Teste
- `scripts/test-all-requirements.sh` - Teste completo automatizado
- `scripts/test-redis-cache.sh` - Teste específico de cache

### Documentação
- `docs/step-by-step/008-testing-requirements.md` - Este arquivo

## 🎯 Próximos Passos

1. **Executar testes automatizados**
2. **Validar todos os requisitos funcionais**
3. **Verificar integração OAuth2**
4. **Testar performance do cache Redis**
5. **Documentar resultados dos testes**
6. **Implementar melhorias se necessário**

## 📞 Suporte

Se encontrar problemas durante os testes:
1. Verificar logs da aplicação
2. Verificar status dos containers Docker
3. Verificar configurações de rede
4. Consultar este documento para troubleshooting
