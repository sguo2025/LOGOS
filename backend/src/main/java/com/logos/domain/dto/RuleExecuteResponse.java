package com.logos.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 规则执行响应 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "规则执行响应")
public class RuleExecuteResponse {

    @Schema(description = "是否通过", example = "true")
    private Boolean passed;

    @Schema(description = "执行结果")
    private Object result;

    @Schema(description = "错误消息（如果被拦截）")
    private String errorMessage;

    @Schema(description = "执行耗时（毫秒）", example = "15")
    private Long executionTime;

    @Schema(description = "执行日志")
    private List<String> logs;

    @Schema(description = "推理溯源路径")
    private String trace;

    /**
     * 创建通过结果
     */
    public static RuleExecuteResponse pass(Object result, long executionTime, List<String> logs, String trace) {
        return RuleExecuteResponse.builder()
                .passed(true)
                .result(result)
                .executionTime(executionTime)
                .logs(logs)
                .trace(trace)
                .build();
    }

    /**
     * 创建拦截结果
     */
    public static RuleExecuteResponse block(String errorMessage, long executionTime, List<String> logs, String trace) {
        return RuleExecuteResponse.builder()
                .passed(false)
                .errorMessage(errorMessage)
                .executionTime(executionTime)
                .logs(logs)
                .trace(trace)
                .build();
    }
}
