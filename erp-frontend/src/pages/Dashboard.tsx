import { useState, useEffect } from 'react';
import { Row, Col, Card, Statistic, Typography, Table, Tag, message, Space } from 'antd';
import {
  AppstoreOutlined,
  ShoppingOutlined,
  FileTextOutlined,
  TeamOutlined,
  DollarOutlined,
  WarningOutlined,
  RiseOutlined,
  ShoppingCartOutlined,
} from '@ant-design/icons';
import { productService } from '../services/productService';
import { orderService } from '../services/orderService';
import { partyService } from '../services/partyService';
import { billingService } from '../services/billingService';
import { inventoryService } from '../services/inventoryService';
import type { Product } from '../types/product';
import type { Orders } from '../types/order';
import type { Party } from '../types/party';
import type { Invoice } from '../types/billing';
import type { Stock } from '../types/inventory';
import type { ColumnsType } from 'antd/es/table';
import dayjs from 'dayjs';

const { Title } = Typography;

const Dashboard = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [orders, setOrders] = useState<Orders[]>([]);
  const [parties, setParties] = useState<Party[]>([]);
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [stock, setStock] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchAll = async () => {
      try {
        setLoading(true);
        const [p, o, pa, inv, st] = await Promise.all([
          productService.getAllProducts().catch(() => []),
          orderService.getAllOrders().catch(() => []),
          partyService.getAllParties().catch(() => []),
          billingService.getAllInvoices().catch(() => []),
          inventoryService.getAllStock().catch(() => []),
        ]);
        setProducts(p);
        setOrders(o);
        setParties(pa);
        setInvoices(inv);
        setStock(st);
      } catch (error) {
        console.error('Dashboard fetch error:', error);
        message.error('Failed to load dashboard data');
      } finally {
        setLoading(false);
      }
    };
    fetchAll();
  }, []);

  // Computed metrics
  const activeOrders = orders.filter(o => !['CANCELLED', 'DELIVERED', 'FULFILLED', 'RETURNED'].includes(o.status));
  const totalRevenue = invoices.filter(i => i.invoiceType === 'SALES').reduce((sum, i) => sum + (i.paidAmount || 0), 0);
  const pendingPayments = invoices.reduce((sum, i) => sum + (i.balanceAmount || 0), 0);
  const lowStockItems = stock.filter(s => s.quantityAvailable <= (s.product?.minStockLevel || 0));

  // Recent orders for the activity table
  const recentOrders = [...orders].sort((a, b) => dayjs(b.orderDate).unix() - dayjs(a.orderDate).unix()).slice(0, 5);

  const recentOrderColumns: ColumnsType<Orders> = [
    {
      title: 'Order No',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      render: (text) => <strong>{text}</strong>
    },
    {
      title: 'Type',
      dataIndex: 'orderType',
      key: 'orderType',
      render: (type: string) => <Tag color={type === 'SALES' ? 'blue' : 'purple'}>{type}</Tag>
    },
    {
      title: 'Party',
      dataIndex: ['party', 'partyName'],
      key: 'partyName',
    },
    {
      title: 'Amount',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amt: number) => `$${(amt || 0).toFixed(2)}`
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        if (status === 'APPROVED' || status === 'CONFIRMED') color = 'cyan';
        if (status === 'PROCESSING') color = 'blue';
        if (status === 'DELIVERED' || status === 'FULFILLED') color = 'green';
        if (status === 'CANCELLED') color = 'red';
        return <Tag color={color}>{status}</Tag>;
      }
    },
  ];

  const lowStockColumns: ColumnsType<Stock> = [
    {
      title: 'Product',
      dataIndex: ['product', 'name'],
      key: 'productName',
    },
    {
      title: 'Warehouse',
      dataIndex: ['warehouse', 'name'],
      key: 'warehouse',
    },
    {
      title: 'Available',
      dataIndex: 'quantityAvailable',
      key: 'quantityAvailable',
      render: (qty: number) => <span style={{ color: qty <= 0 ? '#ff4d4f' : '#faad14', fontWeight: 'bold' }}>{qty}</span>
    },
    {
      title: 'Status',
      key: 'status',
      render: (_, record) => {
        if (record.quantityAvailable <= 0) return <Tag color="red">OUT OF STOCK</Tag>;
        return <Tag color="warning">LOW STOCK</Tag>;
      }
    }
  ];

  const cardStyle = {
    borderRadius: 8,
    boxShadow: '0 1px 2px -2px rgba(0,0,0,0.16), 0 3px 6px 0 rgba(0,0,0,0.12), 0 5px 12px 4px rgba(0,0,0,0.09)'
  };

  return (
    <div>
      <Title level={2} style={{ marginBottom: 24 }}>Dashboard</Title>

      {/* Top-level KPI Cards */}
      <Row gutter={[16, 16]}>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Total Products"
              value={products.length}
              prefix={<AppstoreOutlined style={{ color: '#1677ff' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Active Orders"
              value={activeOrders.length}
              prefix={<ShoppingCartOutlined style={{ color: '#faad14' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Total Revenue"
              value={totalRevenue}
              precision={2}
              prefix={<RiseOutlined style={{ color: '#52c41a' }} />}
              suffix="$"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Total Parties"
              value={parties.length}
              prefix={<TeamOutlined style={{ color: '#722ed1' }} />}
            />
          </Card>
        </Col>
      </Row>

      {/* Secondary KPI Cards */}
      <Row gutter={[16, 16]} style={{ marginTop: 16 }}>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Total Invoices"
              value={invoices.length}
              prefix={<FileTextOutlined style={{ color: '#13c2c2' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Pending Payments"
              value={pendingPayments}
              precision={2}
              prefix={<DollarOutlined style={{ color: '#ff4d4f' }} />}
              suffix="$"
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Low Stock Alerts"
              value={lowStockItems.length}
              prefix={<WarningOutlined style={{ color: lowStockItems.length > 0 ? '#ff4d4f' : '#52c41a' }} />}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={cardStyle} loading={loading}>
            <Statistic
              title="Inventory Items"
              value={stock.length}
              prefix={<ShoppingOutlined style={{ color: '#1677ff' }} />}
            />
          </Card>
        </Col>
      </Row>

      {/* Activity Tables */}
      <Row gutter={[16, 16]} style={{ marginTop: 24 }}>
        <Col xs={24} lg={14}>
          <Card title="Recent Orders" bordered={false} style={cardStyle}>
            <Table
              columns={recentOrderColumns}
              dataSource={recentOrders}
              rowKey="id"
              pagination={false}
              loading={loading}
              size="small"
            />
          </Card>
        </Col>
        <Col xs={24} lg={10}>
          <Card
            title={<Space><WarningOutlined style={{ color: '#ff4d4f' }} /> Low Stock Alerts</Space>}
            bordered={false}
            style={cardStyle}
          >
            <Table
              columns={lowStockColumns}
              dataSource={lowStockItems.slice(0, 5)}
              rowKey="id"
              pagination={false}
              loading={loading}
              size="small"
              locale={{ emptyText: '✅ All stock levels are healthy!' }}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default Dashboard;
