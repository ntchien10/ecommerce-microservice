package com.ecommerce.product_service.kafka;

import com.ecommerce.product_service.event.ProductCreatedEvent;
import com.ecommerce.product_service.event.ProductDeletedEvent;
import com.ecommerce.product_service.event.ProductUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String PRODUCT_CREATED_TOPIC = "product-created";
    private static final String PRODUCT_UPDATED_TOPIC = "product-updated";
    private static final String PRODUCT_DELETED_TOPIC = "product-deleted";

    public void sendProductCreatedEvent(ProductCreatedEvent event) {

        String key = event.getProductId().toString();

        kafkaTemplate.send(
                PRODUCT_CREATED_TOPIC,
                key,
                event
        );

        System.out.println(
                "Sent ProductCreatedEvent | topic="
                        + PRODUCT_CREATED_TOPIC
                        + " | key="
                        + key
                        + " | productId="
                        + event.getProductId()
        );
    }

    public void sendProductUpdatedEvent(ProductUpdatedEvent event) {

        String key = event.getProductId().toString();

        kafkaTemplate.send(
                PRODUCT_UPDATED_TOPIC,
                key,
                event
        );

        System.out.println(
                "Sent ProductUpdatedEvent | topic="
                        + PRODUCT_UPDATED_TOPIC
                        + " | key="
                        + key
                        + " | productId="
                        + event.getProductId()
        );
    }

    public void sendProductDeletedEvent(ProductDeletedEvent event) {

        String key = event.getProductId().toString();

        kafkaTemplate.send(
                PRODUCT_DELETED_TOPIC,
                key,
                event
        );

        System.out.println(
                "Sent ProductDeletedEvent | topic="
                        + PRODUCT_DELETED_TOPIC
                        + " | key="
                        + key
                        + " | productId="
                        + event.getProductId()
        );
    }
}