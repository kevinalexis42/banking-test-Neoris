package org.example.customerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.customerservice.dto.CustomerDto;
import org.example.customerservice.dto.CustomerRequestDto;
import org.example.customerservice.dto.CustomerUpdateDto;
import org.example.customerservice.dto.PersonDto;
import org.example.customerservice.entity.Customer;
import org.example.customerservice.entity.Person;
import org.example.customerservice.mapper.CustomerMapper;
import org.example.customerservice.mapper.PersonMapper;
import org.example.customerservice.repository.CustomerRepository;
import org.example.customerservice.repository.PersonRepository;
import org.example.customerservice.dto.CustomerEventDto;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    private final CustomerMapper customerMapper;
    private final ReactiveKafkaProducerTemplate<String, CustomerEventDto> kafkaProducerTemplate;
    
    @Transactional
    public Mono<CustomerDto> createCustomer(CustomerRequestDto requestDto) {
        log.info("Creating customer for person with identification: {}", requestDto.getPerson().getIdentification());
        
        return personRepository.findByIdentification(requestDto.getPerson().getIdentification())
                .flatMap(existingPerson -> 
                    customerRepository.findByPersonId(existingPerson.getId())
                            .flatMap(existingCustomer -> 
                                Mono.error(new RuntimeException(
                                    String.format("Ya existe un cliente registrado con la identificación '%s'. Por favor, utilice una identificación diferente.", 
                                    requestDto.getPerson().getIdentification()))))
                            .cast(Customer.class)
                            .switchIfEmpty(
                                    Mono.defer(() -> {
                                        existingPerson.setName(requestDto.getPerson().getName());
                                        existingPerson.setGender(requestDto.getPerson().getGender());
                                        existingPerson.setAddress(requestDto.getPerson().getAddress());
                                        existingPerson.setPhone(requestDto.getPerson().getPhone());
                                        existingPerson.setUpdatedAt(LocalDateTime.now());
                                        
                                        return personRepository.save(existingPerson)
                                                .flatMap(updatedPerson -> {
                                                    Customer customer = Customer.builder()
                                                            .personId(updatedPerson.getId())
                                                            .password(requestDto.getPassword())
                                                            .status(requestDto.getStatus())
                                                            .createdAt(LocalDateTime.now())
                                                            .updatedAt(LocalDateTime.now())
                                                            .build();
                                                    return customerRepository.save(customer);
                                                });
                                    })
                            )
                )
                .switchIfEmpty(
                        Mono.defer(() -> {
                            Person person = personMapper.toEntity(
                                    PersonDto.builder()
                                            .name(requestDto.getPerson().getName())
                                            .gender(requestDto.getPerson().getGender())
                                            .identification(requestDto.getPerson().getIdentification())
                                            .address(requestDto.getPerson().getAddress())
                                            .phone(requestDto.getPerson().getPhone())
                                            .build()
                            );
                            person.setCreatedAt(LocalDateTime.now());
                            person.setUpdatedAt(LocalDateTime.now());
                            
                            return personRepository.save(person)
                                    .flatMap(savedPerson -> {
                                        Customer customer = Customer.builder()
                                                .personId(savedPerson.getId())
                                                .password(requestDto.getPassword())
                                                .status(requestDto.getStatus())
                                                .createdAt(LocalDateTime.now())
                                                .updatedAt(LocalDateTime.now())
                                                .build();
                                        return customerRepository.save(customer);
                                    });
                        })
                )
                .doOnSuccess(savedCustomer -> 
                    sendCustomerEvent("CREATED", savedCustomer.getId(), savedCustomer.getPersonId())
                            .subscribe(
                                    null,
                                    error -> log.error("Error sending customer event (non-blocking): {}", error.getMessage())
                            )
                )
                .flatMap(customer -> 
                    personRepository.findById(customer.getPersonId())
                            .map(person -> CustomerDto.builder()
                                    .id(customer.getId())
                                    .name(person.getName())
                                    .gender(person.getGender())
                                    .identification(person.getIdentification())
                                    .address(person.getAddress())
                                    .phone(person.getPhone())
                                    .password(customer.getPassword())
                                    .status(customer.getStatus())
                                    .createdAt(customer.getCreatedAt())
                                    .updatedAt(customer.getUpdatedAt())
                                    .build())
                )
                .doOnSuccess(c -> log.info("Customer created successfully with ID: {}", c.getId()))
                .doOnError(error -> log.error("Error creating customer: {}", error.getMessage()));
    }
    
    public Mono<CustomerDto> getCustomerById(Long id) {
        log.info("Fetching customer with ID: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with ID: " + id)))
                .flatMap(customer -> 
                    personRepository.findById(customer.getPersonId())
                            .map(person -> buildCustomerDto(customer, person))
                )
                .doOnError(error -> log.error("Error fetching customer: {}", error.getMessage()));
    }
    
    public Flux<CustomerDto> getAllCustomers() {
        log.info("Fetching all active customers (status = true)");
        return customerRepository.findByStatus(true)
                .flatMap(customer -> 
                    personRepository.findById(customer.getPersonId())
                            .map(person -> buildCustomerDto(customer, person))
                            .switchIfEmpty(Mono.just(buildCustomerDtoWithoutPerson(customer)))
                )
                .doOnError(error -> log.error("Error fetching customers: {}", error.getMessage()));
    }
    
    @Transactional
    public Mono<CustomerDto> updateCustomer(Long id, CustomerUpdateDto updateDto) {
        log.info("Updating customer with ID: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with ID: " + id)))
                .flatMap(existingCustomer -> {
                    if (updateDto.getPassword() != null) {
                        existingCustomer.setPassword(updateDto.getPassword());
                    }
                    if (updateDto.getStatus() != null) {
                        existingCustomer.setStatus(updateDto.getStatus());
                    }
                    existingCustomer.setUpdatedAt(LocalDateTime.now());
                    
                    return personRepository.findById(existingCustomer.getPersonId())
                            .switchIfEmpty(Mono.error(new RuntimeException("Person not found for customer")))
                            .flatMap(existingPerson -> {
                                if (updateDto.getName() != null) {
                                    existingPerson.setName(updateDto.getName());
                                }
                                if (updateDto.getGender() != null) {
                                    existingPerson.setGender(updateDto.getGender());
                                }
                                if (updateDto.getIdentification() != null) {
                                    existingPerson.setIdentification(updateDto.getIdentification());
                                }
                                if (updateDto.getAddress() != null) {
                                    existingPerson.setAddress(updateDto.getAddress());
                                }
                                if (updateDto.getPhone() != null) {
                                    existingPerson.setPhone(updateDto.getPhone());
                                }
                                existingPerson.setUpdatedAt(LocalDateTime.now());
                                
                                return personRepository.save(existingPerson)
                                        .then(customerRepository.save(existingCustomer))
                                        .flatMap(updated -> {
                                            sendCustomerEvent("UPDATED", updated.getId(), updated.getPersonId())
                                                    .subscribe(
                                                            null,
                                                            error -> log.error("Error sending customer event (non-blocking): {}", error.getMessage())
                                                    );
                                            return personRepository.findById(updated.getPersonId())
                                                    .map(person -> buildCustomerDto(updated, person));
                                        });
                            });
                })
                .doOnSuccess(c -> log.info("Customer updated successfully with ID: {}", c.getId()))
                .doOnError(error -> log.error("Error updating customer: {}", error.getMessage()));
    }
    
    @Transactional
    public Mono<Void> deleteCustomer(Long id) {
        log.info("Deleting customer with ID: {}", id);
        return customerRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Customer not found with ID: " + id)))
                .flatMap(customer -> 
                    customerRepository.delete(customer)
                            .doOnSuccess(v -> 
                                sendCustomerEvent("DELETED", customer.getId(), customer.getId())
                                        .subscribe(
                                                null,
                                                error -> log.error("Error sending customer event (non-blocking): {}", error.getMessage())
                                        )
                            )
                )
                .doOnSuccess(v -> log.info("Customer deleted successfully with ID: {}", id))
                .doOnError(error -> log.error("Error deleting customer: {}", error.getMessage()));
    }
    
    private Mono<Void> sendCustomerEvent(String eventType, Long customerId, Long personId) {
        log.info("Sending customer event: {} for customer ID: {}", eventType, customerId);
        return kafkaProducerTemplate.send("customer-events", 
                CustomerEventDto.builder()
                        .eventType(eventType)
                        .customerId(customerId)
                        .personId(personId)
                        .timestamp(System.currentTimeMillis())
                        .build())
                .then()
                .timeout(java.time.Duration.ofSeconds(5))
                .onErrorResume(error -> {
                    log.error("Error sending customer event (will not block operation): {}", error.getMessage());
                    return Mono.empty(); // Return empty to not block
                });
    }
    
    private CustomerDto buildCustomerDto(Customer customer, Person person) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(person.getName())
                .gender(person.getGender())
                .identification(person.getIdentification())
                .address(person.getAddress())
                .phone(person.getPhone())
                .password(customer.getPassword())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
    
    private CustomerDto buildCustomerDtoWithoutPerson(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .name(null)
                .gender(null)
                .identification(null)
                .address(null)
                .phone(null)
                .password(customer.getPassword())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}

