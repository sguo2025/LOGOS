package com.logos.controller;

import com.logos.domain.dto.ApiResponse;
import com.logos.domain.dto.RuleGenerateRequest;
import com.logos.domain.dto.RuleGenerateResponse;
import com.logos.domain.dto.RuleValidateRequest;
import com.logos.domain.dto.RuleValidateResponse;
import com.logos.domain.dto.RuleUpdateRequest;
import com.logos.domain.dto.RuleUpdateResponse;
import com.logos.domain.dto.RuleExecuteRequest;
import com.logos.domain.dto.RuleExecuteResponse;
import com.logos.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 规则生命周期控制器
 * 对齐规范 4.4 章节 - 标准 OpenAPI 接口
 */
@Slf4j
@RestController
@RequestMapping("/rule")
@RequiredArgsConstructor
@Tag(name = "Rule Lifecycle", description = "规则生命周期管理接口")
public class RuleController {

    private final RuleService ruleService;

    @PostMapping("/generate")
    @Operation(summary = "自然语言生成 SpEL", description = "基于本体图谱 RAG，将自然语言需求转化为可执行的 SpEL 脚本")
    public ApiResponse<RuleGenerateResponse> generate(@Valid @RequestBody RuleGenerateRequest request) {
        log.info("收到规则生成请求: {}", request);
        RuleGenerateResponse response = ruleService.generate(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/validate")
    @Operation(summary = "规则仿真与执行", description = "构造 Mock 数据对生成的 SpEL 进行逻辑闭环验证")
    public ApiResponse<RuleValidateResponse> validate(@Valid @RequestBody RuleValidateRequest request) {
        log.info("收到规则验证请求: {}", request);
        RuleValidateResponse response = ruleService.validate(request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/BusiRule")
    @Operation(summary = "规则热更新与发布", description = "实现规则的实时优化与热发布，对齐规范序号 1-2")
    public ApiResponse<RuleUpdateResponse> updateRule(@Valid @RequestBody RuleUpdateRequest request) {
        log.info("收到规则更新请求: {}", request);
        RuleUpdateResponse response = ruleService.updateRule(request);
        return ApiResponse.success(response);
    }

    @PostMapping("/execute")
    @Operation(summary = "规则执行", description = "执行指定规则并返回结果，支持高性能缓存")
    public ApiResponse<RuleExecuteResponse> executeRule(@Valid @RequestBody RuleExecuteRequest request) {
        log.info("收到规则执行请求: {}", request);
        RuleExecuteResponse response = ruleService.executeRule(request);
        return ApiResponse.success(response);
    }
}
