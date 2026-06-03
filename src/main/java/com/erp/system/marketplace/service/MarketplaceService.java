package com.erp.system.marketplace.service;

import com.erp.system.auth.entity.Party;
import com.erp.system.auth.repository.PartyRepository;
import com.erp.system.marketplace.entity.*;
import com.erp.system.marketplace.repository.*;
import com.erp.system.product.entity.Product;
import com.erp.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MarketplaceService {

    private final MarketplaceVendorRepository vendorRepository;
    private final MarketplaceListingRepository listingRepository;
    private final ProductInquiryRepository inquiryRepository;
    private final BulkOrderRepository bulkOrderRepository;
    private final VendorReviewRepository reviewRepository;
    private final PartyRepository partyRepository;
    private final ProductRepository productRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Vendor Management
    public MarketplaceVendor createVendor(MarketplaceVendor vendor) {
        if (vendor.getParty() != null && vendor.getParty().getId() != null) {
            Party party = partyRepository.findById(vendor.getParty().getId())
                    .orElseThrow(() -> new RuntimeException("Party not found"));
            vendor.setParty(party);
        }
        return vendorRepository.save(vendor);
    }

    public List<MarketplaceVendor> getAllVendors() {
        return vendorRepository.findAll();
    }

    public List<MarketplaceVendor> getActiveVendors() {
        return vendorRepository.findByIsActiveTrue();
    }

    public List<MarketplaceVendor> getVerifiedVendors() {
        return vendorRepository.findByIsVerifiedTrue();
    }

    public Optional<MarketplaceVendor> getVendorByParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new RuntimeException("Party not found"));
        return vendorRepository.findByParty(party);
    }

    public Optional<MarketplaceVendor> getVendorById(Long vendorId) {
        return vendorRepository.findById(vendorId);
    }

    // Marketplace Listings
    @Transactional
    public MarketplaceListing createListing(MarketplaceListing listing) {
        if (listing.getVendor() != null && listing.getVendor().getId() != null) {
            MarketplaceVendor vendor = vendorRepository.findById(listing.getVendor().getId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            listing.setVendor(vendor);

            // Update vendor product count
            vendor.setTotalProducts(vendor.getTotalProducts() + 1);
            vendorRepository.save(vendor);
        }

        if (listing.getProduct() != null && listing.getProduct().getId() != null) {
            Product product = productRepository.findById(listing.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
            listing.setProduct(product);
        }

        return listingRepository.save(listing);
    }

    public List<MarketplaceListing> getAllListings() {
        return listingRepository.findAll();
    }

    public List<MarketplaceListing> getActiveListings() {
        return listingRepository.findByIsActiveTrue();
    }

    public List<MarketplaceListing> getVendorListings(Long vendorId) {
        MarketplaceVendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return listingRepository.findByVendorAndIsActiveTrue(vendor);
    }

    public List<MarketplaceListing> searchListings(String searchTerm) {
        return listingRepository.searchByTitle(searchTerm);
    }

    public List<MarketplaceListing> getAvailableListings() {
        return listingRepository.findAvailableListings();
    }

    public Optional<MarketplaceListing> getListingById(Long listingId) {
        return listingRepository.findById(listingId);
    }

    // Product Inquiries
    @Transactional
    public ProductInquiry createInquiry(ProductInquiry inquiry) {
        if (inquiry.getListing() != null && inquiry.getListing().getId() != null) {
            MarketplaceListing listing = listingRepository.findById(inquiry.getListing().getId())
                    .orElseThrow(() -> new RuntimeException("Listing not found"));
            inquiry.setListing(listing);

            // Increment inquiry count
            listing.setInquiriesCount(listing.getInquiriesCount() + 1);
            listingRepository.save(listing);
        }

        if (inquiry.getInquirerParty() != null && inquiry.getInquirerParty().getId() != null) {
            Party party = partyRepository.findById(inquiry.getInquirerParty().getId())
                    .orElseThrow(() -> new RuntimeException("Party not found"));
            inquiry.setInquirerParty(party);
        }

        // Generate inquiry number
        inquiry.setInquiryNumber(generateInquiryNumber());

        return inquiryRepository.save(inquiry);
    }

    private String generateInquiryNumber() {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long count = inquiryRepository.count() + 1;
        return String.format("INQ-%s-%06d", dateStr, count);
    }

    @Transactional
    public ProductInquiry respondToInquiry(Long inquiryId, BigDecimal quotedPrice, LocalDate validUntil, String notes) {
        ProductInquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setQuotedPrice(quotedPrice);
        inquiry.setQuotedAt(LocalDateTime.now());
        inquiry.setQuoteValidUntil(validUntil);
        inquiry.setVendorNotes(notes);
        inquiry.setStatus(InquiryStatus.QUOTED);

        return inquiryRepository.save(inquiry);
    }

    public List<ProductInquiry> getAllInquiries() {
        return inquiryRepository.findAll();
    }

    public List<ProductInquiry> getInquiriesByStatus(InquiryStatus status) {
        return inquiryRepository.findByStatus(status);
    }

    // Bulk Orders
    @Transactional
    public BulkOrder createBulkOrder(BulkOrder order) {
        if (order.getListing() != null && order.getListing().getId() != null) {
            MarketplaceListing listing = listingRepository.findById(order.getListing().getId())
                    .orElseThrow(() -> new RuntimeException("Listing not found"));
            order.setListing(listing);
        }

        if (order.getBuyerParty() != null && order.getBuyerParty().getId() != null) {
            Party party = partyRepository.findById(order.getBuyerParty().getId())
                    .orElseThrow(() -> new RuntimeException("Buyer party not found"));
            order.setBuyerParty(party);
        }

        // Generate order number
        order.setOrderNumber(generateBulkOrderNumber());

        // Calculate totals
        if (order.getQuantity() != null && order.getUnitPrice() != null) {
            order.setSubtotal(order.getQuantity().multiply(order.getUnitPrice()));
            order.setTotalAmount(order.getSubtotal()
                    .add(order.getShippingCharges() != null ? order.getShippingCharges() : java.math.BigDecimal.ZERO)
                    .subtract(order.getDiscountAmount() != null ? order.getDiscountAmount() : java.math.BigDecimal.ZERO));
        }

        return bulkOrderRepository.save(order);
    }

    private String generateBulkOrderNumber() {
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long count = bulkOrderRepository.count() + 1;
        return String.format("BO-%s-%06d", dateStr, count);
    }

    @Transactional
    public BulkOrder updateOrderStatus(Long orderId, BulkOrderStatus newStatus) {
        BulkOrder order = bulkOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setStatus(newStatus);

        // Update timestamps based on status
        switch (newStatus) {
            case CONFIRMED -> order.setConfirmedAt(LocalDateTime.now());
            case SHIPPED -> order.setShippedAt(LocalDateTime.now());
            case DELIVERED -> order.setDeliveredAt(LocalDateTime.now());
            case CANCELLED -> order.setCancelledAt(LocalDateTime.now());
        }

        return bulkOrderRepository.save(order);
    }

    public List<BulkOrder> getAllBulkOrders() {
        return bulkOrderRepository.findAll();
    }

    public List<BulkOrder> getOrdersByBuyer(Long buyerId) {
        Party buyer = partyRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));
        return bulkOrderRepository.findByBuyerParty(buyer);
    }

    // Vendor Reviews
    @Transactional
    public VendorReview createReview(VendorReview review) {
        if (review.getVendor() != null && review.getVendor().getId() != null) {
            MarketplaceVendor vendor = vendorRepository.findById(review.getVendor().getId())
                    .orElseThrow(() -> new RuntimeException("Vendor not found"));
            review.setVendor(vendor);

            // Update vendor rating
            vendor.setTotalReviews(vendor.getTotalReviews() + 1);
            Optional<Double> avgRating = reviewRepository.getAverageRating(vendor);
            if (avgRating.isPresent()) {
                vendor.setRating(java.math.BigDecimal.valueOf(avgRating.get()));
            }
            vendorRepository.save(vendor);
        }

        if (review.getReviewerParty() != null && review.getReviewerParty().getId() != null) {
            Party party = partyRepository.findById(review.getReviewerParty().getId())
                    .orElseThrow(() -> new RuntimeException("Party not found"));
            review.setReviewerParty(party);
        }

        return reviewRepository.save(review);
    }

    public List<VendorReview> getVendorReviews(Long vendorId) {
        MarketplaceVendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return reviewRepository.findByVendor(vendor);
    }

    public Optional<Double> getVendorAverageRating(Long vendorId) {
        MarketplaceVendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        return reviewRepository.getAverageRating(vendor);
    }
}
