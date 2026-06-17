package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.entity.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id")
    Optional<Product> findByIdForUpdate(Long id);

    @Query("""
            SELECT p
            FROM Product p
            WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.imageUrl) LIKE LOWER(CONCAT('%', :keyword, '%'))
            """)
    Page<Product> search(String keyword, Pageable pageable);

}