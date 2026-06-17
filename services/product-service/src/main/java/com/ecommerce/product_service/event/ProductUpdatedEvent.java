package com.ecommerce.product_service.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdatedEvent {

    private Long productId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String brand;
    private String imageUrl;
}