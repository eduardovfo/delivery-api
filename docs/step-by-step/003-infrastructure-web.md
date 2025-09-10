# Implementação da Camada de Infraestrutura - Web (API REST)

## Visão Geral

Este documento descreve a implementação da camada web do sistema de delivery, incluindo controllers REST, tratamento de exceções, validação e documentação OpenAPI.

## Estrutura Implementada

### 1. DTOs de Resposta

#### PageResponse<T>
- DTO genérico para respostas paginadas
- Campos: content, page, size, totalElements, totalPages, first, last
- Suporte a paginação simples

#### ProblemDetail
- DTO para respostas de erro seguindo RFC 7807
- Campos: type, title, status, detail, instance, timestamp, extensions
- Suporte a extensões customizadas para detalhes de validação

### 2. Tratamento de Exceções

#### GlobalExceptionHandler
- `@RestControllerAdvice` para tratamento global de exceções
- Mapeamento de exceções para respostas HTTP apropriadas
- Respostas no formato RFC 7807 (Problem Details)

#### Exceções Tratadas
- `MethodArgumentNotValidException` - Erros de validação (400)
- `IllegalArgumentException` - Violação de regras de negócio (400)
- `IllegalStateException` - Violação de regras de negócio (400)
- `ResourceNotFoundException` - Recurso não encontrado (404)
- `Exception` - Erro interno genérico (500)

#### ResourceNotFoundException
- Exceção customizada para recursos não encontrados
- Usada pelos controllers para mapear Optional.empty() para 404

### 3. Controllers REST

#### CustomerController
- **POST /v1/customers** - Criar cliente
- **GET /v1/customers/{id}** - Buscar cliente por ID
- **GET /v1/customers** - Listar clientes com paginação

#### ProductController
- **POST /v1/products** - Criar produto
- **GET /v1/products/{id}** - Buscar produto por ID
- **GET /v1/products** - Listar produtos com paginação

#### OrderController
- **POST /v1/orders** - Criar pedido
- **GET /v1/orders/{id}** - Buscar pedido por ID
- **GET /v1/orders** - Listar pedidos com filtro por status e paginação
- **PATCH /v1/orders/{id}/status** - Atualizar status do pedido

### 4. Validação e Documentação

#### Bean Validation
- Validação automática em DTOs de entrada
- Mensagens de erro customizadas em português
- Integração com GlobalExceptionHandler

#### OpenAPI/Swagger
- Documentação completa de todos os endpoints
- Exemplos de request/response
- Descrições detalhadas de parâmetros
- Códigos de status HTTP documentados

### 5. Testes WebMvc

#### Configuração
- `@WebMvcTest` para testes de controllers
- Mock de use cases com `@MockBean`
- `MockMvc` para simulação de requisições HTTP

#### Cobertura de Testes
- **CustomerControllerTest** - 6 cenários de teste
- **ProductControllerTest** - 6 cenários de teste
- **OrderControllerTest** - 8 cenários de teste

## Arquivos Criados

### DTOs
```
src/main/java/br/com/delivery/infrastructure/web/dto/
├── PageResponse.java
└── ProblemDetail.java
```

### Exceções
```
src/main/java/br/com/delivery/infrastructure/web/exception/
├── GlobalExceptionHandler.java
└── ResourceNotFoundException.java
```

### Controllers
```
src/main/java/br/com/delivery/infrastructure/web/controller/
├── CustomerController.java
├── ProductController.java
└── OrderController.java
```

### Testes
```
src/test/java/br/com/delivery/infrastructure/web/controller/
├── CustomerControllerTest.java
├── ProductControllerTest.java
└── OrderControllerTest.java
```

### Configuração
```
src/main/java/br/com/delivery/infrastructure/config/
└── OpenApiConfig.java (atualizado)
```

## Funcionalidades Implementadas

### Endpoints REST Completos

#### Customer Endpoints
- **Criação**: Validação de email/documento únicos
- **Busca**: Por ID com tratamento de 404
- **Listagem**: Paginação simples implementada

#### Product Endpoints
- **Criação**: Validação de preço positivo
- **Busca**: Por ID com tratamento de 404
- **Listagem**: Paginação simples implementada

#### Order Endpoints
- **Criação**: Validação de cliente/produtos existentes
- **Busca**: Por ID com tratamento de 404
- **Listagem**: Filtro por status opcional
- **Atualização**: Status com validação de transições

### Tratamento de Erros Robusto

#### Validação de Entrada
- Bean Validation automática
- Mensagens de erro detalhadas
- Resposta no formato RFC 7807

#### Erros de Negócio
- Mapeamento de exceções para HTTP status
- Mensagens de erro claras
- Detalhes de validação em fieldErrors

#### Recursos Não Encontrados
- Tratamento consistente de Optional.empty()
- Respostas 404 padronizadas
- Mensagens informativas

### Documentação OpenAPI

#### Informações da API
- Título, versão e descrição
- Informações de contato
- Licença MIT
- Servidores de desenvolvimento e produção

#### Documentação de Endpoints
- Descrições detalhadas
- Exemplos de request/response
- Códigos de status documentados
- Parâmetros explicados

## Características Técnicas

### Padrão REST
- URLs semânticas e consistentes
- Métodos HTTP apropriados
- Códigos de status corretos
- Content-Type adequado

### Validação
- Bean Validation em DTOs
- Validação automática pelo Spring
- Mensagens customizadas
- Tratamento global de erros

### Testabilidade
- Testes WebMvc isolados
- Mock de dependências
- Cobertura de cenários de sucesso e erro
- Validação de respostas JSON

### Documentação
- OpenAPI 3.0 completo
- Exemplos práticos
- Interface Swagger UI
- Padrão RFC 7807 para erros

## Considerações de Escalabilidade

### Estrutura de Controllers
- **Separação clara** por domínio
- **Reutilização** de DTOs entre camadas
- **Injeção de dependência** para testabilidade

### Tratamento de Erros
- **Padrão RFC 7807** para consistência
- **GlobalExceptionHandler** centralizado
- **Extensibilidade** para novos tipos de erro

### Paginação
- **DTO genérico** PageResponse
- **Implementação simples** (em produção, usar Spring Data)
- **Preparado** para paginação real

### Documentação
- **OpenAPI** para documentação automática
- **Exemplos** para facilitar integração
- **Versionamento** preparado para v2

## Próximos Passos

1. Implementar autenticação e autorização
2. Adicionar cache para consultas frequentes
3. Implementar paginação real com Spring Data
4. Adicionar logs estruturados
5. Configurar monitoramento e métricas

## Exemplos de Uso

### Criar Cliente
```bash
POST /v1/customers
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@email.com",
  "document": "12345678901"
}
```

### Listar Pedidos com Filtro
```bash
GET /v1/orders?status=CREATED&page=0&size=10
```

### Atualizar Status do Pedido
```bash
PATCH /v1/orders/order-123/status
Content-Type: application/json

{
  "status": "CONFIRMED"
}
```

## Respostas de Erro (RFC 7807)

### Erro de Validação
```json
{
  "type": "https://delivery-api.com/problems/validation-error",
  "title": "Validation Error",
  "status": 400,
  "detail": "One or more fields have validation errors",
  "instance": "/v1/customers",
  "timestamp": "2025-01-27T10:30:00Z",
  "extensions": {
    "fieldErrors": {
      "email": "Email deve ter formato válido",
      "name": "Nome é obrigatório"
    }
  }
}
```

### Recurso Não Encontrado
```json
{
  "type": "https://delivery-api.com/problems/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Cliente não encontrado com ID: customer-123",
  "instance": "/v1/customers/customer-123",
  "timestamp": "2025-01-27T10:30:00Z"
}
```

A implementação está completa e pronta para uso, seguindo as melhores práticas de APIs REST! 🎯
