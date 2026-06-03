package com.erp.system.billing.service;

import com.erp.system.auth.entity.Party;
import com.erp.system.auth.repository.PartyRepository;
import com.erp.system.billing.entity.*;
import com.erp.system.billing.repository.InvoiceRepository;
import com.erp.system.billing.repository.PaymentRepository;
import com.erp.system.product.entity.Product;
import com.erp.system.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final PartyRepository partyRepository;
    private final ProductRepository productRepository;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");

    // Invoice Management
    @Transactional
    public Invoice createInvoice(Invoice invoice, List<InvoiceItemRequest> items) {
        // Validate party
        if (invoice.getParty() != null && invoice.getParty().getId() != null) {
            Party party = partyRepository.findById(invoice.getParty().getId())
                    .orElseThrow(() -> new RuntimeException("Party not found"));
            invoice.setParty(party);
        }

        // Generate invoice number if not provided
        if (invoice.getInvoiceNumber() == null) {
            invoice.setInvoiceNumber(generateInvoiceNumber(invoice.getInvoiceType()));
        }

        // Set default status
        if (invoice.getStatus() == null) {
            invoice.setStatus(InvoiceStatus.DRAFT);
        }

        // Set invoice date if not provided
        if (invoice.getInvoiceDate() == null) {
            invoice.setInvoiceDate(LocalDate.now());
        }

        // Calculate due date
        if (invoice.getDueDate() == null && invoice.getPaymentTermsDays() != null) {
            invoice.setDueDate(invoice.getInvoiceDate().plusDays(invoice.getPaymentTermsDays()));
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Add items
        if (items != null && !items.isEmpty()) {
            int itemNumber = 1;
            for (InvoiceItemRequest itemRequest : items) {
                InvoiceItem item = new InvoiceItem();
                item.setInvoice(savedInvoice);
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

                item.setQuantity(itemRequest.getQuantity());
                item.setUnitPrice(itemRequest.getUnitPrice());
                item.setDiscountPercentage(itemRequest.getDiscountPercentage());
                item.setDiscountAmount(itemRequest.getDiscountAmount());
                item.setTaxRate(itemRequest.getTaxRate());

                savedInvoice.getItems().add(item);
            }
        }

        return invoiceRepository.save(savedInvoice);
    }

    private String generateInvoiceNumber(InvoiceType type) {
        String prefix = switch (type) {
            case SALES -> "SINV";
            case PURCHASE -> "PINV";
            case CREDIT_NOTE -> "CN";
            case DEBIT_NOTE -> "DN";
        };
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long count = invoiceRepository.count() + 1;
        return String.format("%s-%s-%06d", prefix, dateStr, count);
    }

    private String generatePaymentNumber(InvoiceType type) {
        String prefix = "PAY";
        String dateStr = LocalDate.now().format(DATE_FORMAT);
        long count = paymentRepository.count() + 1;
        return String.format("%s-%s-%06d", prefix, dateStr, count);
    }

    @Transactional
    public Invoice confirmInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (invoice.getStatus() != InvoiceStatus.DRAFT) {
            throw new RuntimeException("Only draft invoices can be confirmed");
        }

        invoice.setStatus(InvoiceStatus.CONFIRMED);
        invoice.setConfirmedAt(LocalDateTime.now());

        return invoiceRepository.save(invoice);
    }

    @Transactional
    public Invoice recordPayment(Long invoiceId, Payment payment) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        if (payment.getPaymentNumber() == null) {
            payment.setPaymentNumber(generatePaymentNumber(invoice.getInvoiceType()));
        }

        payment.setInvoice(invoice);
        payment.setParty(invoice.getParty());
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentDate(LocalDateTime.now());

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice paid amount
        invoice.setPaidAmount(invoice.getPaidAmount().add(payment.getAmount()));

        // Update invoice status
        if (invoice.getPaidAmount().compareTo(invoice.getTotalAmount()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIAL_PAID);
        }

        invoiceRepository.save(invoice);

        return invoice;
    }

    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findById(id);
    }

    public Optional<Invoice> getInvoiceByNumber(String number) {
        return invoiceRepository.findByInvoiceNumber(number);
    }

    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> getInvoicesByParty(Long partyId) {
        Party party = partyRepository.findById(partyId)
                .orElseThrow(() -> new RuntimeException("Party not found"));
        return invoiceRepository.findByParty(party);
    }

    public List<Invoice> getInvoicesByStatus(InvoiceStatus status) {
        return invoiceRepository.findByStatus(status);
    }

    public List<Invoice> getOverdueInvoices() {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        LocalDate today = LocalDate.now();

        return allInvoices.stream()
                .filter(inv -> InvoiceStatus.CONFIRMED.equals(inv.getStatus()) ||
                        InvoiceStatus.PARTIAL_PAID.equals(inv.getStatus()))
                .filter(inv -> inv.getDueDate() != null && inv.getDueDate().isBefore(today))
                .filter(inv -> inv.getBalanceAmount().compareTo(BigDecimal.ZERO) > 0)
                .toList();
    }

    // Payment Management
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByNumber(String number) {
        return paymentRepository.findByPaymentNumber(number);
    }

    public List<Payment> getPaymentsByInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
        return paymentRepository.findByInvoice(invoice);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // DTO for invoice items
    public static class InvoiceItemRequest {
        private Long productId;
        private String productName;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
        private BigDecimal discountPercentage;
        private BigDecimal discountAmount;
        private BigDecimal taxRate;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public BigDecimal getQuantity() { return quantity; }
        public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
        public BigDecimal getDiscountPercentage() { return discountPercentage; }
        public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }
        public BigDecimal getTaxRate() { return taxRate; }
        public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    }
}