export interface Warehouse {
  id: number;
  name: string;
  code: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  country?: string;
  pincode?: string;
  contactPerson?: string;
  contactEmail?: string;
  contactPhone?: string;
  isActive: boolean;
  capacityUnits?: number;
  createdAt?: string;
  updatedAt?: string;
}

export interface Stock {
  id: number;
  product: {
    id: number;
    name: string;
    sku?: string;
    minStockLevel?: number;
  };
  warehouse: {
    id: number;
    name: string;
    code: string;
  };
  quantityOnHand: number;
  quantityReserved: number;
  quantityAvailable: number;
  reorderLevel: number;
  lastStockCheck?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface StockMovement {
  id: number;
  product: { id: number; name: string };
  warehouse: { id: number; name: string };
  movementType: string;
  quantity: number;
  unitPrice?: number;
  totalValue?: number;
  referenceType?: string;
  referenceId?: number;
  toWarehouse?: { id: number; name: string };
  createdAt?: string;
}

export interface StockInRequest {
  productId: number;
  warehouseId: number;
  quantity: number;
  unitPrice: number;
  referenceType?: string;
  referenceId?: number;
}

export interface StockOutRequest {
  productId: number;
  warehouseId: number;
  quantity: number;
  referenceType?: string;
  referenceId?: number;
}

export interface TransferRequest {
  fromWarehouseId: number;
  toWarehouseId: number;
  items: TransferItem[];
  remarks?: string;
}

export interface TransferItem {
  productId: number;
  quantity: number;
}
