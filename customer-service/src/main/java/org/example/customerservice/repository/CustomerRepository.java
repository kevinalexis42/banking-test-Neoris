package org.example.customerservice.repository;

import org.example.customerservice.entity.Customer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CustomerRepository extends ReactiveCrudRepository<Customer, Long> {
    
    @Query("SELECT c.id, c.person_id, c.password, c.status, c.created_at, c.updated_at FROM customers c INNER JOIN persons p ON c.person_id = p.id WHERE p.identification = :identification")
    Mono<Customer> findByIdentification(String identification);
    
    @Query("SELECT c.id, c.person_id, c.password, c.status, c.created_at, c.updated_at FROM customers c WHERE c.status = :status")
    Flux<Customer> findByStatus(Boolean status);
    
    @Query("SELECT c.id, c.person_id, c.password, c.status, c.created_at, c.updated_at FROM customers c WHERE c.person_id = :personId")
    Mono<Customer> findByPersonId(Long personId);
}

