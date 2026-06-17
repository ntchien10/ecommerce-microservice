package com.ecommerce.product_service.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer quantity;

    private String imageUrl;

    private String brand;
}