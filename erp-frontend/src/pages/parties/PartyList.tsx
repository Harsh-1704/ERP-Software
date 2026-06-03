import { useState, useEffect } from 'react';
import { Table, Button, Space, Typography, Tag, message, Popconfirm } from 'antd';
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons';
import type { ColumnsType } from 'antd/es/table';
import type { Party } from '../../types/party';
import { partyService } from '../../services/partyService';
import PartyFormModal from './components/PartyFormModal';

const { Title } = Typography;

const PartyList = () => {
  const [parties, setParties] = useState<Party[]>([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editingParty, setEditingParty] = useState<Party | null>(null);

  const fetchParties = async () => {
    try {
      setLoading(true);
      const data = await partyService.getAllParties();
      setParties(data);
    } catch (error) {
      console.error('Failed to fetch parties:', error);
      message.error('Failed to load parties');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchParties();
  }, []);

  const handleAdd = () => {
    setEditingParty(null);
    setModalOpen(true);
  };

  const handleEdit = (record: Party) => {
    setEditingParty(record);
    setModalOpen(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await partyService.deleteParty(id);
      message.success('Party deleted successfully');
      fetchParties();
    } catch (error) {
      console.error('Failed to delete party:', error);
      message.error('Failed to delete party');
    }
  };

  const columns: ColumnsType<Party> = [
    {
      title: 'Party Name',
      dataIndex: 'partyName',
      key: 'partyName',
      sorter: (a, b) => a.partyName.localeCompare(b.partyName),
      render: (text, record) => (
        <div>
          <strong>{text}</strong>
          {record.legalName && record.legalName !== text && (
            <div style={{ fontSize: '0.85em', color: '#888' }}>{record.legalName}</div>
          )}
        </div>
      )
    },
    {
      title: 'GST Number',
      dataIndex: 'gstNumber',
      key: 'gstNumber',
    },
    {
      title: 'PAN',
      dataIndex: 'panNumber',
      key: 'panNumber',
    },
    {
      title: 'Status',
      dataIndex: 'status',
      key: 'status',
      render: (status: string) => {
        let color = 'default';
        if (status === 'ACTIVE') color = 'green';
        if (status === 'INACTIVE') color = 'orange';
        if (status === 'BLACKLISTED') color = 'red';
        
        return <Tag color={color}>{status}</Tag>;
      },
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="text" 
            icon={<EditOutlined />} 
            onClick={() => handleEdit(record)} 
          />
          <Popconfirm
            title="Delete the party"
            description="Are you sure you want to delete this party?"
            onConfirm={() => handleDelete(record.id)}
            okText="Yes"
            cancelText="No"
          >
            <Button type="text" danger icon={<DeleteOutlined />} />
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
        <Title level={2} style={{ margin: 0 }}>Parties (Suppliers & Customers)</Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          Add Party
        </Button>
      </div>

      <Table
        columns={columns}
        dataSource={parties}
        rowKey="id"
        loading={loading}
      />

      <PartyFormModal
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onSuccess={() => {
          setModalOpen(false);
          fetchParties();
        }}
        editingParty={editingParty}
      />
    </div>
  );
};

export default PartyList;
