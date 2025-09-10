# Exemplos de cURL com Autenticação Keycloak

Este documento contém exemplos de como usar a API com autenticação via Keycloak.

## 🔐 Autenticação

### 1. Obter Token de Acesso

#### Usuário Admin (acesso completo)
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

#### Usuário Regular (acesso limitado)
```bash
curl -X POST http://localhost:8081/realms/delivery/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=delivery-api" \
  -d "client_secret=delivery-api-secret" \
  -d "username=user" \
  -d "password=user123" \
  -d "scope=customers:read products:read orders:read"
```

### 2. Extrair Token da Resposta

A resposta será algo como:
```json
{
  "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expires_in": 300,
  "refresh_expires_in": 1800,
  "token_type": "Bearer",
  "not-before-policy": 0,
  "scope": "customers:read customers:write products:read products:write orders:read orders:write"
}
```

Use o valor de `access_token` nos exemplos abaixo.

## 👥 Clientes

### Criar Cliente (Requer: customers:write)
```bash
curl -X POST http://localhost:8080/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "João Silva",
    "email": "joao@email.com",
    "document": "12345678901"
  }'
```

### Buscar Cliente por ID (Requer: customers:read)
```bash
curl -X GET http://localhost:8080/v1/customers/customer-id \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Listar Clientes (Requer: customers:read)
```bash
curl -X GET "http://localhost:8080/v1/customers?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 📦 Produtos

### Criar Produto (Requer: products:write)
```bash
curl -X POST http://localhost:8080/v1/products \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "name": "Notebook Dell Inspiron 15",
    "price": 2999.99
  }'
```

### Buscar Produto por ID (Requer: products:read)
```bash
curl -X GET http://localhost:8080/v1/products/product-id \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Listar Produtos (Requer: products:read)
```bash
curl -X GET "http://localhost:8080/v1/products?page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

## 🛒 Pedidos

### Criar Pedido (Requer: orders:write)
```bash
curl -X POST http://localhost:8080/v1/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "customerId": "customer-id",
    "items": [
      {
        "productId": "product-id",
        "quantity": 2
      }
    ]
  }'
```

### Buscar Pedido por ID (Requer: orders:read)
```bash
curl -X GET http://localhost:8080/v1/orders/order-id \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Listar Pedidos (Requer: orders:read)
```bash
curl -X GET "http://localhost:8080/v1/orders?status=CREATED&page=0&size=10" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Atualizar Status do Pedido (Requer: orders:write)
```bash
curl -X PATCH http://localhost:8080/v1/orders/order-id/status \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "status": "CONFIRMED"
  }'
```

## 🔄 Refresh Token

### Renovar Token de Acesso
```bash
curl -X POST http://localhost:8081/realms/delivery/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=refresh_token" \
  -d "client_id=delivery-api" \
  -d "client_secret=delivery-api-secret" \
  -d "refresh_token=YOUR_REFRESH_TOKEN"
```

## 🚫 Exemplos de Erro

### Token Inválido
```bash
curl -X GET http://localhost:8080/v1/customers \
  -H "Authorization: Bearer invalid-token"
```

Resposta:
```json
{
  "error": "invalid_token",
  "error_description": "Token verification failed"
}
```

### Sem Token
```bash
curl -X GET http://localhost:8080/v1/customers
```

Resposta:
```json
{
  "error": "unauthorized",
  "error_description": "Full authentication is required to access this resource"
}
```

### Escopo Insuficiente
```bash
# Usuário com apenas customers:read tentando criar cliente
curl -X POST http://localhost:8080/v1/customers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_WITHOUT_WRITE_SCOPE" \
  -d '{"name": "Test", "email": "test@email.com", "document": "12345678901"}'
```

Resposta:
```json
{
  "error": "access_denied",
  "error_description": "Access is denied"
}
```

## 📝 Scripts de Teste

### Script para Testar Autenticação
```bash
#!/bin/bash

# Configurações
KEYCLOAK_URL="http://localhost:8081"
API_URL="http://localhost:8080"
CLIENT_ID="delivery-api"
CLIENT_SECRET="delivery-api-secret"

# Função para obter token
get_token() {
    local username=$1
    local password=$2
    local scopes=$3
    
    curl -s -X POST "$KEYCLOAK_URL/realms/delivery/protocol/openid-connect/token" \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -d "grant_type=password" \
      -d "client_id=$CLIENT_ID" \
      -d "client_secret=$CLIENT_SECRET" \
      -d "username=$username" \
      -d "password=$password" \
      -d "scope=$scopes" | jq -r '.access_token'
}

# Testar com usuário admin
echo "🔐 Obtendo token para admin..."
ADMIN_TOKEN=$(get_token "admin" "admin123" "customers:read customers:write products:read products:write orders:read orders:write")

echo "✅ Token obtido: ${ADMIN_TOKEN:0:50}..."

# Testar endpoint protegido
echo "🧪 Testando endpoint protegido..."
curl -s -X GET "$API_URL/v1/customers" \
  -H "Authorization: Bearer $ADMIN_TOKEN" | jq

echo "✅ Teste concluído!"
```

### Script para Testar Escopos
```bash
#!/bin/bash

# Testar diferentes combinações de escopos
test_scopes() {
    local username=$1
    local password=$2
    local scopes=$3
    local endpoint=$4
    local method=$5
    
    echo "🔐 Testando $username com escopos: $scopes"
    
    TOKEN=$(curl -s -X POST "http://localhost:8081/realms/delivery/protocol/openid-connect/token" \
      -H "Content-Type: application/x-www-form-urlencoded" \
      -d "grant_type=password" \
      -d "client_id=delivery-api" \
      -d "client_secret=delivery-api-secret" \
      -d "username=$username" \
      -d "password=$password" \
      -d "scope=$scopes" | jq -r '.access_token')
    
    if [ "$TOKEN" != "null" ]; then
        echo "✅ Token obtido"
        curl -s -X $method "http://localhost:8080$endpoint" \
          -H "Authorization: Bearer $TOKEN" | jq
    else
        echo "❌ Falha ao obter token"
    fi
    echo "---"
}

# Testes
test_scopes "admin" "admin123" "customers:read" "/v1/customers" "GET"
test_scopes "user" "user123" "customers:read" "/v1/customers" "GET"
test_scopes "user" "user123" "customers:write" "/v1/customers" "POST"
```

## 🔧 Configuração do Keycloak

### Importar Realm
```bash
# Importar realm via CLI
docker exec -it keycloak /opt/keycloak/bin/kc.sh import \
  --file /opt/keycloak/data/import/realm-export.json \
  --override true
```

### Verificar Configuração
```bash
# Verificar se o realm foi importado
curl -X GET http://localhost:8081/realms/delivery/.well-known/openid_configuration | jq
```

## 📚 Recursos Adicionais

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Security OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [OpenID Connect](https://openid.net/connect/)
