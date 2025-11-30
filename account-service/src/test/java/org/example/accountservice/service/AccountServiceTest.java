package org.example.accountservice.service;

import org.example.accountservice.dto.AccountDto;
import org.example.accountservice.entity.Account;
import org.example.accountservice.mapper.AccountMapper;
import org.example.accountservice.repository.AccountRepository;
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
class AccountServiceTest {
    
    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private AccountMapper accountMapper;
    
    @InjectMocks
    private AccountService accountService;
    
    private Account testAccount;
    private AccountDto testAccountDto;
    
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
        
        testAccountDto = AccountDto.builder()
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
    }
    
    @Test
    void testCreateAccount_Success() {
        // Given
        when(accountRepository.findByAccountNumber("1234567890"))
                .thenReturn(Mono.empty());
        when(accountMapper.toEntity(any(AccountDto.class)))
                .thenReturn(testAccount);
        when(accountRepository.save(any(Account.class)))
                .thenReturn(Mono.just(testAccount));
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(testAccountDto);
        
        // When & Then
        StepVerifier.create(accountService.createAccount(testAccountDto))
                .expectNextCount(1)
                .verifyComplete();
    }
    
    @Test
    void testGetAccountById_Success() {
        // Given
        when(accountRepository.findById(1L))
                .thenReturn(Mono.just(testAccount));
        when(accountMapper.toDto(any(Account.class)))
                .thenReturn(testAccountDto);
        
        // When & Then
        StepVerifier.create(accountService.getAccountById(1L))
                .expectNext(testAccountDto)
                .verifyComplete();
    }
    
    @Test
    void testGetAccountById_NotFound() {
        // Given
        when(accountRepository.findById(1L))
                .thenReturn(Mono.empty());
        
        // When & Then
        StepVerifier.create(accountService.getAccountById(1L))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException && 
                    throwable.getMessage().equals("Account not found with ID: 1"))
                .verify();
    }
}

