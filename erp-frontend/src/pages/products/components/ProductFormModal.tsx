import { useEffect } from 'react';
import { Modal, Form, Input, InputNumber, Switch, message } from 'antd';
import type { Product } from '../../../types/product';
import { productService } from '../../../services/productService';

interface ProductFormModalProps {
  open: boolean;
  onCancel: () => void;
  onSuccess: () => void;
  editingProduct?: Product | null;
}

const ProductFormModal = ({ open, onCancel, onSuccess, editingProduct }: ProductFormModalProps) => {
  const [form] = Form.useForm();

  useEffect(() => {
    if (open) {
      if (editingProduct) {
        form.setFieldsValue(editingProduct);
      } else {
        form.resetFields();
        form.setFieldsValue({ active: true, taxRate: 0, minStockLevel: 0, price: 0 });
      }
    }
  }, [open, editingProduct, form]);

  const handleOk = async () => {
    try {
      const values = await form.validateFields();
      if (editingProduct) {
        await productService.updateProduct(editingProduct.id, values);
        message.success('Product updated successfully');
      } else {
        await productService.createProduct(values);
        message.success('Product created successfully');
      }
      onSuccess();
    } catch (error) {
      console.error('Validation or API Error:', error);
      // Let validation errors show naturally; only show API errors in message if needed
      if ((error as any).response) {
        message.error('Failed to save product');
      }
    }
  };

  return (
    <Modal
      title={editingProduct ? "Edit Product" : "Add New Product"}
      open={open}
      onOk={handleOk}
      onCancel={onCancel}
      destroyOnClose
    >
      <Form form={form} layout="vertical" preserve={false}>
        <Form.Item
          name="name"
          label="Product Name"
          rules={[{ required: true, message: 'Please enter product name' }]}
        >
          <Input placeholder="e.g. Premium Widget" />
        </Form.Item>

        <Form.Item name="description" label="Description">
          <Input.TextArea rows={3} placeholder="Optional description" />
        </Form.Item>

        <Form.Item name="sku" label="SKU">
          <Input placeholder="Stock Keeping Unit" />
        </Form.Item>

        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item
            name="price"
            label="Selling Price"
            rules={[{ required: true, message: 'Required' }]}
            style={{ flex: 1 }}
          >
            <InputNumber min={0} step={0.01} style={{ width: '100%' }} placeholder="0.00" />
          </Form.Item>

          <Form.Item name="costPrice" label="Cost Price" style={{ flex: 1 }}>
            <InputNumber min={0} step={0.01} style={{ width: '100%' }} placeholder="0.00" />
          </Form.Item>
        </div>

        <div style={{ display: 'flex', gap: '16px' }}>
          <Form.Item
            name="taxRate"
            label="Tax Rate (%)"
            rules={[{ required: true, message: 'Required' }]}
            style={{ flex: 1 }}
          >
            <InputNumber min={0} max={100} style={{ width: '100%' }} />
          </Form.Item>

          <Form.Item
            name="minStockLevel"
            label="Min Stock Level"
            rules={[{ required: true, message: 'Required' }]}
            style={{ flex: 1 }}
          >
            <InputNumber min={0} style={{ width: '100%' }} />
          </Form.Item>
        </div>

        <Form.Item name="active" label="Active" valuePropName="checked">
          <Switch />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default ProductFormModal;
