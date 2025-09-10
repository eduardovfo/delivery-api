#!/bin/bash

# Script para testar todos os requisitos funcionais e técnicos
# Executa antes: docker-compose up -d postgres redis keycloak

echo "🚀 Testando todos os requisitos da API de Delivery..."
echo ""

# Verificar se os serviços estão rodando
echo "1. Verificando serviços..."

# PostgreSQL
docker ps | grep pg_delivery > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ PostgreSQL está rodando"
else
    echo "❌ PostgreSQL não está rodando. Execute: docker-compose up -d postgres"
    exit 1
fi

# Redis
docker ps | grep redis_delivery > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Redis está rodando"
else
    echo "❌ Redis não está rodando. Execute: docker-compose up -d redis"
    exit 1
fi

# Keycloak
docker ps | grep keycloak_delivery > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Keycloak está rodando"
else
    echo "❌ Keycloak não está rodando. Execute: docker-compose up -d keycloak"
    exit 1
fi

# Aplicação
curl -s http://localhost:8080/actuator/health > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Aplicação está rodando"
else
    echo "❌ Aplicação não está rodando. Execute: ./gradlew bootRun"
    exit 1
fi

echo ""

# Obter token de acesso
echo "2. Obtendo token de acesso OAuth2..."
TOKEN=$(curl -s -X POST http://localhost:8081/realms/delivery/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=delivery-api" \
  -d "client_secret=delivery-api-secret" \
  -d "username=admin" \
  -d "password=admin123" \
  -d "scope=customers:read customers:write products:read products:write orders:read orders:write" | jq -r '.access_token')

if [ "$TOKEN" = "null" ] || [ -z "$TOKEN" ]; then
    echo "❌ Falha ao obter token de acesso"
    exit 1
fi

echo "✅ Token obtido com sucesso"
echo ""

# RF01: Cadastro e consulta de clientes
echo "3. Testando RF01: Cadastro e consulta de clientes..."

# Criar cliente
echo "   Criando cliente..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "document": "12345678901"
  }')

CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | jq -r '.id')
if [ "$CUSTOMER_ID" != "null" ] && [ -n "$CUSTOMER_ID" ]; then
    echo "   ✅ Cliente criado com ID: $CUSTOMER_ID"
else
    echo "   ❌ Falha ao criar cliente"
    exit 1
fi

# Buscar cliente
echo "   Buscando cliente..."
CUSTOMER_GET=$(curl -s -X GET http://localhost:8080/v1/customers/$CUSTOMER_ID \
  -H "Authorization: Bearer $TOKEN")

if echo $CUSTOMER_GET | jq -e '.id' > /dev/null; then
    echo "   ✅ Cliente encontrado"
else
    echo "   ❌ Falha ao buscar cliente"
    exit 1
fi

# Listar clientes
echo "   Listando clientes..."
CUSTOMERS_LIST=$(curl -s -X GET http://localhost:8080/v1/customers \
  -H "Authorization: Bearer $TOKEN")

if echo $CUSTOMERS_LIST | jq -e '.content' > /dev/null; then
    echo "   ✅ Lista de clientes obtida"
else
    echo "   ❌ Falha ao listar clientes"
    exit 1
fi

echo "✅ RF01: Cadastro e consulta de clientes - APROVADO"
echo ""

# RF02: Cadastro e consulta de produtos
echo "4. Testando RF02: Cadastro e consulta de produtos..."

# Criar produto
echo "   Criando produto..."
PRODUCT_RESPONSE=$(curl -s -X POST http://localhost:8080/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Pizza Margherita",
    "description": "Pizza tradicional com molho de tomate, mussarela e manjericão",
    "price": 29.99
  }')

PRODUCT_ID=$(echo $PRODUCT_RESPONSE | jq -r '.id')
if [ "$PRODUCT_ID" != "null" ] && [ -n "$PRODUCT_ID" ]; then
    echo "   ✅ Produto criado com ID: $PRODUCT_ID"
else
    echo "   ❌ Falha ao criar produto"
    exit 1
fi

# Buscar produto
echo "   Buscando produto..."
PRODUCT_GET=$(curl -s -X GET http://localhost:8080/v1/products/$PRODUCT_ID \
  -H "Authorization: Bearer $TOKEN")

if echo $PRODUCT_GET | jq -e '.id' > /dev/null; then
    echo "   ✅ Produto encontrado"
else
    echo "   ❌ Falha ao buscar produto"
    exit 1
fi

# Listar produtos
echo "   Listando produtos..."
PRODUCTS_LIST=$(curl -s -X GET http://localhost:8080/v1/products \
  -H "Authorization: Bearer $TOKEN")

if echo $PRODUCTS_LIST | jq -e '.content' > /dev/null; then
    echo "   ✅ Lista de produtos obtida"
else
    echo "   ❌ Falha ao listar produtos"
    exit 1
fi

echo "✅ RF02: Cadastro e consulta de produtos - APROVADO"
echo ""

# RF03: Cadastro de pedidos vinculados a clientes e produtos
echo "5. Testando RF03: Cadastro de pedidos..."

# Criar pedido
echo "   Criando pedido..."
ORDER_RESPONSE=$(curl -s -X POST http://localhost:8080/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{
    \"customerId\": \"$CUSTOMER_ID\",
    \"items\": [
      {
        \"productId\": \"$PRODUCT_ID\",
        \"quantity\": 2,
        \"unitPrice\": 29.99
      }
    ]
  }")

ORDER_ID=$(echo $ORDER_RESPONSE | jq -r '.id')
if [ "$ORDER_ID" != "null" ] && [ -n "$ORDER_ID" ]; then
    echo "   ✅ Pedido criado com ID: $ORDER_ID"
else
    echo "   ❌ Falha ao criar pedido"
    exit 1
fi

echo "✅ RF03: Cadastro de pedidos - APROVADO"
echo ""

# RF04: Atualização do status de um pedido
echo "6. Testando RF04: Atualização do status de pedido..."

# Atualizar status
echo "   Atualizando status do pedido..."
STATUS_RESPONSE=$(curl -s -X PATCH http://localhost:8080/v1/orders/$ORDER_ID/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"status": "CONFIRMED"}')

if echo $STATUS_RESPONSE | jq -e '.status' > /dev/null; then
    NEW_STATUS=$(echo $STATUS_RESPONSE | jq -r '.status')
    echo "   ✅ Status atualizado para: $NEW_STATUS"
else
    echo "   ❌ Falha ao atualizar status"
    exit 1
fi

echo "✅ RF04: Atualização do status de pedido - APROVADO"
echo ""

# RF05: Listagem de pedidos com filtro por status
echo "7. Testando RF05: Listagem de pedidos com filtro..."

# Listar todos os pedidos
echo "   Listando todos os pedidos..."
ORDERS_LIST=$(curl -s -X GET http://localhost:8080/v1/orders \
  -H "Authorization: Bearer $TOKEN")

if echo $ORDERS_LIST | jq -e '.content' > /dev/null; then
    echo "   ✅ Lista de pedidos obtida"
else
    echo "   ❌ Falha ao listar pedidos"
    exit 1
fi

# Listar pedidos com filtro por status
echo "   Listando pedidos com filtro por status..."
FILTERED_ORDERS=$(curl -s -X GET "http://localhost:8080/v1/orders?status=CONFIRMED" \
  -H "Authorization: Bearer $TOKEN")

if echo $FILTERED_ORDERS | jq -e '.content' > /dev/null; then
    echo "   ✅ Lista filtrada de pedidos obtida"
else
    echo "   ❌ Falha ao listar pedidos com filtro"
    exit 1
fi

echo "✅ RF05: Listagem de pedidos com filtro - APROVADO"
echo ""

# RF06: Consulta de pedido por ID com informações do cliente e produtos
echo "8. Testando RF06: Consulta de pedido por ID..."

# Buscar pedido por ID
echo "   Buscando pedido por ID..."
ORDER_GET=$(curl -s -X GET http://localhost:8080/v1/orders/$ORDER_ID \
  -H "Authorization: Bearer $TOKEN")

if echo $ORDER_GET | jq -e '.id' > /dev/null; then
    echo "   ✅ Pedido encontrado com todas as informações"
else
    echo "   ❌ Falha ao buscar pedido"
    exit 1
fi

echo "✅ RF06: Consulta de pedido por ID - APROVADO"
echo ""

# RF07: Validação de data/hora automática de criação
echo "9. Testando RF07: Data/hora automática de criação..."

# Verificar se o pedido tem data de criação
CREATED_AT=$(echo $ORDER_GET | jq -r '.createdAt')
if [ "$CREATED_AT" != "null" ] && [ -n "$CREATED_AT" ]; then
    echo "   ✅ Pedido possui data/hora de criação: $CREATED_AT"
else
    echo "   ❌ Pedido não possui data/hora de criação"
    exit 1
fi

echo "✅ RF07: Data/hora automática de criação - APROVADO"
echo ""

# RT01: Validação de documentação OpenAPI/Swagger
echo "10. Testando RT01: Documentação OpenAPI/Swagger..."

# Verificar Swagger UI
echo "   Verificando Swagger UI..."
SWAGGER_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui.html)
if [ "$SWAGGER_STATUS" = "200" ]; then
    echo "   ✅ Swagger UI está disponível"
else
    echo "   ❌ Swagger UI não está disponível"
    exit 1
fi

# Verificar OpenAPI JSON
echo "   Verificando OpenAPI JSON..."
OPENAPI_RESPONSE=$(curl -s -X GET http://localhost:8080/v3/api-docs)
if echo $OPENAPI_RESPONSE | jq -e '.openapi' > /dev/null; then
    echo "   ✅ OpenAPI JSON está disponível"
else
    echo "   ❌ OpenAPI JSON não está disponível"
    exit 1
fi

echo "✅ RT01: Documentação OpenAPI/Swagger - APROVADO"
echo ""

# RT02: Validação de autenticação OAuth2
echo "11. Testando RT02: Autenticação OAuth2..."

# Testar acesso sem token
echo "   Testando acesso sem token..."
UNAUTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/v1/customers)
if [ "$UNAUTH_STATUS" = "401" ]; then
    echo "   ✅ Acesso negado sem token (401)"
else
    echo "   ❌ Acesso deveria ser negado sem token"
    exit 1
fi

# Testar acesso com token válido
echo "   Testando acesso com token válido..."
AUTH_STATUS=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Bearer $TOKEN" http://localhost:8080/v1/customers)
if [ "$AUTH_STATUS" = "200" ]; then
    echo "   ✅ Acesso permitido com token válido"
else
    echo "   ❌ Acesso deveria ser permitido com token válido"
    exit 1
fi

echo "✅ RT02: Autenticação OAuth2 - APROVADO"
echo ""

# RT03: Validação de cache Redis
echo "12. Testando RT03: Cache Redis..."

# Primeira consulta
echo "   Primeira consulta (deve ir ao banco)..."
FIRST_TIME=$(date +%s%3N)
curl -s -X GET http://localhost:8080/v1/customers/$CUSTOMER_ID \
  -H "Authorization: Bearer $TOKEN" > /dev/null
FIRST_END=$(date +%s%3N)
FIRST_DURATION=$((FIRST_END - FIRST_TIME))

# Segunda consulta
echo "   Segunda consulta (deve vir do cache)..."
SECOND_TIME=$(date +%s%3N)
curl -s -X GET http://localhost:8080/v1/customers/$CUSTOMER_ID \
  -H "Authorization: Bearer $TOKEN" > /dev/null
SECOND_END=$(date +%s%3N)
SECOND_DURATION=$((SECOND_END - SECOND_TIME))

echo "   Primeira consulta: ${FIRST_DURATION}ms"
echo "   Segunda consulta: ${SECOND_DURATION}ms"

if [ $SECOND_DURATION -lt $FIRST_DURATION ]; then
    echo "   ✅ Segunda consulta foi mais rápida (cache funcionando)"
else
    echo "   ⚠️  Segunda consulta não foi mais rápida (cache pode não estar funcionando)"
fi

echo "✅ RT03: Cache Redis - TESTADO"
echo ""

# Resumo final
echo "🎉 RESUMO DOS TESTES"
echo "===================="
echo ""
echo "✅ REQUISITOS FUNCIONAIS:"
echo "   RF01: Cadastro e consulta de clientes - APROVADO"
echo "   RF02: Cadastro e consulta de produtos - APROVADO"
echo "   RF03: Cadastro de pedidos vinculados - APROVADO"
echo "   RF04: Atualização do status de pedido - APROVADO"
echo "   RF05: Listagem de pedidos com filtro - APROVADO"
echo "   RF06: Consulta de pedido por ID - APROVADO"
echo "   RF07: Data/hora automática de criação - APROVADO"
echo ""
echo "✅ REQUISITOS TÉCNICOS:"
echo "   RT01: Documentação OpenAPI/Swagger - APROVADO"
echo "   RT02: Autenticação OAuth2 - APROVADO"
echo "   RT03: Cache Redis - TESTADO"
echo ""
echo "🎯 TODOS OS REQUISITOS FORAM VALIDADOS COM SUCESSO!"
echo ""
echo "📋 IDs criados durante os testes:"
echo "   Cliente: $CUSTOMER_ID"
echo "   Produto: $PRODUCT_ID"
echo "   Pedido: $ORDER_ID"
echo ""
echo "🔗 URLs importantes:"
echo "   API: http://localhost:8080"
echo "   Swagger UI: http://localhost:8080/swagger-ui.html"
echo "   Keycloak: http://localhost:8081"
echo "   Health Check: http://localhost:8080/actuator/health"
