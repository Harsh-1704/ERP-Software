package com.erp.system.billing.repository;

import com.erp.system.auth.entity.Party;
import com.erp.system.billing.entity.Invoice;
import com.erp.system.billing.entity.InvoiceStatus;
import com.erp.system.billing.entity.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByParty(Party party);
    List<Invoice> findByStatus(InvoiceStatus status);
    List<Invoice> findByInvoiceType(InvoiceType type);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :startDate AND :endDate")
    List<Invoice> findByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT i FROM Invoice i WHERE i.party = :party AND i.status IN :statuses")
    List<Invoice> findByPartyAndStatus(@Param("party") Party party, @Param("statuses") List<InvoiceStatus> statuses);

    boolean existsByInvoiceNumber(String invoiceNumber);
}