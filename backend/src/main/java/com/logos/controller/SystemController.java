package com.logos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统健康检查控制器
 */
@RestController
@RequestMapping("/api/logos/v1")
@Tag(name = "System", description = "系统接口")
@CrossOrigin(origins = "*")
public class SystemController {
    
    @Value("${spring.application.name:LOGOS}")
    private String appName;
    
    /**
     * 健康检查
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("application", appName);
        result.put("timestamp", LocalDateTime.now().toString());
        result.put("version", "1.0.0");
        return result;
    }
    
    /**
     * 系统信息
     */
    @GetMapping("/info")
    @Operation(summary = "系统信息")
    public Map<String, Object> info() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "LOGOS - Logic Ontology Generation & Operation System");
        result.put("description", "智能规则中台：基于本体建模与大模型的业务规则进化系统");
        result.put("version", "1.0.0");
        result.put("features", new String[]{
                "NL2SpEL - 自然语言到SpEL转换",
                "Source-to-KG - 源码知识提取",
                "Sandbox - 规则仿真验证",
                "Ontology - 本体知识管理"
        });
        return result;
    }
}
