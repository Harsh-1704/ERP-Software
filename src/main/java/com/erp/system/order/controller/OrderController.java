package com.erp.system.order.controller;

import com.erp.system.order.entity.OrderStatus;
import com.erp.system.order.entity.Orders;
import com.erp.system.order.service.OrderService;
import com.erp.system.order.service.OrderService.OrderItemRequest;
import com.erp.system.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Order endpoints
    @PostMapping
    public ApiResponse<Orders> createOrder(@RequestBody CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(request.getOrder(), request.getItems()), "Order created successfully");
    }

    @GetMapping("/{id}")
    public ApiResponse<Orders> getOrder(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Order not found"));
    }

    @GetMapping("/number/{number}")
    public ApiResponse<Orders> getOrderByNumber(@PathVariable String number) {
        return orderService.getOrderByNumber(number)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Order not found"));
    }

    @GetMapping
    public ApiResponse<List<Orders>> getAllOrders() {
        return ApiResponse.success(orderService.getAllOrders());
    }

    @GetMapping("/party/{partyId}")
    public ApiResponse<List<Orders>> getOrdersByParty(@PathVariable Long partyId) {
        return ApiResponse.success(orderService.getOrdersByParty(partyId));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<List<Orders>> getOrdersByStatus(@PathVariable OrderStatus status) {
        return ApiResponse.success(orderService.getOrdersByStatus(status));
    }

    @GetMapping("/sales")
    public ApiResponse<List<Orders>> getSalesOrders() {
        return ApiResponse.success(orderService.getSalesOrders());
    }

    @GetMapping("/purchase")
    public ApiResponse<List<Orders>> getPurchaseOrders() {
        return ApiResponse.success(orderService.getPurchaseOrders());
    }

    @PostMapping("/{id}/status")
    public ApiResponse<Orders> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request) {
        try {
            Orders order = orderService.updateOrderStatus(
                    id,
                    request.getStatus(),
                    request.getChangedBy(),
                    request.getRemarks()
            );
            return ApiResponse.success(order, "Order status updated successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/{id}/approve")
    public ApiResponse<Orders> approveOrder(
            @PathVariable Long id,
            @RequestBody ApproveOrderRequest request) {
        try {
            Orders order = orderService.approveOrder(id, request.getApprovedBy(), request.getRemarks());
            return ApiResponse.success(order, "Order approved successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // Request DTOs
    public static class CreateOrderRequest {
        private Orders order;
        private List<OrderItemRequest> items;

        public Orders getOrder() { return order; }
        public void setOrder(Orders order) { this.order = order; }
        public List<OrderItemRequest> getItems() { return items; }
        public void setItems(List<OrderItemRequest> items) { this.items = items; }
    }

    public static class UpdateStatusRequest {
        private OrderStatus status;
        private String changedBy;
        private String remarks;

        public OrderStatus getStatus() { return status; }
        public void setStatus(OrderStatus status) { this.status = status; }
        public String getChangedBy() { return changedBy; }
        public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class ApproveOrderRequest {
        private String approvedBy;
        private String remarks;

        public String getApprovedBy() { return approvedBy; }
        public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }
}
