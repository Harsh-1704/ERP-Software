import { useState } from 'react';
import { Form, Input, Button, Card, Typography, message, Radio } from 'antd';
import {
  UserOutlined,
  LockOutlined,
  CrownOutlined,
  UserSwitchOutlined,
} from '@ant-design/icons';
import { useNavigate, Link } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { authService } from '../services/authService';

const { Title, Text } = Typography;

const Register = () => {
  const [loading, setLoading] = useState(false);
  const [selectedRole, setSelectedRole] = useState<string>('ROLE_USER');
  const setAuth = useAuthStore((state: any) => state.setAuth);
  const navigate = useNavigate();

  const onFinish = async (values: any) => {
    if (values.password !== values.confirmPassword) {
      message.error('Passwords do not match!');
      return;
    }

    try {
      setLoading(true);
      const token = await authService.register({
        username: values.username,
        password: values.password,
        role: selectedRole,
      });
      setAuth(token, values.username);
      message.success('Registration successful! Welcome aboard.');
      navigate('/dashboard');
    } catch (error: any) {
      console.error('Registration error:', error);
      message.error(error.message || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        minHeight: '100vh',
        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
        padding: '24px',
      }}
    >
      <Card
        style={{
          width: 460,
          boxShadow: '0 20px 60px rgba(0, 0, 0, 0.3)',
          borderRadius: 16,
          border: 'none',
          overflow: 'hidden',
        }}
        styles={{
          body: { padding: '40px 36px 32px' },
        }}
      >
        {/* Header */}
        <div style={{ textAlign: 'center', marginBottom: 32 }}>
          <div
            style={{
              width: 64,
              height: 64,
              borderRadius: 16,
              background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
              display: 'inline-flex',
              alignItems: 'center',
              justifyContent: 'center',
              marginBottom: 16,
              boxShadow: '0 8px 24px rgba(102, 126, 234, 0.4)',
            }}
          >
            <UserSwitchOutlined style={{ fontSize: 28, color: '#fff' }} />
          </div>
          <Title level={2} style={{ margin: 0, color: '#1a1a2e' }}>
            Create Account
          </Title>
          <Text type="secondary" style={{ fontSize: 14 }}>
            Sign up to get started with ERP System
          </Text>
        </div>

        {/* Role Selector Panel */}
        <div style={{ marginBottom: 28 }}>
          <Text
            strong
            style={{
              display: 'block',
              marginBottom: 12,
              color: '#555',
              fontSize: 13,
              textTransform: 'uppercase',
              letterSpacing: '0.5px',
            }}
          >
            Choose your role
          </Text>
          <Radio.Group
            value={selectedRole}
            onChange={(e) => setSelectedRole(e.target.value)}
            style={{ width: '100%', display: 'flex', gap: 12 }}
          >
            <Radio.Button
              value="ROLE_USER"
              style={{
                flex: 1,
                height: 'auto',
                padding: '16px 12px',
                textAlign: 'center',
                borderRadius: 12,
                border:
                  selectedRole === 'ROLE_USER'
                    ? '2px solid #667eea'
                    : '2px solid #e8e8e8',
                background:
                  selectedRole === 'ROLE_USER'
                    ? 'linear-gradient(135deg, rgba(102,126,234,0.08) 0%, rgba(118,75,162,0.08) 100%)'
                    : '#fafafa',
                transition: 'all 0.3s ease',
                boxShadow:
                  selectedRole === 'ROLE_USER'
                    ? '0 4px 12px rgba(102, 126, 234, 0.2)'
                    : 'none',
              }}
            >
              <div>
                <UserOutlined
                  style={{
                    fontSize: 24,
                    color: selectedRole === 'ROLE_USER' ? '#667eea' : '#999',
                    display: 'block',
                    marginBottom: 6,
                  }}
                />
                <div
                  style={{
                    fontWeight: 600,
                    color: selectedRole === 'ROLE_USER' ? '#667eea' : '#666',
                    fontSize: 14,
                  }}
                >
                  User
                </div>
                <div
                  style={{
                    fontSize: 11,
                    color: '#999',
                    marginTop: 2,
                  }}
                >
                  Standard access
                </div>
              </div>
            </Radio.Button>

            <Radio.Button
              value="ROLE_ADMIN"
              style={{
                flex: 1,
                height: 'auto',
                padding: '16px 12px',
                textAlign: 'center',
                borderRadius: 12,
                border:
                  selectedRole === 'ROLE_ADMIN'
                    ? '2px solid #764ba2'
                    : '2px solid #e8e8e8',
                background:
                  selectedRole === 'ROLE_ADMIN'
                    ? 'linear-gradient(135deg, rgba(118,75,162,0.08) 0%, rgba(102,126,234,0.08) 100%)'
                    : '#fafafa',
                transition: 'all 0.3s ease',
                boxShadow:
                  selectedRole === 'ROLE_ADMIN'
                    ? '0 4px 12px rgba(118, 75, 162, 0.2)'
                    : 'none',
              }}
            >
              <div>
                <CrownOutlined
                  style={{
                    fontSize: 24,
                    color: selectedRole === 'ROLE_ADMIN' ? '#764ba2' : '#999',
                    display: 'block',
                    marginBottom: 6,
                  }}
                />
                <div
                  style={{
                    fontWeight: 600,
                    color: selectedRole === 'ROLE_ADMIN' ? '#764ba2' : '#666',
                    fontSize: 14,
                  }}
                >
                  Admin
                </div>
                <div
                  style={{
                    fontSize: 11,
                    color: '#999',
                    marginTop: 2,
                  }}
                >
                  Full control
                </div>
              </div>
            </Radio.Button>
          </Radio.Group>
        </div>

        {/* Registration Form */}
        <Form
          name="register_form"
          onFinish={onFinish}
          layout="vertical"
          size="large"
          requiredMark={false}
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: 'Please enter a username' },
              { min: 3, message: 'Username must be at least 3 characters' },
            ]}
          >
            <Input
              prefix={<UserOutlined style={{ color: '#bbb' }} />}
              placeholder="Username"
              style={{ borderRadius: 10, height: 46 }}
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: 'Please enter a password' },
              { min: 6, message: 'Password must be at least 6 characters' },
            ]}
          >
            <Input.Password
              prefix={<LockOutlined style={{ color: '#bbb' }} />}
              placeholder="Password"
              style={{ borderRadius: 10, height: 46 }}
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Please confirm your password' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('Passwords do not match'));
                },
              }),
            ]}
          >
            <Input.Password
              prefix={<LockOutlined style={{ color: '#bbb' }} />}
              placeholder="Confirm Password"
              style={{ borderRadius: 10, height: 46 }}
            />
          </Form.Item>

          <Form.Item style={{ marginBottom: 16 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              style={{
                height: 48,
                borderRadius: 10,
                fontWeight: 600,
                fontSize: 15,
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                border: 'none',
                boxShadow: '0 4px 16px rgba(102, 126, 234, 0.4)',
                transition: 'all 0.3s ease',
              }}
            >
              Create Account
            </Button>
          </Form.Item>
        </Form>

        {/* Footer Link */}
        <div style={{ textAlign: 'center', marginTop: 8 }}>
          <Text type="secondary">
            Already have an account?{' '}
            <Link
              to="/login"
              style={{
                color: '#667eea',
                fontWeight: 600,
              }}
            >
              Sign In
            </Link>
          </Text>
        </div>
      </Card>
    </div>
  );
};

export default Register;
