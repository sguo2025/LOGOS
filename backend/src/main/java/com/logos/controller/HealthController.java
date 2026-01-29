package com.logos.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@Tag(name = "Health", description = "健康检查接口")
public class HealthController {

    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查服务是否正常运行")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("service", "LOGOS Backend");
        result.put("version", "1.0.0");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
