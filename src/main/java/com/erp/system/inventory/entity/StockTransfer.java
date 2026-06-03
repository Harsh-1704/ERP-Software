package com.erp.system.inventory.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_transfer")
@Getter
@Setter
public class StockTransfer extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String transferNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_warehouse_id", nullable = false)
    private Warehouse fromWarehouse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_warehouse_id", nullable = false)
    private Warehouse toWarehouse;

    private LocalDateTime transferDate = LocalDateTime.now();
    private LocalDateTime expectedArrivalDate;
    private LocalDateTime actualArrivalDate;

    private String status = "IN_TRANSIT"; // INITIATED, IN_TRANSIT, RECEIVED, CANCELLED

    private String remarks;
    private String createdBy;

    @OneToMany(mappedBy = "transfer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockTransferItem> items = new ArrayList<>();
}