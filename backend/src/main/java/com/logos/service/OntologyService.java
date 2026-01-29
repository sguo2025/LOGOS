package com.logos.service;

import com.logos.domain.dto.OntologyExtractResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 本体服务接口
 */
public interface OntologyService {

    /**
     * 从源码提取本体知识
     *
     * @param file Java 源码文件
     * @return 提取结果
     */
    OntologyExtractResponse extractFromCode(MultipartFile file);

    /**
     * 初始化本体数据
     */
    void initializeOntology();
}
