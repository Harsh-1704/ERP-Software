package com.erp.system.order.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_challan")
@Getter
@Setter
public class DeliveryChallan extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String challanNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Orders order;

    private Long invoiceId;

    private LocalDateTime dispatchDate = LocalDateTime.now();

    private String vehicleNumber;
    private String transporterName;
    private String driverName;
    private String driverPhone;
    private String lrNumber;

    private String status = "DISPATCHED";

    private LocalDateTime deliveredAt;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    private String createdBy;

    @OneToMany(mappedBy = "challan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DeliveryChallanItem> items = new ArrayList<>();
}
