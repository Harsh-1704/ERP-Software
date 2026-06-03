import api from '../config/api';
import type { Warehouse, Stock, StockInRequest, StockOutRequest, TransferRequest } from '../types/inventory';

export const inventoryService = {
  getAllWarehouses: async (): Promise<Warehouse[]> => {
    const response = await api.get<Warehouse[]>('/inventory/warehouses');
    return response.data;
  },

  getAllStock: async (): Promise<Stock[]> => {
    // Backend has no single "get all stock" endpoint.
    // Strategy: fetch all warehouses, then fetch stock for each warehouse, combine.
    const warehousesRes = await api.get<Warehouse[]>('/inventory/warehouses');
    const warehouses: Warehouse[] = warehousesRes.data;
    if (!warehouses || warehouses.length === 0) return [];

    const stockPromises = warehouses.map(w =>
      api.get<Stock[]>(`/inventory/stock/warehouse/${w.id}`).then(res => res.data).catch(() => [])
    );
    const stockArrays = await Promise.all(stockPromises);
    return stockArrays.flat();
  },

  stockIn: async (request: StockInRequest): Promise<void> => {
    await api.post('/inventory/stock/in', request);
  },

  stockOut: async (request: StockOutRequest): Promise<void> => {
    await api.post('/inventory/stock/out', request);
  },

  transferStock: async (request: TransferRequest): Promise<void> => {
    await api.post('/inventory/stock/transfer', request);
  }
};
