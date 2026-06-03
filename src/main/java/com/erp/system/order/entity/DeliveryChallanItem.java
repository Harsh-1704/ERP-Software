package com.erp.system.order.entity;

import com.erp.system.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "delivery_challan_item")
@Getter
@Setter
public class DeliveryChallanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challan_id", nullable = false)
    private DeliveryChallan challan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(precision = 15, scale = 3, nullable = false)
    private BigDecimal dispatchedQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal deliveredQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal damagedQuantity = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String remarks;
}
