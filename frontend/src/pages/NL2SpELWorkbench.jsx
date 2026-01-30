import { useState } from 'react'
import {
  Card,
  Row,
  Col,
  Input,
  Button,
  Select,
  Tag,
  Drawer,
  Table,
  Alert,
  Space,
  Progress,
  message,
  Divider,
  Typography,
} from 'antd'
import {
  SendOutlined,
  PlayCircleOutlined,
  CopyOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
} from '@ant-design/icons'
import { ruleApi } from '../services/api'

const { TextArea } = Input
const { Title, Paragraph, Text } = Typography

// 产品选项
const productOptions = [
  { value: '80000122', label: '灵犀专线 (80000122)' },
  { value: '80000123', label: '智能宽带 (80000123)' },
]

function NL2SpELWorkbench() {
  const [productId, setProductId] = useState('80000122')
  const [naturalLanguage, setNaturalLanguage] = useState('')
  const [loading, setLoading] = useState(false)
  const [result, setResult] = useState(null)
  const [sandboxOpen, setSandboxOpen] = useState(false)
  const [validating, setValidating] = useState(false)
  const [validationResult, setValidationResult] = useState(null)
  const [mockData, setMockData] = useState(JSON.stringify({
    businessTypeCode: '3',
    soId: '2831',
    operType: '1300'
  }, null, 2))

  // 生成 SpEL
  const handleGenerate = async () => {
    if (!naturalLanguage.trim()) {
      message.warning('请输入自然语言描述')
      return
    }

    setLoading(true)
    try {
      const response = await ruleApi.generate({
        productId,
        naturalLanguage,
        context: 'AccessProdInst'
      })
      setResult(response.data)
      message.success('SpEL 生成成功')
    } catch (error) {
      message.error('生成失败: ' + (error.message || '未知错误'))
    } finally {
      setLoading(false)
    }
  }

  // 验证 SpEL
  const handleValidate = async () => {
    if (!result?.spel) {
      message.warning('请先生成 SpEL 表达式')
      return
    }

    setValidating(true)
    try {
      const parsedMockData = JSON.parse(mockData)
      const response = await ruleApi.validate({
        spel: result.spel,
        mockData: parsedMockData
      })
      setValidationResult(response.data)
      message.success('验证完成')
    } catch (error) {
      message.error('验证失败: ' + (error.message || '未知错误'))
    } finally {
      setValidating(false)
    }
  }

  // 复制 SpEL
  const handleCopy = () => {
    if (result?.spel) {
      navigator.clipboard.writeText(result.spel)
      message.success('已复制到剪贴板')
    }
  }

  // 置信度颜色
  const getConfidenceColor = (confidence) => {
    if (confidence >= 0.9) return 'success'
    if (confidence >= 0.7) return 'warning'
    return 'error'
  }

  return (
    <div>
      {/* 产品上下文头部 */}
      <Card size="small" style={{ marginBottom: 16 }}>
        <Space>
          <Text strong>当前产品：</Text>
          <Select
            value={productId}
            onChange={setProductId}
            options={productOptions}
            style={{ width: 200 }}
          />
          <Tag color="blue">AccessProdInst</Tag>
          <Tag color="green">本体完整</Tag>
        </Space>
      </Card>

      {/* 主工作区 */}
      <Row gutter={16} style={{ marginBottom: 16 }}>
        {/* 左侧：自然语言输入 */}
        <Col span={8}>
          <Card title="📝 自然语言输入" className="panel" style={{ minHeight: 320 }}>
            <TextArea
              rows={6}
              placeholder="请输入业务规则描述，例如：当业务类型是融合光网时，只准做拆机操作"
              value={naturalLanguage}
              onChange={(e) => setNaturalLanguage(e.target.value)}
              style={{ marginBottom: 12 }}
            />
            <Button
              type="primary"
              icon={<SendOutlined />}
              loading={loading}
              onClick={handleGenerate}
              block
              size="large"
            >
              生成 SpEL
            </Button>
          </Card>
        </Col>

        {/* 中间：语义理解与SpEL预览 */}
        <Col span={8}>
          <Card title="🎯 语义理解" className="panel" style={{ minHeight: 320 }}>
            {result ? (
              <>
                <div style={{ marginBottom: 16 }}>
                  <Text strong>IF-THEN 结构：</Text>
                  <div style={{ 
                    background: '#f5f5f5', 
                    padding: 12, 
                    borderRadius: 4,
                    marginTop: 8,
                    fontFamily: 'monospace'
                  }}>
                    <div><Text type="secondary">IF</Text></div>
                    <div style={{ marginLeft: 16 }}>
                      <Tag color="blue">业务类型</Tag> == <Tag color="green">融合光网</Tag>
                    </div>
                    <div><Text type="secondary">THEN</Text></div>
                    <div style={{ marginLeft: 16 }}>
                      <Tag color="orange">允许操作</Tag> = <Tag color="purple">拆机</Tag>
                    </div>
                  </div>
                </div>

                <Divider />

                <div style={{ marginBottom: 16 }}>
                  <Space>
                    <Text strong>SpEL 表达式：</Text>
                    <Button size="small" icon={<CopyOutlined />} onClick={handleCopy}>
                      复制
                    </Button>
                  </Space>
                  <div className="spel-preview" style={{ marginTop: 8 }}>
                    {result.spel}
                  </div>
                </div>

                <div className="confidence-meter">
                  <Text>置信度：</Text>
                  <Progress
                    percent={Math.round((result.confidence || 0.95) * 100)}
                    status={getConfidenceColor(result.confidence || 0.95)}
                    size="small"
                    style={{ flex: 1 }}
                  />
                </div>
              </>
            ) : (
              <div style={{ textAlign: 'center', color: '#999', paddingTop: 60 }}>
                <ExclamationCircleOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <div>等待生成...</div>
              </div>
            )}
          </Card>
        </Col>

        {/* 右侧：证据图谱 */}
        <Col span={8}>
          <Card title="🔗 证据图谱" className="panel" style={{ minHeight: 320 }}>
            {result?.evidenceNodes?.length > 0 ? (
              <>
                <Paragraph type="secondary">
                  以下节点为本次生成的依据来源：
                </Paragraph>
                <div style={{ marginTop: 16 }}>
                  {result.evidenceNodes.map((node, index) => (
                    <Tag
                      key={index}
                      color={
                        node.includes('Type') || node.includes('Code') ? 'green' :
                        node.includes('Inst') || node.includes('Context') ? 'blue' :
                        'orange'
                      }
                      style={{ margin: 4 }}
                    >
                      {node}
                    </Tag>
                  ))}
                </div>

                <Divider />

                <Title level={5}>字段映射</Title>
                <Table
                  size="small"
                  pagination={false}
                  dataSource={[
                    { key: '1', semantic: '业务类型', physical: 'COL1', source: 'Neo4j' },
                    { key: '2', semantic: '服务提供ID', physical: 'serviceOfferId', source: 'Neo4j' },
                  ]}
                  columns={[
                    { title: '语义名', dataIndex: 'semantic', key: 'semantic' },
                    { title: '物理路径', dataIndex: 'physical', key: 'physical' },
                    { title: '来源', dataIndex: 'source', key: 'source' },
                  ]}
                />
              </>
            ) : (
              <div style={{ textAlign: 'center', color: '#999', paddingTop: 60 }}>
                <CheckCircleOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <div>生成后将显示证据节点</div>
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {/* 解释说明 */}
      {result?.explanation && (
        <Card size="small" style={{ marginBottom: 16 }}>
          <Space>
            <CheckCircleOutlined style={{ color: '#52c41a' }} />
            <Text strong>生成说明：</Text>
            <Text>{result.explanation}</Text>
          </Space>
        </Card>
      )}

      {/* 仿真验证按钮 */}
      <Card>
        <Space>
          <Button
            type="primary"
            icon={<PlayCircleOutlined />}
            onClick={() => setSandboxOpen(true)}
            disabled={!result?.spel}
          >
            仿真验证
          </Button>
          <Button disabled={!result?.spel}>
            保存草稿
          </Button>
          <Button type="primary" disabled={!result?.spel || !validationResult?.success}>
            发布规则
          </Button>
        </Space>
      </Card>

      {/* 仿真沙箱抽屉 */}
      <Drawer
        title="🧪 仿真沙箱"
        placement="bottom"
        height={400}
        open={sandboxOpen}
        onClose={() => setSandboxOpen(false)}
      >
        <Row gutter={24}>
          <Col span={12}>
            <Title level={5}>Mock 数据</Title>
            <TextArea
              rows={10}
              value={mockData}
              onChange={(e) => setMockData(e.target.value)}
              style={{ fontFamily: 'monospace' }}
            />
            <Button
              type="primary"
              icon={<PlayCircleOutlined />}
              onClick={handleValidate}
              loading={validating}
              style={{ marginTop: 16 }}
            >
              执行验证
            </Button>
          </Col>
          <Col span={12}>
            <Title level={5}>执行结果</Title>
            {validationResult ? (
              <div className={`sandbox-result ${validationResult.success ? 'success' : 'error'}`}>
                <Space direction="vertical" style={{ width: '100%' }}>
                  <div>
                    <Text strong>状态：</Text>
                    <Tag color={validationResult.success ? 'success' : 'error'}>
                      {validationResult.success ? '通过' : '失败'}
                    </Tag>
                  </div>
                  <div>
                    <Text strong>返回值：</Text>
                    <Text code>{String(validationResult.actualValue)}</Text>
                  </div>
                  <Divider />
                  <div>
                    <Text strong>执行日志：</Text>
                    <div style={{ 
                      background: '#1e1e1e', 
                      color: '#d4d4d4', 
                      padding: 12, 
                      borderRadius: 4,
                      fontFamily: 'monospace',
                      fontSize: 12,
                      maxHeight: 150,
                      overflow: 'auto'
                    }}>
                      {validationResult.logs?.map((log, i) => (
                        <div key={i}>{log}</div>
                      ))}
                    </div>
                  </div>
                </Space>
              </div>
            ) : (
              <Alert message="点击执行验证查看结果" type="info" showIcon />
            )}
          </Col>
        </Row>
      </Drawer>
    </div>
  )
}

export default NL2SpELWorkbench
