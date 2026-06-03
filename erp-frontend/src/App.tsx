import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import DashboardLayout from './layouts/DashboardLayout';
import Dashboard from './pages/Dashboard';
import Login from './pages/Login';
import Register from './pages/Register';
import ProtectedRoute from './components/ProtectedRoute';
import ProductList from './pages/products/ProductList';
import InventoryDashboard from './pages/inventory/InventoryDashboard';
import PartyList from './pages/parties/PartyList';
import OrderList from './pages/orders/OrderList';
import InvoiceList from './pages/billing/InvoiceList';

import InvoiceDetail from './pages/billing/InvoiceDetail';
import MarketplaceDashboard from './pages/marketplace/MarketplaceDashboard';

function App() {
  return (
    <ConfigProvider
      theme={{
        token: {
          colorPrimary: '#1677ff',
          borderRadius: 6,
        },
      }}
    >
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          
          <Route element={<ProtectedRoute />}>
            <Route element={<DashboardLayout />}>
              <Route path="/" element={<Navigate to="/dashboard" replace />} />
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/products" element={<ProductList />} />
              <Route path="/inventory" element={<InventoryDashboard />} />
              <Route path="/parties" element={<PartyList />} />
              <Route path="/orders" element={<OrderList />} />
              <Route path="/billing" element={<InvoiceList />} />
              <Route path="/billing/:id" element={<InvoiceDetail />} />
              <Route path="/marketplace" element={<MarketplaceDashboard />} />
            </Route>
          </Route>
          
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
