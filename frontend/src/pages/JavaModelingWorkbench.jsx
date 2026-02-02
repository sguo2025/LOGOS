import { useState } from 'react'
import {
  Card,
  Row,
  Col,
  Button,
  Upload,
  Tree,
  Table,
  Tag,
  message,
  Progress,
  Tabs,
  Space,
  Typography,
  Modal,
  Descriptions,
  Alert,
} from 'antd'
import {
  UploadOutlined,
  ApartmentOutlined,
  FileTextOutlined,
  ApiOutlined,
  CloudUploadOutlined,
  EyeOutlined,
  CheckCircleOutlined,
} from '@ant-design/icons'
import { ontologyApi } from '../services/api'

const { Title, Text, Paragraph } = Typography
const { TabPane } = Tabs

function JavaModelingWorkbench() {
  const [extracting, setExtracting] = useState(false)
  const [extractedData, setExtractedData] = useState(null)
  const [selectedNode, setSelectedNode] = useState(null)
  const [detailModalOpen, setDetailModalOpen] = useState(false)
  const [pushing, setPushing] = useState(false)

  // 代码内容状态 - 支持上传文件更新
  const [codeContent, setCodeContent] = useState(`package com.example.dto;

@Entity
@Description("接入产品实例")
public class AccessProdInst {
    
    @MetaField(name = "业务类型编码")
    private String businessTypeCode;
    
    @MetaField(name = "订单ID")
    private String soId;
    
    @MetaField(name = "操作类型")
    private String operType;
    
    @MetaField(name = "服务提供实例ID")  
    private String serviceOfferInstId;
    
    // getters and setters
}`)

  // 处理文件上传 - 读取文件内容并更新预览
  const handleUpload = async (info) => {
    const { file } = info
    if (file.status === 'done' && file.originFileObj) {
      const reader = new FileReader()
      reader.onload = (e) => {
        const content = e.target.result
        setCodeContent(content)
        message.success(`文件 ${file.name} 上传成功`)
      }
      reader.onerror = () => {
        message.error('文件读取失败')
      }
      reader.readAsText(file.originFileObj)
    }
  }

  // 执行提取
  const handleExtract = async () => {
    setExtracting(true)
    try {
      const response = await ontologyApi.extract({
        sourceCode: codeContent,
        sourceType: 'JAVA',
        productId: '80000122'
      })
      setExtractedData(response.data)
      message.success('本体提取成功')
    } catch (error) {
      // 使用模拟数据
      setExtractedData({
        extractedEntities: [
          {
            name: 'AccessProdInst',
            description: '接入产品实例',
            type: 'BUSINESS_CONTEXT',
            fields: [
              { name: 'businessTypeCode', displayName: '业务类型编码', type: 'String' },
              { name: 'soId', displayName: '订单ID', type: 'String' },
              { name: 'operType', displayName: '操作类型', type: 'String' },
              { name: 'serviceOfferInstId', displayName: '服务提供实例ID', type: 'String' },
            ]
          }
        ],
        extractedMetadata: [
          { semanticName: '业务类型编码', physicalPath: 'businessTypeCode', dataType: 'String' },
          { semanticName: '订单ID', physicalPath: 'soId', dataType: 'String' },
          { semanticName: '操作类型', physicalPath: 'operType', dataType: 'String' },
          { semanticName: '服务提供实例ID', physicalPath: 'serviceOfferInstId', dataType: 'String' },
        ],
        mappingPreview: [
          { from: 'businessTypeCode', to: 'COL1', type: 'METADATA' },
          { from: 'soId', to: 'SO_ID', type: 'METADATA' },
          { from: 'operType', to: 'OPER_TYPE', type: 'METADATA' },
        ]
      })
      message.success('本体提取成功（模拟数据）')
    } finally {
      setExtracting(false)
    }
  }

  // 推送到 Neo4j
  const handlePushToNeo4j = async () => {
    setPushing(true)
    try {
      await ontologyApi.init({ productId: '80000122' })
      message.success('本体已成功推送到 Neo4j')
    } catch (error) {
      message.error('推送失败: ' + (error.message || '未知错误'))
    } finally {
      setPushing(false)
    }
  }

  // 构建树形数据
  const buildTreeData = () => {
    if (!extractedData?.extractedEntities) return []
    
    return extractedData.extractedEntities.map((entity, idx) => ({
      key: `entity-${idx}`,
      title: (
        <Space>
          <ApartmentOutlined />
          <span>{entity.name}</span>
          <Tag color="blue">{entity.type}</Tag>
        </Space>
      ),
      data: entity,
      children: entity.fields?.map((field, fidx) => ({
        key: `entity-${idx}-field-${fidx}`,
        title: (
          <Space>
            <FileTextOutlined />
            <span>{field.displayName || field.name}</span>
            <Tag color="green">{field.type}</Tag>
          </Space>
        ),
        data: field,
        isLeaf: true,
      })) || []
    }))
  }

  // 选择节点
  const handleSelectNode = (selectedKeys, info) => {
    if (info.node.data) {
      setSelectedNode(info.node.data)
      setDetailModalOpen(true)
    }
  }

  return (
    <div>
      {/* 步骤提示 */}
      <Alert
        message="Java 代码建模流程"
        description="1. 上传 Java 代码文件 → 2. 提取本体元素 → 3. 审核映射关系 → 4. 推送到知识图谱"
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />

      <Row gutter={16}>
        {/* 左侧：源代码面板 */}
        <Col span={8}>
          <Card 
            title={
              <Space>
                <FileTextOutlined />
                源代码
              </Space>
            }
            className="panel"
            extra={
              <Upload
                accept=".java"
                showUploadList={false}
                customRequest={({ onSuccess }) => {
                  setTimeout(() => onSuccess('ok'), 1000)
                }}
                onChange={handleUpload}
              >
                <Button icon={<UploadOutlined />} size="small">
                  上传文件
                </Button>
              </Upload>
            }
            style={{ height: 600 }}
          >
            <pre style={{ 
              background: '#1e1e1e', 
              color: '#d4d4d4', 
              padding: 16, 
              borderRadius: 4,
              fontSize: 12,
              height: 450,
              overflow: 'auto',
              fontFamily: 'Monaco, Consolas, monospace'
            }}>
              <code>{codeContent}</code>
            </pre>

            <Button
              type="primary"
              icon={<ApiOutlined />}
              onClick={handleExtract}
              loading={extracting}
              style={{ marginTop: 16 }}
              block
            >
              提取本体
            </Button>
          </Card>
        </Col>

        {/* 中间：提取与映射面板 */}
        <Col span={8}>
          <Card
            title={
              <Space>
                <ApartmentOutlined />
                提取结果与映射
              </Space>
            }
            className="panel"
            style={{ height: 600 }}
          >
            {extractedData ? (
              <Tabs defaultActiveKey="tree">
                <TabPane tab="结构树" key="tree">
                  <Tree
                    showLine
                    defaultExpandAll
                    treeData={buildTreeData()}
                    onSelect={handleSelectNode}
                    style={{ maxHeight: 400, overflow: 'auto' }}
                  />
                </TabPane>
                <TabPane tab="元数据列表" key="metadata">
                  <Table
                    size="small"
                    pagination={false}
                    dataSource={extractedData.extractedMetadata?.map((m, i) => ({
                      ...m,
                      key: i
                    }))}
                    columns={[
                      { 
                        title: '语义名称', 
                        dataIndex: 'semanticName', 
                        key: 'semanticName',
                        render: (text) => <Tag color="blue">{text}</Tag>
                      },
                      { 
                        title: '物理路径', 
                        dataIndex: 'physicalPath', 
                        key: 'physicalPath',
                        render: (text) => <Text code>{text}</Text>
                      },
                      { 
                        title: '类型', 
                        dataIndex: 'dataType', 
                        key: 'dataType',
                        render: (text) => <Tag color="green">{text}</Tag>
                      },
                    ]}
                  />
                </TabPane>
                <TabPane tab="映射预览" key="mapping">
                  <Table
                    size="small"
                    pagination={false}
                    dataSource={extractedData.mappingPreview?.map((m, i) => ({
                      ...m,
                      key: i
                    }))}
                    columns={[
                      { 
                        title: '源字段', 
                        dataIndex: 'from', 
                        key: 'from',
                        render: (text) => <Text code>{text}</Text>
                      },
                      { 
                        title: '', 
                        key: 'arrow',
                        width: 40,
                        render: () => <span>→</span>
                      },
                      { 
                        title: '目标', 
                        dataIndex: 'to', 
                        key: 'to',
                        render: (text) => <Tag color="orange">{text}</Tag>
                      },
                      { 
                        title: '类型', 
                        dataIndex: 'type', 
                        key: 'type',
                        render: (text) => <Tag>{text}</Tag>
                      },
                    ]}
                  />
                </TabPane>
              </Tabs>
            ) : (
              <div style={{ textAlign: 'center', color: '#999', paddingTop: 150 }}>
                <ApartmentOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <div>请先上传代码并提取本体</div>
              </div>
            )}
          </Card>
        </Col>

        {/* 右侧：本体预览与操作 */}
        <Col span={8}>
          <Card
            title={
              <Space>
                <EyeOutlined />
                本体预览
              </Space>
            }
            className="panel"
            style={{ height: 600 }}
          >
            {extractedData ? (
              <div>
                <div style={{ marginBottom: 16 }}>
                  <Title level={5}>提取统计</Title>
                  <Row gutter={16}>
                    <Col span={12}>
                      <div className="stat-card" style={{ 
                        background: '#e6f7ff', 
                        padding: 16, 
                        borderRadius: 8,
                        textAlign: 'center'
                      }}>
                        <div style={{ fontSize: 24, fontWeight: 'bold', color: '#1890ff' }}>
                          {extractedData.extractedEntities?.length || 0}
                        </div>
                        <div style={{ color: '#666' }}>实体</div>
                      </div>
                    </Col>
                    <Col span={12}>
                      <div className="stat-card" style={{ 
                        background: '#f6ffed', 
                        padding: 16, 
                        borderRadius: 8,
                        textAlign: 'center'
                      }}>
                        <div style={{ fontSize: 24, fontWeight: 'bold', color: '#52c41a' }}>
                          {extractedData.extractedMetadata?.length || 0}
                        </div>
                        <div style={{ color: '#666' }}>元数据</div>
                      </div>
                    </Col>
                  </Row>
                </div>

                <div style={{ marginBottom: 16 }}>
                  <Title level={5}>质量评估</Title>
                  <div style={{ marginBottom: 8 }}>
                    <Text>完整度</Text>
                    <Progress percent={85} status="active" />
                  </div>
                  <div>
                    <Text>映射覆盖率</Text>
                    <Progress percent={75} status="active" />
                  </div>
                </div>

                <div style={{ marginBottom: 16 }}>
                  <Title level={5}>Neo4j 预览</Title>
                  <div style={{ 
                    background: '#f0f2f5', 
                    padding: 16, 
                    borderRadius: 8,
                    height: 150,
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    flexDirection: 'column'
                  }}>
                    <ApartmentOutlined style={{ fontSize: 32, color: '#1890ff', marginBottom: 8 }} />
                    <Text type="secondary">图谱预览（简化版）</Text>
                    <div style={{ marginTop: 8 }}>
                      <Tag color="blue">Entity</Tag>
                      <span style={{ margin: '0 8px' }}>→</span>
                      <Tag color="green">Metadata</Tag>
                    </div>
                  </div>
                </div>

                <Space direction="vertical" style={{ width: '100%' }}>
                  <Button
                    type="primary"
                    icon={<CloudUploadOutlined />}
                    onClick={handlePushToNeo4j}
                    loading={pushing}
                    block
                  >
                    推送到 Neo4j
                  </Button>
                  <Button block icon={<EyeOutlined />}>
                    在图谱浏览器中查看
                  </Button>
                </Space>
              </div>
            ) : (
              <div style={{ textAlign: 'center', color: '#999', paddingTop: 150 }}>
                <CloudUploadOutlined style={{ fontSize: 48, marginBottom: 16 }} />
                <div>提取后可预览本体结构</div>
              </div>
            )}
          </Card>
        </Col>
      </Row>

      {/* 节点详情弹窗 */}
      <Modal
        title="节点详情"
        open={detailModalOpen}
        onCancel={() => setDetailModalOpen(false)}
        footer={null}
      >
        {selectedNode && (
          <Descriptions bordered column={1}>
            <Descriptions.Item label="名称">
              {selectedNode.name || selectedNode.displayName}
            </Descriptions.Item>
            {selectedNode.description && (
              <Descriptions.Item label="描述">
                {selectedNode.description}
              </Descriptions.Item>
            )}
            {selectedNode.type && (
              <Descriptions.Item label="类型">
                <Tag color="blue">{selectedNode.type}</Tag>
              </Descriptions.Item>
            )}
            {selectedNode.fields && (
              <Descriptions.Item label="字段数">
                {selectedNode.fields.length}
              </Descriptions.Item>
            )}
          </Descriptions>
        )}
      </Modal>
    </div>
  )
}

export default JavaModelingWorkbench
