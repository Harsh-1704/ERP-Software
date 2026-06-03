package com.erp.system.inventory.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import com.erp.system.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "warehouse_id"})
})
@Getter
@Setter
public class Stock extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "warehouse_id", nullable = false)
    private Warehouse warehouse;

    @Column(precision = 15, scale = 3)
    private BigDecimal quantityOnHand = BigDecimal.ZERO;

    @Column(precision = 15, scale = 3)
    private BigDecimal quantityReserved = BigDecimal.ZERO;

    @Column(precision = 15, scale = 3)
    private BigDecimal quantityAvailable = BigDecimal.ZERO;

    @Column(precision = 15, scale = 3)
    private BigDecimal reorderLevel = BigDecimal.ZERO;

    private LocalDateTime lastStockCheck;

    @PrePersist
    @PreUpdate
    private void calculateAvailable() {
        if (quantityOnHand != null && quantityReserved != null) {
            this.quantityAvailable = quantityOnHand.subtract(quantityReserved);
        }
    }
}