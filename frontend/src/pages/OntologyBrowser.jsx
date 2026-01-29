import { useState, useEffect } from 'react'
import {
  Card,
  Row,
  Col,
  Tree,
  Table,
  Tag,
  Input,
  Button,
  Space,
  Descriptions,
  Typography,
  Empty,
  Spin,
} from 'antd'
import {
  ApartmentOutlined,
  SearchOutlined,
  ReloadOutlined,
  NodeIndexOutlined,
  FileTextOutlined,
  SettingOutlined,
  LinkOutlined,
} from '@ant-design/icons'

const { Title, Text, Paragraph } = Typography
const { Search } = Input

function OntologyBrowser() {
  const [loading, setLoading] = useState(false)
  const [searchText, setSearchText] = useState('')
  const [selectedNode, setSelectedNode] = useState(null)
  const [ontologyData, setOntologyData] = useState(null)

  // 模拟本体数据
  const mockOntologyData = {
    entities: [
      {
        key: 'entity-1',
        name: 'AccessProdInst',
        description: '接入产品实例',
        type: 'BUSINESS_CONTEXT',
        productId: '80000122',
        children: [
          { key: 'meta-1', name: 'businessTypeCode', displayName: '业务类型编码', type: 'String' },
          { key: 'meta-2', name: 'soId', displayName: '订单ID', type: 'String' },
          { key: 'meta-3', name: 'operType', displayName: '操作类型', type: 'String' },
          { key: 'meta-4', name: 'serviceOfferInstId', displayName: '服务提供实例ID', type: 'String' },
        ]
      },
      {
        key: 'entity-2',
        name: 'BusinessType',
        description: '业务类型',
        type: 'CODE_TABLE',
        productId: '80000122',
        children: [
          { key: 'code-1', name: '1', displayName: '新装', type: 'CODE_VALUE' },
          { key: 'code-2', name: '2', displayName: '移机', type: 'CODE_VALUE' },
          { key: 'code-3', name: '3', displayName: '融合光网', type: 'CODE_VALUE' },
        ]
      },
      {
        key: 'entity-3',
        name: 'OperationType',
        description: '操作类型',
        type: 'CODE_TABLE',
        productId: '80000122',
        children: [
          { key: 'oper-1', name: '1300', displayName: '拆机', type: 'CODE_VALUE' },
          { key: 'oper-2', name: '1400', displayName: '开通', type: 'CODE_VALUE' },
          { key: 'oper-3', name: '1500', displayName: '变更', type: 'CODE_VALUE' },
        ]
      },
    ],
    constraints: [
      {
        key: 'constraint-1',
        name: '融合光网拆机约束',
        condition: 'businessTypeCode == "3"',
        action: 'ALLOW_ONLY',
        targetOper: '1300',
        productId: '80000122',
      },
    ],
    relationships: [
      { from: 'AccessProdInst', to: 'BusinessType', type: 'HAS_TYPE' },
      { from: 'AccessProdInst', to: 'OperationType', type: 'HAS_OPERATION' },
      { from: 'BusinessConstraint', to: 'AccessProdInst', type: 'APPLIES_TO' },
    ]
  }

  // 加载本体数据
  const loadOntologyData = async () => {
    setLoading(true)
    try {
      // 模拟API调用延迟
      await new Promise(resolve => setTimeout(resolve, 500))
      setOntologyData(mockOntologyData)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadOntologyData()
  }, [])

  // 构建树形数据
  const buildTreeData = () => {
    if (!ontologyData) return []

    const entityNodes = ontologyData.entities.map(entity => ({
      key: entity.key,
      title: (
        <Space>
          <ApartmentOutlined style={{ color: '#1890ff' }} />
          <span>{entity.name}</span>
          <Tag color={entity.type === 'BUSINESS_CONTEXT' ? 'blue' : 'green'} size="small">
            {entity.type}
          </Tag>
        </Space>
      ),
      data: entity,
      children: entity.children?.map(child => ({
        key: child.key,
        title: (
          <Space>
            <FileTextOutlined style={{ color: '#52c41a' }} />
            <span>{child.displayName || child.name}</span>
            <Tag color="default" size="small">{child.type}</Tag>
          </Space>
        ),
        data: child,
        isLeaf: true,
      }))
    }))

    const constraintNodes = ontologyData.constraints.map(constraint => ({
      key: constraint.key,
      title: (
        <Space>
          <SettingOutlined style={{ color: '#fa8c16' }} />
          <span>{constraint.name}</span>
          <Tag color="orange" size="small">约束</Tag>
        </Space>
      ),
      data: constraint,
      isLeaf: true,
    }))

    return [
      {
        key: 'root-entities',
        title: <Text strong>实体 ({ontologyData.entities.length})</Text>,
        children: entityNodes,
      },
      {
        key: 'root-constraints',
        title: <Text strong>约束 ({ontologyData.constraints.length})</Text>,
        children: constraintNodes,
      }
    ]
  }

  // 选择节点
  const handleSelectNode = (selectedKeys, info) => {
    if (info.node.data) {
      setSelectedNode(info.node.data)
    }
  }

  // 过滤搜索
  const filteredTreeData = () => {
    const treeData = buildTreeData()
    if (!searchText) return treeData

    const filterNode = (nodes) => {
      return nodes.filter(node => {
        const matchTitle = node.data?.name?.toLowerCase().includes(searchText.toLowerCase()) ||
                          node.data?.displayName?.toLowerCase().includes(searchText.toLowerCase())
        const matchChildren = node.children ? filterNode(node.children).length > 0 : false
        return matchTitle || matchChildren
      }).map(node => ({
        ...node,
        children: node.children ? filterNode(node.children) : undefined
      }))
    }

    return filterNode(treeData)
  }

  return (
    <div>
      <Row gutter={16}>
        {/* 左侧：本体树 */}
        <Col span={8}>
          <Card
            title={
              <Space>
                <ApartmentOutlined />
                本体结构
              </Space>
            }
            className="panel"
            extra={
              <Button icon={<ReloadOutlined />} size="small" onClick={loadOntologyData}>
                刷新
              </Button>
            }
            style={{ height: 'calc(100vh - 200px)' }}
          >
            <Search
              placeholder="搜索实体或字段..."
              allowClear
              onChange={(e) => setSearchText(e.target.value)}
              style={{ marginBottom: 16 }}
            />
            
            <Spin spinning={loading}>
              {ontologyData ? (
                <Tree
                  showLine
                  defaultExpandAll
                  treeData={filteredTreeData()}
                  onSelect={handleSelectNode}
                  style={{ maxHeight: 'calc(100vh - 350px)', overflow: 'auto' }}
                />
              ) : (
                <Empty description="暂无本体数据" />
              )}
            </Spin>
          </Card>
        </Col>

        {/* 中间：节点详情 */}
        <Col span={8}>
          <Card
            title={
              <Space>
                <NodeIndexOutlined />
                节点详情
              </Space>
            }
            className="panel"
            style={{ height: 'calc(100vh - 200px)' }}
          >
            {selectedNode ? (
              <div>
                <Title level={4}>{selectedNode.displayName || selectedNode.name}</Title>
                
                <Descriptions bordered column={1} size="small" style={{ marginTop: 16 }}>
                  <Descriptions.Item label="标识">
                    <Text code>{selectedNode.name}</Text>
                  </Descriptions.Item>
                  {selectedNode.displayName && (
                    <Descriptions.Item label="显示名称">
                      {selectedNode.displayName}
                    </Descriptions.Item>
                  )}
                  {selectedNode.description && (
                    <Descriptions.Item label="描述">
                      {selectedNode.description}
                    </Descriptions.Item>
                  )}
                  <Descriptions.Item label="类型">
                    <Tag color="blue">{selectedNode.type}</Tag>
                  </Descriptions.Item>
                  {selectedNode.productId && (
                    <Descriptions.Item label="产品ID">
                      <Tag color="green">{selectedNode.productId}</Tag>
                    </Descriptions.Item>
                  )}
                  {selectedNode.condition && (
                    <Descriptions.Item label="条件">
                      <Text code>{selectedNode.condition}</Text>
                    </Descriptions.Item>
                  )}
                  {selectedNode.action && (
                    <Descriptions.Item label="动作">
                      <Tag color="orange">{selectedNode.action}</Tag>
                    </Descriptions.Item>
                  )}
                </Descriptions>

                {selectedNode.children && (
                  <div style={{ marginTop: 16 }}>
                    <Title level={5}>子节点 ({selectedNode.children.length})</Title>
                    <Table
                      size="small"
                      pagination={false}
                      dataSource={selectedNode.children.map((c, i) => ({ ...c, key: i }))}
                      columns={[
                        { title: '名称', dataIndex: 'name', key: 'name' },
                        { 
                          title: '显示名', 
                          dataIndex: 'displayName', 
                          key: 'displayName',
                          render: (text) => text || '-'
                        },
                        { 
                          title: '类型', 
                          dataIndex: 'type', 
                          key: 'type',
                          render: (text) => <Tag>{text}</Tag>
                        },
                      ]}
                    />
                  </div>
                )}
              </div>
            ) : (
              <div style={{ textAlign: 'center', color: '#999', paddingTop: 150 }}>
                <NodeIndexOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <div>请在左侧选择一个节点查看详情</div>
              </div>
            )}
          </Card>
        </Col>

        {/* 右侧：关系视图 */}
        <Col span={8}>
          <Card
            title={
              <Space>
                <LinkOutlined />
                关系视图
              </Space>
            }
            className="panel"
            style={{ height: 'calc(100vh - 200px)' }}
          >
            {ontologyData?.relationships ? (
              <div>
                <Title level={5}>实体关系</Title>
                <Table
                  size="small"
                  pagination={false}
                  dataSource={ontologyData.relationships.map((r, i) => ({ ...r, key: i }))}
                  columns={[
                    { 
                      title: '源', 
                      dataIndex: 'from', 
                      key: 'from',
                      render: (text) => <Tag color="blue">{text}</Tag>
                    },
                    { 
                      title: '关系', 
                      dataIndex: 'type', 
                      key: 'type',
                      render: (text) => (
                        <Space>
                          <span>→</span>
                          <Text type="secondary">{text}</Text>
                          <span>→</span>
                        </Space>
                      )
                    },
                    { 
                      title: '目标', 
                      dataIndex: 'to', 
                      key: 'to',
                      render: (text) => <Tag color="green">{text}</Tag>
                    },
                  ]}
                />

                <div style={{ 
                  marginTop: 24,
                  padding: 24,
                  background: '#f5f5f5',
                  borderRadius: 8,
                  textAlign: 'center'
                }}>
                  <Title level={5}>图谱可视化</Title>
                  <div style={{ 
                    height: 200, 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center',
                    background: '#fff',
                    borderRadius: 4,
                    marginTop: 16
                  }}>
                    {/* 简化的图谱示意 */}
                    <div style={{ textAlign: 'center' }}>
                      <div style={{ marginBottom: 16 }}>
                        <Tag color="blue" style={{ padding: '8px 16px', fontSize: 14 }}>
                          AccessProdInst
                        </Tag>
                      </div>
                      <div style={{ display: 'flex', justifyContent: 'center', gap: 40 }}>
                        <div>
                          <div style={{ color: '#999', marginBottom: 8 }}>↓ HAS_TYPE</div>
                          <Tag color="green" style={{ padding: '4px 12px' }}>
                            BusinessType
                          </Tag>
                        </div>
                        <div>
                          <div style={{ color: '#999', marginBottom: 8 }}>↓ HAS_OPERATION</div>
                          <Tag color="green" style={{ padding: '4px 12px' }}>
                            OperationType
                          </Tag>
                        </div>
                      </div>
                    </div>
                  </div>
                  <Paragraph type="secondary" style={{ marginTop: 16 }}>
                    完整图谱可视化功能开发中...
                  </Paragraph>
                </div>
              </div>
            ) : (
              <Empty description="暂无关系数据" />
            )}
          </Card>
        </Col>
      </Row>
    </div>
  )
}

export default OntologyBrowser
