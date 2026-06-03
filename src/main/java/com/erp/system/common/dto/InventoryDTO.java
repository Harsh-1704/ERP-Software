package com.erp.system.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class InventoryDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWarehouseRequest {
        @NotBlank(message = "Warehouse name is required")
        private String name;

        @NotBlank(message = "Warehouse code is required")
        private String code;

        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String country;
        private String pincode;

        private String contactPerson;
        private String contactEmail;
        private String contactPhone;

        private Integer capacityUnits;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WarehouseResponse {
        private Long id;
        private String name;
        private String code;
        private String addressLine1;
        private String city;
        private String state;
        private String country;
        private String contactPerson;
        private String contactEmail;
        private String contactPhone;
        private boolean isActive;
        private Integer capacityUnits;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockInRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Warehouse ID is required")
        private Long warehouseId;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.01", message = "Unit price must be greater than 0")
        private BigDecimal unitPrice;

        private String referenceType;
        private Long referenceId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockOutRequest {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Warehouse ID is required")
        private Long warehouseId;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;

        private String referenceType;
        private Long referenceId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productSku;
        private Long warehouseId;
        private String warehouseName;
        private String warehouseCode;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private Integer minStockLevel;
        private boolean isLowStock;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferItem {
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @DecimalMin(value = "0.01", message = "Quantity must be greater than 0")
        private BigDecimal quantity;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferStockRequest {
        @NotNull(message = "Source warehouse ID is required")
        private Long fromWarehouseId;

        @NotNull(message = "Destination warehouse ID is required")
        private Long toWarehouseId;

        @NotNull(message = "Items are required")
        @NotEmpty(message = "At least one item is required")
        private java.util.List<TransferItem> items;

        private String remarks;
    }
}