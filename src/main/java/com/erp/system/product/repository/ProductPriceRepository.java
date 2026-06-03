package com.erp.system.product.repository;

import com.erp.system.product.entity.Product;
import com.erp.system.product.entity.ProductPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductPriceRepository extends JpaRepository<ProductPrice, Long> {
    List<ProductPrice> findByProduct(Product product);

    @Query("SELECT p FROM ProductPrice p WHERE p.product = :product AND p.isCurrent = true ORDER BY p.effectiveDate DESC")
    Optional<ProductPrice> findCurrentPrice(@Param("product") Product product);

    @Query("SELECT p FROM ProductPrice p WHERE p.product = :product ORDER BY p.effectiveDate DESC")
    List<ProductPrice> findPriceHistory(@Param("product") Product product);
}