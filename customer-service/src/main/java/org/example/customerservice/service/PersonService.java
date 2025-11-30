package org.example.customerservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.customerservice.dto.PersonDto;
import org.example.customerservice.entity.Person;
import org.example.customerservice.mapper.PersonMapper;
import org.example.customerservice.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {
    
    private final PersonRepository personRepository;
    private final PersonMapper personMapper;
    
    public Mono<PersonDto> createPerson(PersonDto personDto) {
        log.info("Creating person with identification: {}", personDto.getIdentification());
        
        return personRepository.findByIdentification(personDto.getIdentification())
                .flatMap(existing -> Mono.error(new RuntimeException("Person with identification already exists")))
                .cast(Person.class)
                .switchIfEmpty(
                        Mono.defer(() -> {
                            Person person = personMapper.toEntity(personDto);
                            person.setCreatedAt(LocalDateTime.now());
                            person.setUpdatedAt(LocalDateTime.now());
                            return personRepository.save(person);
                        })
                )
                .map(personMapper::toDto)
                .doOnSuccess(p -> log.info("Person created successfully with ID: {}", p.getId()))
                .doOnError(error -> log.error("Error creating person: {}", error.getMessage()));
    }
    
    public Mono<PersonDto> getPersonById(Long id) {
        log.info("Fetching person with ID: {}", id);
        return personRepository.findById(id)
                .map(personMapper::toDto)
                .switchIfEmpty(Mono.error(new RuntimeException("Person not found with ID: " + id)))
                .doOnError(error -> log.error("Error fetching person: {}", error.getMessage()));
    }
    
    public Flux<PersonDto> getAllPersons() {
        log.info("Fetching all persons");
        return personRepository.findAll()
                .map(personMapper::toDto)
                .doOnError(error -> log.error("Error fetching persons: {}", error.getMessage()));
    }
    
    @Transactional
    public Mono<PersonDto> updatePerson(Long id, PersonDto personDto) {
        log.info("Updating person with ID: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Person not found with ID: " + id)))
                .flatMap(existing -> {
                    personMapper.updateEntityFromDto(personDto, existing);
                    existing.setUpdatedAt(LocalDateTime.now());
                    return personRepository.save(existing);
                })
                .map(personMapper::toDto)
                .doOnSuccess(p -> log.info("Person updated successfully with ID: {}", p.getId()))
                .doOnError(error -> log.error("Error updating person: {}", error.getMessage()));
    }
    
    @Transactional
    public Mono<Void> deletePerson(Long id) {
        log.info("Deleting person with ID: {}", id);
        return personRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Person not found with ID: " + id)))
                .flatMap(personRepository::delete)
                .doOnSuccess(v -> log.info("Person deleted successfully with ID: {}", id))
                .doOnError(error -> log.error("Error deleting person: {}", error.getMessage()));
    }
}

