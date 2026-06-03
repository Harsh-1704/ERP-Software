package com.erp.system.billing.controller;

import com.erp.system.billing.entity.Invoice;
import com.erp.system.billing.entity.InvoiceStatus;
import com.erp.system.billing.entity.Payment;
import com.erp.system.billing.service.BillingService;
import com.erp.system.billing.service.BillingService.InvoiceItemRequest;
import com.erp.system.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    // Invoice endpoints
    @PostMapping("/invoices")
    public ApiResponse<Invoice> createInvoice(@RequestBody CreateInvoiceRequest request) {
        return ApiResponse.success(billingService.createInvoice(request.getInvoice(), request.getItems()), "Invoice created successfully");
    }

    @GetMapping("/invoices/{id}")
    public ApiResponse<Invoice> getInvoice(@PathVariable Long id) {
        return billingService.getInvoiceById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Invoice not found"));
    }

    @GetMapping("/invoices/number/{number}")
    public ApiResponse<Invoice> getInvoiceByNumber(@PathVariable String number) {
        return billingService.getInvoiceByNumber(number)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Invoice not found"));
    }

    @GetMapping("/invoices")
    public ApiResponse<List<Invoice>> getAllInvoices() {
        return ApiResponse.success(billingService.getAllInvoices());
    }

    @GetMapping("/invoices/party/{partyId}")
    public ApiResponse<List<Invoice>> getInvoicesByParty(@PathVariable Long partyId) {
        return ApiResponse.success(billingService.getInvoicesByParty(partyId));
    }

    @GetMapping("/invoices/status/{status}")
    public ApiResponse<List<Invoice>> getInvoicesByStatus(@PathVariable InvoiceStatus status) {
        return ApiResponse.success(billingService.getInvoicesByStatus(status));
    }

    @GetMapping("/invoices/overdue")
    public ApiResponse<List<Invoice>> getOverdueInvoices() {
        return ApiResponse.success(billingService.getOverdueInvoices());
    }

    @PostMapping("/invoices/{id}/confirm")
    public ApiResponse<Invoice> confirmInvoice(@PathVariable Long id) {
        try {
            return ApiResponse.success(billingService.confirmInvoice(id), "Invoice confirmed");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    @PostMapping("/invoices/{id}/payment")
    public ApiResponse<Invoice> recordPayment(@PathVariable Long id, @RequestBody Payment payment) {
        try {
            return ApiResponse.success(billingService.recordPayment(id, payment), "Payment recorded successfully");
        } catch (RuntimeException e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    // Payment endpoints
    @GetMapping("/payments/{id}")
    public ApiResponse<Payment> getPayment(@PathVariable Long id) {
        return billingService.getPaymentById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Payment not found"));
    }

    @GetMapping("/payments/number/{number}")
    public ApiResponse<Payment> getPaymentByNumber(@PathVariable String number) {
        return billingService.getPaymentByNumber(number)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error("Payment not found"));
    }

    @GetMapping("/payments/invoice/{invoiceId}")
    public ApiResponse<List<Payment>> getPaymentsByInvoice(@PathVariable Long invoiceId) {
        return ApiResponse.success(billingService.getPaymentsByInvoice(invoiceId));
    }

    @GetMapping("/payments")
    public ApiResponse<List<Payment>> getAllPayments() {
        return ApiResponse.success(billingService.getAllPayments());
    }

    // Request DTO
    public static class CreateInvoiceRequest {
        private Invoice invoice;
        private List<InvoiceItemRequest> items;

        public Invoice getInvoice() { return invoice; }
        public void setInvoice(Invoice invoice) { this.invoice = invoice; }
        public List<InvoiceItemRequest> getItems() { return items; }
        public void setItems(List<InvoiceItemRequest> items) { this.items = items; }
    }
}