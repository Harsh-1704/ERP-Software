package com.erp.system.marketplace.repository;

import com.erp.system.marketplace.entity.MarketplaceVendor;
import com.erp.system.marketplace.entity.VendorReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorReviewRepository extends JpaRepository<VendorReview, Long> {
    List<VendorReview> findByVendor(MarketplaceVendor vendor);
    List<VendorReview> findByVendorOrderByCreatedAtDesc(MarketplaceVendor vendor);

    @Query("SELECT AVG(v.overallRating) FROM VendorReview v WHERE v.vendor = :vendor")
    Optional<Double> getAverageRating(@Param("vendor") MarketplaceVendor vendor);

    @Query("SELECT COUNT(v) FROM VendorReview v WHERE v.vendor = :vendor")
    Long countReviews(@Param("vendor") MarketplaceVendor vendor);
}
