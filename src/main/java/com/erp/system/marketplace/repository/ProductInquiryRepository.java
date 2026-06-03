package com.erp.system.marketplace.repository;

import com.erp.system.marketplace.entity.InquiryStatus;
import com.erp.system.marketplace.entity.MarketplaceListing;
import com.erp.system.marketplace.entity.ProductInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductInquiryRepository extends JpaRepository<ProductInquiry, Long> {
    Optional<ProductInquiry> findByInquiryNumber(String inquiryNumber);
    List<ProductInquiry> findByListing(MarketplaceListing listing);
    List<ProductInquiry> findByStatus(InquiryStatus status);
}
