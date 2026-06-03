package com.erp.system.auth.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "address")
@Getter
@Setter
public class Address extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id", nullable = false)
    private Party party;

    private String addressType; // e.g., "REGISTERED", "BILLING", "SHIPPING", "WAREHOUSE"

    private String street;
    private String city;
    private String state;
    private String country;
    private String pincode;

    private Boolean isDefault = false;
}