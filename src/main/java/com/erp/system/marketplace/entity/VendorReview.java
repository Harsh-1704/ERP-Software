package com.erp.system.marketplace.entity;

import com.erp.system.auth.entity.Party;
import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "vendor_review")
@Getter
@Setter
public class VendorReview extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private MarketplaceVendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_party_id", nullable = false)
    private Party reviewerParty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private BulkOrder order;

    @Column(nullable = false)
    private Integer overallRating;

    private Integer productQualityRating;
    private Integer deliveryRating;
    private Integer communicationRating;

    @Column(columnDefinition = "TEXT")
    private String reviewText;

    private Boolean isVerifiedPurchase = false;

    @Column(columnDefinition = "TEXT")
    private String vendorResponse;

    private LocalDateTime vendorResponseAt;

    @PrePersist
    @PreUpdate
    private void validateRatings() {
        if (overallRating != null && (overallRating < 1 || overallRating > 5)) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }
}
