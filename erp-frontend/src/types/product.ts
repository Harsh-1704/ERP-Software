export interface Product {
  id: number;
  name: string;
  description?: string;
  category?: ProductCategory;
  unit?: Unit;
  sku?: string;
  barcode?: string;
  active: boolean;
  manufacturer?: string;
  hsnCode?: string;
  taxRate: number;
  price?: number;
  minStockLevel: number;
  maxStockLevel?: number;
  costPrice?: number;
  prices?: ProductPrice[];
  createdAt?: string;
  updatedAt?: string;
}

export interface ProductCategory {
  id: number;
  name: string;
  description?: string;
  parentCategory?: ProductCategory;
}

export interface Unit {
  id: number;
  name: string;
  symbol?: string;
}

export interface ProductPrice {
  id: number;
  product?: Product;
  price: number;
  effectiveFrom?: string;
  effectiveTo?: string;
}
