package com.erp.system.marketplace.controller;

import com.erp.system.marketplace.entity.*;
import com.erp.system.marketplace.service.MarketplaceService;
import com.erp.system.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/marketplace")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    // Vendor endpoints
    @PostMapping("/vendors")
    public ApiResponse<MarketplaceVendor> createVendor(@RequestBody MarketplaceVendor vendor) {
        return ApiResponse.success(marketplaceService.createVendor(vendor), "Vendor created successfully");
    }

    @GetMapping("/vendors")
    public ApiResponse<List<MarketplaceVendor>> getAllVendors() {
        return ApiResponse.success(marketplaceService.getAllVendors());
    }

    @GetMapping("/vendors/{id}")
    public ApiResponse<MarketplaceVendor> getVendorById(@PathVariable Long id) {
        return marketplaceService.getVendorById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Vendor not found"));
    }

    @GetMapping("/vendors/active")
    public ApiResponse<List<MarketplaceVendor>> getActiveVendors() {
        return ApiResponse.success(marketplaceService.getActiveVendors());
    }

    @GetMapping("/vendors/verified")
    public ApiResponse<List<MarketplaceVendor>> getVerifiedVendors() {
        return ApiResponse.success(marketplaceService.getVerifiedVendors());
    }

    @GetMapping("/vendors/party/{partyId}")
    public ApiResponse<MarketplaceVendor> getVendorByParty(@PathVariable Long partyId) {
        return marketplaceService.getVendorByParty(partyId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Vendor not found"));
    }

    // Listing endpoints
    @PostMapping("/listings")
    public ApiResponse<MarketplaceListing> createListing(@RequestBody MarketplaceListing listing) {
        return ApiResponse.success(marketplaceService.createListing(listing), "Listing created successfully");
    }

    @GetMapping("/listings")
    public ApiResponse<List<MarketplaceListing>> getAllListings() {
        return ApiResponse.success(marketplaceService.getAllListings());
    }

    @GetMapping("/listings/{id}")
    public ApiResponse<MarketplaceListing> getListingById(@PathVariable Long id) {
        return marketplaceService.getListingById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Listing not found"));
    }

    @GetMapping("/listings/active")
    public ApiResponse<List<MarketplaceListing>> getActiveListings() {
        return ApiResponse.success(marketplaceService.getActiveListings());
    }

    @GetMapping("/listings/vendor/{vendorId}")
    public ApiResponse<List<MarketplaceListing>> getVendorListings(@PathVariable Long vendorId) {
        return ApiResponse.success(marketplaceService.getVendorListings(vendorId));
    }

    @GetMapping("/listings/search")
    public ApiResponse<List<MarketplaceListing>> searchListings(@RequestParam String q) {
        return ApiResponse.success(marketplaceService.searchListings(q));
    }

    @GetMapping("/listings/available")
    public ApiResponse<List<MarketplaceListing>> getAvailableListings() {
        return ApiResponse.success(marketplaceService.getAvailableListings());
    }

    // Inquiry endpoints
    @PostMapping("/inquiries")
    public ApiResponse<ProductInquiry> createInquiry(@RequestBody ProductInquiry inquiry) {
        return ApiResponse.success(marketplaceService.createInquiry(inquiry), "Inquiry created successfully");
    }

    @GetMapping("/inquiries")
    public ApiResponse<List<ProductInquiry>> getAllInquiries() {
        return ApiResponse.success(marketplaceService.getAllInquiries());
    }

    @GetMapping("/inquiries/status/{status}")
    public ApiResponse<List<ProductInquiry>> getInquiriesByStatus(@PathVariable InquiryStatus status) {
        return ApiResponse.success(marketplaceService.getInquiriesByStatus(status));
    }

    @PostMapping("/inquiries/{id}/respond")
    public ApiResponse<ProductInquiry> respondToInquiry(
            @PathVariable Long id,
            @RequestBody InquiryResponseRequest request) {
        try {
            ProductInquiry inquiry = marketplaceService.respondToInquiry(
                    id, request.getQuotedPrice(), request.getValidUntil(), request.getNotes());
            return ApiResponse.success(inquiry, "Inquiry responded successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // Bulk Order endpoints
    @PostMapping("/orders")
    public ApiResponse<BulkOrder> createBulkOrder(@RequestBody BulkOrder order) {
        return ApiResponse.success(marketplaceService.createBulkOrder(order), "Bulk order created successfully");
    }

    @GetMapping("/orders")
    public ApiResponse<List<BulkOrder>> getAllBulkOrders() {
        return ApiResponse.success(marketplaceService.getAllBulkOrders());
    }

    @GetMapping("/orders/buyer/{buyerId}")
    public ApiResponse<List<BulkOrder>> getOrdersByBuyer(@PathVariable Long buyerId) {
        return ApiResponse.success(marketplaceService.getOrdersByBuyer(buyerId));
    }

    @PostMapping("/orders/{id}/status")
    public ApiResponse<BulkOrder> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request) {
        try {
            BulkOrder order = marketplaceService.updateOrderStatus(id, request.getStatus());
            return ApiResponse.success(order, "Order status updated successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // Review endpoints
    @PostMapping("/reviews")
    public ApiResponse<VendorReview> createReview(@RequestBody VendorReview review) {
        return ApiResponse.success(marketplaceService.createReview(review), "Review created successfully");
    }

    @GetMapping("/vendors/{id}/reviews")
    public ApiResponse<List<VendorReview>> getVendorReviews(@PathVariable Long id) {
        return ApiResponse.success(marketplaceService.getVendorReviews(id));
    }

    @GetMapping("/vendors/{id}/rating")
    public ApiResponse<Double> getVendorAverageRating(@PathVariable Long id) {
        return marketplaceService.getVendorAverageRating(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Vendor not found"));
    }

    // Request DTOs
    public static class InquiryResponseRequest {
        private BigDecimal quotedPrice;
        private LocalDate validUntil;
        private String notes;

        public BigDecimal getQuotedPrice() { return quotedPrice; }
        public void setQuotedPrice(BigDecimal quotedPrice) { this.quotedPrice = quotedPrice; }
        public LocalDate getValidUntil() { return validUntil; }
        public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class StatusUpdateRequest {
        private BulkOrderStatus status;

        public BulkOrderStatus getStatus() { return status; }
        public void setStatus(BulkOrderStatus status) { this.status = status; }
    }
}
