# Endpoint Coverage Report

Generated: 2026-06-01

## Summary

- Backend controllers scanned: 9
- Total backend endpoints: 81
- Frontend API calls (services): 38
- Missing endpoints: 0
- Response shape mismatches: 0 (ApiResponse normalized and unwrapped in frontend)

## Backend Endpoints and Frontend Coverage

Legend:
- **Used** = called by a frontend service
- **Unused** = no matching frontend call found

### Auth

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/auth/login | POST | Used (`erp-frontend/src/services/authService.ts#login`) |

### Users

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/users/create | POST | Used (`erp-frontend/src/services/authService.ts#createUser`) |
| /api/users/all | GET | Used (`erp-frontend/src/services/authService.ts#getAllUsers`) |

### Roles

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/roles/create | POST | Used (`erp-frontend/src/services/authService.ts#createRole`) |

### Parties

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/parties | POST | Used (`erp-frontend/src/services/partyService.ts#createParty`) |
| /api/parties/all | GET | Used (`erp-frontend/src/services/partyService.ts#getAllParties`) |
| /api/parties/{id} | GET | Used (`erp-frontend/src/services/partyService.ts#getPartyById`) |
| /api/parties/{id} | PUT | Used (`erp-frontend/src/services/partyService.ts#updateParty`) |
| /api/parties/{id} | DELETE | Used (`erp-frontend/src/services/partyService.ts#deleteParty`) |

### Products

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/products | POST | Used (`erp-frontend/src/services/productService.ts#createProduct`) |
| /api/products/{id} | PUT | Used (`erp-frontend/src/services/productService.ts#updateProduct`) |
| /api/products/{id} | GET | Used (`erp-frontend/src/services/productService.ts#getProductById`) |
| /api/products | GET | Used (`erp-frontend/src/services/productService.ts#getAllProducts`) |
| /api/products/active | GET | Unused |
| /api/products/sku/{sku} | GET | Unused |
| /api/products/barcode/{barcode} | GET | Unused |
| /api/products/search | GET | Unused |
| /api/products/{id} | DELETE | Used (`erp-frontend/src/services/productService.ts#deleteProduct`) |
| /api/products/categories | POST | Unused |
| /api/products/categories | GET | Unused |
| /api/products/categories/root | GET | Unused |
| /api/products/units | POST | Unused |
| /api/products/units | GET | Unused |
| /api/products/{id}/price | POST | Unused |
| /api/products/{id}/price/current | GET | Unused |
| /api/products/{id}/price/history | GET | Unused |

### Inventory

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/inventory/warehouses | POST | Unused |
| /api/inventory/warehouses | GET | Used (`erp-frontend/src/services/inventoryService.ts#getAllWarehouses`) |
| /api/inventory/warehouses/active | GET | Unused |
| /api/inventory/warehouses/code/{code} | GET | Unused |
| /api/inventory/stock/product/{productId} | GET | Unused |
| /api/inventory/stock/warehouse/{warehouseId} | GET | Used (`erp-frontend/src/services/inventoryService.ts#getAllStock`) |
| /api/inventory/stock/{productId}/warehouse/{warehouseId} | GET | Unused |
| /api/inventory/stock/low-stock | GET | Unused |
| /api/inventory/stock/in | POST | Used (`erp-frontend/src/services/inventoryService.ts#stockIn`) |
| /api/inventory/stock/out | POST | Used (`erp-frontend/src/services/inventoryService.ts#stockOut`) |
| /api/inventory/stock/transfer | POST | Used (`erp-frontend/src/services/inventoryService.ts#transferStock`) |
| /api/inventory/movements/product/{productId}/warehouse/{warehouseId} | GET | Unused |

### Billing

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/billing/invoices | POST | Used (`erp-frontend/src/services/billingService.ts#createInvoice`) |
| /api/billing/invoices/{id} | GET | Used (`erp-frontend/src/services/billingService.ts#getInvoiceById`) |
| /api/billing/invoices/number/{number} | GET | Unused |
| /api/billing/invoices | GET | Used (`erp-frontend/src/services/billingService.ts#getAllInvoices`) |
| /api/billing/invoices/party/{partyId} | GET | Unused |
| /api/billing/invoices/status/{status} | GET | Unused |
| /api/billing/invoices/overdue | GET | Unused |
| /api/billing/invoices/{id}/confirm | POST | Used (`erp-frontend/src/services/billingService.ts#updateInvoiceStatus`) |
| /api/billing/invoices/{id}/payment | POST | Used (`erp-frontend/src/services/billingService.ts#recordPayment`) |
| /api/billing/payments/{id} | GET | Unused |
| /api/billing/payments/number/{number} | GET | Unused |
| /api/billing/payments/invoice/{invoiceId} | GET | Used (`erp-frontend/src/services/billingService.ts#getPaymentsByInvoice`) |
| /api/billing/payments | GET | Unused |

### Orders

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/orders | POST | Used (`erp-frontend/src/services/orderService.ts#createOrder`) |
| /api/orders/{id} | GET | Used (`erp-frontend/src/services/orderService.ts#getOrderById`) |
| /api/orders/number/{number} | GET | Unused |
| /api/orders | GET | Used (`erp-frontend/src/services/orderService.ts#getAllOrders`) |
| /api/orders/party/{partyId} | GET | Unused |
| /api/orders/status/{status} | GET | Unused |
| /api/orders/sales | GET | Unused |
| /api/orders/purchase | GET | Unused |
| /api/orders/{id}/status | POST | Used (`erp-frontend/src/services/orderService.ts#updateOrderStatus`) |
| /api/orders/{id}/approve | POST | Unused |

### Marketplace

| Endpoint | Method | Coverage |
| --- | --- | --- |
| /api/marketplace/vendors | POST | Unused |
| /api/marketplace/vendors | GET | Used (`erp-frontend/src/services/marketplaceService.ts#getAllVendors`) |
| /api/marketplace/vendors/{id} | GET | Used (`erp-frontend/src/services/marketplaceService.ts#getVendorById`) |
| /api/marketplace/vendors/active | GET | Unused |
| /api/marketplace/vendors/verified | GET | Unused |
| /api/marketplace/vendors/party/{partyId} | GET | Unused |
| /api/marketplace/listings | POST | Used (`erp-frontend/src/services/marketplaceService.ts#createListing`) |
| /api/marketplace/listings | GET | Used (`erp-frontend/src/services/marketplaceService.ts#getAllListings`) |
| /api/marketplace/listings/{id} | GET | Used (`erp-frontend/src/services/marketplaceService.ts#getListingById`) |
| /api/marketplace/listings/active | GET | Unused |
| /api/marketplace/listings/vendor/{vendorId} | GET | Unused |
| /api/marketplace/listings/search | GET | Unused |
| /api/marketplace/listings/available | GET | Unused |
| /api/marketplace/inquiries | POST | Used (`erp-frontend/src/services/marketplaceService.ts#createInquiry`) |
| /api/marketplace/inquiries | GET | Used (`erp-frontend/src/services/marketplaceService.ts#getAllInquiries`) |
| /api/marketplace/inquiries/status/{status} | GET | Unused |
| /api/marketplace/inquiries/{id}/respond | POST | Unused |
| /api/marketplace/orders | POST | Used (`erp-frontend/src/services/marketplaceService.ts#createBulkOrder`) |
| /api/marketplace/orders | GET | Used (`erp-frontend/src/services/marketplaceService.ts#getAllBulkOrders`) |
| /api/marketplace/orders/buyer/{buyerId} | GET | Unused |
| /api/marketplace/orders/{id}/status | POST | Unused |
| /api/marketplace/reviews | POST | Unused |
| /api/marketplace/vendors/{id}/reviews | GET | Unused |
| /api/marketplace/vendors/{id}/rating | GET | Unused |

