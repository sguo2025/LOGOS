package com.logos.controller;

import com.logos.domain.dto.*;
import com.logos.domain.entity.ProductNode;
import com.logos.service.OntologyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 本体管理控制器
 * 提供知识提取、本体查询等接口
 */
@Slf4j
@RestController
@RequestMapping("/api/logos/v1/ontology")
@RequiredArgsConstructor
@Tag(name = "Knowledge Engine", description = "知识引擎接口")
@CrossOrigin(origins = "*")
public class OntologyController {
    
    private final OntologyService ontologyService;
    
    /**
     * 源码知识提取
     */
    @PostMapping("/extract")
    @Operation(summary = "源码知识提取", description = "上传Java插件源码，利用LLM解析并提取实体、属性及逻辑约束")
    public ApiResponse<OntologyExtractResponse> extractFromSource(@RequestBody OntologyExtractRequest request) {
        log.info("收到知识提取请求: productId={}", request.getProductId());
        
        try {
            OntologyExtractResponse response = ontologyService.extractFromSource(request);
            return ApiResponse.success(response);
        } catch (Exception e) {
            log.error("知识提取失败", e);
            return ApiResponse.error(ErrorCode.LLM_ERROR, e.getMessage());
        }
    }
    
    /**
     * 确认提取结果
     */
    @PostMapping("/confirm/{productId}")
    @Operation(summary = "确认并保存提取的知识")
    public ApiResponse<Void> confirmExtraction(
            @PathVariable String productId,
            @RequestBody OntologyExtractResponse extraction) {
        log.info("确认知识提取: productId={}", productId);
        
        try {
            ontologyService.confirmExtraction(productId, extraction);
            return ApiResponse.success(null);
        } catch (Exception e) {
            log.error("确认提取失败", e);
            return ApiResponse.error(ErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
    
    /**
     * 获取产品本体数据
     */
    @GetMapping("/{productId}")
    @Operation(summary = "获取产品本体数据")
    public ApiResponse<OntologyExtractResponse> getOntology(@PathVariable String productId) {
        OntologyExtractResponse response = ontologyService.getOntology(productId);
        return ApiResponse.success(response);
    }
    
    /**
     * 获取所有产品列表
     */
    @GetMapping("/products")
    @Operation(summary = "获取所有产品列表")
    public ApiResponse<List<ProductNode>> getAllProducts() {
        List<ProductNode> products = ontologyService.getAllProducts();
        return ApiResponse.success(products);
    }
    
    /**
     * 获取产品详情
     */
    @GetMapping("/product/{productId}")
    @Operation(summary = "获取产品详情")
    public ApiResponse<ProductNode> getProductDetail(@PathVariable String productId) {
        try {
            ProductNode product = ontologyService.getProductDetail(productId);
            return ApiResponse.success(product);
        } catch (Exception e) {
            log.error("获取产品详情失败", e);
            return ApiResponse.error(ErrorCode.PRODUCT_NOT_FOUND, productId);
        }
    }
}
