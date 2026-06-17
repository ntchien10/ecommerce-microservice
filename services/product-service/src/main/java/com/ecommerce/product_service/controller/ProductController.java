package com.ecommerce.product_service.controller;

import com.ecommerce.product_service.client.AuthClient;
import com.ecommerce.product_service.document.ProductDocument;
import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.PageResponse;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.repository.search.ProductSearchRepository;
import com.ecommerce.product_service.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final AuthClient authClient;
    private final ProductSearchRepository productSearchRepository;

    public ProductController(ProductService productService, AuthClient authClient, ProductSearchRepository productSearchRepository) {
        this.productService = productService;
        this.authClient = authClient;
        this.productSearchRepository = productSearchRepository;
    }

    @GetMapping("/page")
    public PageResponse<ProductResponse> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return productService.getPage(page, size);
    }

    @PostMapping
    public ProductResponse create(
            @RequestBody ProductRequest request
    ) {
        return productService.create(request);
    }

    @GetMapping
    public List<ProductResponse> getAll(){
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public ProductResponse findProductById(@PathVariable Long id){
        return productService.findProductById(id);
    }

    @PatchMapping("/{id}")
    public ProductResponse updateProductById(@PathVariable Long id, @RequestBody ProductRequest request){
        return productService.updateProductById(id,request);
    }

    @DeleteMapping("/{id}")
    public ProductResponse deleteProductById(@PathVariable Long id){
        return productService.deleteProductById(id);
    }

    @GetMapping("/search")
    public PageResponse<ProductResponse> search(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return productService.search(keyword, page, size);
    }

    @GetMapping("/test-feign")
    public String testFeign() {
        return authClient.hello();
    }

    @GetMapping("/es/search")
    public List<ProductDocument> searchEs(
            @RequestParam String keyword
    ) {

        return productSearchRepository
                .searchByKeyword(keyword);
    }

    @GetMapping("/es/fuzzy")
    public List<ProductDocument> fuzzySearch(
            @RequestParam String keyword
    ) {
        return productSearchRepository.fuzzySearch(keyword);
    }

    @PostMapping("/es/sync")
    public String syncToElasticsearch() {

        productService.syncProductsToElasticsearch();

        return "Sync success";
    }

    @GetMapping("/es/brand")
    public List<ProductDocument> searchByBrand(@RequestParam String brand) {
        return productSearchRepository.findByBrand(brand);
    }

    @GetMapping("/es/smart-search")
    public List<ProductDocument> smartSearch(@RequestParam String keyword) {
        return productSearchRepository.smartSearch(keyword);
    }

    @GetMapping("/es/page-search")
    public Page<ProductDocument> pageSearch(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "price") String sort
    ) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(sort).ascending()
        );

        return productSearchRepository
                .findByNameContainingOrDescriptionContaining(
                        keyword,
                        keyword,
                        pageable
                );
    }
}