package com.ecommerce.product_service.repository.search;

import com.ecommerce.product_service.document.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository
        extends ElasticsearchRepository<ProductDocument, Long> {

    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["name", "description"]
              }
            }
            """)
    List<ProductDocument> searchByKeyword(String keyword);

    @Query("""
            {
              "multi_match": {
                "query": "?0",
                "fields": ["name", "description"],
                "fuzziness": "AUTO"
              }
            }
            """)
    List<ProductDocument> fuzzySearch(String keyword);

    List<ProductDocument> findByBrand(String brand);

    @Query("""
    {
      "bool": {
        "should": [
          {
            "multi_match": {
              "query": "?0",
              "fields": ["name", "description"]
            }
          },
          {
            "term": {
              "brand": "?0"
            }
          }
        ]
      }
    }
    """)
    List<ProductDocument> smartSearch(String keyword);

    Page<ProductDocument> findByNameContainingOrDescriptionContaining(
            String name,
            String description,
            Pageable pageable
    );
}