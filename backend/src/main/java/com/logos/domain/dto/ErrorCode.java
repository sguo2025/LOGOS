package com.logos.domain.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 错误码枚举
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // 成功
    SUCCESS(0, "操作成功"),
    
    // 本体相关错误 (4001-4099)
    ONTOLOGY_NOT_FOUND(4001, "本体库中未找到对应的产品或属性"),
    AMBIGUOUS_INTENT(4002, "自然语言描述存在二义性，无法确定逻辑方向"),
    PRODUCT_NOT_FOUND(4003, "产品不存在"),
    METADATA_NOT_FOUND(4004, "元数据不存在"),
    
    // SpEL相关错误 (5001-5099)
    SPEL_SYNTAX_ERROR(5001, "SpEL脚本存在语法错误"),
    SPEL_EXECUTION_ERROR(5002, "SpEL脚本执行失败"),
    SPEL_TIMEOUT(5003, "SpEL脚本执行超时"),
    
    // LLM相关错误 (5101-5199)
    LLM_TIMEOUT(5101, "大模型响应超时"),
    LLM_ERROR(5102, "大模型调用失败"),
    LLM_INVALID_RESPONSE(5103, "大模型返回格式异常"),
    
    // 沙箱相关错误 (6001-6099)
    SANDBOX_EXEC_FAILED(6001, "仿真执行异常"),
    SANDBOX_MISSING_FIELD(6002, "Mock数据缺失必要字段"),
    
    // 通用错误 (9001-9999)
    INVALID_PARAMETER(9001, "参数错误"),
    INTERNAL_ERROR(9999, "系统内部错误");
    
    private final int code;
    private final String message;
}
