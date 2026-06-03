package com.erp.system.order.service;

import com.erp.system.auth.entity.Party;
import com.erp.system.auth.repository.PartyRepository;
import com.erp.system.order.entity.OrderStatus;
import com.erp.system.order.entity.OrderStatusHistory;
import com.erp.system.order.entity.OrderType;
import com.erp.system.order.entity.Orders;
import com.erp.system.order.repository.OrderRepository;
import com.erp.system.product.entity.Product;
import com.erp.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final PartyRepository partyRepository;
    private final ProductRepository productRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public Orders createOrder(Orders order, List<OrderItemRequest> items) {
        // Validate party
        if (order.getParty() != null && order.getParty().getId() != null) {
            Party party = partyRepository.findById(order.getParty().getId())
                    .orElseThrow(() -> new RuntimeException("Party not found"));
            order.setParty(party);
        }

        // Generate order number if not provided
        if (order.getOrderNumber() == null) {
            order.setOrderNumber(generateOrderNumber(order.getOrderType()));
        }

        // Set default status
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.DRAFT);
        }

        // Set order date if not provided
        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDate.now());
        }

        Orders savedOrder = orderRepository.save(order);

        // Add items
        if (items != null && !items.isEmpty()) {
            int itemNumber = 1;
            for (OrderItemRequest itemRequest : items) {
                com.erp.system.order.entity.OrderItem item = new com.erp.system.order.entity.OrderItem();
                item.setOrder(savedOrder);
                item.setItemNumber(itemNumber++);

                if (itemRequest.getProductId() != null) {
                    Product product = productRepository.findById(itemRequest.getProductId())
                            .orElseThrow(() -> new RuntimeException("Product not found"));
                    item.setProduct(product);
                    item.setProductName(product.getName());
                    item.setSku(product.getSku());
                    item.setHsnCode(product.getHsnCode());
                } else {
                    item.setProductName(itemRequest.getProductName());
                }

                item.setOrderedQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setDiscountPercentage(itemRequest.getDiscountPercentage());
                item.setDiscountAmount(itemRequest.getDiscountAmount());
                item.setTaxRate(itemRequest.getTaxRate());

                savedOrder.getItems().add(item);
            }
        }

        // Add initial status history
        OrderStatusHistory statusHistory = new OrderStatusHistory();
        statusHistory.setOrder(savedOrder);
        statusHistory.setToStatus(order.getStatus());
        statusHistory.setChangedBy(order.getCreatedBy());
        savedOrder.getStatusHistory().add(statusHistory);

        return orderRepository.save(savedOrder);
    }

    private String generateOrderNumber(OrderType type) {
        String prefix = switch (type) {
            case SALES -> "SO";
            case PURCHASE -> "PO";
        };
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long count = orderRepository.count() + 1;
        return String.format("%s-%s-%06d", prefix, dateStr, count);
    }

    @Transactional
    public Orders updateOrderStatus(Long orderId, OrderStatus newStatus, String changedBy, String remarks) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);

        // Update status-specific dates
        if (newStatus == OrderStatus.CONFIRMED && oldStatus != OrderStatus.CONFIRMED) {
            order.setConfirmedAt(LocalDateTime.now());
        } else if (newStatus == OrderStatus.DELIVERED) {
            order.setActualDeliveryDate(LocalDate.now());
        } else if (newStatus == OrderStatus.CANCELLED) {
            order.setCancelledAt(LocalDateTime.now());
        }

        // Add status history
        OrderStatusHistory history = new OrderStatusHistory();
        history.setOrder(order);
        history.setFromStatus(oldStatus);
        history.setToStatus(newStatus);
        history.setChangedBy(changedBy);
        history.setRemarks(remarks);
        order.getStatusHistory().add(history);

        return orderRepository.save(order);
    }

    @Transactional
    public Orders approveOrder(Long orderId, String approvedBy, String remarks) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING_APPROVAL) {
            throw new RuntimeException("Only orders pending approval can be approved");
        }

        order.setApprovedBy(approvedBy);
        order.setApprovedAt(LocalDateTime.now());
        order.setApprovalRemarks(remarks);

        return updateOrderStatus(orderId, OrderStatus.APPROVED, approvedBy, remarks);
    }

    public Optional<Orders> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Orders> getOrderByNumber(String number) {
        return orderRepository.findByOrderNumber(number);
    }

    public List<Orders> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<Orders> getOrdersByParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new RuntimeException("Party not found"));
        return orderRepository.findByParty(party);
    }

    public List<Orders> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    public List<Orders> getSalesOrders() {
        return orderRepository.findByOrderType(OrderType.SALES);
    }

    public List<Orders> getPurchaseOrders() {
        return orderRepository.findByOrderType(OrderType.PURCHASE);
    }

    // DTO for order items
    public static class OrderItemRequest {
        private Long productId;
        private String productName;
        private java.math.BigDecimal quantity;
        private java.math.BigDecimal unitPrice;
        private java.math.BigDecimal discountPercentage;
        private java.math.BigDecimal discountAmount;
        private java.math.BigDecimal taxRate;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public java.math.BigDecimal getQuantity() { return quantity; }
        public void setQuantity(java.math.BigDecimal quantity) { this.quantity = quantity; }
        public java.math.BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(java.math.BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public java.math.BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(java.math.BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
        public java.math.BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(java.math.BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public java.math.BigDecimal getTaxRate() { return taxRate; }
        public void setTaxRate(java.math.BigDecimal taxRate) { this.taxRate = taxRate; }
    }
}
