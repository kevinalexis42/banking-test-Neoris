package org.example.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.accountservice.dto.CustomerEventDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerEventConsumer {
    
    private final ReceiverOptions<String, CustomerEventDto> kafkaReceiverOptions;
    
    @PostConstruct
    public void consumeCustomerEvents() {
        KafkaReceiver.create(kafkaReceiverOptions)
                .receive()
                .subscribe(record -> {
                    CustomerEventDto event = record.value();
                    log.info("Received customer event: {} for customer ID: {}", 
                            event.getEventType(), event.getCustomerId());
                    
                    switch (event.getEventType()) {
                        case "CREATED":
                            log.info("Customer created: {}", event.getCustomerId());
                            break;
                        case "UPDATED":
                            log.info("Customer updated: {}", event.getCustomerId());
                            break;
                        case "DELETED":
                            log.info("Customer deleted: {}", event.getCustomerId());
                            break;
                        default:
                            log.warn("Unknown event type: {}", event.getEventType());
                    }
                    
                    record.receiverOffset().acknowledge();
                });
    }
}

