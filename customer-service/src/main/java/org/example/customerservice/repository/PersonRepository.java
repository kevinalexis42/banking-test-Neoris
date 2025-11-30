package org.example.customerservice.repository;

import org.example.customerservice.entity.Person;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface PersonRepository extends ReactiveCrudRepository<Person, Long> {
    
    @Query("SELECT id, name, gender, identification, address, phone, created_at, updated_at FROM persons WHERE identification = :identification")
    Mono<Person> findByIdentification(String identification);
    
    @Query("SELECT id, name, gender, identification, address, phone, created_at, updated_at FROM persons WHERE id = :id")
    Mono<Person> findById(Long id);
}

