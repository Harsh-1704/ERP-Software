import { useEffect, useState } from 'react';
import { Modal, Form, InputNumber, Select, Input, message } from 'antd';
import type { StockInRequest, StockOutRequest, Warehouse } from '../../../types/inventory';
import type { Product } from '../../../types/product';
import { inventoryService } from '../../../services/inventoryService';
import { productService } from '../../../services/productService';

interface StockMovementModalProps {
  open: boolean;
  type: 'IN' | 'OUT';
  onCancel: () => void;
  onSuccess: () => void;
}

const StockMovementModal = ({ open, type, onCancel, onSuccess }: StockMovementModalProps) => {
  const [form] = Form.useForm();
  const [products, setProducts] = useState<Product[]>([]);
  const [warehouses, setWarehouses] = useState<Warehouse[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (open) {
      form.resetFields();
      fetchDropdownData();
    }
  }, [open, form]);

  const fetchDropdownData = async () => {
    try {
      const [productsData, warehousesData] = await Promise.all([
        productService.getAllProducts(),
        inventoryService.getAllWarehouses()
      ]);
      setProducts(productsData.filter(p => p.active));
      setWarehouses(warehousesData.filter(w => w.isActive));
    } catch (error) {
      console.error('Failed to fetch dropdown data:', error);
      message.error('Failed to load products and warehouses');
    }
  };

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      setLoading(true);
      
      if (type === 'IN') {
        const payload: StockInRequest = {
          productId: values.productId,
          warehouseId: values.warehouseId,
          quantity: values.quantity,
          unitPrice: values.unitPrice || 0,
          referenceType: values.referenceType,
        };
        await inventoryService.stockIn(payload);
        message.success('Stock added successfully');
      } else {
        const payload: StockOutRequest = {
          productId: values.productId,
          warehouseId: values.warehouseId,
          quantity: values.quantity,
          referenceType: values.referenceType,
        };
        await inventoryService.stockOut(payload);
        message.success('Stock removed successfully');
      }
      
      onSuccess();
    } catch (error) {
      console.error('Validation or API Error:', error);
      if ((error as any).response) {
        message.error(`Failed to record stock ${type.toLowerCase()}`);
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal
      title={type === 'IN' ? 'Stock In (Receive)' : 'Stock Out (Issue)'}
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      confirmLoading={loading}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <Form.Item
          name="productId"
          label="Product"
          rules={[{ required: true, message: 'Please select a product' }]}
        >
          <Select
            showSearch
            placeholder="Select a product"
            optionFilterProp="children"
            filterOption={(input, option) =>
              (option?.label ?? '').toString().toLowerCase().includes(input.toLowerCase())
            }
            options={products.map(p => ({ value: p.id, label: `${p.sku || p.id} - ${p.name}` }))}
          />
        </Form.Item>

        <Form.Item
          name="warehouseId"
          label="Warehouse"
          rules={[{ required: true, message: 'Please select a warehouse' }]}
        >
          <Select
            placeholder="Select a warehouse"
            options={warehouses.map(w => ({ value: w.id, label: `${w.code} - ${w.name}` }))}
          />
        </Form.Item>

        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item
            name="quantity"
            label="Quantity"
            rules={[{ required: true, message: 'Required' }]}
            style={{ flex: 1 }}
          >
            <InputNumber min={1} style={{ width: '100%' }} />
          </Form.Item>

          {type === 'IN' && (
            <Form.Item
              name="unitPrice"
              label="Unit Price"
              rules={[{ required: true, message: 'Required' }]}
              style={{ flex: 1 }}
            >
              <InputNumber min={0} step={0.01} style={{ width: '100%' }} />
            </Form.Item>
          )}
        </div>

        <Form.Item name="referenceType" label="Reference Note">
          <Input placeholder="e.g., Manual Adjustment, Initial Stock" />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default StockMovementModal;
