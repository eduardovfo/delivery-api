package br.com.delivery.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("Delivery API")
            .version("v1.0.0")
            .description("API REST para gerenciamento de entregas de pedidos, contemplando clientes e produtos.")
            .contact(new Contact()
                .name("Equipe de Desenvolvimento")
                .email("dev@delivery-api.com")
                .url("https://delivery-api.com"))
            .license(new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT")))
        .servers(List.of(
            new Server()
                .url("http://localhost:8080")
                .description("Servidor de Desenvolvimento"),
            new Server()
                .url("https://api.delivery.com")
                .description("Servidor de Produção")
        ))
        .tags(List.of(
            new Tag()
                .name("Customers")
                .description("Operações para gerenciamento de clientes"),
            new Tag()
                .name("Products")
                .description("Operações para gerenciamento de produtos"),
            new Tag()
                .name("Orders")
                .description("Operações para gerenciamento de pedidos")
        ));
  }

  @Bean
  public GroupedOpenApi v1Api() {
    return GroupedOpenApi.builder()
        .group("v1")
        .pathsToMatch("/v1/**")
        .build();
  }
}
