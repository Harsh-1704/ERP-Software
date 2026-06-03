package com.erp.system.product.service;

import com.erp.system.common.exception.ResourceNotFoundException;
import com.erp.system.product.entity.Product;
import com.erp.system.product.entity.ProductCategory;
import com.erp.system.product.entity.ProductPrice;
import com.erp.system.product.entity.Unit;
import com.erp.system.product.repository.ProductCategoryRepository;
import com.erp.system.product.repository.ProductPriceRepository;
import com.erp.system.product.repository.ProductRepository;
import com.erp.system.product.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository categoryRepository;
    private final UnitRepository unitRepository;
    private final ProductPriceRepository priceRepository;

    // Product CRUD
    @Transactional
    public Product createProduct(Product product, Long categoryId, Long unitId) {
        // Validate SKU uniqueness
        if (productRepository.existsBySku(product.getSku())) {
            throw new IllegalArgumentException("Product with SKU already exists: " + product.getSku());
        }

        // Validate barcode uniqueness if provided
        if (product.getBarcode() != null && productRepository.existsByBarcode(product.getBarcode())) {
            throw new IllegalArgumentException("Product with barcode already exists: " + product.getBarcode());
        }

        // Validate category if provided
        if (categoryId != null) {
            ProductCategory category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category", categoryId));
            product.setCategory(category);
        }

        // Validate unit if provided
        if (unitId != null) {
            Unit unit = unitRepository.findById(unitId)
                    .orElseThrow(() -> new ResourceNotFoundException("Unit", unitId));
            product.setUnit(unit);
        }

        // Set defaults
        if (product.getActive() == null) {
            product.setActive(true);
        }
        if (product.getMinStockLevel() == null) {
            product.setMinStockLevel(0);
        }

        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));

        // Validate SKU uniqueness if changed
        if (!product.getSku().equals(productDetails.getSku()) && productRepository.existsBySku(productDetails.getSku())) {
            throw new IllegalArgumentException("Product with SKU already exists: " + productDetails.getSku());
        }

        // Validate barcode uniqueness if changed
        if (productDetails.getBarcode() != null &&
            (product.getBarcode() == null || !product.getBarcode().equals(productDetails.getBarcode())) &&
            productRepository.existsByBarcode(productDetails.getBarcode())) {
            throw new IllegalArgumentException("Product with barcode already exists: " + productDetails.getBarcode());
        }

        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setSku(productDetails.getSku());
        product.setBarcode(productDetails.getBarcode());
        product.setActive(productDetails.getActive());
        product.setManufacturer(productDetails.getManufacturer());
        product.setHsnCode(productDetails.getHsnCode());
        product.setTaxRate(productDetails.getTaxRate());
        product.setMinStockLevel(productDetails.getMinStockLevel());
        product.setMaxStockLevel(productDetails.getMaxStockLevel());
        product.setCostPrice(productDetails.getCostPrice());

        return productRepository.save(product);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> getActiveProducts() {
        return productRepository.findByActiveTrue(Pageable.unpaged()).getContent();
    }

    public Optional<Product> getProductBySku(String sku) {
        return productRepository.findBySku(sku);
    }

    public Optional<Product> getProductByBarcode(String barcode) {
        return productRepository.findByBarcode(barcode);
    }

    public List<Product> searchProducts(String searchTerm) {
        return productRepository.searchProducts(searchTerm, Pageable.unpaged()).getContent();
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    // Product Category
    public ProductCategory createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<ProductCategory> getRootCategories() {
        return categoryRepository.findByParentIdIsNull();
    }

    // Unit
    public Unit createUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    public List<Unit> getAllUnits() {
        return unitRepository.findAll();
    }

    // Product Price
    @Transactional
    public ProductPrice setProductPrice(Long productId, BigDecimal price) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));

        // Validate price
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be greater than or equal to 0");
        }

        // Mark existing current price as not current
        priceRepository.findCurrentPrice(product).ifPresent(p -> {
            p.setIsCurrent(false);
            priceRepository.save(p);
        });

        ProductPrice newPrice = new ProductPrice();
        newPrice.setProduct(product);
        newPrice.setPrice(price);
        newPrice.setEffectiveDate(LocalDateTime.now());
        newPrice.setIsCurrent(true);

        return priceRepository.save(newPrice);
    }

    public Optional<ProductPrice> getCurrentPrice(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return priceRepository.findCurrentPrice(product);
    }

    public List<ProductPrice> getPriceHistory(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return priceRepository.findPriceHistory(product);
    }
}