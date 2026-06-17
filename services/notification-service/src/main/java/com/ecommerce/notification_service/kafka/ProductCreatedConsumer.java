package com.ecommerce.notification_service.kafka;

import com.ecommerce.notification_service.event.ProductCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class ProductCreatedConsumer {

    @KafkaListener(
            topics = "product-created",
            groupId = "notification-group"
    )
    public void consume(
            ProductCreatedEvent event,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition
    ) {
        System.out.println("CONSUMED PARTITION = " + partition);
        System.out.println("EVENT = " + event);

        // Tạm thời bỏ dòng throw để test partition
        // throw new RuntimeException("TEST ERROR");
    }

    @KafkaListener(
            topics = "product-created-dlt",
            groupId = "notification-dlt-group"
    )
    public void consumeDlt(ProductCreatedEvent event) {

        System.out.println("MESSAGE MOVED TO DLT:");
        System.out.println(event);
    }
}