package com.erp.system.inventory.repository;

import com.erp.system.inventory.entity.StockAdjustment;
import com.erp.system.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
    Optional<StockAdjustment> findByAdjustmentNumber(String adjustmentNumber);
    List<StockAdjustment> findByWarehouse(Warehouse warehouse);
    List<StockAdjustment> findByStatus(String status);
}