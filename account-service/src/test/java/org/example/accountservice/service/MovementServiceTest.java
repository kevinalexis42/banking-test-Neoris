package org.example.accountservice.service;

import org.example.accountservice.dto.MovementDto;
import org.example.accountservice.entity.Account;
import org.example.accountservice.entity.Movement;
import org.example.accountservice.mapper.MovementMapper;
import org.example.accountservice.repository.AccountRepository;
import org.example.accountservice.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {
    
    @Mock
    private MovementRepository movementRepository;
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private MovementMapper movementMapper;
    
    @InjectMocks
    private MovementService movementService;
    
    private Account testAccount;
    private MovementDto testMovementDto;
    private Movement testMovement;
    
    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .id(1L)
                .accountNumber("1234567890")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1000.00"))
                .status(true)
                .customerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        testMovementDto = MovementDto.builder()
                .accountId(1L)
                .movementType("DEBIT")
                .value(new BigDecimal("100.00"))
                .build();
        
        testMovement = Movement.builder()
                .id(1L)
                .accountId(1L)
                .movementType("DEBIT")
                .value(new BigDecimal("100.00"))
                .balance(new BigDecimal("900.00"))
                .movementDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    @Test
    void testCreateMovement_Debit_Success() {
        // Given
        when(accountRepository.findById(1L)).thenReturn(Mono.just(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(Mono.just(testAccount));
        when(movementMapper.toEntity(any(MovementDto.class))).thenReturn(testMovement);
        when(movementRepository.save(any(Movement.class))).thenReturn(Mono.just(testMovement));
        when(movementMapper.toDto(any(Movement.class))).thenReturn(testMovementDto);
        
        // When & Then
        StepVerifier.create(movementService.createMovement(testMovementDto))
                .expectNextCount(1)
                .verifyComplete();
    }
    
    @Test
    void testCreateMovement_InsufficientBalance_Error() {
        // Given
        testMovementDto.setValue(new BigDecimal("2000.00"));
        when(accountRepository.findById(1L)).thenReturn(Mono.just(testAccount));
        
        // When & Then
        StepVerifier.create(movementService.createMovement(testMovementDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Saldo no disponible"))
                .verify();
    }
    
    @Test
    void testCreateMovement_ValueZero_Error() {
        // Given
        testMovementDto.setValue(BigDecimal.ZERO);
        
        // When & Then
        StepVerifier.create(movementService.createMovement(testMovementDto))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException && 
                    throwable.getMessage().equals("Movement value must be greater than zero"))
                .verify();
    }
}

