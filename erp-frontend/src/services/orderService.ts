import api from '../config/api';
import type { Orders, CreateOrderRequest, UpdateStatusRequest } from '../types/order';

export const orderService = {
  getAllOrders: async (): Promise<Orders[]> => {
    const response = await api.get<Orders[]>('/orders');
    return response.data;
  },

  getOrderById: async (id: number): Promise<Orders> => {
    const response = await api.get<Orders>(`/orders/${id}`);
    return response.data;
  },

  createOrder: async (request: CreateOrderRequest): Promise<Orders> => {
    const response = await api.post<Orders>('/orders', request);
    return response.data;
  },

  updateOrderStatus: async (id: number, request: UpdateStatusRequest): Promise<Orders> => {
    // Backend uses POST not PUT for status updates
    const response = await api.post<Orders>(`/orders/${id}/status`, request);
    return response.data;
  }
};
