package org.example.accountservice.integration;

import org.example.accountservice.dto.AccountDto;
import org.example.accountservice.dto.MovementDto;
import org.example.accountservice.entity.Account;
import org.example.accountservice.entity.Movement;
import org.example.accountservice.repository.AccountRepository;
import org.example.accountservice.repository.MovementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIf;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@DisabledIf("isDockerUnavailable")
class AccountIntegrationTest {
    
    public static boolean isDockerUnavailable() {
        try {
            DockerClientFactory.instance().client();
            return false;
        } catch (Exception e) {
            return true;
        }
    }
    
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            DockerImageName.parse("postgres:15-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
    
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> 
            "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getFirstMappedPort() + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private MovementRepository movementRepository;
    
    @BeforeEach
    void setUp() {
        // Clean up before each test
        accountRepository.deleteAll().block();
        movementRepository.deleteAll().block();
    }
    
    @Test
    @DisabledIf("isDockerUnavailable")
    void testCreateAccountAndMovement_Integration() {
        // Create account
        Account account = Account.builder()
                .accountNumber("1234567890")
                .accountType("SAVINGS")
                .initialBalance(new BigDecimal("1000.00"))
                .currentBalance(new BigDecimal("1000.00"))
                .status(true)
                .customerId(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        Account savedAccount = accountRepository.save(account).block();
        assertNotNull(savedAccount);
        assertNotNull(savedAccount.getId());
        
        // Create movement
        Movement movement = Movement.builder()
                .accountId(savedAccount.getId())
                .movementType("DEBIT")
                .value(new BigDecimal("100.00"))
                .balance(new BigDecimal("900.00"))
                .movementDate(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
        
        Movement savedMovement = movementRepository.save(movement).block();
        assertNotNull(savedMovement);
        assertNotNull(savedMovement.getId());
        
        // Verify account balance was updated
        Account updatedAccount = accountRepository.findById(savedAccount.getId()).block();
        assertNotNull(updatedAccount);
        assertEquals(new BigDecimal("1000.00"), updatedAccount.getCurrentBalance());
    }
}

