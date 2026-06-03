import { useState, useEffect } from 'react';
import { Table, Button, Space, Typography, Tag, message } from 'antd';
import { PlusOutlined, ReloadOutlined, DollarOutlined, FileSearchOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Invoice } from '../../types/billing';
import { billingService } from '../../services/billingService';
import InvoiceFormModal from './components/InvoiceFormModal';
import PaymentModal from './components/PaymentModal';
import dayjs from 'dayjs';
import { useNavigate } from 'react-router-dom';

const { Title } = Typography;

const InvoiceList = () => {
  const [invoices, setInvoices] = useState<Invoice[]>([]);
  const [loading, setLoading] = useState(true);
  const [createModalOpen, setCreateModalOpen] = useState(false);
  const [paymentModalOpen, setPaymentModalOpen] = useState(false);
  const [selectedInvoiceId, setSelectedInvoiceId] = useState<number | null>(null);
  const [selectedBalance, setSelectedBalance] = useState(0);
  const navigate = useNavigate();

  const fetchInvoices = async () => {
    try {
      setLoading(true);
      const data = await billingService.getAllInvoices();
      setInvoices(data);
    } catch (error) {
      console.error('Failed to fetch invoices:', error);
      message.error('Failed to load invoices');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInvoices();
  }, []);

  const handleRecordPayment = (record: Invoice) => {
    setSelectedInvoiceId(record.id);
    setSelectedBalance(record.balanceAmount || 0);
    setPaymentModalOpen(true);
  };

  const columns: ColumnsType<Invoice> = [
    {
      title: 'Invoice No',
      dataIndex: 'invoiceNumber',
      key: 'invoiceNumber',
      sorter: (a, b) => a.invoiceNumber.localeCompare(b.invoiceNumber),
      render: (text) => <strong>{text}</strong>
    },
    {
      title: 'Type',
      dataIndex: 'invoiceType',
      key: 'invoiceType',
      render: (type: string) => {
        let color = 'blue';
        if (type === 'PURCHASE') color = 'purple';
        if (type === 'CREDIT_NOTE') color = 'cyan';
        if (type === 'DEBIT_NOTE') color = 'orange';
        return <Tag color={color}>{type.replace('_', ' ')}</Tag>;
      }
    },
    {
      title: 'Party',
      dataIndex: ['party', 'partyName'],
      key: 'partyName',
    },
    {
      title: 'Date',
      dataIndex: 'invoiceDate',
      key: 'invoiceDate',
      render: (date: string) => dayjs(date).format('MMM DD, YYYY')
    },
    {
      title: 'Total',
      dataIndex: 'totalAmount',
      key: 'totalAmount',
      render: (amt: number) => <strong>${(amt || 0).toFixed(2)}</strong>
    },
    {
      title: 'Paid',
      dataIndex: 'paidAmount',
      key: 'paidAmount',
      render: (amt: number) => <span style={{ color: '#52c41a' }}>${(amt || 0).toFixed(2)}</span>
    },
    {
      title: 'Balance',
      dataIndex: 'balanceAmount',
      key: 'balanceAmount',
      render: (amt: number) => <span style={{ color: amt > 0 ? '#ff4d4f' : '#52c41a', fontWeight: 'bold' }}>${(amt || 0).toFixed(2)}</span>
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        if (status === 'DRAFT') color = 'default';
        if (status === 'CONFIRMED' || status === 'SENT') color = 'blue';
        if (status === 'PARTIAL_PAID') color = 'orange';
        if (status === 'PAID') color = 'green';
        if (status === 'OVERDUE') color = 'red';
        if (status === 'CANCELLED') color = 'red';
        return <Tag color={color}>{status.replace('_', ' ')}</Tag>;
      },
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button
            type="link"
            icon={<FileSearchOutlined />}
            onClick={() => navigate(`/billing/${record.id}`)}
          >
            View
          </Button>
          {record.balanceAmount > 0 && record.status !== 'CANCELLED' && (
            <Button
              type="link"
              icon={<DollarOutlined />}
              onClick={() => handleRecordPayment(record)}
            >
              Pay
            </Button>
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2} style={{ margin: 0 }}>Billing & Invoices</Title>
        <Space>
          <Button icon={<ReloadOutlined />} onClick={fetchInvoices}>Refresh</Button>
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setCreateModalOpen(true)}>
            Create Invoice
          </Button>
        </Space>
      </div>

      <Table
        columns={columns}
        dataSource={invoices}
        rowKey="id"
        loading={loading}
      />

      <InvoiceFormModal
        open={createModalOpen}
        onCancel={() => setCreateModalOpen(false)}
        onSuccess={() => {
          setCreateModalOpen(false);
          fetchInvoices();
        }}
      />

      <PaymentModal
        open={paymentModalOpen}
        invoiceId={selectedInvoiceId}
        balanceAmount={selectedBalance}
        onCancel={() => setPaymentModalOpen(false)}
        onSuccess={() => {
          setPaymentModalOpen(false);
          fetchInvoices();
        }}
      />
    </div>
  );
};

export default InvoiceList;
