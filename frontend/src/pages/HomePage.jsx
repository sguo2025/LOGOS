import { Card, Row, Col, Statistic, Button, Typography } from 'antd'
import { useNavigate } from 'react-router-dom'
import {
  ApiOutlined,
  CodeOutlined,
  DatabaseOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons'

const { Title, Paragraph } = Typography

function HomePage() {
  const navigate = useNavigate()

  const features = [
    {
      title: 'NL2SpEL 工作台',
      description: '将自然语言需求转化为可执行的 SpEL 表达式',
      icon: <ApiOutlined style={{ fontSize: 48, color: '#1890ff' }} />,
      path: '/nl2spel',
    },
    {
      title: 'Java 代码建模',
      description: '从 Java 源码中自动提取本体知识',
      icon: <CodeOutlined style={{ fontSize: 48, color: '#52c41a' }} />,
      path: '/modeling',
    },
    {
      title: '本体浏览器',
      description: '可视化浏览和管理知识图谱',
      icon: <DatabaseOutlined style={{ fontSize: 48, color: '#722ed1' }} />,
      path: '/ontology',
    },
  ]

  return (
    <div>
      <Card style={{ marginBottom: 24 }}>
        <Title level={2}>欢迎使用 LOGOS 智能规则中台</Title>
        <Paragraph>
          LOGOS 是基于本体建模与大模型的业务规则进化平台，旨在将自然语言需求精准转化为可执行的 SpEL 脚本。
          通过知识图谱锁定物理与业务的映射关系，防止 LLM 生成过程中产生"幻觉"。
        </Paragraph>
      </Card>

      <Row gutter={[24, 24]} style={{ marginBottom: 24 }}>
        <Col span={6}>
          <Card>
            <Statistic
              title="本体节点"
              value={12}
              prefix={<DatabaseOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="业务约束"
              value={1}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="已生成规则"
              value={0}
              prefix={<ApiOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="验证通过率"
              value="100%"
              valueStyle={{ color: '#3f8600' }}
            />
          </Card>
        </Col>
      </Row>

      <Title level={4} style={{ marginBottom: 16 }}>快速开始</Title>
      <Row gutter={[24, 24]}>
        {features.map((feature) => (
          <Col span={8} key={feature.path}>
            <Card
              hoverable
              onClick={() => navigate(feature.path)}
              style={{ textAlign: 'center', height: '100%' }}
            >
              <div style={{ marginBottom: 16 }}>{feature.icon}</div>
              <Title level={4}>{feature.title}</Title>
              <Paragraph type="secondary">{feature.description}</Paragraph>
              <Button type="primary">进入</Button>
            </Card>
          </Col>
        ))}
      </Row>
    </div>
  )
}

export default HomePage
