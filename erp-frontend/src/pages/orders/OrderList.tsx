import { useState, useEffect } from 'react';
import { Table, Button, Space, Typography, Tag, message } from 'antd';
import { PlusOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Orders } from '../../types/order';
import { orderService } from '../../services/orderService';
import OrderFormModal from './components/OrderFormModal';
import dayjs from 'dayjs';

const { Title } = Typography;

const OrderList = () => {
  const [orders, setOrders] = useState<Orders[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);

  const fetchOrders = async () => {
    try {
      setLoading(true);
      const data = await orderService.getAllOrders();
      setOrders(data);
    } catch (error) {
      console.error('Failed to fetch orders:', error);
      message.error('Failed to load orders');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleCreate = () => {
    setModalOpen(true);
  };

  const columns: ColumnsType<Orders> = [
    {
      title: 'Order No',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      sorter: (a, b) => a.orderNumber.localeCompare(b.orderNumber),
      render: (text) => <strong>{text}</strong>
    },
    {
      title: 'Type',
      dataIndex: 'orderType',
      key: 'orderType',
      render: (type: string) => (
        <Tag color={type === 'SALES' ? 'blue' : 'purple'}>{type}</Tag>
      )
    },
    {
      title: 'Party',
      dataIndex: ['party', 'partyName'],
      key: 'partyName',
    },
    {
      title: 'Date',
      dataIndex: 'orderDate',
      key: 'orderDate',
      render: (date: string) => dayjs(date).format('MMM DD, YYYY')
    },
    {
      title: 'Total Amount',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amount: number) => `$${(amount || 0).toFixed(2)}`
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        if (status === 'APPROVED' || status === 'CONFIRMED') color = 'cyan';
        if (status === 'PROCESSING' || status === 'PARTIALLY_FULFILLED') color = 'blue';
        if (status === 'SHIPPED') color = 'geekblue';
        if (status === 'DELIVERED' || status === 'FULFILLED') color = 'green';
        if (status === 'CANCELLED' || status === 'RETURNED') color = 'red';
        
        return <Tag color={color}>{status}</Tag>;
      },
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button type="link" onClick={() => message.info(`View details for ${record.orderNumber}`)}>
            View Details
          </Button>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2} style={{ margin: 0 }}>Orders (Sales & Purchases)</Title>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={fetchOrders}>
            Refresh
          </Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={handleCreate}>
            Create Order
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={orders}
        rowKey="id"
        loading={loading}
        rowClassName={(record) => record.status === 'CANCELLED' ? 'cancelled-row' : ''}
      />

      <OrderFormModal
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onSuccess={() => {
          setModalOpen(false);
          fetchOrders();
        }}
      />
    </div>
  );
};

export default OrderList;
