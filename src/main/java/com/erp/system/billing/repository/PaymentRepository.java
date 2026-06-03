package com.erp.system.billing.repository;

import com.erp.system.auth.entity.Party;
import com.erp.system.billing.entity.Invoice;
import com.erp.system.billing.entity.Payment;
import com.erp.system.billing.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentNumber(String paymentNumber);
    List<Payment> findByInvoice(Invoice invoice);
    List<Payment> findByParty(Party party);
    List<Payment> findByPaymentStatus(PaymentStatus status);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :startDate AND :endDate")
    List<Payment> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.invoice = :invoice AND p.paymentStatus = :status")
    Optional<Long> getTotalPaidAmount(@Param("invoice") Invoice invoice, @Param("status") PaymentStatus status);

    boolean existsByPaymentNumber(String paymentNumber);
}