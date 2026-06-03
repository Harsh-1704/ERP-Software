import { useState } from 'react';
import { Modal, Form, InputNumber, Select, Input, DatePicker, message } from 'antd';
import type { Payment } from '../../../types/billing';
import { billingService } from '../../../services/billingService';
import dayjs from 'dayjs';

const { Option } = Select;

interface PaymentModalProps {
  open: boolean;
  invoiceId: number | null;
  balanceAmount: number;
  onCancel: () => void;
  onSuccess: () => void;
}

const PaymentModal = ({ open, invoiceId, balanceAmount, onCancel, onSuccess }: PaymentModalProps) => {
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);

  const handleOk = async () => {
    if (!invoiceId) return;
    try {
      const values = await form.validateFields();
      setLoading(true);

      const payment: Partial<Payment> = {
        amount: values.amount,
        paymentMode: values.paymentMode,
        paymentDate: values.paymentDate?.toISOString(),
        referenceNumber: values.referenceNumber,
        remarks: values.remarks,
      };

      await billingService.recordPayment(invoiceId, payment);
      message.success('Payment recorded successfully');
      form.resetFields();
      onSuccess();
    } catch (error) {
      console.error('Payment error:', error);
      if ((error as any).response) {
        message.error('Failed to record payment');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title="Record Payment"
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false} initialValues={{ paymentDate: dayjs(), paymentMode: 'BANK_TRANSFER' }}>
        <Form.Item label="Balance Due">
          <InputNumber value={balanceAmount} disabled style={{ width: '100%' }} prefix="$" />
        </Form.Item>

        <Form.Item name="amount" label="Payment Amount" rules={[{ required: true, message: 'Enter amount' }]}>
          <InputNumber style={{ width: '100%' }} min={0.01} max={balanceAmount} step={0.01} placeholder="Enter amount" />
        </Form.Item>

        <Form.Item name="paymentMode" label="Payment Mode" rules={[{ required: true }]}>
          <Select>
            <Option value="CASH">Cash</Option>
            <Option value="BANK_TRANSFER">Bank Transfer</Option>
            <Option value="CHEQUE">Cheque</Option>
            <Option value="UPI">UPI</Option>
            <Option value="CREDIT_CARD">Credit Card</Option>
          </Select>
        </Form.Item>

        <Form.Item name="paymentDate" label="Payment Date" rules={[{ required: true }]}>
          <DatePicker style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item name="referenceNumber" label="Reference / Transaction ID">
          <Input placeholder="e.g., TXN123456" />
        </Form.Item>

        <Form.Item name="remarks" label="Remarks">
          <Input.TextArea rows={2} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default PaymentModal;
