package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.document.ProductDocument;
import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.PageResponse;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.event.ProductCreatedEvent;
import com.ecommerce.product_service.event.ProductDeletedEvent;
import com.ecommerce.product_service.event.ProductUpdatedEvent;
import com.ecommerce.product_service.kafka.ProductProducer;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.repository.search.ProductSearchRepository;
import com.ecommerce.product_service.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductProducer productProducer;
    private final ProductSearchRepository productSearchRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public ProductServiceImpl(
            ProductRepository productRepository,
            ProductProducer productProducer,
            ProductSearchRepository productSearchRepository,
            RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper
    ) {
        this.productRepository = productRepository;
        this.productProducer = productProducer;
        this.productSearchRepository = productSearchRepository;
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {

        Product product = new Product();

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setQuantity(request.getQuantity());
        product.setImageUrl(request.getImageUrl());
        product.setBrand(request.getBrand());

        Product savedProduct = productRepository.save(product);

        productProducer.sendProductCreatedEvent(
                new ProductCreatedEvent(
                        savedProduct.getId(),
                        savedProduct.getName(),
                        savedProduct.getDescription(),
                        savedProduct.getPrice(),
                        savedProduct.getQuantity(),
                        savedProduct.getBrand(),
                        savedProduct.getImageUrl()
                )
        );

        return mapToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getAll() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ProductResponse findProductById(Long id) {

        try {
            String key = "product:" + id;

            String cachedJson = redisTemplate.opsForValue().get(key);

            if (cachedJson != null) {
                System.out.println("===== REDIS STRING JSON VERSION =====");
                System.out.println("GET PRODUCT FROM REDIS: " + id);
                return objectMapper.readValue(cachedJson, ProductResponse.class);
            }

            Product product = productRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductResponse response = mapToResponse(product);

            String json = objectMapper.writeValueAsString(response);

            redisTemplate.opsForValue().set(
                    key,
                    json,
                    Duration.ofMinutes(10)
            );

            System.out.println("GET PRODUCT FROM DATABASE: " + id);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("Redis cache error", e);
        }
    }

    @Override
    @Transactional
    public ProductResponse updateProductById(Long id, ProductRequest request) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (request.getName() != null) {
            product.setName(request.getName());
        }

        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }

        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }

        if (request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }

        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }

        if (request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }

        Product savedProduct = productRepository.save(product);

        String key = "product:" + id;
        redisTemplate.delete(key);

        productProducer.sendProductUpdatedEvent(
                new ProductUpdatedEvent(
                        savedProduct.getId(),
                        savedProduct.getName(),
                        savedProduct.getDescription(),
                        savedProduct.getPrice(),
                        savedProduct.getQuantity(),
                        savedProduct.getBrand(),
                        savedProduct.getImageUrl()
                )
        );

        return mapToResponse(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponse deleteProductById(Long id) {

        Product product = productRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        productRepository.delete(product);

        String key = "product:" + id;
        redisTemplate.delete(key);

        productProducer.sendProductDeletedEvent(
                new ProductDeletedEvent(id)
        );

        return mapToResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> getPage(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage = productRepository.findAll(pageable);

        PageResponse<ProductResponse> response = new PageResponse<>();

        response.setContent(
                productPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );

        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> search(
            String keyword,
            int page,
            int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Product> productPage =
                productRepository.search(keyword, pageable);

        PageResponse<ProductResponse> response =
                new PageResponse<>();

        response.setContent(
                productPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList()
        );

        response.setPage(productPage.getNumber());
        response.setSize(productPage.getSize());
        response.setTotalElements(productPage.getTotalElements());
        response.setTotalPages(productPage.getTotalPages());
        response.setLast(productPage.isLast());

        return response;
    }

    @Override
    public void syncProductsToElasticsearch() {

        List<Product> products = productRepository.findAll();

        List<ProductDocument> documents =
                products.stream()
                        .map(this::mapToDocument)
                        .toList();

        productSearchRepository.saveAll(documents);
    }

    private ProductResponse mapToResponse(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setImageUrl(product.getImageUrl());
        response.setBrand(product.getBrand());

        return response;
    }

    private ProductDocument mapToDocument(Product product) {

        ProductDocument document = new ProductDocument();

        document.setId(product.getId());
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setPrice(product.getPrice());
        document.setQuantity(product.getQuantity());
        document.setBrand(product.getBrand());
        document.setImageUrl(product.getImageUrl());

        return document;
    }
}