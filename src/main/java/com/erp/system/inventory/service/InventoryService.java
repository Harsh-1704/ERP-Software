package com.erp.system.inventory.service;

import com.erp.system.common.exception.ResourceNotFoundException;
import com.erp.system.inventory.entity.*;
import com.erp.system.inventory.repository.*;
import com.erp.system.product.entity.Product;
import com.erp.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final WarehouseRepository warehouseRepository;
    private final StockRepository stockRepository;
    private final StockMovementRepository movementRepository;
    private final ProductRepository productRepository;

    // Warehouse Management
    @Transactional
    public Warehouse createWarehouse(Warehouse warehouse) {
        if (warehouse.getCode() == null || warehouse.getCode().isBlank()) {
            throw new IllegalArgumentException("Warehouse code is required");
        }
        if (warehouseRepository.existsByCode(warehouse.getCode())) {
            throw new IllegalArgumentException("Warehouse code already exists: " + warehouse.getCode());
        }
        if (warehouse.getName() == null || warehouse.getName().isBlank()) {
            throw new IllegalArgumentException("Warehouse name is required");
        }
        if (warehouse.getIsActive() == null) {
            warehouse.setIsActive(true);
        }
        return warehouseRepository.save(warehouse);
    }

    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }

    public Optional<Warehouse> getWarehouseByCode(String code) {
        return warehouseRepository.findByCode(code);
    }

    // Stock Management
    @Transactional
    public Stock getOrCreateStock(Product product, Warehouse warehouse) {
        return stockRepository.findByProductAndWarehouse(product, warehouse)
                .orElseGet(() -> {
                    Stock stock = new Stock();
                    stock.setProduct(product);
                    stock.setWarehouse(warehouse);
                    stock.setQuantityOnHand(BigDecimal.ZERO);
                    stock.setQuantityReserved(BigDecimal.ZERO);
                    stock.setQuantityAvailable(BigDecimal.ZERO);
                    return stockRepository.save(stock);
                });
    }

    public Optional<Stock> getStock(Long productId, Long warehouseId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return stockRepository.findByProductAndWarehouse(product, warehouse);
    }

    public List<Stock> getProductStock(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return stockRepository.findByProduct(product);
    }

    public List<Stock> getWarehouseStock(Long warehouseId) {
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return stockRepository.findByWarehouse(warehouse);
    }

    public List<Stock> getLowStockItems() {
        return stockRepository.findAllLowStockItems();
    }

    // Stock Movements
    @Transactional
    public StockMovement recordMovement(StockMovement movement) {
        Product product = movement.getProduct();
        Warehouse warehouse = movement.getWarehouse();
        BigDecimal quantity = movement.getQuantity();

        Stock stock = getOrCreateStock(product, warehouse);

        // Update stock based on movement type
        switch (movement.getMovementType()) {
            case PURCHASE_IN, ADJUSTMENT_IN, RETURN_IN, MANUFACTURING_IN -> {
                stock.setQuantityOnHand(stock.getQuantityOnHand().add(quantity));
            }
            case SALES_OUT, ADJUSTMENT_OUT, RETURN_OUT, MANUFACTURING_OUT -> {
                stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(quantity));
            }
            case TRANSFER_OUT -> {
                stock.setQuantityOnHand(stock.getQuantityOnHand().subtract(quantity));
            }
            case TRANSFER_IN -> {
                stock.setQuantityOnHand(stock.getQuantityOnHand().add(quantity));
            }
        }

        // Calculate total value
        if (movement.getUnitPrice() != null) {
            movement.setTotalValue(movement.getUnitPrice().multiply(quantity));
        }

        stockRepository.save(stock);
        return movementRepository.save(movement);
    }

    @Transactional
    public StockMovement stockIn(Long productId, Long warehouseId, BigDecimal quantity,
                                  BigDecimal unitPrice, String referenceType, Long referenceId) {
        // Validate inputs
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID is required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Unit price must be greater than or equal to 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setMovementType(StockMovementType.PURCHASE_IN);
        movement.setQuantity(quantity);
        movement.setUnitPrice(unitPrice);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);

        return recordMovement(movement);
    }

    @Transactional
    public StockMovement stockOut(Long productId, Long warehouseId, BigDecimal quantity,
                                   String referenceType, Long referenceId) {
        // Validate inputs
        if (productId == null) {
            throw new IllegalArgumentException("Product ID is required");
        }
        if (warehouseId == null) {
            throw new IllegalArgumentException("Warehouse ID is required");
        }
        if (quantity == null || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", productId));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse", warehouseId));

        Stock stock = getOrCreateStock(product, warehouse);
        if (stock.getQuantityAvailable().compareTo(quantity) < 0) {
            throw new IllegalArgumentException("Insufficient stock available. Available: " + stock.getQuantityAvailable());
        }

        StockMovement movement = new StockMovement();
        movement.setProduct(product);
        movement.setWarehouse(warehouse);
        movement.setMovementType(StockMovementType.SALES_OUT);
        movement.setQuantity(quantity);
        movement.setReferenceType(referenceType);
        movement.setReferenceId(referenceId);

        return recordMovement(movement);
    }

    @Transactional
    public StockTransfer transferStock(Long fromWarehouseId, Long toWarehouseId,
                                        List<TransferItemRequest> items, String remarks) {
        Warehouse fromWarehouse = warehouseRepository.findById(fromWarehouseId)
                .orElseThrow(() -> new RuntimeException("From warehouse not found"));
        Warehouse toWarehouse = warehouseRepository.findById(toWarehouseId)
                .orElseThrow(() -> new RuntimeException("To warehouse not found"));

        StockTransfer transfer = new StockTransfer();
        transfer.setTransferNumber("TRF-" + System.currentTimeMillis());
        transfer.setFromWarehouse(fromWarehouse);
        transfer.setToWarehouse(toWarehouse);
        transfer.setRemarks(remarks);
        transfer.setStatus("INITIATED");

        for (TransferItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProductId()));

            // Check stock availability
            Stock stock = getOrCreateStock(product, fromWarehouse);
            if (stock.getQuantityAvailable().compareTo(item.getQuantity()) < 0) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }

            StockTransferItem transferItem = new StockTransferItem();
            transferItem.setTransfer(transfer);
            transferItem.setProduct(product);
            transferItem.setQuantity(item.getQuantity());

            transfer.getItems().add(transferItem);

            // Record transfer out movement
            StockMovement outMovement = new StockMovement();
            outMovement.setProduct(product);
            outMovement.setWarehouse(fromWarehouse);
            outMovement.setMovementType(StockMovementType.TRANSFER_OUT);
            outMovement.setQuantity(item.getQuantity());
            outMovement.setToWarehouse(toWarehouse);
            outMovement.setReferenceType("STOCK_TRANSFER");
            movementRepository.save(outMovement);
        }

        return transfer; // Note: Need to save transfer separately with items
    }

    public List<StockMovement> getMovementHistory(Long productId, Long warehouseId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new RuntimeException("Warehouse not found"));
        return movementRepository.findProductWarehouseHistory(product, warehouse);
    }

    public static class TransferItemRequest {
        private Long productId;
        private BigDecimal quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    }
}