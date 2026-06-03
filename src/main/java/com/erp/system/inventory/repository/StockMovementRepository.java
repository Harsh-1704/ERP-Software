package com.erp.system.inventory.repository;

import com.erp.system.inventory.entity.StockMovement;
import com.erp.system.inventory.entity.StockMovementType;
import com.erp.system.inventory.entity.Warehouse;
import com.erp.system.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByProduct(Product product);
    List<StockMovement> findByWarehouse(Warehouse warehouse);
    List<StockMovement> findByMovementType(StockMovementType movementType);
    List<StockMovement> findByProductAndWarehouse(Product product, Warehouse warehouse);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.product = :product AND sm.warehouse = :warehouse ORDER BY sm.movementDate DESC")
    List<StockMovement> findProductWarehouseHistory(@Param("product") Product product, @Param("warehouse") Warehouse warehouse);

    List<StockMovement> findByMovementDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<StockMovement> findByReferenceTypeAndReferenceId(String referenceType, Long referenceId);
}