package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.PageResponse;
import com.ecommerce.product_service.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse create(ProductRequest request);
    List<ProductResponse> getAll();
    ProductResponse findProductById(Long id);
    ProductResponse updateProductById(Long id, ProductRequest request);
    ProductResponse deleteProductById(Long id);
    PageResponse<ProductResponse> getPage(int page, int size);
    PageResponse<ProductResponse> search(
            String keyword,
            int page,
            int size
    );
    void syncProductsToElasticsearch();
}