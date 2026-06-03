package com.erp.system.marketplace.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import com.erp.system.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "marketplace_listing")
@Getter
@Setter
public class MarketplaceListing extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private MarketplaceVendor vendor;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String shortDescription;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice;

    @Column(precision = 15, scale = 3)
    private BigDecimal minOrderQuantity = BigDecimal.ONE;

    @Column(precision = 15, scale = 3)
    private BigDecimal maxOrderQuantity;

    @Column(columnDefinition = "jsonb")
    private String bulkPricing; // JSON array of pricing tiers

    private Boolean isAvailable = true;
    private String availabilityStatus = "IN_STOCK"; // IN_STOCK, OUT_OF_STOCK, MADE_TO_ORDER

    private Integer leadTimeDays = 7;
    private Boolean moqNegotiable = false;

    @Column(columnDefinition = "jsonb")
    private String shippingAvailableStates;

    @Column(precision = 10, scale = 2)
    private BigDecimal freeShippingThreshold;

    @Column(precision = 10, scale = 2)
    private BigDecimal shippingCharges = BigDecimal.ZERO;

    private Boolean isFeatured = false;
    private Boolean isActive = true;

    private Integer viewsCount = 0;
    private Integer inquiriesCount = 0;

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BulkPriceTier> priceTiers = new ArrayList<>();

    @OneToMany(mappedBy = "listing", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();
}
