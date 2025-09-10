package br.com.delivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.com.delivery.infrastructure.persistence.repository")
public class DeliveryApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DeliveryApiApplication.class, args);
    }
}
