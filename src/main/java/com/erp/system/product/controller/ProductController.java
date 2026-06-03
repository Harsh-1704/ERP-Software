package com.erp.system.product.controller;

import com.erp.system.common.dto.ProductDTO;
import com.erp.system.common.mapper.EntityMapper;
import com.erp.system.common.response.ApiResponse;
import com.erp.system.product.entity.Product;
import com.erp.system.product.entity.ProductCategory;
import com.erp.system.product.entity.ProductPrice;
import com.erp.system.product.entity.Unit;
import com.erp.system.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management APIs")
public class ProductController {

    private final ProductService productService;
    private final EntityMapper entityMapper;


    // Product endpoints
    @PostMapping
    @Operation(summary = "Create a new product", description = "Creates a new product with optional category and unit")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ApiResponse<ProductDTO.ProductResponse> createProduct(@RequestBody ProductDTO.CreateProductRequest request,
                                 @RequestParam(required = false) Long categoryId,
                                 @RequestParam(required = false) Long unitId) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setMaxStockLevel(request.getMaxStockLevel());
        product.setTaxRate(request.getTaxRate());
        product.setManufacturer(request.getManufacturer());
        product.setHsnCode(request.getHsnCode());
        Product created = productService.createProduct(product, categoryId, unitId);
        return ApiResponse.success(entityMapper.toProductResponse(created), "Product created successfully");
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a product", description = "Updates an existing product")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<ProductDTO.ProductResponse> updateProduct(@PathVariable Long id, @RequestBody ProductDTO.CreateProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setSku(request.getSku());
        product.setBarcode(request.getBarcode());
        product.setPrice(request.getPrice());
        Product updated = productService.updateProduct(id, product);
        return ApiResponse.success(entityMapper.toProductResponse(updated), "Product updated successfully");
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a single product by its ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<ProductDTO.ProductResponse> getProduct(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(p -> ApiResponse.success(entityMapper.toProductResponse(p)))
                .orElse(ApiResponse.error("Product not found"));
    }

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a list of all products")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ApiResponse<List<ProductDTO.ProductResponse>> getAllProducts() {
        return ApiResponse.success(entityMapper.toProductResponseList(productService.getAllProducts()));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active products", description = "Returns only active products")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ApiResponse<List<ProductDTO.ProductResponse>> getActiveProducts() {
        return ApiResponse.success(entityMapper.toProductResponseList(productService.getActiveProducts()));
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Get product by SKU", description = "Returns a product by its SKU")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<ProductDTO.ProductResponse> getProductBySku(@PathVariable String sku) {
        return productService.getProductBySku(sku)
                .map(p -> ApiResponse.success(entityMapper.toProductResponse(p)))
                .orElse(ApiResponse.error("Product not found"));
    }

    @GetMapping("/barcode/{barcode}")
    @Operation(summary = "Get product by barcode", description = "Returns a product by its barcode")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<ProductDTO.ProductResponse> getProductByBarcode(@PathVariable String barcode) {
        return productService.getProductByBarcode(barcode)
                .map(p -> ApiResponse.success(entityMapper.toProductResponse(p)))
                .orElse(ApiResponse.error("Product not found"));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name or description")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Products retrieved successfully")
    public ApiResponse<List<ProductDTO.ProductResponse>> searchProducts(@RequestParam String q) {
        return ApiResponse.success(entityMapper.toProductResponseList(productService.searchProducts(q)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a product", description = "Deletes a product by its ID")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success(null, "Product deleted successfully");
    }

    // Category endpoints
    @PostMapping("/categories")
    @Operation(summary = "Create a new product category", description = "Creates a new product category with optional parent")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Category created successfully")
    public ApiResponse<ProductDTO.CategoryResponse> createCategory(@RequestBody ProductDTO.CreateCategoryRequest request) {
        ProductCategory category = new ProductCategory();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        ProductCategory created = productService.createCategory(category);
        return ApiResponse.success(entityMapper.toCategoryResponse(created), "Category created successfully");
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Returns all product categories")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ApiResponse<List<ProductDTO.CategoryResponse>> getAllCategories() {
        return ApiResponse.success(productService.getAllCategories().stream()
                .map(entityMapper::toCategoryResponse)
                .toList());
    }

    @GetMapping("/categories/root")
    @Operation(summary = "Get root categories", description = "Returns top-level categories (no parent)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ApiResponse<List<ProductDTO.CategoryResponse>> getRootCategories() {
        return ApiResponse.success(productService.getRootCategories().stream()
                .map(entityMapper::toCategoryResponse)
                .toList());
    }

    // Unit endpoints
    @PostMapping("/units")
    @Operation(summary = "Create a new unit", description = "Creates a new unit of measurement")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Unit created successfully")
    public ApiResponse<ProductDTO.UnitResponse> createUnit(@RequestBody ProductDTO.CreateUnitRequest request) {
        Unit unit = new Unit();
        unit.setName(request.getName());
        unit.setSymbol(request.getSymbol());
        Unit created = productService.createUnit(unit);
        return ApiResponse.success(entityMapper.toUnitResponse(created), "Unit created successfully");
    }

    @GetMapping("/units")
    @Operation(summary = "Get all units", description = "Returns all units of measurement")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Units retrieved successfully")
    public ApiResponse<List<ProductDTO.UnitResponse>> getAllUnits() {
        return ApiResponse.success(productService.getAllUnits().stream()
                .map(entityMapper::toUnitResponse)
                .toList());
    }

    // Price endpoints
    @PostMapping("/{id}/price")
    @Operation(summary = "Set product price", description = "Sets a new price for a product")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Price set successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<ProductPrice> setPrice(@PathVariable Long id, @RequestBody PriceRequest request) {
        ProductPrice price = productService.setProductPrice(id, request.getPrice());
        return ApiResponse.success(price, "Price updated successfully");
    }

    @GetMapping("/{id}/price/current")
    @Operation(summary = "Get current price", description = "Returns the current price of a product")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Price found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ApiResponse<ProductPrice> getCurrentPrice(@PathVariable Long id) {
        return productService.getCurrentPrice(id)
                .map(p -> ApiResponse.success(p, "Current price"))
                .orElse(ApiResponse.error("Product not found"));
    }

    @GetMapping("/{id}/price/history")
    @Operation(summary = "Get price history", description = "Returns the price history of a product")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Price history retrieved successfully")
    public ApiResponse<List<ProductPrice>> getPriceHistory(@PathVariable Long id) {
        return ApiResponse.success(productService.getPriceHistory(id));
    }

    public static class PriceRequest {
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        private BigDecimal price;

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }
}