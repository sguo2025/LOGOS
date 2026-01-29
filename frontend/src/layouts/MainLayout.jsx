import { Layout, Menu } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  HomeOutlined,
  CodeOutlined,
  ApiOutlined,
  DatabaseOutlined,
} from '@ant-design/icons'

const { Header, Content, Sider, Footer } = Layout

const menuItems = [
  {
    key: '/',
    icon: <HomeOutlined />,
    label: '首页',
  },
  {
    key: '/nl2spel',
    icon: <ApiOutlined />,
    label: 'NL2SpEL 工作台',
  },
  {
    key: '/modeling',
    icon: <CodeOutlined />,
    label: 'Java 代码建模',
  },
  {
    key: '/ontology',
    icon: <DatabaseOutlined />,
    label: '本体浏览器',
  },
]

function MainLayout() {
  const navigate = useNavigate()
  const location = useLocation()

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        breakpoint="lg"
        collapsedWidth="0"
        style={{ background: '#001529' }}
      >
        <div className="logo">LOGOS</div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={menuItems}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: '0 24px', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
          <h2 style={{ margin: 0 }}>智能规则中台</h2>
          <span style={{ color: '#999' }}>基于本体建模与大模型的业务规则进化平台</span>
        </Header>
        <Content className="site-layout-content">
          <Outlet />
        </Content>
        <Footer style={{ textAlign: 'center' }}>
          LOGOS ©2026 - 智能规则中台
        </Footer>
      </Layout>
    </Layout>
  )
}

export default MainLayout
