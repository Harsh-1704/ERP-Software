package com.erp.system.marketplace.repository;

import com.erp.system.marketplace.entity.MarketplaceListing;
import com.erp.system.marketplace.entity.MarketplaceVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketplaceListingRepository extends JpaRepository<MarketplaceListing, Long> {
    List<MarketplaceListing> findByVendor(MarketplaceVendor vendor);
    List<MarketplaceListing> findByIsActiveTrue();
    List<MarketplaceListing> findByIsFeaturedTrue();
    List<MarketplaceListing> findByVendorAndIsActiveTrue(MarketplaceVendor vendor);

    @Query("SELECT l FROM MarketplaceListing l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND l.isActive = true")
    List<MarketplaceListing> searchByTitle(@Param("searchTerm") String searchTerm);

    @Query("SELECT l FROM MarketplaceListing l WHERE l.isAvailable = true AND l.isActive = true")
    List<MarketplaceListing> findAvailableListings();
}
