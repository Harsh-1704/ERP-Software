package com.erp.system.inventory.repository;

import com.erp.system.inventory.entity.StockTransfer;
import com.erp.system.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    Optional<StockTransfer> findByTransferNumber(String transferNumber);
    List<StockTransfer> findByFromWarehouse(Warehouse warehouse);
    List<StockTransfer> findByToWarehouse(Warehouse warehouse);
    List<StockTransfer> findByStatus(String status);
}