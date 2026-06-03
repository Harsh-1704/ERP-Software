package com.erp.system.marketplace.entity;

import com.erp.system.common.entity.BaseAuditEntity;
import com.erp.system.product.entity.Product;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_image")
@Getter
@Setter
public class ProductImage extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id")
    private MarketplaceListing listing;

    @Column(nullable = false)
    private String imageUrl;

    private String imageType = "PRIMARY"; // PRIMARY, GALLERY, THUMBNAIL

    private Integer displayOrder = 0;

    private String altText;
}
