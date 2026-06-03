import { useEffect, useMemo, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Alert, Button, Divider, Spin, Tag, Typography, message } from 'antd';
import { ArrowLeftOutlined, DownloadOutlined, PrinterOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import html2canvas from 'html2canvas';
import jsPDF from 'jspdf';
import type { Invoice, InvoiceItem } from '../../types/billing';
import { billingService } from '../../services/billingService';

const { Title, Text } = Typography;

const formatCurrency = (amount?: number) => {
  const value = amount || 0;
  return `₹${value.toFixed(2)}`;
};

const InvoiceDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [invoice, setInvoice] = useState<Invoice | null>(null);
  const [loading, setLoading] = useState(true);
  const [downloading, setDownloading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const invoiceId = useMemo(() => (id ? Number(id) : null), [id]);

  useEffect(() => {
    const fetchInvoice = async () => {
      if (!invoiceId || Number.isNaN(invoiceId)) {
        setError('Invalid invoice id');
        setLoading(false);
        return;
      }

      try {
        setLoading(true);
        const data = await billingService.getInvoiceById(invoiceId);
        setInvoice(data);
      } catch (err) {
        console.error('Failed to load invoice:', err);
        setError('Failed to load invoice');
      } finally {
        setLoading(false);
      }
    };

    fetchInvoice();
  }, [invoiceId]);

  const handleDownloadPdf = async () => {
    const target = document.getElementById('invoice-print');
    if (!target || !invoice) {
      message.error('Invoice content not ready');
      return;
    }

    try {
      setDownloading(true);
      const canvas = await html2canvas(target, { scale: 2, useCORS: true });
      const imgData = canvas.toDataURL('image/png');
      const pdf = new jsPDF('p', 'pt', 'a4');

      const pageWidth = pdf.internal.pageSize.getWidth();
      const pageHeight = pdf.internal.pageSize.getHeight();
      const imgWidth = pageWidth;
      const imgHeight = (canvas.height * imgWidth) / canvas.width;

      let heightLeft = imgHeight;
      let position = 0;

      pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
      heightLeft -= pageHeight;

      while (heightLeft > 0) {
        position -= pageHeight;
        pdf.addPage();
        pdf.addImage(imgData, 'PNG', 0, position, imgWidth, imgHeight);
        heightLeft -= pageHeight;
      }

      const fileName = `invoice-${invoice.invoiceNumber || invoice.id}.pdf`;
      pdf.save(fileName);
    } catch (err) {
      console.error('Failed to generate PDF:', err);
      message.error('Failed to generate PDF');
    } finally {
      setDownloading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', padding: 32 }}>
        <Spin size="large" />
      </div>
    );
  }

  if (error || !invoice) {
    return (
      <Alert
        type="error"
        message={error || 'Invoice not found'}
        action={<Button onClick={() => navigate('/billing')}>Back to Billing</Button>}
        showIcon
      />
    );
  }

  const items: InvoiceItem[] = invoice.items || [];

  return (
    <div>
      <div
        className="no-print"
        style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}
      >
        <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
          <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/billing')}>
            Back
          </Button>
          <Title level={3} style={{ margin: 0 }}>
            Invoice {invoice.invoiceNumber}
          </Title>
          <Tag>{invoice.status?.replace('_', ' ')}</Tag>
        </div>
        <div style={{ display: 'flex', gap: 8 }}>
          <Button icon={<PrinterOutlined />} onClick={() => window.print()}>
            Print
          </Button>
          <Button
            type="primary"
            icon={<DownloadOutlined />}
            loading={downloading}
            onClick={handleDownloadPdf}
          >
            Download PDF
          </Button>
        </div>
      </div>

      <div
        id="invoice-print"
        style={{
          background: '#fff',
          padding: 24,
          border: '1px solid #f0f0f0',
          borderRadius: 8,
        }}
      >
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <Title level={4} style={{ marginBottom: 4 }}>Invoice</Title>
            <Text type="secondary">{invoice.invoiceType?.replace('_', ' ')}</Text>
          </div>
          <div style={{ textAlign: 'right' }}>
            <div><Text strong>Invoice No:</Text> {invoice.invoiceNumber}</div>
            <div><Text strong>Date:</Text> {dayjs(invoice.invoiceDate).format('MMM DD, YYYY')}</div>
            <div><Text strong>Due:</Text> {invoice.dueDate ? dayjs(invoice.dueDate).format('MMM DD, YYYY') : 'N/A'}</div>
          </div>
        </div>

        <Divider />

        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
          <div>
            <Text strong>Bill To</Text>
            <div>{invoice.party?.partyName || '—'}</div>
          </div>
          <div style={{ textAlign: 'right' }}>
            <Text strong>Created By</Text>
            <div>{invoice.createdBy || '—'}</div>
          </div>
        </div>

        <Divider />

        <table style={{ width: '100%', borderCollapse: 'collapse' }}>
          <thead>
            <tr style={{ background: '#fafafa' }}>
              <th style={{ textAlign: 'left', padding: 8, borderBottom: '1px solid #eee' }}>Item</th>
              <th style={{ textAlign: 'right', padding: 8, borderBottom: '1px solid #eee' }}>Qty</th>
              <th style={{ textAlign: 'right', padding: 8, borderBottom: '1px solid #eee' }}>Unit Price</th>
              <th style={{ textAlign: 'right', padding: 8, borderBottom: '1px solid #eee' }}>Tax %</th>
              <th style={{ textAlign: 'right', padding: 8, borderBottom: '1px solid #eee' }}>Total</th>
            </tr>
          </thead>
          <tbody>
            {items.length === 0 && (
              <tr>
                <td colSpan={5} style={{ padding: 12, textAlign: 'center' }}>
                  No items found
                </td>
              </tr>
            )}
            {items.map((item, idx) => (
              <tr key={item.id || idx}>
                <td style={{ padding: 8, borderBottom: '1px solid #f0f0f0' }}>
                  {item.product?.name || item.productName || 'Item'}
                </td>
                <td style={{ padding: 8, textAlign: 'right', borderBottom: '1px solid #f0f0f0' }}>
                  {item.quantity}
                </td>
                <td style={{ padding: 8, textAlign: 'right', borderBottom: '1px solid #f0f0f0' }}>
                  {formatCurrency(item.unitPrice)}
                </td>
                <td style={{ padding: 8, textAlign: 'right', borderBottom: '1px solid #f0f0f0' }}>
                  {item.taxRate ?? 0}
                </td>
                <td style={{ padding: 8, textAlign: 'right', borderBottom: '1px solid #f0f0f0' }}>
                  {formatCurrency(item.totalAmount)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        <Divider />

        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
          <div style={{ minWidth: 280 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Subtotal</Text>
              <Text>{formatCurrency(invoice.subtotal)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Tax</Text>
              <Text>{formatCurrency(invoice.totalTax)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Discount</Text>
              <Text>{formatCurrency(invoice.discountAmount)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Shipping</Text>
              <Text>{formatCurrency(invoice.shippingCharges)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Other Charges</Text>
              <Text>{formatCurrency(invoice.otherCharges)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Round Off</Text>
              <Text>{formatCurrency(invoice.roundOff)}</Text>
            </div>
            <Divider style={{ margin: '12px 0' }} />
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text strong>Total</Text>
              <Text strong>{formatCurrency(invoice.totalAmount)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text>Paid</Text>
              <Text>{formatCurrency(invoice.paidAmount)}</Text>
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <Text strong>Balance</Text>
              <Text strong>{formatCurrency(invoice.balanceAmount)}</Text>
            </div>
          </div>
        </div>

        {invoice.remarks && (
          <>
            <Divider />
            <Text strong>Remarks</Text>
            <div>{invoice.remarks}</div>
          </>
        )}
      </div>
    </div>
  );
};

export default InvoiceDetail;
