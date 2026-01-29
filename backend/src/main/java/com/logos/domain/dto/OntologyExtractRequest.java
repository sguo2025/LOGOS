package com.logos.domain.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 本体提取请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OntologyExtractRequest {
    
    /**
     * Java源码内容
     */
    private String sourceCode;
    
    /**
     * 产品ID（绑定到哪个产品）
     */
    private String productId;
    
    /**
     * 提取模式（AUTO/MANUAL）
     */
    private String mode;
}
