package com.erp.system.marketplace.entity;

import com.erp.system.auth.entity.Party;
import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "marketplace_vendor")
@Getter
@Setter
public class MarketplaceVendor extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @Column(nullable = false)
    private String companyName;

    private String businessType; // MANUFACTURER, TRADER, WHOLESALER, DISTRIBUTOR

    private Boolean gstVerified = false;
    private Boolean panVerified = false;

    private BigDecimal rating = BigDecimal.ZERO;
    private Integer totalReviews = 0;
    private Integer totalOrders = 0;
    private BigDecimal responseRate = BigDecimal.ZERO;
    private Integer responseTimeHours;

    private Integer totalProducts = 0;
    private Integer featuredProducts = 0;

    private String subscriptionPlan = "FREE"; // FREE, BASIC, PREMIUM, ENTERPRISE
    private LocalDate subscriptionStartDate;
    private LocalDate subscriptionEndDate;

    private Boolean isActive = true;
    private Boolean isVerified = false;
    private Boolean isFeatured = false;

    private String websiteUrl;
    private String contactEmail;
    private String contactPhone;
}
