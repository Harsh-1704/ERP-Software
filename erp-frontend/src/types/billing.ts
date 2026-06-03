export type InvoiceType = 'SALES' | 'PURCHASE' | 'CREDIT_NOTE' | 'DEBIT_NOTE';
export type InvoiceStatus = 'DRAFT' | 'CONFIRMED' | 'SENT' | 'PARTIAL_PAID' | 'PAID' | 'OVERDUE' | 'CANCELLED';
export type PaymentMode = 'CASH' | 'BANK_TRANSFER' | 'CHEQUE' | 'UPI' | 'CREDIT_CARD';
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'FAILED';

export interface Invoice {
  id: number;
  invoiceNumber: string;
  invoiceType: InvoiceType;
  status: InvoiceStatus;
  party: { id: number; partyName: string };
  billingAddress?: { id: number };
  shippingAddress?: { id: number };
  subtotal: number;
  totalTax: number;
  taxBreakup?: string;
  discountAmount: number;
  discountPercentage: number;
  shippingCharges: number;
  otherCharges: number;
  roundOff: number;
  totalAmount: number;
  paidAmount: number;
  balanceAmount: number;
  invoiceDate: string;
  dueDate?: string;
  paymentTermsDays: number;
  remarks?: string;
  termsAndConditions?: string;
  createdBy?: string;
  confirmedAt?: string;
  cancelledAt?: string;
  cancelledReason?: string;
  items: InvoiceItem[];
  payments?: Payment[];
  createdAt?: string;
  updatedAt?: string;
}

export interface InvoiceItem {
  id?: number;
  itemNumber?: number;
  product?: { id: number; name: string };
  productName: string;
  sku?: string;
  hsnCode?: string;
  quantity: number;
  unit?: { id: number; name: string };
  unitPrice: number;
  discountPercentage: number;
  discountAmount: number;
  taxRate: number;
  taxAmount: number;
  subtotal: number;
  totalAmount: number;
  remarks?: string;
}

export interface InvoiceItemRequest {
  productId?: number;
  productName?: string;
  quantity: number;
  unitPrice: number;
  discountPercentage?: number;
  discountAmount?: number;
  taxRate?: number;
}

export interface CreateInvoiceRequest {
  invoice: Partial<Invoice>;
  items: InvoiceItemRequest[];
}

export interface Payment {
  id: number;
  paymentNumber?: string;
  invoice?: { id: number; invoiceNumber: string };
  party?: { id: number; partyName: string };
  amount: number;
  paymentMode?: PaymentMode;
  paymentStatus?: PaymentStatus;
  paymentDate?: string;
  referenceNumber?: string;
  remarks?: string;
}
