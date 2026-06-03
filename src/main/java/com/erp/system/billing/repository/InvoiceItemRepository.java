package com.erp.system.billing.repository;

import com.erp.system.billing.entity.Invoice;
import com.erp.system.billing.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {
    List<InvoiceItem> findByInvoice(Invoice invoice);
    void deleteByInvoice(Invoice invoice);
}