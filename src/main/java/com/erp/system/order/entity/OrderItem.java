package com.erp.system.order.entity;

import com.erp.system.product.entity.Product;
import com.erp.system.product.entity.Unit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "order_item")
@Getter
@Setter
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Orders order;

    private Integer itemNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String productName;

    private String sku;
    private String hsnCode;

    @Column(precision = 15, scale = 3, nullable = false)
    private BigDecimal orderedQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal confirmedQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal shippedQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal deliveredQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal returnedQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal cancelledQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal taxRate = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    private LocalDate expectedDeliveryDate;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @PrePersist
    @PreUpdate
    private void calculateAmounts() {
        if (orderedQuantity != null && unitPrice != null) {
            this.subtotal = orderedQuantity.multiply(unitPrice).subtract(discountAmount);
            this.taxAmount = subtotal.multiply(taxRate.divide(new BigDecimal("100")));
            this.totalAmount = subtotal.add(taxAmount);
        }
    }
}
