package com.logos.service;

import com.logos.domain.dto.RuleGenerateRequest;
import com.logos.domain.dto.RuleGenerateResponse;
import com.logos.domain.dto.RuleValidateRequest;
import com.logos.domain.dto.RuleValidateResponse;
import com.logos.domain.dto.RuleUpdateRequest;
import com.logos.domain.dto.RuleUpdateResponse;
import com.logos.domain.dto.RuleExecuteRequest;
import com.logos.domain.dto.RuleExecuteResponse;

/**
 * 规则服务接口
 */
public interface RuleService {

    /**
     * 根据自然语言生成 SpEL 表达式
     *
     * @param request 生成请求
     * @return 生成响应
     */
    RuleGenerateResponse generate(RuleGenerateRequest request);

    /**
     * 验证 SpEL 表达式
     *
     * @param request 验证请求
     * @return 验证响应
     */
    RuleValidateResponse validate(RuleValidateRequest request);

    /**
     * 更新规则并热发布
     * 对齐规范 4.4 章节 - 规则热加载
     *
     * @param request 更新请求
     * @return 更新响应
     */
    RuleUpdateResponse updateRule(RuleUpdateRequest request);

    /**
     * 执行规则
     *
     * @param request 执行请求
     * @return 执行响应
     */
    RuleExecuteResponse executeRule(RuleExecuteRequest request);
}
