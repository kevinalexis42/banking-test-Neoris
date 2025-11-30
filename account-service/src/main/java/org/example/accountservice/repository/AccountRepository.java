package org.example.accountservice.repository;

import org.example.accountservice.entity.Account;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    
    Mono<Account> findByAccountNumber(String accountNumber);
    
    Flux<Account> findByCustomerId(Long customerId);
    
    Flux<Account> findByCustomerIdAndStatus(Long customerId, Boolean status);
}

