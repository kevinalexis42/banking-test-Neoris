package org.example.customerservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.customerservice.dto.CustomerDto;
import org.example.customerservice.dto.CustomerRequestDto;
import org.example.customerservice.dto.CustomerUpdateDto;
import org.example.customerservice.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Controller", description = "API for managing customers")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    @Operation(summary = "Create a new customer", description = "Creates a new customer with person information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Mono<ResponseEntity<CustomerDto>> createCustomer(@Valid @RequestBody CustomerRequestDto requestDto) {
        log.info("POST /api/v1/customers - Creating customer");
        return customerService.createCustomer(requestDto)
                .map(customer -> ResponseEntity.status(HttpStatus.CREATED).body(customer));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID", description = "Retrieves a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer found"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public Mono<ResponseEntity<CustomerDto>> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("GET /api/v1/customers/{} - Fetching customer", id);
        return customerService.getCustomerById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    @Operation(summary = "Get all customers", description = "Retrieves all customers")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    })
    public Mono<ResponseEntity<Flux<CustomerDto>>> getAllCustomers() {
        log.info("GET /api/v1/customers - Fetching all customers");
        return Mono.just(ResponseEntity.ok(customerService.getAllCustomers()));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer", description = "Updates an existing customer. Only ID is required, all other fields are optional. Only provided fields will be updated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer updated successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Mono<ResponseEntity<CustomerDto>> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id,
            @RequestBody CustomerUpdateDto updateDto) {
        log.info("PUT /api/v1/customers/{} - Updating customer", id);
        return customerService.updateCustomer(id, updateDto)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete customer", description = "Deletes a customer by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public Mono<ResponseEntity<Void>> deleteCustomer(
            @Parameter(description = "Customer ID") @PathVariable Long id) {
        log.info("DELETE /api/v1/customers/{} - Deleting customer", id);
        return customerService.deleteCustomer(id)
                .then(Mono.just(ResponseEntity.status(HttpStatus.NO_CONTENT).<Void>build()))
                .onErrorResume(error -> {
                    if (error.getMessage() != null && error.getMessage().contains("not found")) {
                        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).<Void>build());
                    }
                    return Mono.error(error);
                });
    }
}

