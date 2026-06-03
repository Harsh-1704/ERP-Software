package com.erp.system.inventory.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_adjustment")
@Getter
@Setter
public class StockAdjustment extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(unique = true, nullable = false)
    private String adjustmentNumber;

    private LocalDateTime adjustmentDate = LocalDateTime.now();

    private String reason;
    private String remarks;

    private String status = "PENDING"; // PENDING, APPROVED, REJECTED

    private String approvedBy;
    private LocalDateTime approvedAt;

    private String createdBy;

    @OneToMany(mappedBy = "adjustment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<StockAdjustmentItem> items = new ArrayList<>();
}