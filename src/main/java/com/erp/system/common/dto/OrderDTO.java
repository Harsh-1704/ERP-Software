package com.erp.system.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateOrderRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        @NotNull(message = "At least one item is required")
        @NotEmpty(message = "At least one item is required")
        private List<OrderItemRequest> items;

        private String shippingAddress;
        private String billingAddress;
        private String remarks;
        private LocalDateTime expectedDeliveryDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        private BigDecimal unitPrice;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderResponse {
        private Long id;
        private String orderNumber;
        private CustomerInfo customer;
        private List<OrderItemInfo> items;
        private BigDecimal subtotal;
        private BigDecimal taxAmount;
        private BigDecimal discountAmount;
        private BigDecimal totalAmount;
        private String status;
        private String shippingAddress;
        private String billingAddress;
        private String remarks;
        private LocalDateTime expectedDeliveryDate;
        private LocalDateTime createdAt;

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
        public static class OrderItemInfo {
            private Long id;
            private Long productId;
            private String productName;
            private String productSku;
            private Integer quantity;
            private BigDecimal unitPrice;
            private BigDecimal totalAmount;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOrderStatusRequest {
        @NotBlank(message = "Status is required")
        private String status;

        private String remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DeliveryChallanResponse {
        private Long id;
        private String challanNumber;
        private Long orderId;
        private String orderNumber;
        private List<ChallanItemInfo> items;
        private String shippingAddress;
        private String remarks;
        private String status;
        private LocalDateTime createdAt;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ChallanItemInfo {
            private Long id;
            private Long productId;
            private String productName;
            private Integer quantity;
        }
    }
}