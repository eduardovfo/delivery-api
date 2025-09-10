# Implementação de Cache Redis

## Visão Geral

Este documento descreve a implementação de cache Redis para acelerar consultas GET e invalidar cache em operações de escrita, incluindo configuração, anotações de cache e testes de integração.

## Estrutura Implementada

### 1. Configuração Redis

#### RedisConfig
- **RedisTemplate** configurado com Jackson2JsonRedisSerializer
- **Tipos seguros** com ObjectMapper configurado
- **CacheManager** com TTL específico por cache
- **Serialização JSON** para objetos complexos

#### Configurações
- **Host**: localhost:6379
- **Pool de conexões** configurado
- **TTL padrão**: 300 segundos (5 minutos)
- **Estatísticas** habilitadas

### 2. Anotações de Cache

#### @Cacheable (Consultas)
- **GetCustomerUseCase**: `@Cacheable(value = "customer", key = "#customerId")`
- **GetProductUseCase**: `@Cacheable(value = "product", key = "#productId")`
- **GetOrderUseCase**: `@Cacheable(value = "order", key = "#orderId")`
- **ListCustomersUseCase**: `@Cacheable(value = "customers")`
- **ListProductsUseCase**: `@Cacheable(value = "products")`
- **ListOrdersUseCase**: `@Cacheable(value = "orders", key = "#status != null ? #status.name() : 'ALL'")`

#### @CacheEvict/@Caching (Escritas)
- **CreateCustomerUseCase**: Evicta `customers` e `customer`
- **CreateProductUseCase**: Evicta `products` e `product`
- **CreateOrderUseCase**: Evicta `orders` e `order`
- **UpdateOrderStatusUseCase**: Evicta `orders` e `order` específico

### 3. TTL Configurado

#### Cache de Listas (300s)
- **customers** - Lista de clientes
- **products** - Lista de produtos
- **orders** - Lista de pedidos

#### Cache de Detalhes (600s)
- **customer** - Cliente individual
- **product** - Produto individual
- **order** - Pedido individual

### 4. Testes de Integração

#### RedisCacheIntegrationTest
- **Testcontainers Redis** para testes isolados
- **Verificação de hit/miss** do cache
- **Teste de invalidação** em operações de escrita
- **Verificação de chaves** específicas

#### CacheHitMissTest
- **Demonstração de cache miss/hit**
- **Teste de eviction** em operações de escrita
- **Verificação de estatísticas** do cache
- **Teste com valores null**

## Arquivos Criados

### Configuração de Cache
```
src/main/java/br/com/delivery/infrastructure/cache/
└── RedisConfig.java
```

### Testes de Cache
```
src/test/java/br/com/delivery/infrastructure/cache/
├── RedisCacheIntegrationTest.java
└── CacheHitMissTest.java
```

### Configuração
```
src/main/resources/
└── application.yml (atualizado)
```

## Funcionalidades Implementadas

### Cache de Consultas

#### @Cacheable
- **Cache automático** em consultas GET
- **Chaves específicas** por ID
- **TTL diferenciado** por tipo de cache
- **Serialização JSON** para objetos complexos

#### Estratégias de Cache
- **Cache por ID**: Para entidades individuais
- **Cache de lista**: Para listagens completas
- **Cache por status**: Para filtros específicos
- **Não cacheia null**: Evita cache de valores vazios

### Invalidação de Cache

#### @CacheEvict
- **Invalidação automática** em operações de escrita
- **Evict all entries**: Para listas que podem ter mudado
- **Evict específico**: Para entidades individuais
- **@Caching**: Para múltiplas operações de evict

#### Estratégias de Invalidação
- **Create operations**: Evicta listas e entidades relacionadas
- **Update operations**: Evicta listas e entidade específica
- **Delete operations**: Evicta listas e entidade específica

### Configuração Redis

#### RedisTemplate
- **Jackson2JsonRedisSerializer** para serialização
- **Tipos seguros** com ObjectMapper configurado
- **StringRedisSerializer** para chaves
- **Pool de conexões** otimizado

#### CacheManager
- **TTL específico** por cache
- **Configuração de null values** desabilitada
- **Estatísticas** habilitadas
- **Configuração por cache** individual

## Características Técnicas

### Serialização JSON
- **Jackson2JsonRedisSerializer** para valores
- **StringRedisSerializer** para chaves
- **ObjectMapper** com tipos seguros
- **Serialização de objetos complexos**

### TTL Configurado
- **Listas**: 300 segundos (5 minutos)
- **Detalhes**: 600 segundos (10 minutos)
- **Configuração flexível** por cache
- **Expiração automática** de entradas

### Testes Abrangentes
- **Testcontainers Redis** para isolamento
- **Testes de hit/miss** do cache
- **Testes de invalidação** em escritas
- **Verificação de chaves** específicas

## Exemplos de Uso

### Consulta com Cache
```java
// Primeira chamada - vai ao banco e cacheia
Optional<CustomerDto> customer = getCustomerUseCase.execute("customer-1");

// Segunda chamada - vem do cache
Optional<CustomerDto> cachedCustomer = getCustomerUseCase.execute("customer-1");
```

### Invalidação de Cache
```java
// Criar cliente - invalida cache de listas e detalhes
CustomerDto newCustomer = createCustomerUseCase.execute(request);

// Próxima consulta - vai ao banco novamente
List<CustomerDto> customers = listCustomersUseCase.execute();
```

### Verificação de Cache
```java
// Verificar se existe no cache
Cache cache = cacheManager.getCache("customer");
Object cachedValue = cache.get("customer-1");

// Verificar estatísticas
CacheStatistics stats = cache.getStatistics();
```

## Configuração de Produção

### Redis
- **Clusters Redis** para alta disponibilidade
- **Persistência** configurada adequadamente
- **Monitoramento** de performance
- **Backup** regular dos dados

### Cache
- **TTL apropriado** para cada tipo de dados
- **Limpeza automática** de entradas expiradas
- **Monitoramento** de hit/miss ratio
- **Alertas** para problemas de cache

## Considerações de Performance

### Benefícios
- **Redução de latência** em consultas frequentes
- **Menor carga** no banco de dados
- **Melhor experiência** do usuário
- **Escalabilidade** melhorada

### Monitoramento
- **Hit/Miss ratio** do cache
- **Tempo de resposta** das consultas
- **Uso de memória** do Redis
- **Latência** das operações

## Troubleshooting

### Problemas Comuns

#### Cache não funciona
- Verificar se o Redis está rodando
- Confirmar configuração de conexão
- Validar anotações de cache

#### Invalidação não funciona
- Verificar anotações @CacheEvict
- Confirmar chaves de cache
- Validar configuração de @Caching

#### Performance ruim
- Verificar TTL apropriado
- Monitorar hit/miss ratio
- Ajustar pool de conexões

## Próximos Passos

1. Implementar cache distribuído
2. Adicionar métricas de cache
3. Configurar alertas de performance
4. Implementar cache warming
5. Adicionar cache de queries complexas

## Comandos Úteis

### Verificar Cache
```bash
# Conectar ao Redis
redis-cli

# Listar chaves
KEYS *

# Ver valor específico
GET "customer::customer-1"

# Ver TTL
TTL "customer::customer-1"
```

### Limpar Cache
```bash
# Limpar cache específico
DEL "customer::customer-1"

# Limpar todos os caches
FLUSHALL
```

A implementação de cache Redis está completa e pronta para uso em produção! 🚀
