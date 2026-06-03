import api from '../config/api';
import type { MarketplaceVendor, MarketplaceListing, ProductInquiry, BulkOrder } from '../types/marketplace';

export const marketplaceService = {
  // Vendors
  getAllVendors: async (): Promise<MarketplaceVendor[]> => {
    const response = await api.get<MarketplaceVendor[]>('/marketplace/vendors');
    return response.data;
  },

  getVendorById: async (id: number): Promise<MarketplaceVendor> => {
    const response = await api.get<MarketplaceVendor>(`/marketplace/vendors/${id}`);
    return response.data;
  },

  // Listings
  getAllListings: async (): Promise<MarketplaceListing[]> => {
    const response = await api.get<MarketplaceListing[]>('/marketplace/listings');
    return response.data;
  },

  getListingById: async (id: number): Promise<MarketplaceListing> => {
    const response = await api.get<MarketplaceListing>(`/marketplace/listings/${id}`);
    return response.data;
  },

  createListing: async (listing: Partial<MarketplaceListing>): Promise<MarketplaceListing> => {
    const response = await api.post<MarketplaceListing>('/marketplace/listings', listing);
    return response.data;
  },

  // Inquiries
  getAllInquiries: async (): Promise<ProductInquiry[]> => {
    const response = await api.get<ProductInquiry[]>('/marketplace/inquiries');
    return response.data;
  },

  createInquiry: async (inquiry: Partial<ProductInquiry>): Promise<ProductInquiry> => {
    const response = await api.post<ProductInquiry>('/marketplace/inquiries', inquiry);
    return response.data;
  },

  // Bulk Orders — backend uses /marketplace/orders (NOT /marketplace/bulk-orders)
  getAllBulkOrders: async (): Promise<BulkOrder[]> => {
    const response = await api.get<BulkOrder[]>('/marketplace/orders');
    return response.data;
  },

  createBulkOrder: async (order: Partial<BulkOrder>): Promise<BulkOrder> => {
    const response = await api.post<BulkOrder>('/marketplace/orders', order);
    return response.data;
  }
};
