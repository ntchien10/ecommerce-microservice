package com.ecommerce.notification_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {

    private Long productId;

    private String name;

    private BigDecimal price;
}