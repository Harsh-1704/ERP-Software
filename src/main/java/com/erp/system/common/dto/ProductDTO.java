package com.erp.system.common.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class ProductDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateProductRequest {
        @NotBlank(message = "Product name is required")
        private String name;

        private String description;

        @NotNull(message = "Category ID is required")
        private Long categoryId;

        @NotNull(message = "Unit ID is required")
        private Long unitId;

        @NotBlank(message = "SKU is required")
        private String sku;

        private String barcode;

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private BigDecimal price;

        private BigDecimal costPrice;

        private Integer minStockLevel;

        private Integer maxStockLevel;

        private BigDecimal taxRate;

        private String manufacturer;

        private String hsnCode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private String sku;
        private String barcode;
        private BigDecimal price;
        private BigDecimal costPrice;
        private Integer minStockLevel;
        private Integer maxStockLevel;
        private BigDecimal taxRate;
        private String manufacturer;
        private String hsnCode;
        private boolean active;
        private CategoryInfo category;
        private UnitInfo unit;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CategoryInfo {
            private Long id;
            private String name;
        }

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class UnitInfo {
            private Long id;
            private String name;
            private String symbol;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCategoryRequest {
        @NotBlank(message = "Category name is required")
        private String name;
        private String description;
        private Long parentId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryResponse {
        private Long id;
        private String name;
        private String description;
        private CategoryResponse parent;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUnitRequest {
        @NotBlank(message = "Unit name is required")
        private String name;
        private String symbol;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnitResponse {
        private Long id;
        private String name;
        private String symbol;
    }
}