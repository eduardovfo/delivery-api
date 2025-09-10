# Implementação da Camada de Infraestrutura - Persistência

## Visão Geral

Este documento descreve a implementação da camada de infraestrutura de persistência do sistema de delivery, incluindo entidades JPA, repositórios Spring Data, adapters das ports e testes de integração.

## Estrutura Implementada

### 1. Entidades JPA

#### CustomerEntity
- Mapeamento para tabela `customers`
- Campos: id, name, email, document, createdAt, updatedAt
- Constraints: email e document únicos
- Triggers automáticos para updated_at

#### ProductEntity
- Mapeamento para tabela `products`
- Campos: id, name, price, createdAt, updatedAt
- Constraint: price >= 0
- Triggers automáticos para updated_at

#### OrderEntity
- Mapeamento para tabela `orders`
- Campos: id, customer_id, status, total, createdAt, updatedAt
- Relacionamento 1:N com OrderItemEntity
- Enum OrderStatus mapeado como STRING
- Constraint: total >= 0

#### OrderItemEntity
- Mapeamento para tabela `order_items`
- Campos: id (auto-increment), order_id, product_id, quantity, unit_price
- Relacionamento N:1 com OrderEntity
- Constraints: quantity > 0, unit_price >= 0

### 2. Repositórios Spring Data JPA

#### CustomerJpaRepository
- Herda de JpaRepository<CustomerEntity, String>
- Métodos customizados: findByEmail, findByDocument

#### ProductJpaRepository
- Herda de JpaRepository<ProductEntity, String>
- Método customizado: findByNameContaining (case-insensitive)

#### OrderJpaRepository
- Herda de JpaRepository<OrderEntity, String>
- Métodos customizados: findByCustomerId, findByStatus

### 3. Adapters das Ports

#### CustomerRepositoryAdapter
- Implementa CustomerRepositoryPort
- Converte entre Customer (domínio) e CustomerEntity (JPA)
- Métodos: save, findById, findAll, deleteById, existsById, findByEmail, findByDocument

#### ProductRepositoryAdapter
- Implementa ProductRepositoryPort
- Converte entre Product (domínio) e ProductEntity (JPA)
- Métodos: save, findById, findAll, deleteById, existsById, findByNameContaining

#### OrderRepositoryAdapter
- Implementa OrderRepositoryPort
- Converte entre Order (domínio) e OrderEntity (JPA)
- Gerencia relacionamento com OrderItemEntity
- Métodos: save, findById, findAll, deleteById, existsById, findByCustomerId, findByStatus

### 4. Migrações Flyway

#### V1__init.sql
- Criação das tabelas: customers, products, orders, order_items
- Constraints de integridade referencial
- Índices para performance
- Triggers para updated_at automático
- Validações de negócio (preços >= 0, quantidades > 0)

### 5. Testes de Integração

#### Configuração Testcontainers
- PostgreSQL 15 Alpine
- Configuração dinâmica de propriedades
- Isolamento por teste

#### Testes Implementados
- **CustomerRepositoryAdapterIntegrationTest**: CRUD completo + busca por email/documento
- **ProductRepositoryAdapterIntegrationTest**: CRUD completo + busca por nome
- **OrderRepositoryAdapterIntegrationTest**: CRUD completo + relacionamentos + filtros

## Arquivos Criados

### Entidades JPA
```
src/main/java/br/com/delivery/infrastructure/persistence/entity/
├── CustomerEntity.java
├── ProductEntity.java
├── OrderEntity.java
└── OrderItemEntity.java
```

### Repositórios
```
src/main/java/br/com/delivery/infrastructure/persistence/repository/
├── CustomerJpaRepository.java
├── ProductJpaRepository.java
└── OrderJpaRepository.java
```

### Adapters
```
src/main/java/br/com/delivery/infrastructure/persistence/adapter/
├── CustomerRepositoryAdapter.java
├── ProductRepositoryAdapter.java
└── OrderRepositoryAdapter.java
```

### Migrações
```
src/main/resources/db/migration/
└── V1__init.sql
```

### Testes de Integração
```
src/test/java/br/com/delivery/infrastructure/persistence/
├── CustomerRepositoryAdapterIntegrationTest.java
├── ProductRepositoryAdapterIntegrationTest.java
└── OrderRepositoryAdapterIntegrationTest.java
```

## Funcionalidades Implementadas

### Mapeamento Objeto-Relacional
- **Entidades JPA** com anotações completas
- **Relacionamentos** 1:N entre Order e OrderItem
- **Enums** mapeados como STRING
- **Timestamps** automáticos com triggers

### Conversão Domínio-Entidade
- **Adapters** que implementam as ports do domínio
- **Conversão bidirecional** entre entidades de domínio e JPA
- **Preservação** de regras de negócio durante conversão

### Persistência Robusta
- **Transações** gerenciadas pelo Spring
- **Cascata** para operações em relacionamentos
- **Constraints** de integridade no banco

### Testes Abrangentes
- **Testcontainers** para ambiente real
- **Cobertura** de todos os métodos das ports
- **Cenários** de sucesso e erro
- **Isolamento** entre testes

## Características Técnicas

### Performance
- **Índices** estratégicos para consultas frequentes
- **Lazy Loading** para relacionamentos
- **Queries** otimizadas com @Query

### Integridade
- **Constraints** de banco para validação
- **Foreign Keys** para integridade referencial
- **Triggers** para timestamps automáticos

### Manutenibilidade
- **Separação** clara entre domínio e persistência
- **Adapters** que isolam mudanças de implementação
- **Testes** que garantem comportamento correto

## Considerações de Escalabilidade

### Estrutura de Dados
- **UUIDs** como chaves primárias para distribuição
- **Índices** otimizados para consultas
- **Particionamento** preparado para crescimento

### Arquitetura
- **Adapters** permitem troca de implementação
- **Ports** isolam dependências
- **Testes** garantem compatibilidade

### Performance
- **Lazy Loading** reduz uso de memória
- **Índices** aceleram consultas
- **Queries** customizadas para casos específicos

## Próximos Passos

1. Implementar camada web (controllers REST)
2. Configurar autenticação e autorização
3. Adicionar cache para consultas frequentes
4. Implementar auditoria de dados
5. Configurar monitoramento e logs

## Validações de Banco

### Constraints Implementadas
- **customers.email** UNIQUE
- **customers.document** UNIQUE
- **products.price** >= 0
- **orders.total** >= 0
- **order_items.quantity** > 0
- **order_items.unit_price** >= 0
- **orders.status** IN (CREATED, CONFIRMED, SHIPPED, DELIVERED, CANCELED)

### Índices Criados
- **idx_customers_email** - Busca por email
- **idx_customers_document** - Busca por documento
- **idx_products_name** - Busca por nome de produto
- **idx_orders_customer_id** - Pedidos por cliente
- **idx_orders_status** - Pedidos por status
- **idx_order_items_order_id** - Itens por pedido
- **idx_order_items_product_id** - Itens por produto

A implementação está completa e pronta para uso, seguindo as melhores práticas de persistência com Spring Data JPA! 🎯
