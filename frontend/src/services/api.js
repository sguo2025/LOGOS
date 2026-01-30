/**
 * LOGOS 智能规则中台 - API 服务
 */

const API_BASE_URL = '/api/logos/v1';

/**
 * 通用请求函数
 */
async function request(url, options = {}) {
  const config = {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    ...options,
  };

  try {
    const response = await fetch(`${API_BASE_URL}${url}`, config);
    const data = await response.json();
    
    if (!response.ok) {
      throw new Error(data.message || `HTTP error! status: ${response.status}`);
    }
    
    return data;
  } catch (error) {
    console.error('API request failed:', error);
    throw error;
  }
}

/**
 * 规则相关 API
 */
export const ruleApi = {
  /**
   * 自然语言生成 SpEL 表达式
   * @param {Object} params - 请求参数
   * @param {string} params.naturalLanguage - 自然语言描述
   * @param {Object} params.context - 上下文信息
   */
  generate: (params) => {
    return request('/rule/generate', {
      method: 'POST',
      body: JSON.stringify(params),
    });
  },

  /**
   * 验证 SpEL 表达式
   * @param {Object} params - 请求参数
   * @param {string} params.spelExpression - SpEL 表达式
   * @param {Object} params.testData - 测试数据
   */
  validate: (params) => {
    return request('/rule/validate', {
      method: 'POST',
      body: JSON.stringify(params),
    });
  },

  /**
   * 执行 SpEL 表达式
   * @param {Object} params - 请求参数
   * @param {string} params.spelExpression - SpEL 表达式
   * @param {Object} params.context - 执行上下文
   */
  execute: (params) => {
    return request('/rule/execute', {
      method: 'POST',
      body: JSON.stringify(params),
    });
  },
};

/**
 * 本体相关 API
 */
export const ontologyApi = {
  /**
   * 从 Java 代码提取本体
   * @param {string} javaCode - Java 代码
   */
  extract: (javaCode) => {
    return request('/ontology/extract', {
      method: 'POST',
      body: JSON.stringify({ javaCode }),
    });
  },

  /**
   * 获取所有实体
   */
  getEntities: () => {
    return request('/ontology/entities', {
      method: 'GET',
    });
  },

  /**
   * 根据实体名称获取实体详情
   * @param {string} name - 实体名称
   */
  getEntityByName: (name) => {
    return request(`/ontology/entities/${encodeURIComponent(name)}`, {
      method: 'GET',
    });
  },

  /**
   * 获取所有元数据
   */
  getMetadata: () => {
    return request('/ontology/metadata', {
      method: 'GET',
    });
  },

  /**
   * 获取所有业务约束
   */
  getConstraints: () => {
    return request('/ontology/constraints', {
      method: 'GET',
    });
  },

  /**
   * 获取所有动作
   */
  getActions: () => {
    return request('/ontology/actions', {
      method: 'GET',
    });
  },

  /**
   * 保存实体
   * @param {Object} entity - 实体对象
   */
  saveEntity: (entity) => {
    return request('/ontology/entities', {
      method: 'POST',
      body: JSON.stringify(entity),
    });
  },

  /**
   * 保存元数据
   * @param {Object} metadata - 元数据对象
   */
  saveMetadata: (metadata) => {
    return request('/ontology/metadata', {
      method: 'POST',
      body: JSON.stringify(metadata),
    });
  },

  /**
   * 保存业务约束
   * @param {Object} constraint - 业务约束对象
   */
  saveConstraint: (constraint) => {
    return request('/ontology/constraints', {
      method: 'POST',
      body: JSON.stringify(constraint),
    });
  },

  /**
   * 保存动作
   * @param {Object} action - 动作对象
   */
  saveAction: (action) => {
    return request('/ontology/actions', {
      method: 'POST',
      body: JSON.stringify(action),
    });
  },
};

/**
 * 健康检查 API
 */
export const healthApi = {
  /**
   * 获取服务健康状态
   */
  check: () => {
    return request('/health', {
      method: 'GET',
    });
  },
};

export default {
  rule: ruleApi,
  ontology: ontologyApi,
  health: healthApi,
};
