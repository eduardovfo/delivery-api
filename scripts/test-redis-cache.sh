#!/bin/bash

# Script para testar integração com Redis e validação de cache
# Executa antes: docker-compose up -d redis

echo "🔍 Testando integração com Redis..."

# Verificar se Redis está rodando
echo "1. Verificando se Redis está rodando..."
redis-cli -h localhost -p 6379 ping
if [ $? -eq 0 ]; then
    echo "✅ Redis está rodando"
else
    echo "❌ Redis não está rodando. Execute: docker-compose up -d redis"
    exit 1
fi

# Verificar se a aplicação está rodando
echo "2. Verificando se a aplicação está rodando..."
curl -s http://localhost:8080/actuator/health > /dev/null
if [ $? -eq 0 ]; then
    echo "✅ Aplicação está rodando"
else
    echo "❌ Aplicação não está rodando. Execute: ./gradlew bootRun"
    exit 1
fi

# Obter token de acesso
echo "3. Obtendo token de acesso..."
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

# Criar cliente para teste
echo "4. Criando cliente para teste de cache..."
CUSTOMER_RESPONSE=$(curl -s -X POST http://localhost:8080/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "Cliente Teste Cache",
    "email": "cache@teste.com",
    "document": "12345678901"
  }')

CUSTOMER_ID=$(echo $CUSTOMER_RESPONSE | jq -r '.id')
echo "✅ Cliente criado com ID: $CUSTOMER_ID"

# Primeira consulta (deve ir ao banco)
echo "5. Primeira consulta (deve ir ao banco)..."
FIRST_REQUEST_TIME=$(date +%s%3N)
curl -s -X GET http://localhost:8080/v1/customers/$CUSTOMER_ID \
  -H "Authorization: Bearer $TOKEN" > /dev/null
FIRST_RESPONSE_TIME=$(date +%s%3N)
FIRST_DURATION=$((FIRST_RESPONSE_TIME - FIRST_REQUEST_TIME))
echo "   Tempo da primeira consulta: ${FIRST_DURATION}ms"

# Segunda consulta (deve vir do cache)
echo "6. Segunda consulta (deve vir do cache)..."
SECOND_REQUEST_TIME=$(date +%s%3N)
curl -s -X GET http://localhost:8080/v1/customers/$CUSTOMER_ID \
  -H "Authorization: Bearer $TOKEN" > /dev/null
SECOND_RESPONSE_TIME=$(date +%s%3N)
SECOND_DURATION=$((SECOND_RESPONSE_TIME - SECOND_REQUEST_TIME))
echo "   Tempo da segunda consulta: ${SECOND_DURATION}ms"

# Comparar tempos
echo "7. Comparando tempos de resposta..."
if [ $SECOND_DURATION -lt $FIRST_DURATION ]; then
    echo "✅ Segunda consulta foi mais rápida (possível cache funcionando)"
else
    echo "⚠️  Segunda consulta não foi mais rápida (cache pode não estar funcionando)"
fi

# Verificar chaves no Redis
echo "8. Verificando chaves no Redis..."
REDIS_KEYS=$(redis-cli -h localhost -p 6379 keys "*" | wc -l)
echo "   Número de chaves no Redis: $REDIS_KEYS"

if [ $REDIS_KEYS -gt 0 ]; then
    echo "✅ Redis contém chaves (cache pode estar funcionando)"
    echo "   Chaves encontradas:"
    redis-cli -h localhost -p 6379 keys "*" | head -10
else
    echo "⚠️  Redis não contém chaves (cache pode não estar funcionando)"
fi

# Teste de lista de clientes
echo "9. Testando cache para lista de clientes..."
curl -s -X GET http://localhost:8080/v1/customers \
  -H "Authorization: Bearer $TOKEN" > /dev/null
echo "✅ Lista de clientes consultada"

# Verificar logs da aplicação para cache
echo "10. Verificando logs de cache..."
echo "   (Verifique os logs da aplicação para mensagens relacionadas ao cache)"

echo ""
echo "🎉 Teste de integração com Redis concluído!"
echo ""
echo "📋 Resumo:"
echo "   - Primeira consulta: ${FIRST_DURATION}ms"
echo "   - Segunda consulta: ${SECOND_DURATION}ms"
echo "   - Chaves no Redis: $REDIS_KEYS"
echo ""
echo "💡 Dicas:"
echo "   - Se a segunda consulta for mais rápida, o cache está funcionando"
echo "   - Verifique os logs da aplicação para mensagens de cache"
echo "   - O Redis pode estar usando cache simples em ambiente de teste"
