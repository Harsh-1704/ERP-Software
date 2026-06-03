package com.erp.system.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

public class BillingDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateInvoiceRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        @NotNull(message = "At least one item is required")
        @NotEmpty(message = "At least one item is required")
        private List<InvoiceItemRequest> items;

        private String referenceNumber;
        private String remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private Integer quantity;

        private BigDecimal unitPrice;

        private BigDecimal discount;

        private BigDecimal taxRate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceResponse {
        private Long id;
        private String invoiceNumber;
        private CustomerInfo customer;
        private List<InvoiceItemInfo> items;
        private BigDecimal subtotal;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private BigDecimal paidAmount;
        private BigDecimal balanceDue;
        private String status;
        private String referenceNumber;
        private String remarks;
        private String createdAt;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CustomerInfo {
            private Long id;
            private String name;
            private String email;
            private String phone;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class InvoiceItemInfo {
            private Long id;
            private Long productId;
            private String productName;
            private String productSku;
            private Integer quantity;
            private BigDecimal unitPrice;
            private BigDecimal discount;
            private BigDecimal taxRate;
            private BigDecimal totalAmount;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordPaymentRequest {
        @NotNull(message = "Payment amount is required")
        @DecimalMin(value = "0.01", message = "Payment amount must be greater than 0")
        private BigDecimal amount;

        private String paymentMethod;

        private String transactionReference;

        private String remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentResponse {
        private Long id;
        private String paymentNumber;
        private String invoiceNumber;
        private BigDecimal amount;
        private String paymentMethod;
        private String transactionReference;
        private String remarks;
        private String status;
        private String createdAt;
    }
}