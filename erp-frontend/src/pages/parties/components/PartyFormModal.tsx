import { useEffect } from 'react';
import { Modal, Form, Input, Select, message } from 'antd';
import type { Party } from '../../../types/party';
import { partyService } from '../../../services/partyService';

const { Option } = Select;

interface PartyFormModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  editingParty?: Party | null;
}

const PartyFormModal = ({ open, onCancel, onSuccess, editingParty }: PartyFormModalProps) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open) {
      if (editingParty) {
        form.setFieldsValue(editingParty);
      } else {
        form.resetFields();
        form.setFieldsValue({ status: 'ACTIVE' });
      }
    }
  }, [open, editingParty, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      if (editingParty) {
        await partyService.updateParty(editingParty.id, values);
        message.success('Party updated successfully');
      } else {
        await partyService.createParty(values);
        message.success('Party created successfully');
      }
      onSuccess();
    } catch (error) {
      console.error('Validation or API Error:', error);
      if ((error as any).response) {
        message.error('Failed to save party details');
      }
    }
  };

  return (
    <Modal
      title={editingParty ? "Edit Party" : "Add New Party"}
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <Form.Item
          name="partyName"
          label="Party Name"
          rules={[{ required: true, message: 'Please enter the party name' }]}
        >
          <Input placeholder="e.g. Acme Corporation" />
        </Form.Item>

        <Form.Item name="legalName" label="Legal Name">
          <Input placeholder="Official registered name (if different)" />
        </Form.Item>

        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item name="gstNumber" label="GST Number" style={{ flex: 1 }}>
            <Input placeholder="GSTIN" />
          </Form.Item>

          <Form.Item name="panNumber" label="PAN Number" style={{ flex: 1 }}>
            <Input placeholder="PAN" />
          </Form.Item>
        </div>

        <Form.Item name="status" label="Status" rules={[{ required: true }]}>
          <Select>
            <Option value="ACTIVE">ACTIVE</Option>
            <Option value="INACTIVE">INACTIVE</Option>
            <Option value="BLACKLISTED">BLACKLISTED</Option>
          </Select>
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default PartyFormModal;
