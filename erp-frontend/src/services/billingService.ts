import api from '../config/api';
import type { Invoice, CreateInvoiceRequest, Payment } from '../types/billing';

export const billingService = {
  getAllInvoices: async (): Promise<Invoice[]> => {
    const response = await api.get<Invoice[]>('/billing/invoices');
    return response.data;
  },

  getInvoiceById: async (id: number): Promise<Invoice> => {
    const response = await api.get<Invoice>(`/billing/invoices/${id}`);
    return response.data;
  },

  createInvoice: async (request: CreateInvoiceRequest): Promise<Invoice> => {
    const response = await api.post<Invoice>('/billing/invoices', request);
    return response.data;
  },

  updateInvoiceStatus: async (id: number, _status: string): Promise<Invoice> => {
    const response = await api.post<Invoice>(`/billing/invoices/${id}/confirm`);
    return response.data;
  },

  // Backend endpoint is /invoices/{id}/payment (singular, not plural)
  recordPayment: async (invoiceId: number, payment: Partial<Payment>): Promise<Invoice> => {
    const response = await api.post<Invoice>(`/billing/invoices/${invoiceId}/payment`, payment);
    return response.data;
  },

  getPaymentsByInvoice: async (invoiceId: number): Promise<Payment[]> => {
    const response = await api.get<Payment[]>(`/billing/payments/invoice/${invoiceId}`);
    return response.data;
  }
};
