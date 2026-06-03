package com.erp.system.inventory.repository;

import com.erp.system.inventory.entity.Stock;
import com.erp.system.inventory.entity.Warehouse;
import com.erp.system.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductAndWarehouse(Product product, Warehouse warehouse);
    List<Stock> findByProduct(Product product);
    List<Stock> findByWarehouse(Warehouse warehouse);

    @Query("SELECT s FROM Stock s WHERE s.product = :product AND s.quantityAvailable < s.reorderLevel")
    List<Stock> findLowStockItems(@Param("product") Product product);

    @Query("SELECT s FROM Stock s WHERE s.quantityAvailable < s.reorderLevel")
    List<Stock> findAllLowStockItems();

    @Query("SELECT SUM(s.quantityAvailable) FROM Stock s WHERE s.product = :product")
    Optional<Long> getTotalProductStock(@Param("product") Product product);
}