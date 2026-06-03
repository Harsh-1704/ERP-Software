package com.erp.system.inventory.controller;

import com.erp.system.common.dto.InventoryDTO;
import com.erp.system.common.mapper.EntityMapper;
import com.erp.system.common.response.ApiResponse;
import com.erp.system.inventory.entity.Stock;
import com.erp.system.inventory.entity.StockMovement;
import com.erp.system.inventory.entity.Warehouse;
import com.erp.system.inventory.service.InventoryService;
import com.erp.system.inventory.service.InventoryService.TransferItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory and warehouse management APIs")
public class InventoryController {

    private final InventoryService inventoryService;
    private final EntityMapper entityMapper;

    // Warehouse endpoints
    @PostMapping("/warehouses")
    @Operation(summary = "Create a new warehouse", description = "Creates a new warehouse with the specified details")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Warehouse created successfully")
    public ApiResponse<InventoryDTO.WarehouseResponse> createWarehouse(@RequestBody InventoryDTO.CreateWarehouseRequest request) {
        Warehouse warehouse = new Warehouse();
        warehouse.setName(request.getName());
        warehouse.setCode(request.getCode());
        warehouse.setAddressLine1(request.getAddressLine1());
        warehouse.setAddressLine2(request.getAddressLine2());
        warehouse.setCity(request.getCity());
        warehouse.setState(request.getState());
        warehouse.setCountry(request.getCountry());
        warehouse.setPincode(request.getPincode());
        warehouse.setContactPerson(request.getContactPerson());
        warehouse.setContactEmail(request.getContactEmail());
        warehouse.setContactPhone(request.getContactPhone());
        warehouse.setCapacityUnits(request.getCapacityUnits());
        Warehouse created = inventoryService.createWarehouse(warehouse);
        return ApiResponse.success(entityMapper.toWarehouseResponse(created), "Warehouse created successfully");
    }

    @GetMapping("/warehouses")
    @Operation(summary = "Get all warehouses", description = "Returns a list of all warehouses")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully")
    public ApiResponse<List<InventoryDTO.WarehouseResponse>> getAllWarehouses() {
        return ApiResponse.success(entityMapper.toWarehouseResponseList(inventoryService.getAllWarehouses()));
    }

    @GetMapping("/warehouses/active")
    @Operation(summary = "Get active warehouses", description = "Returns only active warehouses")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Warehouses retrieved successfully")
    public ApiResponse<List<InventoryDTO.WarehouseResponse>> getActiveWarehouses() {
        return ApiResponse.success(entityMapper.toWarehouseResponseList(inventoryService.getActiveWarehouses()));
    }

    @GetMapping("/warehouses/code/{code}")
    @Operation(summary = "Get warehouse by code", description = "Returns a warehouse by its unique code")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Warehouse found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Warehouse not found")
    })
    public ApiResponse<InventoryDTO.WarehouseResponse> getWarehouseByCode(@PathVariable String code) {
        return inventoryService.getWarehouseByCode(code)
                .map(w -> ApiResponse.success(entityMapper.toWarehouseResponse(w)))
                .orElse(ApiResponse.error("Warehouse not found"));
    }

    // Stock endpoints
    @GetMapping("/stock/product/{productId}")
    @Operation(summary = "Get product stock", description = "Returns stock levels for a product across all warehouses")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock retrieved successfully")
    public ApiResponse<List<Stock>> getProductStock(@PathVariable Long productId) {
        return ApiResponse.success(inventoryService.getProductStock(productId));
    }

    @GetMapping("/stock/warehouse/{warehouseId}")
    @Operation(summary = "Get warehouse stock", description = "Returns all stock in a specific warehouse")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock retrieved successfully")
    public ApiResponse<List<Stock>> getWarehouseStock(@PathVariable Long warehouseId) {
        return ApiResponse.success(inventoryService.getWarehouseStock(warehouseId));
    }

    @GetMapping("/stock/{productId}/warehouse/{warehouseId}")
    @Operation(summary = "Get stock by product and warehouse", description = "Returns stock level for a specific product in a specific warehouse")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Stock not found")
    })
    public ApiResponse<Stock> getStock(@PathVariable Long productId, @PathVariable Long warehouseId) {
        return inventoryService.getStock(productId, warehouseId)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Stock not found"));
    }

    @GetMapping("/stock/low-stock")
    @Operation(summary = "Get low stock items", description = "Returns products with stock below minimum level")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Low stock items retrieved successfully")
    public ApiResponse<List<Stock>> getLowStockItems() {
        return ApiResponse.success(inventoryService.getLowStockItems());
    }

    // Stock Movement endpoints
    @PostMapping("/stock/in")
    @Operation(summary = "Stock IN", description = "Records stock coming into a warehouse (purchase, return, etc.)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock recorded successfully")
    public ApiResponse<StockMovement> stockIn(@RequestBody InventoryDTO.StockInRequest request) {
        StockMovement movement = inventoryService.stockIn(
                request.getProductId(),
                request.getWarehouseId(),
                request.getQuantity(),
                request.getUnitPrice(),
                request.getReferenceType(),
                request.getReferenceId()
        );
        return ApiResponse.success(movement, "Stock recorded successfully");
    }

    @PostMapping("/stock/out")
    @Operation(summary = "Stock OUT", description = "Records stock going out from a warehouse (sale, damage, etc.)")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock recorded successfully")
    public ApiResponse<StockMovement> stockOut(@RequestBody InventoryDTO.StockOutRequest request) {
        StockMovement movement = inventoryService.stockOut(
                request.getProductId(),
                request.getWarehouseId(),
                request.getQuantity(),
                request.getReferenceType(),
                request.getReferenceId()
        );
        return ApiResponse.success(movement, "Stock recorded successfully");
    }

    @PostMapping("/stock/transfer")
    @Operation(summary = "Transfer stock", description = "Transfers stock from one warehouse to another")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Stock transferred successfully")
    public ApiResponse<?> transferStock(@RequestBody InventoryDTO.TransferStockRequest request) {
        Object result = inventoryService.transferStock(
                request.getFromWarehouseId(),
                request.getToWarehouseId(),
                request.getItems().stream()
                    .map(item -> {
                        TransferItemRequest req = new TransferItemRequest();
                        req.setProductId(item.getProductId());
                        req.setQuantity(item.getQuantity());
                        return req;
                    })
                    .toList(),
                request.getRemarks()
        );
        return ApiResponse.success(result, "Stock transferred successfully");
    }

    @GetMapping("/movements/product/{productId}/warehouse/{warehouseId}")
    @Operation(summary = "Get movement history", description = "Returns all stock movements for a product in a warehouse")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Movements retrieved successfully")
    public ApiResponse<List<StockMovement>> getMovementHistory(@PathVariable Long productId,
                                                   @PathVariable Long warehouseId) {
        return ApiResponse.success(inventoryService.getMovementHistory(productId, warehouseId));
    }

}