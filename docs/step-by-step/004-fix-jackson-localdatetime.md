# Correção do Problema de Serialização Jackson com LocalDateTime

## Problema Identificado

O sistema estava apresentando erro de serialização do Jackson ao tentar serializar objetos que contêm `LocalDateTime`:

```
Caused by: com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDateTime` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling
```

## Causa do Problema

O Jackson não consegue serializar tipos de data/hora do Java 8 (`LocalDateTime`, `LocalDate`, etc.) por padrão. É necessário registrar o módulo `JavaTimeModule` para habilitar o suporte a esses tipos.

## Solução Implementada

### 1. Criação da Configuração Global do Jackson

**Arquivo**: `src/main/java/br/com/delivery/infrastructure/config/JacksonConfig.java`

```java
@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
```

### 2. Configuração no application.yml

**Arquivo**: `src/main/resources/application.yml`

```yaml
spring:
  jackson:
    serialization:
      write-dates-as-timestamps: false
    deserialization:
      fail-on-unknown-properties: false
```

### 3. Atualização da Configuração do Redis

**Arquivo**: `src/main/java/br/com/delivery/infrastructure/cache/RedisConfig.java`

- Adicionado `JavaTimeModule` na configuração do `ObjectMapper` do Redis
- Garantido que o cache também suporte `LocalDateTime`

## Benefícios da Solução

1. **Suporte Completo**: Jackson agora serializa/deserializa `LocalDateTime` corretamente
2. **Configuração Global**: Aplicada em toda a aplicação, não apenas no cache
3. **Formato ISO**: Datas são serializadas no formato ISO 8601 (mais legível)
4. **Compatibilidade**: Mantém compatibilidade com APIs REST

## Arquivos Modificados

1. `src/main/java/br/com/delivery/infrastructure/config/JacksonConfig.java` (novo)
2. `src/main/resources/application.yml`
3. `src/main/java/br/com/delivery/infrastructure/cache/RedisConfig.java`

## Teste da Solução

Para testar a correção:

1. Execute o projeto: `./gradlew bootRun`
2. Faça uma requisição GET para: `GET /api/orders?status=CONFIRMED`
3. Verifique se as datas são serializadas corretamente no formato ISO 8601

## Exemplo de Serialização

**Antes** (erro):
```json
{
  "error": "Java 8 date/time type `java.time.LocalDateTime` not supported"
}
```

**Depois** (correto):
```json
{
  "id": "123",
  "customerId": "456",
  "status": "CONFIRMED",
  "createdAt": "2025-01-10T01:48:31.094",
  "items": [...]
}
```

## Análise de Escalabilidade e Manutenibilidade

A solução implementada é **escalável** e **fácil de manter** porque:

- **Escalabilidade**: Configuração global garante que todos os endpoints funcionem corretamente
- **Manutenibilidade**: Configuração centralizada facilita futuras alterações
- **Padrão**: Segue as melhores práticas do Spring Boot para configuração do Jackson
- **Flexibilidade**: Permite configurações específicas por ambiente via `application.yml`

## Próximos Passos Sugeridos

1. **Validação**: Adicionar testes de serialização/deserialização
2. **Formatação**: Considerar configurações de timezone se necessário
3. **Documentação**: Atualizar documentação da API com exemplos de datas
4. **Monitoramento**: Adicionar logs para verificar serialização em produção
