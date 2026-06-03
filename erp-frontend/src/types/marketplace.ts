export type BulkOrderStatus = 'PENDING_CONFIRMATION' | 'CONFIRMED' | 'PAYMENT_PENDING' | 'PAYMENT_RECEIVED' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'PARTIALLY_DELIVERED' | 'CANCELLED' | 'DISPUTED';
export type InquiryStatus = 'PENDING' | 'QUOTED' | 'NEGOTIATING' | 'ORDERED' | 'CANCELLED' | 'EXPIRED';

export interface MarketplaceVendor {
  id: number;
  party: { id: number; partyName: string };
  companyName: string;
  businessType?: string;
  gstVerified: boolean;
  panVerified: boolean;
  rating: number;
  totalReviews: number;
  totalOrders: number;
  responseRate: number;
  responseTimeHours?: number;
  totalProducts: number;
  featuredProducts: number;
  subscriptionPlan: string;
  subscriptionStartDate?: string;
  subscriptionEndDate?: string;
  isActive: boolean;
  isVerified: boolean;
  isFeatured: boolean;
  websiteUrl?: string;
  contactEmail?: string;
  contactPhone?: string;
  createdAt?: string;
}

export interface MarketplaceListing {
  id: number;
  product: { id: number; name: string };
  vendor: { id: number; companyName: string };
  title: string;
  description?: string;
  shortDescription?: string;
  basePrice: number;
  minOrderQuantity: number;
  maxOrderQuantity?: number;
  isAvailable: boolean;
  availabilityStatus: string;
  leadTimeDays: number;
  moqNegotiable: boolean;
  shippingCharges: number;
  freeShippingThreshold?: number;
  isFeatured: boolean;
  isActive: boolean;
  viewsCount: number;
  inquiriesCount: number;
  createdAt?: string;
}

export interface ProductInquiry {
  id: number;
  listing?: { id: number; title: string };
  buyerParty?: { id: number; partyName: string };
  status: InquiryStatus;
  requestedQuantity?: number;
  targetPrice?: number;
  quotedPrice?: number;
  validUntil?: string;
  message?: string;
  notes?: string;
  createdAt?: string;
}

export interface BulkOrder {
  id: number;
  listing?: { id: number; title: string };
  buyer?: { id: number; partyName: string };
  vendor?: { id: number; companyName: string };
  status: BulkOrderStatus;
  quantity: number;
  unitPrice: number;
  totalAmount: number;
  shippingAddress?: string;
  remarks?: string;
  createdAt?: string;
}

export interface VendorReview {
  id: number;
  vendor: { id: number; companyName: string };
  reviewer: { id: number; partyName: string };
  rating: number;
  title?: string;
  comment?: string;
  createdAt?: string;
}
