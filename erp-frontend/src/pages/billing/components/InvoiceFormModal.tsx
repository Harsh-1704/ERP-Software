import { useEffect, useState } from 'react';
import { Modal, Form, Input, Select, DatePicker, Button, Space, message, InputNumber, Divider } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import type { Party } from '../../../types/party';
import type { Product } from '../../../types/product';
import type { CreateInvoiceRequest, InvoiceItemRequest } from '../../../types/billing';
import { partyService } from '../../../services/partyService';
import { productService } from '../../../services/productService';
import { billingService } from '../../../services/billingService';
import dayjs from 'dayjs';
import { useAuthStore } from '../../../stores/authStore';
import { useNavigate } from 'react-router-dom';

const { Option } = Select;

interface InvoiceFormModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
}

const InvoiceFormModal = ({ open, onCancel, onSuccess }: InvoiceFormModalProps) => {
  const [form] = Form.useForm();
  const [parties, setParties] = useState<Party[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const username = useAuthStore((state: any) => state.username);
  const navigate = useNavigate();

  useEffect(() => {
    if (open) {
      form.resetFields();
      form.setFieldsValue({
        invoiceType: 'SALES',
        invoiceDate: dayjs(),
        paymentTermsDays: 30,
        items: [{}]
      });
      fetchDropdownData();
    }
  }, [open, form]);

  const fetchDropdownData = async () => {
    try {
      const [partiesData, productsData] = await Promise.all([
        partyService.getAllParties(),
        productService.getAllProducts()
      ]);
      setParties(partiesData.filter(p => p.status === 'ACTIVE'));
      setProducts(productsData.filter(p => p.active));
    } catch (error) {
      console.error('Failed to fetch dropdown data:', error);
      message.error('Failed to load parties and products');
    }
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);

      const items: InvoiceItemRequest[] = values.items.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        taxRate: item.taxRate || 0,
        discountPercentage: item.discountPercentage || 0,
        discountAmount: item.discountAmount || 0,
      }));

      const payload: CreateInvoiceRequest = {
        invoice: {
          invoiceType: values.invoiceType,
          party: { id: values.partyId, partyName: '' },
          invoiceDate: values.invoiceDate.toISOString(),
          dueDate: values.dueDate?.toISOString(),
          paymentTermsDays: values.paymentTermsDays,
          remarks: values.remarks,
          createdBy: username,
        },
        items: items
      };

      const created = await billingService.createInvoice(payload);
      message.success('Invoice created successfully');
      onSuccess();
      if (created?.id) {
        navigate(`/billing/${created.id}`);
      }
    } catch (error) {
      console.error('Validation or API Error:', error);
      if ((error as any).response) {
        message.error('Failed to create invoice');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleProductChange = (productId: number, index: number) => {
    const product = products.find(p => p.id === productId);
    if (product) {
      const items = form.getFieldValue('items');
      items[index] = {
        ...items[index],
        unitPrice: product.costPrice || 0,
        taxRate: product.taxRate || 0,
      };
      form.setFieldsValue({ items });
    }
  };

  return (
    <Modal
      title="Create New Invoice"
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      confirmLoading={loading}
      width={850}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item name="invoiceType" label="Invoice Type" style={{ flex: 1 }} rules={[{ required: true }]}>
            <Select>
              <Option value="SALES">Sales Invoice</Option>
              <Option value="PURCHASE">Purchase Invoice</Option>
              <Option value="CREDIT_NOTE">Credit Note</Option>
              <Option value="DEBIT_NOTE">Debit Note</Option>
            </Select>
          </Form.Item>

          <Form.Item name="partyId" label="Party" style={{ flex: 2 }} rules={[{ required: true, message: 'Select a party' }]}>
            <Select showSearch optionFilterProp="children" placeholder="Select Customer/Supplier">
              {parties.map(p => (
                <Option key={p.id} value={p.id}>{p.partyName}</Option>
              ))}
            </Select>
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item name="invoiceDate" label="Invoice Date" style={{ flex: 1 }} rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="dueDate" label="Due Date" style={{ flex: 1 }}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
          <Form.Item name="paymentTermsDays" label="Payment Terms (Days)" style={{ flex: 1 }}>
            <InputNumber style={{ width: '100%' }} min={0} />
          </Form.Item>
        </div>

        <Divider>Invoice Items</Divider>

        <Form.List name="items">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => (
                <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                  <Form.Item
                    {...restField}
                    name={[name, 'productId']}
                    rules={[{ required: true, message: 'Select product' }]}
                    style={{ width: 250 }}
                  >
                    <Select
                      showSearch
                      placeholder="Select Product"
                      optionFilterProp="children"
                      onChange={(val) => handleProductChange(val, index)}
                    >
                      {products.map(p => (
                        <Option key={p.id} value={p.id}>{p.name}</Option>
                      ))}
                    </Select>
                  </Form.Item>
                  <Form.Item {...restField} name={[name, 'quantity']} rules={[{ required: true, message: 'Qty' }]}>
                    <InputNumber placeholder="Qty" min={1} />
                  </Form.Item>
                  <Form.Item {...restField} name={[name, 'unitPrice']} rules={[{ required: true, message: 'Price' }]}>
                    <InputNumber placeholder="Unit Price" min={0} step={0.01} />
                  </Form.Item>
                  <Form.Item {...restField} name={[name, 'taxRate']}>
                    <InputNumber placeholder="Tax %" min={0} max={100} step={0.5} />
                  </Form.Item>
                  <MinusCircleOutlined onClick={() => remove(name)} style={{ color: 'red' }} />
                </Space>
              ))}
              <Form.Item>
                <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                  Add Item
                </Button>
              </Form.Item>
            </>
          )}
        </Form.List>

        <Form.Item name="remarks" label="Remarks">
          <Input.TextArea rows={2} />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default InvoiceFormModal;
