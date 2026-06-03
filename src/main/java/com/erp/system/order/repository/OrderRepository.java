package com.erp.system.order.repository;

import com.erp.system.auth.entity.Party;
import com.erp.system.order.entity.OrderStatus;
import com.erp.system.order.entity.OrderType;
import com.erp.system.order.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByOrderNumber(String orderNumber);
    List<Orders> findByParty(Party party);
    List<Orders> findByStatus(OrderStatus status);
    List<Orders> findByOrderType(OrderType type);

    @Query("SELECT o FROM Orders o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<Orders> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT o FROM Orders o WHERE o.party = :party AND o.status IN :statuses")
    List<Orders> findByPartyAndStatus(@Param("party") Party party, @Param("statuses") List<OrderStatus> statuses);

    boolean existsByOrderNumber(String orderNumber);
}
