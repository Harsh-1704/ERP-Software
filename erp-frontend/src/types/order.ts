export type OrderType = 'SALES' | 'PURCHASE';
export type OrderStatus = 'DRAFT' | 'PENDING_APPROVAL' | 'APPROVED' | 'CONFIRMED' | 'PROCESSING' | 'PARTIALLY_FULFILLED' | 'FULFILLED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED' | 'RETURNED';

export interface Orders {
  id: number;
  orderNumber: string;
  orderType: OrderType;
  status: OrderStatus;
  party: { id: number; partyName: string };
  billingAddress?: { id: number };
  shippingAddress?: { id: number };
  contactPerson?: { id: number };
  salesPerson?: { id: number; username: string };
  subtotal: number;
  totalTax: number;
  discountAmount: number;
  discountPercentage: number;
  shippingCharges: number;
  otherCharges: number;
  roundOff: number;
  totalAmount: number;
  orderDate: string;
  expectedDeliveryDate?: string;
  actualDeliveryDate?: string;
  paymentTermsDays: number;
  shippingMethod?: string;
  trackingNumber?: string;
  courierName?: string;
  remarks?: string;
  termsAndConditions?: string;
  internalNotes?: string;
  approvedBy?: string;
  approvedAt?: string;
  createdBy?: string;
  confirmedAt?: string;
  cancelledAt?: string;
  cancelledReason?: string;
  items: OrderItem[];
  statusHistory?: OrderStatusHistory[];
  createdAt?: string;
  updatedAt?: string;
}

export interface OrderItem {
  id?: number;
  itemNumber?: number;
  product?: { id: number; name: string };
  productName: string;
  sku?: string;
  hsnCode?: string;
  orderedQuantity: number;
  confirmedQuantity?: number;
  shippedQuantity?: number;
  deliveredQuantity?: number;
  unit?: { id: number; name: string };
  unitPrice: number;
  discountPercentage: number;
  discountAmount: number;
  taxRate: number;
  taxAmount: number;
  subtotal: number;
  totalAmount: number;
  expectedDeliveryDate?: string;
  remarks?: string;
}

export interface OrderItemRequest {
  productId?: number;
  productName?: string;
  quantity: number;
  unitPrice: number;
  discountPercentage?: number;
  discountAmount?: number;
  taxRate?: number;
}

export interface CreateOrderRequest {
  order: Partial<Orders>;
  items: OrderItemRequest[];
}

export interface OrderStatusHistory {
  id: number;
  fromStatus?: OrderStatus;
  toStatus: OrderStatus;
  changedBy?: string;
  remarks?: string;
  createdAt?: string;
}

export interface UpdateStatusRequest {
  status: OrderStatus;
  changedBy: string;
  remarks?: string;
}
