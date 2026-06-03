import { useState, useEffect } from 'react';
import { Table, Button, Typography, Space, message, Tag } from 'antd';
import { ImportOutlined, ExportOutlined, ReloadOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Stock } from '../../types/inventory';
import { inventoryService } from '../../services/inventoryService';
import StockMovementModal from './components/StockMovementModal';

const { Title } = Typography;

const InventoryDashboard = () => {
  const [stockList, setStockList] = useState<Stock[]>([]);
  const [loading, setLoading] = useState(true);
  
  const [modalOpen, setModalOpen] = useState(false);
  const [modalType, setModalType] = useState<'IN' | 'OUT'>('IN');

  const fetchStock = async () => {
    try {
      setLoading(true);
      const data = await inventoryService.getAllStock();
      setStockList(data);
    } catch (error) {
      console.error('Failed to fetch stock:', error);
      message.error('Failed to load inventory stock');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStock();
  }, []);

  const handleStockIn = () => {
    setModalType('IN');
    setModalOpen(true);
  };

  const handleStockOut = () => {
    setModalType('OUT');
    setModalOpen(true);
  };

  const columns: ColumnsType<Stock> = [
    {
      title: 'Product',
      dataIndex: ['product', 'name'],
      key: 'productName',
      sorter: (a, b) => a.product.name.localeCompare(b.product.name),
      render: (text, record) => (
        <div>
          <strong>{text}</strong>
          <br />
          <span style={{ fontSize: '0.85em', color: '#888' }}>SKU: {record.product.sku || 'N/A'}</span>
        </div>
      )
    },
    {
      title: 'Warehouse',
      dataIndex: ['warehouse', 'name'],
      key: 'warehouseName',
      render: (text, record) => `${record.warehouse.code} - ${text}`,
    },
    {
      title: 'Qty On Hand',
      dataIndex: 'quantityOnHand',
      key: 'quantityOnHand',
      render: (qty: number) => <strong style={{ fontSize: '1.1em' }}>{qty}</strong>,
    },
    {
      title: 'Qty Available',
      dataIndex: 'quantityAvailable',
      key: 'quantityAvailable',
    },
    {
      title: 'Status',
      key: 'status',
      render: (_, record) => {
        const minLevel = record.product.minStockLevel || 0;
        if (record.quantityAvailable <= 0) {
          return <Tag color="red">OUT OF STOCK</Tag>;
        }
        if (record.quantityAvailable <= minLevel) {
          return <Tag color="warning">LOW STOCK</Tag>;
        }
        return <Tag color="green">IN STOCK</Tag>;
      }
    }
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2} style={{ margin: 0 }}>Inventory Dashboard</Title>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={fetchStock}>
            Refresh
          </Button>
          <Button type="primary" icon={<ImportOutlined />} onClick={handleStockIn}>
            Stock In
          </Button>
          <Button danger type="primary" icon={<ExportOutlined />} onClick={handleStockOut}>
            Stock Out
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={stockList}
        rowKey="id"
        loading={loading}
      />

      <StockMovementModal
        open={modalOpen}
        type={modalType}
        onCancel={() => setModalOpen(false)}
        onSuccess={() => {
          setModalOpen(false);
          fetchStock();
        }}
      />
    </div>
  );
};

export default InventoryDashboard;
