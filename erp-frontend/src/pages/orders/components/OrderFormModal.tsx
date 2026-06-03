import { useEffect, useState } from 'react';
import { Modal, Form, Input, Select, DatePicker, Button, Space, message, InputNumber, Divider } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';
import type { Party } from '../../../types/party';
import type { Product } from '../../../types/product';
import type { CreateOrderRequest, OrderItemRequest } from '../../../types/order';
import { partyService } from '../../../services/partyService';
import { productService } from '../../../services/productService';
import { orderService } from '../../../services/orderService';
import dayjs from 'dayjs';
import { useAuthStore } from '../../../stores/authStore';

const { Option } = Select;

interface OrderFormModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
}

const OrderFormModal = ({ open, onCancel, onSuccess }: OrderFormModalProps) => {
  const [form] = Form.useForm();
  const [parties, setParties] = useState<Party[]>([]);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(false);
  const username = useAuthStore((state: any) => state.username);

  useEffect(() => {
    if (open) {
      form.resetFields();
      form.setFieldsValue({ 
        orderType: 'SALES',
        orderDate: dayjs(),
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
      
      const items: OrderItemRequest[] = values.items.map((item: any) => ({
        productId: item.productId,
        quantity: item.quantity,
        unitPrice: item.unitPrice,
        taxRate: 0,
        discountPercentage: 0,
        discountAmount: 0,
      }));

      const payload: CreateOrderRequest = {
        order: {
          orderType: values.orderType,
          party: { id: values.partyId, partyName: '' },
          orderDate: values.orderDate.toISOString(),
          expectedDeliveryDate: values.expectedDeliveryDate?.toISOString(),
          remarks: values.remarks,
          createdBy: username,
        },
        items: items
      };

      await orderService.createOrder(payload);
      message.success('Order created successfully');
      onSuccess();
    } catch (error) {
      console.error('Validation or API Error:', error);
      if ((error as any).response) {
        message.error('Failed to create order');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleProductChange = (productId: number, index: number) => {
    const product = products.find(p => p.id === productId);
    if (product && product.costPrice) {
      // Very basic auto-fill logic for price, would usually use selling price for SALES
      const items = form.getFieldValue('items');
      items[index] = { ...items[index], unitPrice: product.costPrice };
      form.setFieldsValue({ items });
    }
  };

  return (
    <Modal
      title="Create New Order"
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      confirmLoading={loading}
      width={800}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item name="orderType" label="Order Type" style={{ flex: 1 }} rules={[{ required: true }]}>
            <Select>
              <Option value="SALES">Sales Order</Option>
              <Option value="PURCHASE">Purchase Order</Option>
            </Select>
          </Form.Item>

          <Form.Item name="partyId" label="Party (Customer/Supplier)" style={{ flex: 2 }} rules={[{ required: true, message: 'Please select a party' }]}>
            <Select showSearch optionFilterProp="children">
              {parties.map(p => (
                <Option key={p.id} value={p.id}>{p.partyName}</Option>
              ))}
            </Select>
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item name="orderDate" label="Order Date" style={{ flex: 1 }} rules={[{ required: true }]}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item name="expectedDeliveryDate" label="Expected Delivery Date" style={{ flex: 1 }}>
            <DatePicker style={{ width: '100%' }} />
          </Form.Item>
        </div>

        <Divider>Order Items</Divider>

        <Form.List name="items">
          {(fields, { add, remove }) => (
            <>
              {fields.map(({ key, name, ...restField }, index) => (
                <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                  <Form.Item
                    {...restField}
                    name={[name, 'productId']}
                    rules={[{ required: true, message: 'Missing product' }]}
                    style={{ width: 300 }}
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
                  <Form.Item
                    {...restField}
                    name={[name, 'quantity']}
                    rules={[{ required: true, message: 'Missing quantity' }]}
                  >
                    <InputNumber placeholder="Qty" min={1} />
                  </Form.Item>
                  <Form.Item
                    {...restField}
                    name={[name, 'unitPrice']}
                    rules={[{ required: true, message: 'Missing price' }]}
                  >
                    <InputNumber placeholder="Price" min={0} step={0.01} />
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

export default OrderFormModal;
