package br.com.delivery.infrastructure.persistence.repository;

import br.com.delivery.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, String> {
    
    Optional<CustomerEntity> findByEmail(String email);
    
    Optional<CustomerEntity> findByDocument(String document);
}
