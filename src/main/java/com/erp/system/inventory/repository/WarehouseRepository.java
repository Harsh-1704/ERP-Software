package com.erp.system.inventory.repository;

import com.erp.system.inventory.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByCode(String code);
    List<Warehouse> findByIsActiveTrue();
    List<Warehouse> findByCity(String city);
    boolean existsByCode(String code);
}