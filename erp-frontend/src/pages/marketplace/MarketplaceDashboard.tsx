import { useState, useEffect } from 'react';
import { Table, Card, Row, Col, Typography, Tag, Statistic, message, Tabs, Rate, Button, Space } from 'antd';
import { ShopOutlined, TagOutlined, MessageOutlined, ShoppingCartOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { MarketplaceVendor, MarketplaceListing, ProductInquiry, BulkOrder } from '../../types/marketplace';
import { marketplaceService } from '../../services/marketplaceService';

const { Title } = Typography;

const MarketplaceDashboard = () => {
  const [vendors, setVendors] = useState<MarketplaceVendor[]>([]);
  const [listings, setListings] = useState<MarketplaceListing[]>([]);
  const [inquiries, setInquiries] = useState<ProductInquiry[]>([]);
  const [bulkOrders, setBulkOrders] = useState<BulkOrder[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchAll = async () => {
    try {
      setLoading(true);
      const [v, l, i, b] = await Promise.all([
        marketplaceService.getAllVendors().catch(() => []),
        marketplaceService.getAllListings().catch(() => []),
        marketplaceService.getAllInquiries().catch(() => []),
        marketplaceService.getAllBulkOrders().catch(() => []),
      ]);
      setVendors(v);
      setListings(l);
      setInquiries(i);
      setBulkOrders(b);
    } catch (error) {
      console.error('Failed to fetch marketplace data:', error);
      message.error('Failed to load marketplace data');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAll();
  }, []);

  const vendorColumns: ColumnsType<MarketplaceVendor> = [
    {
      title: 'Company',
      dataIndex: 'companyName',
      key: 'companyName',
      render: (text, record) => (
        <div>
          <strong>{text}</strong>
          {record.businessType && <div style={{ fontSize: '0.85em', color: '#888' }}>{record.businessType}</div>}
        </div>
      )
    },
    {
      title: 'Rating',
      dataIndex: 'rating',
      key: 'rating',
      render: (rating: number, record) => (
        <span><Rate disabled defaultValue={rating} style={{ fontSize: 14 }} /> ({record.totalReviews})</span>
      )
    },
    {
      title: 'Products',
      dataIndex: 'totalProducts',
      key: 'totalProducts',
    },
    {
      title: 'Orders',
      dataIndex: 'totalOrders',
      key: 'totalOrders',
    },
    {
      title: 'Response Rate',
      dataIndex: 'responseRate',
      key: 'responseRate',
      render: (rate: number) => `${(rate || 0).toFixed(0)}%`
    },
    {
      title: 'Verified',
      key: 'verified',
      render: (_, record) => (
        <Space>
          {record.gstVerified && <Tag color="green">GST ✓</Tag>}
          {record.panVerified && <Tag color="green">PAN ✓</Tag>}
          {record.isVerified && <Tag color="blue">VERIFIED</Tag>}
        </Space>
      )
    },
    {
      title: 'Status',
      dataIndex: 'isActive',
      key: 'isActive',
      render: (active: boolean) => <Tag color={active ? 'green' : 'red'}>{active ? 'ACTIVE' : 'INACTIVE'}</Tag>
    },
  ];

  const listingColumns: ColumnsType<MarketplaceListing> = [
    {
      title: 'Title',
      dataIndex: 'title',
      key: 'title',
      render: (text, record) => (
        <div>
          <strong>{text}</strong>
          {record.shortDescription && <div style={{ fontSize: '0.85em', color: '#888' }}>{record.shortDescription}</div>}
        </div>
      )
    },
    {
      title: 'Vendor',
      dataIndex: ['vendor', 'companyName'],
      key: 'vendor',
    },
    {
      title: 'Base Price',
      dataIndex: 'basePrice',
      key: 'basePrice',
      render: (price: number) => `$${(price || 0).toFixed(2)}`
    },
    {
      title: 'MOQ',
      dataIndex: 'minOrderQuantity',
      key: 'minOrderQuantity',
      render: (moq: number, record) => (
        <span>{moq} {record.moqNegotiable && <Tag color="cyan">Negotiable</Tag>}</span>
      )
    },
    {
      title: 'Lead Time',
      dataIndex: 'leadTimeDays',
      key: 'leadTimeDays',
      render: (days: number) => `${days} days`
    },
    {
      title: 'Views',
      dataIndex: 'viewsCount',
      key: 'viewsCount',
    },
    {
      title: 'Status',
      key: 'status',
      render: (_, record) => (
        <Space>
          {record.isFeatured && <Tag color="gold">FEATURED</Tag>}
          <Tag color={record.isAvailable ? 'green' : 'red'}>{record.isAvailable ? 'Available' : 'Unavailable'}</Tag>
        </Space>
      )
    },
  ];

  const inquiryColumns: ColumnsType<ProductInquiry> = [
    {
      title: 'Listing',
      dataIndex: ['listing', 'title'],
      key: 'listing',
    },
    {
      title: 'Buyer',
      dataIndex: ['buyerParty', 'partyName'],
      key: 'buyer',
    },
    {
      title: 'Qty',
      dataIndex: 'requestedQuantity',
      key: 'requestedQuantity',
    },
    {
      title: 'Target Price',
      dataIndex: 'targetPrice',
      key: 'targetPrice',
      render: (price: number) => price ? `$${price.toFixed(2)}` : '-'
    },
    {
      title: 'Quoted Price',
      dataIndex: 'quotedPrice',
      key: 'quotedPrice',
      render: (price: number) => price ? `$${price.toFixed(2)}` : '-'
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        if (status === 'PENDING') color = 'orange';
        if (status === 'QUOTED') color = 'blue';
        if (status === 'NEGOTIATING') color = 'cyan';
        if (status === 'ORDERED') color = 'green';
        if (status === 'CANCELLED' || status === 'EXPIRED') color = 'red';
        return <Tag color={color}>{status}</Tag>;
      }
    },
  ];

  const bulkOrderColumns: ColumnsType<BulkOrder> = [
    {
      title: 'Listing',
      dataIndex: ['listing', 'title'],
      key: 'listing',
    },
    {
      title: 'Buyer',
      dataIndex: ['buyer', 'partyName'],
      key: 'buyer',
    },
    {
      title: 'Vendor',
      dataIndex: ['vendor', 'companyName'],
      key: 'vendor',
    },
    {
      title: 'Qty',
      dataIndex: 'quantity',
      key: 'quantity',
    },
    {
      title: 'Total',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amt: number) => <strong>${(amt || 0).toFixed(2)}</strong>
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        if (status === 'CONFIRMED' || status === 'PAYMENT_RECEIVED') color = 'blue';
        if (status === 'PROCESSING' || status === 'SHIPPED') color = 'cyan';
        if (status === 'DELIVERED') color = 'green';
        if (status === 'CANCELLED' || status === 'DISPUTED') color = 'red';
        return <Tag color={color}>{status.replace(/_/g, ' ')}</Tag>;
      }
    },
  ];

  const tabItems = [
    {
      key: 'vendors',
      label: <span><ShopOutlined /> Vendors ({vendors.length})</span>,
      children: <Table columns={vendorColumns} dataSource={vendors} rowKey="id" loading={loading} />,
    },
    {
      key: 'listings',
      label: <span><TagOutlined /> Listings ({listings.length})</span>,
      children: <Table columns={listingColumns} dataSource={listings} rowKey="id" loading={loading} />,
    },
    {
      key: 'inquiries',
      label: <span><MessageOutlined /> Inquiries ({inquiries.length})</span>,
      children: <Table columns={inquiryColumns} dataSource={inquiries} rowKey="id" loading={loading} />,
    },
    {
      key: 'bulk-orders',
      label: <span><ShoppingCartOutlined /> Bulk Orders ({bulkOrders.length})</span>,
      children: <Table columns={bulkOrderColumns} dataSource={bulkOrders} rowKey="id" loading={loading} />,
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2} style={{ margin: 0 }}>B2B Marketplace</Title>
        <Button icon={<ReloadOutlined />} onClick={fetchAll}>Refresh</Button>
      </div>

      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={{ borderRadius: 8, boxShadow: '0 1px 3px rgba(0,0,0,0.12)' }}>
            <Statistic title="Active Vendors" value={vendors.filter(v => v.isActive).length} prefix={<ShopOutlined style={{ color: '#1677ff' }} />} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={{ borderRadius: 8, boxShadow: '0 1px 3px rgba(0,0,0,0.12)' }}>
            <Statistic title="Active Listings" value={listings.filter(l => l.isActive).length} prefix={<TagOutlined style={{ color: '#52c41a' }} />} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={{ borderRadius: 8, boxShadow: '0 1px 3px rgba(0,0,0,0.12)' }}>
            <Statistic title="Open Inquiries" value={inquiries.filter(i => i.status === 'PENDING' || i.status === 'QUOTED').length} prefix={<MessageOutlined style={{ color: '#faad14' }} />} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={6}>
          <Card bordered={false} style={{ borderRadius: 8, boxShadow: '0 1px 3px rgba(0,0,0,0.12)' }}>
            <Statistic title="Active Bulk Orders" value={bulkOrders.filter(b => !['DELIVERED', 'CANCELLED'].includes(b.status)).length} prefix={<ShoppingCartOutlined style={{ color: '#722ed1' }} />} />
          </Card>
        </Col>
      </Row>

      <Tabs items={tabItems} defaultActiveKey="vendors" />
    </div>
  );
};

export default MarketplaceDashboard;
