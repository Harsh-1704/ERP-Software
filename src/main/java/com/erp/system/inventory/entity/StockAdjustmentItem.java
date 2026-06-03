package com.erp.system.inventory.entity;

import com.erp.system.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "stock_adjustment_item")
@Getter
@Setter
public class StockAdjustmentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adjustment_id", nullable = false)
    private StockAdjustment adjustment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal currentQuantity;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal adjustedQuantity;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal differenceQuantity;

    private String reason;
    private String remarks;
}