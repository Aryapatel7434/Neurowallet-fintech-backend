package com.smartwallet.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartwallet.dto.TransactionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TransactionEventProducer {

    private static final Logger logger =
            LoggerFactory.getLogger(TransactionEventProducer.class);

    private static final String TOPIC = "transaction-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public TransactionEventProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<Boolean> publishTransactionEvent(
            TransactionEvent event) {

        try {

            logger.info(
                    "Preparing Kafka transaction event for publishing"
            );

            String message =
                    objectMapper.writeValueAsString(event);

            CompletableFuture<SendResult<String, String>> future =
                    kafkaTemplate.send(TOPIC, message);

            return future
                    .thenApply(result -> {

                        logger.info(
                                "Kafka transaction event published successfully. Topic: {}, Partition: {}, Offset: {}",
                                TOPIC,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );

                        return true;
                    })
                    .exceptionally(exception -> {

                        logger.error(
                                "Failed to publish Kafka transaction event to topic {}: {}",
                                TOPIC,
                                exception.getMessage()
                        );

                        return false;
                    });

        } catch (Exception e) {

            logger.error(
                    "Failed to prepare Kafka transaction event: {}",
                    e.getMessage()
            );

            return CompletableFuture.completedFuture(false);
        }
    }
}