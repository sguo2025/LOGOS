package com.logos.controller;

import com.logos.domain.dto.ApiResponse;
import com.logos.domain.dto.OntologyExtractResponse;
import com.logos.service.OntologyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 本体知识引擎控制器
 */
@Slf4j
@RestController
@RequestMapping("/ontology")
@RequiredArgsConstructor
@Tag(name = "Knowledge Engine", description = "本体知识引擎接口")
public class OntologyController {

    private final OntologyService ontologyService;

    @PostMapping("/extract")
    @Operation(summary = "源码知识提取", description = "上传 Java 插件源码，利用 LLM 解析并提取实体、属性及逻辑约束")
    public ApiResponse<OntologyExtractResponse> extract(@RequestParam("file") MultipartFile file) {
        log.info("收到源码提取请求，文件名: {}", file.getOriginalFilename());
        OntologyExtractResponse response = ontologyService.extractFromCode(file);
        return ApiResponse.success(response);
    }

    @PostMapping("/init")
    @Operation(summary = "初始化本体数据", description = "初始化系统预置的本体数据")
    public ApiResponse<String> initialize() {
        log.info("收到本体初始化请求");
        ontologyService.initializeOntology();
        return ApiResponse.success("本体数据初始化完成");
    }
}
