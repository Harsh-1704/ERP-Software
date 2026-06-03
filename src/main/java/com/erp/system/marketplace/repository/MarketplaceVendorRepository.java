package com.erp.system.marketplace.repository;

import com.erp.system.auth.entity.Party;
import com.erp.system.marketplace.entity.MarketplaceVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketplaceVendorRepository extends JpaRepository<MarketplaceVendor, Long> {
    Optional<MarketplaceVendor> findByParty(Party party);
    List<MarketplaceVendor> findByIsActiveTrue();
    List<MarketplaceVendor> findByIsVerifiedTrue();
    List<MarketplaceVendor> findByIsFeaturedTrue();
    List<MarketplaceVendor> findBySubscriptionPlan(String plan);
}
