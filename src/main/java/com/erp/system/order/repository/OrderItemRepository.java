package com.erp.system.order.repository;

import com.erp.system.order.entity.OrderItem;
import com.erp.system.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Orders order);
    void deleteByOrder(Orders order);
}
