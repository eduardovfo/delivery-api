# Implementação da Camada de Aplicação - Casos de Uso

## Visão Geral

Este documento descreve a implementação da camada de aplicação (application layer) do sistema de delivery, incluindo casos de uso, DTOs, mappers e testes unitários.

## Estrutura Implementada

### 1. DTOs (Data Transfer Objects)

#### DTOs de Entrada (Requests)
- `CreateCustomerRequest` - Dados para criação de cliente
- `CreateProductRequest` - Dados para criação de produto
- `CreateOrderRequest` - Dados para criação de pedido
- `CreateOrderItemRequest` - Dados para item do pedido
- `UpdateOrderStatusRequest` - Dados para atualização de status do pedido

#### DTOs de Saída (Response)
- `CustomerDto` - Dados do cliente
- `ProductDto` - Dados do produto
- `OrderDto` - Dados do pedido
- `OrderItemDto` - Dados do item do pedido

### 2. Mappers MapStruct

- `CustomerMapper` - Conversão entre Customer e DTOs
- `ProductMapper` - Conversão entre Product e DTOs
- `OrderMapper` - Conversão entre Order e DTOs
- `OrderItemMapper` - Conversão entre OrderItem e DTOs

### 3. Casos de Uso (Use Cases)

#### Customer Use Cases
- `CreateCustomerUseCase` - Criação de cliente com validação de email/documento únicos
- `GetCustomerUseCase` - Busca de cliente por ID
- `ListCustomersUseCase` - Listagem de todos os clientes

#### Product Use Cases
- `CreateProductUseCase` - Criação de produto
- `GetProductUseCase` - Busca de produto por ID
- `ListProductsUseCase` - Listagem de todos os produtos

#### Order Use Cases
- `CreateOrderUseCase` - Criação de pedido com validação de cliente/produtos e preços atuais
- `GetOrderUseCase` - Busca de pedido por ID
- `ListOrdersUseCase` - Listagem de pedidos com filtro opcional por status
- `UpdateOrderStatusUseCase` - Atualização de status do pedido

### 4. Testes Unitários

Todos os casos de uso possuem testes unitários completos que:
- Mockam as dependências (ports)
- Testam cenários de sucesso
- Testam cenários de erro
- Validam validações de entrada
- Verificam comportamento dos mocks

## Arquivos Criados

### DTOs
```
src/main/java/br/com/delivery/application/dto/
├── CreateCustomerRequest.java
├── CreateProductRequest.java
├── CreateOrderRequest.java
├── CreateOrderItemRequest.java
├── UpdateOrderStatusRequest.java
├── CustomerDto.java
├── ProductDto.java
├── OrderDto.java
└── OrderItemDto.java
```

### Mappers
```
src/main/java/br/com/delivery/application/mapper/
├── CustomerMapper.java
├── ProductMapper.java
├── OrderMapper.java
└── OrderItemMapper.java
```

### Use Cases
```
src/main/java/br/com/delivery/application/usecase/
├── CreateCustomerUseCase.java
├── GetCustomerUseCase.java
├── ListCustomersUseCase.java
├── CreateProductUseCase.java
├── GetProductUseCase.java
├── ListProductsUseCase.java
├── CreateOrderUseCase.java
├── GetOrderUseCase.java
├── ListOrdersUseCase.java
└── UpdateOrderStatusUseCase.java
```

### Testes
```
src/test/java/br/com/delivery/application/usecase/
├── CreateCustomerUseCaseTest.java
├── GetCustomerUseCaseTest.java
├── ListCustomersUseCaseTest.java
├── CreateProductUseCaseTest.java
├── GetProductUseCaseTest.java
├── ListProductsUseCaseTest.java
├── CreateOrderUseCaseTest.java
├── GetOrderUseCaseTest.java
├── ListOrdersUseCaseTest.java
└── UpdateOrderStatusUseCaseTest.java
```

## Funcionalidades Implementadas

### Validações de Negócio

1. **CreateCustomerUseCase**
   - Validação de email único
   - Validação de documento único
   - Geração automática de ID

2. **CreateOrderUseCase**
   - Validação de existência do cliente
   - Validação de existência dos produtos
   - Uso de preços atuais dos produtos
   - Cálculo automático do total

3. **UpdateOrderStatusUseCase**
   - Validação de regras de transição de status
   - Prevenção de alterações em pedidos cancelados/entregues

### Características Técnicas

- **Injeção de Dependência**: Uso de construtores para injeção
- **Transações**: Anotação `@Transactional` nos casos de uso que modificam dados
- **Validação**: Uso de Bean Validation nos DTOs de entrada
- **Mapeamento**: MapStruct para conversão entre DTOs e entidades
- **Testes**: Cobertura completa com mocks das dependências

## Próximos Passos

1. Implementar camada de infraestrutura (persistence)
2. Implementar camada de apresentação (web controllers)
3. Configurar integração com banco de dados
4. Implementar autenticação e autorização
5. Adicionar logs e monitoramento

## Considerações de Escalabilidade

- **Separação de Responsabilidades**: Cada caso de uso tem uma responsabilidade específica
- **Reutilização**: Mappers podem ser reutilizados em diferentes contextos
- **Testabilidade**: Fácil mockagem de dependências para testes
- **Manutenibilidade**: Código organizado e bem estruturado
- **Extensibilidade**: Fácil adição de novos casos de uso seguindo o mesmo padrão
