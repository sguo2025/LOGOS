package com.logos.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.logos.domain.dto.OntologyExtractRequest;
import com.logos.domain.dto.OntologyExtractResponse;
import com.logos.domain.entity.*;
import com.logos.llm.LlmClient;
import com.logos.llm.PromptTemplates;
import com.logos.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 本体服务
 * 处理知识提取、本体管理等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OntologyService {
    
    private final LlmClient llmClient;
    private final MetadataRepository metadataRepository;
    private final ProductRepository productRepository;
    private final EnumValueRepository enumValueRepository;
    private final EntityRepository entityRepository;
    
    /**
     * 从Java源码提取本体知识
     */
    public OntologyExtractResponse extractFromSource(OntologyExtractRequest request) {
        log.info("开始从源码提取本体知识, productId={}", request.getProductId());
        
        // 构建提示词
        String userPrompt = String.format(PromptTemplates.SOURCE_PARSE_USER_TEMPLATE, request.getSourceCode());
        
        // 调用LLM
        String llmResponse = llmClient.chat(PromptTemplates.SOURCE_PARSE_SYSTEM, userPrompt);
        
        // 解析响应
        return parseExtractResponse(llmResponse);
    }
    
    /**
     * 确认并保存提取的知识
     */
    @Transactional
    public void confirmExtraction(String productId, OntologyExtractResponse extraction) {
        log.info("确认并保存提取的知识, productId={}, nodes={}", 
                productId, extraction.getNodes().size());
        
        for (OntologyExtractResponse.ExtractedNode node : extraction.getNodes()) {
            switch (node.getType()) {
                case "Metadata" -> saveMetadata(productId, node);
                case "EnumValue" -> saveEnumValue(node);
            }
        }
    }
    
    /**
     * 获取产品的本体数据
     */
    @Transactional(readOnly = true)
    public OntologyExtractResponse getOntology(String productId) {
        List<MetadataNode> metadataList = metadataRepository.findByProductId(productId);
        List<OntologyExtractResponse.ExtractedNode> nodes = new ArrayList<>();
        List<OntologyExtractResponse.ExtractedRelation> relations = new ArrayList<>();
        
        for (MetadataNode meta : metadataList) {
            nodes.add(OntologyExtractResponse.ExtractedNode.builder()
                    .id(meta.getMetaId())
                    .name(meta.getName())
                    .type("Metadata")
                    .path(meta.getPath())
                    .confidence(meta.getConfidence())
                    .build());
            
            // 添加关系
            relations.add(OntologyExtractResponse.ExtractedRelation.builder()
                    .from(meta.getMetaId())
                    .to(productId)
                    .type("BELONGS_TO")
                    .confidence(1.0)
                    .build());
            
            // 添加枚举值
            List<EnumValueNode> enumValues = enumValueRepository.findByAttributeId(meta.getMetaId());
            for (EnumValueNode ev : enumValues) {
                nodes.add(OntologyExtractResponse.ExtractedNode.builder()
                        .id(ev.getValue())
                        .name(ev.getMeaning())
                        .type("EnumValue")
                        .confidence(ev.getConfidence())
                        .build());
            }
        }
        
        return OntologyExtractResponse.builder()
                .nodes(nodes)
                .relations(relations)
                .logicFragments(new ArrayList<>())
                .build();
    }
    
    /**
     * 获取所有产品列表
     */
    @Transactional(readOnly = true)
    public List<ProductNode> getAllProducts() {
        return productRepository.findAllProducts();
    }
    
    /**
     * 获取产品详情
     */
    @Transactional(readOnly = true)
    public ProductNode getProductDetail(String productId) {
        return productRepository.findByProdIdWithDetails(productId)
                .orElseThrow(() -> new RuntimeException("产品不存在: " + productId));
    }
    
    /**
     * 保存元数据
     */
    private void saveMetadata(String productId, OntologyExtractResponse.ExtractedNode node) {
        MetadataNode metadata = MetadataNode.builder()
                .metaId(node.getId())
                .name(node.getName())
                .path(node.getPath())
                .type("String")
                .source("LLM提取")
                .confidence(node.getConfidence())
                .build();
        metadataRepository.save(metadata);
    }
    
    /**
     * 保存枚举值
     */
    private void saveEnumValue(OntologyExtractResponse.ExtractedNode node) {
        // 需要从node中解析出attributeId
        EnumValueNode enumValue = EnumValueNode.builder()
                .value(node.getId())
                .meaning(node.getName())
                .confidence(node.getConfidence())
                .build();
        enumValueRepository.save(enumValue);
    }
    
    /**
     * 解析提取响应
     */
    private OntologyExtractResponse parseExtractResponse(String llmResponse) {
        try {
            String json = extractJson(llmResponse);
            JSONObject obj = JSON.parseObject(json);
            
            List<OntologyExtractResponse.ExtractedNode> nodes = new ArrayList<>();
            List<OntologyExtractResponse.ExtractedRelation> relations = new ArrayList<>();
            List<OntologyExtractResponse.ExtractedLogic> logicFragments = new ArrayList<>();
            
            // 解析nodes
            if (obj.containsKey("nodes")) {
                JSONArray nodesArray = obj.getJSONArray("nodes");
                for (int i = 0; i < nodesArray.size(); i++) {
                    JSONObject nodeObj = nodesArray.getJSONObject(i);
                    nodes.add(OntologyExtractResponse.ExtractedNode.builder()
                            .id(nodeObj.getString("id"))
                            .name(nodeObj.getString("name"))
                            .type(nodeObj.getString("type"))
                            .path(nodeObj.getString("path"))
                            .confidence(nodeObj.getDoubleValue("confidence"))
                            .evidence(nodeObj.getString("evidence"))
                            .build());
                }
            }
            
            // 解析relations
            if (obj.containsKey("relations")) {
                JSONArray relArray = obj.getJSONArray("relations");
                for (int i = 0; i < relArray.size(); i++) {
                    JSONObject relObj = relArray.getJSONObject(i);
                    relations.add(OntologyExtractResponse.ExtractedRelation.builder()
                            .from(relObj.getString("from"))
                            .to(relObj.getString("to"))
                            .type(relObj.getString("type"))
                            .confidence(relObj.getDoubleValue("confidence"))
                            .build());
                }
            }
            
            // 解析logicFragments
            if (obj.containsKey("logicFragments")) {
                JSONArray logicArray = obj.getJSONArray("logicFragments");
                for (int i = 0; i < logicArray.size(); i++) {
                    JSONObject logicObj = logicArray.getJSONObject(i);
                    logicFragments.add(OntologyExtractResponse.ExtractedLogic.builder()
                            .pattern(logicObj.getString("pattern"))
                            .explanation(logicObj.getString("explanation"))
                            .usedFields(logicObj.getList("usedFields", String.class))
                            .confidence(logicObj.getDoubleValue("confidence"))
                            .build());
                }
            }
            
            return OntologyExtractResponse.builder()
                    .nodes(nodes)
                    .relations(relations)
                    .logicFragments(logicFragments)
                    .build();
                    
        } catch (Exception e) {
            log.error("解析提取响应失败", e);
            return OntologyExtractResponse.builder()
                    .nodes(new ArrayList<>())
                    .relations(new ArrayList<>())
                    .logicFragments(new ArrayList<>())
                    .build();
        }
    }
    
    private String extractJson(String response) {
        int start = response.indexOf("{");
        int end = response.lastIndexOf("}");
        if (start >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        return response;
    }
}
