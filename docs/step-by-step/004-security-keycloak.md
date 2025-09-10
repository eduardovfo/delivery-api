# Implementação de Segurança com Keycloak (Resource Server)

## Visão Geral

Este documento descreve a implementação de segurança OAuth2 JWT com Keycloak como Resource Server no sistema de delivery, incluindo autenticação, autorização e testes de integração.

## Estrutura Implementada

### 1. Configuração Spring Security

#### SecurityConfig
- **Resource Server** configurado para OAuth2 JWT
- **JWT Decoder** configurado para Keycloak
- **Method Security** habilitado para `@PreAuthorize`
- **Endpoints públicos** para documentação e health check

#### Configurações
- **Issuer URI**: `http://localhost:8081/realms/delivery`
- **JWK Set URI**: Configurado automaticamente
- **Profiles**: Ativo em produção, desabilitado em dev/test

### 2. Autorização por Escopo

#### Escopos Implementados
- **customers:read** - Leitura de clientes
- **customers:write** - Escrita de clientes
- **products:read** - Leitura de produtos
- **products:write** - Escrita de produtos
- **orders:read** - Leitura de pedidos
- **orders:write** - Escrita de pedidos

#### Anotações de Autorização
- **@PreAuthorize** em todos os endpoints protegidos
- **Validação de escopo** por operação
- **Separação clara** entre leitura e escrita

### 3. Configuração Keycloak

#### Realm Export
- **Realm**: `delivery`
- **Client**: `delivery-api` (confidential)
- **Usuários**: `admin` e `user` com senhas
- **Escopos**: Todos os escopos necessários
- **Roles**: Admin e User com permissões apropriadas

#### Usuários Configurados
- **admin**: Acesso completo a todos os escopos
- **user**: Acesso limitado apenas a leitura

### 4. Testes de Segurança

#### SecurityIntegrationTest
- **Testes de escopo** com `@WithMockUser`
- **Validação de permissões** por endpoint
- **Cenários de sucesso e falha**

#### JwtTokenTest
- **Token inválido** e malformado
- **Token expirado** e com issuer errado
- **Escopos insuficientes** e incorretos
- **Formato de header** incorreto

## Arquivos Criados

### Configuração de Segurança
```
src/main/java/br/com/delivery/infrastructure/security/
└── SecurityConfig.java (atualizado)
```

### Configuração Keycloak
```
keycloak/
└── realm-export.json
```

### Exemplos de API
```
docs/api-examples/
└── curl-examples.md
```

### Testes de Segurança
```
src/test/java/br/com/delivery/infrastructure/web/security/
├── SecurityIntegrationTest.java
└── JwtTokenTest.java
```

### Configuração
```
src/main/resources/
└── application.yml (atualizado)
```

## Funcionalidades Implementadas

### Autenticação OAuth2 JWT

#### Configuração do Resource Server
- **JWT Decoder** configurado para Keycloak
- **Validação automática** de tokens
- **Cache de chaves públicas** para performance

#### Endpoints Protegidos
- **Todos os endpoints** `/v1/**` requerem autenticação
- **Endpoints públicos** para documentação e health check
- **Validação de token** em todas as requisições

### Autorização por Escopo

#### Escopos de Leitura
- **customers:read** - GET /v1/customers
- **products:read** - GET /v1/products
- **orders:read** - GET /v1/orders

#### Escopos de Escrita
- **customers:write** - POST /v1/customers
- **products:write** - POST /v1/products
- **orders:write** - POST /v1/orders, PATCH /v1/orders/{id}/status

#### Validação de Escopo
- **@PreAuthorize** em cada endpoint
- **Verificação de autoridade** por escopo
- **Resposta 403** para escopos insuficientes

### Configuração Keycloak

#### Realm Delivery
- **Client confidential** com secret
- **Scopes customizados** para cada recurso
- **Usuários de teste** com permissões apropriadas

#### Usuários de Teste
- **admin/admin123** - Acesso completo
- **user/user123** - Acesso limitado

## Características Técnicas

### Spring Security OAuth2
- **Resource Server** configurado
- **JWT Decoder** com Nimbus
- **Method Security** habilitado
- **Profiles** para diferentes ambientes

### Keycloak Integration
- **Realm export** completo
- **Client configuration** adequada
- **Scopes** bem definidos
- **Usuários** de teste configurados

### Testes Abrangentes
- **Testes de integração** com MockMvc
- **Testes de token** com cenários reais
- **Validação de escopos** completa
- **Cenários de erro** cobertos

## Exemplos de Uso

### Obter Token de Acesso
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

### Usar Token na API
```bash
curl -X GET http://localhost:8080/v1/customers \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

### Criar Recurso com Token
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

## Respostas de Erro

### Token Inválido (401)
```json
{
  "error": "invalid_token",
  "error_description": "Token verification failed"
}
```

### Escopo Insuficiente (403)
```json
{
  "error": "access_denied",
  "error_description": "Access is denied"
}
```

### Sem Token (401)
```json
{
  "error": "unauthorized",
  "error_description": "Full authentication is required to access this resource"
}
```

## Considerações de Segurança

### Validação de Token
- **Assinatura** verificada com chaves públicas
- **Expiração** validada automaticamente
- **Issuer** verificado contra configuração
- **Escopos** validados por endpoint

### Configuração de Produção
- **HTTPS** obrigatório em produção
- **Chaves** rotacionadas regularmente
- **Logs** de segurança implementados
- **Monitoramento** de tentativas de acesso

### Boas Práticas
- **Princípio do menor privilégio** aplicado
- **Escopos granulares** por recurso
- **Tokens** com tempo de vida apropriado
- **Refresh tokens** para renovação

## Próximos Passos

1. Implementar refresh token automático
2. Adicionar logs de auditoria
3. Configurar rate limiting
4. Implementar cache de tokens
5. Adicionar métricas de segurança

## Troubleshooting

### Problemas Comuns

#### Token Inválido
- Verificar se o Keycloak está rodando
- Confirmar se o realm está importado
- Validar se o client está configurado

#### Escopo Insuficiente
- Verificar se o usuário tem o escopo necessário
- Confirmar se o client tem o escopo configurado
- Validar se o token foi gerado com o escopo correto

#### Erro de Conexão
- Verificar se o issuer-uri está correto
- Confirmar se a rede está acessível
- Validar se o certificado SSL está correto

A implementação de segurança está completa e pronta para uso em produção! 🔐
