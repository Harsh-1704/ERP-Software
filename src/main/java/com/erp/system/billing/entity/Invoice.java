package com.erp.system.billing.entity;

import com.erp.system.auth.entity.Address;
import com.erp.system.auth.entity.Party;
import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
public class Invoice extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String invoiceNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InvoiceType invoiceType;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status = InvoiceStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id")
    private Address billingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id")
    private Address shippingAddress;

    @Column(precision = 15, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal totalTax = BigDecimal.ZERO;

    @Column(columnDefinition = "jsonb")
    private String taxBreakup;

    @Column(precision = 15, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal shippingCharges = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal otherCharges = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal roundOff = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2)
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(precision = 15, scale = 2, nullable = false)
    private BigDecimal balanceAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDate invoiceDate;

    private LocalDate dueDate;

    private Integer paymentTermsDays = 30;

    private Long salesOrderId;
    private Long purchaseOrderId;
    private Long deliveryChallanId;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(columnDefinition = "TEXT")
    private String termsAndConditions;

    @Column(columnDefinition = "TEXT")
    private String shippingMarks;

    private String createdBy;

    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    @Column(columnDefinition = "TEXT")
    private String cancelledReason;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<InvoiceItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();

    @PrePersist
    @PreUpdate
    private void calculateTotals() {
        if (items != null && !items.isEmpty()) {
            this.subtotal = items.stream()
                    .map(InvoiceItem::getSubtotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.totalTax = items.stream()
                    .map(InvoiceItem::getTaxAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        this.totalAmount = subtotal.add(totalTax).add(shippingCharges).add(otherCharges)
                .subtract(discountAmount).add(roundOff);
        this.balanceAmount = totalAmount.subtract(paidAmount);
    }
}