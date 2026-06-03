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
@Table(name = "product_inquiry")
@Getter
@Setter
public class ProductInquiry extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String inquiryNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private MarketplaceListing listing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquirer_party_id", nullable = false)
    private Party inquirerParty;

    private String contactPerson;
    private String contactEmail;
    private String contactPhone;

    @Column(precision = 15, scale = 3)
    private BigDecimal requiredQuantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal targetPrice;

    private String deliveryLocation;
    private LocalDate expectedDeliveryDate;

    @Enumerated(EnumType.STRING)
    private InquiryStatus status = InquiryStatus.PENDING;

    @Column(precision = 10, scale = 2)
    private BigDecimal quotedPrice;

    private LocalDateTime quotedAt;
    private LocalDate quoteValidUntil;

    @Column(columnDefinition = "TEXT")
    private String vendorNotes;

    @Column(columnDefinition = "TEXT")
    private String requirements;
}
