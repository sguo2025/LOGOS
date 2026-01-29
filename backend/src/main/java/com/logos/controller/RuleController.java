package com.logos.controller;

import com.logos.domain.dto.*;
import com.logos.domain.entity.RuleNode;
import com.logos.service.RuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 规则管理控制器
 * 提供NL2SpEL生成、规则验证、规则发布等接口
 */
@Slf4j
@RestController
@RequestMapping("/api/logos/v1/rule")
@RequiredArgsConstructor
@Tag(name = "Rule Lifecycle", description = "规则生命周期管理接口")
@CrossOrigin(origins = "*")
public class RuleController {
    
    private final RuleService ruleService;
    
    /**
     * 自然语言生成SpEL
     */
    @PostMapping("/generate")
    @Operation(summary = "自然语言生成SpEL", description = "基于本体图谱RAG，将自然语言需求转化为可执行的SpEL脚本")
    public ApiResponse<RuleGenerateResponse> generateRule(@Valid @RequestBody RuleGenerateRequest request) {
        log.info("收到规则生成请求: productId={}, nl={}", request.getProductId(), request.getNaturalLanguage());
        
        try {
            RuleGenerateResponse response = ruleService.generateRule(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("规则生成失败", e);
            return ApiResponse.error(ErrorCode.LLM_ERROR, e.getMessage());
        }
    }
    
    /**
     * 规则仿真验证
     */
    @PostMapping("/validate")
    @Operation(summary = "规则仿真与执行", description = "构造Mock数据对生成的SpEL进行逻辑闭环验证")
    public ApiResponse<RuleValidateResponse> validateRule(@Valid @RequestBody RuleValidateRequest request) {
        log.info("收到规则验证请求: spel={}", request.getSpel());
        
        try {
            RuleValidateResponse response = ruleService.validateRule(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("规则验证失败", e);
            return ApiResponse.error(ErrorCode.SANDBOX_EXEC_FAILED, e.getMessage());
        }
    }
    
    /**
     * 保存规则草稿
     */
    @PostMapping("/save")
    @Operation(summary = "保存规则草稿")
    public ApiResponse<RuleNode> saveRule(@RequestBody SaveRuleRequest request) {
        log.info("保存规则: productId={}, name={}", request.getProductId(), request.getName());
        
        try {
            RuleNode rule = ruleService.saveRule(
                    request.getProductId(),
                    request.getName(),
                    request.getNaturalLanguage(),
                    request.getSpel(),
                    request.getExplanation(),
                    request.getConfidence()
            );
            return ApiResponse.success(rule);
        } catch (Exception e) {
            log.error("保存规则失败", e);
            return ApiResponse.error(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
    
    /**
     * 获取产品的所有规则
     */
    @GetMapping("/list/{productId}")
    @Operation(summary = "获取产品规则列表")
    public ApiResponse<List<RuleNode>> getRulesByProductId(@PathVariable String productId) {
        List<RuleNode> rules = ruleService.getRulesByProductId(productId);
        return ApiResponse.success(rules);
    }
    
    /**
     * 发布规则
     */
    @PostMapping("/publish/{ruleId}")
    @Operation(summary = "发布规则")
    public ApiResponse<RuleNode> publishRule(@PathVariable String ruleId) {
        log.info("发布规则: ruleId={}", ruleId);
        
        try {
            RuleNode rule = ruleService.publishRule(ruleId);
            return ApiResponse.success(rule);
        } catch (Exception e) {
            log.error("发布规则失败", e);
            return ApiResponse.error(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
    
    /**
     * 保存规则请求DTO
     */
    @lombok.Data
    public static class SaveRuleRequest {
        private String productId;
        private String name;
        private String naturalLanguage;
        private String spel;
        private String explanation;
        private Double confidence;
    }
}
