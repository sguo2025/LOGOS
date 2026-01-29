package com.logos.service;

import com.logos.domain.dto.RuleGenerateRequest;
import com.logos.domain.dto.RuleGenerateResponse;
import com.logos.domain.dto.RuleValidateRequest;
import com.logos.domain.dto.RuleValidateResponse;

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
}
