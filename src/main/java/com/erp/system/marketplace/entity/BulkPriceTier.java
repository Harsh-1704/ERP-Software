package com.erp.system.marketplace.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "bulk_price_tier")
@Getter
@Setter
public class BulkPriceTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private MarketplaceListing listing;

    @Column(precision = 15, scale = 3, nullable = false)
    private BigDecimal minQuantity;

    @Column(precision = 15, scale = 3)
    private BigDecimal maxQuantity;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;
}
