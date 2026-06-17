package com.ecommerce.product_service.kafka;

import com.ecommerce.product_service.document.ProductDocument;
import com.ecommerce.product_service.event.ProductCreatedEvent;
import com.ecommerce.product_service.event.ProductDeletedEvent;
import com.ecommerce.product_service.event.ProductUpdatedEvent;
import com.ecommerce.product_service.repository.search.ProductSearchRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ProductConsumer {

    private final ProductSearchRepository productSearchRepository;

    public ProductConsumer(ProductSearchRepository productSearchRepository) {
        this.productSearchRepository = productSearchRepository;
    }

    @KafkaListener(
            topics = "product-created",
            groupId = "product-search-group"
    )
    public void handleProductCreated(
            ProductCreatedEvent event
    ) {

        System.out.println(
                "Received Product Created Event: "
                        + event.getProductId()
        );

        ProductDocument document = new ProductDocument();

        document.setId(event.getProductId());
        document.setName(event.getName());
        document.setDescription(event.getDescription());
        document.setPrice(event.getPrice());
        document.setQuantity(event.getQuantity());
        document.setBrand(event.getBrand());
        document.setImageUrl(event.getImageUrl());

        productSearchRepository.save(document);
    }

    @KafkaListener(topics = "product-updated", groupId = "product-search-group")
    public void handleProductUpdated(ProductUpdatedEvent event) {

        ProductDocument document = new ProductDocument();

        document.setId(event.getProductId());
        document.setName(event.getName());
        document.setDescription(event.getDescription());
        document.setPrice(event.getPrice());
        document.setQuantity(event.getQuantity());
        document.setBrand(event.getBrand());
        document.setImageUrl(event.getImageUrl());

        productSearchRepository.save(document);

        System.out.println("Updated product in Elasticsearch: " + event.getProductId());
    }

    @KafkaListener(topics = "product-deleted", groupId = "product-search-group")
    public void handleProductDeleted(ProductDeletedEvent event) {

        productSearchRepository.deleteById(event.getProductId());

        System.out.println("Deleted product from Elasticsearch: " + event.getProductId());
    }
}