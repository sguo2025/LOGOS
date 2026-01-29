package com.logos.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.logos.domain.dto.OntologyExtractResponse;
import com.logos.domain.dto.OntologyExtractResponse.ExtractedNode;
import com.logos.domain.dto.OntologyExtractResponse.ExtractedRelation;
import com.logos.domain.entity.ActionNode;
import com.logos.domain.entity.BusinessConstraintNode;
import com.logos.domain.entity.EntityNode;
import com.logos.domain.entity.MetadataNode;
import com.logos.llm.LlmClient;
import com.logos.llm.PromptTemplates;
import com.logos.repository.ActionRepository;
import com.logos.repository.BusinessConstraintRepository;
import com.logos.repository.EntityRepository;
import com.logos.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 本体服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OntologyServiceImpl implements OntologyService {

    private final LlmClient llmClient;
    private final EntityRepository entityRepository;
    private final MetadataRepository metadataRepository;
    private final BusinessConstraintRepository constraintRepository;
    private final ActionRepository actionRepository;

    @Override
    public OntologyExtractResponse extractFromCode(MultipartFile file) {
        log.info("开始从源码提取本体，文件: {}", file.getOriginalFilename());

        try {
            // 读取文件内容
            String code = new String(file.getBytes(), StandardCharsets.UTF_8);

            // 调用 LLM 提取
            String userMessage = PromptTemplates.buildCodeExtractionUserMessage(code);
            String llmResponse = llmClient.chat(PromptTemplates.CODE_EXTRACTION_SYSTEM, userMessage);

            // 解析响应
            return parseExtractResponse(llmResponse);
        } catch (IOException e) {
            log.error("读取文件失败", e);
            throw new RuntimeException("读取文件失败: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void initializeOntology() {
        log.info("开始初始化本体数据...");

        // 1. 创建核心实体节点
        createEntityNodes();

        // 2. 创建元数据节点
        createMetadataNodes();

        // 3. 创建业务约束节点
        createConstraintNodes();

        // 4. 创建动作节点
        createActionNodes();

        log.info("本体数据初始化完成");
    }

    private void createEntityNodes() {
        EntityNode ruleContext = new EntityNode();
        ruleContext.setCode("RuleContext");
        ruleContext.setName("规则上下文");
        ruleContext.setDescription("输入参数与执行环境");
        entityRepository.save(ruleContext);

        EntityNode prodInst = new EntityNode();
        prodInst.setCode("ProdInst");
        prodInst.setName("产品实例");
        prodInst.setDescription("规则检查的目标对象");
        entityRepository.save(prodInst);

        EntityNode businessConstraint = new EntityNode();
        businessConstraint.setCode("BusinessConstraint");
        businessConstraint.setName("业务约束");
        businessConstraint.setDescription("业务逻辑与判断条件");
        entityRepository.save(businessConstraint);

        log.info("创建实体节点完成");
    }

    private void createMetadataNodes() {
        // RuleContext 属性
        MetadataNode soId = new MetadataNode();
        soId.setId("soId");
        soId.setName("服务提供ID");
        soId.setPath("orderRequest.serviceOfferId");
        soId.setType("String");
        metadataRepository.save(soId);

        MetadataNode operType = new MetadataNode();
        operType.setId("operType");
        operType.setName("操作类型");
        operType.setPath("orderRequest.operType");
        operType.setType("String");
        metadataRepository.save(operType);

        // ProdInst 属性
        MetadataNode prodId = new MetadataNode();
        prodId.setId("prodId");
        prodId.setName("产品规格ID");
        prodId.setPath("PROD_ID");
        prodId.setType("String");
        metadataRepository.save(prodId);

        MetadataNode businessTypeCode = new MetadataNode();
        businessTypeCode.setId("businessTypeCode");
        businessTypeCode.setName("业务类型编码");
        businessTypeCode.setPath("COL1");
        businessTypeCode.setType("String");
        metadataRepository.save(businessTypeCode);

        MetadataNode actionType = new MetadataNode();
        actionType.setId("actionType");
        actionType.setName("动作类型");
        actionType.setPath("ACTION_TYPE");
        actionType.setType("String");
        metadataRepository.save(actionType);

        log.info("创建元数据节点完成");
    }

    private void createConstraintNodes() {
        BusinessConstraintNode bc = new BusinessConstraintNode();
        bc.setId("BC_LX_001");
        bc.setName("灵犀融合光网约束");
        bc.setTargetProductId("80000122");
        bc.setTargetBusinessType("3");
        bc.setAllowedActions("2831");
        bc.setExemptOperTypes("[\"1100\", \"1200\"]");
        bc.setErrorMessage("灵犀专线业务类型为融合光网时，只允许做拆机操作");
        constraintRepository.save(bc);

        log.info("创建业务约束节点完成");
    }

    private void createActionNodes() {
        ActionNode skip = new ActionNode();
        skip.setCode("ShouldSkipCheck");
        skip.setHandler("LogosUtils.shouldSkip");
        skip.setDescription("判断是否跳过检查");
        actionRepository.save(skip);

        ActionNode validate = new ActionNode();
        validate.setCode("ValidateConstraint");
        validate.setHandler("LogosEngine.validate");
        validate.setDescription("验证约束逻辑");
        actionRepository.save(validate);

        ActionNode block = new ActionNode();
        block.setCode("BlockExecution");
        block.setHandler("Errors.error");
        block.setDescription("阻断执行");
        actionRepository.save(block);

        log.info("创建动作节点完成");
    }

    private OntologyExtractResponse parseExtractResponse(String llmResponse) {
        try {
            String jsonStr = extractJson(llmResponse);
            JSONObject json = JSON.parseObject(jsonStr);

            List<ExtractedNode> nodes = new ArrayList<>();
            JSONArray nodesArray = json.getJSONArray("nodes");
            if (nodesArray != null) {
                for (int i = 0; i < nodesArray.size(); i++) {
                    JSONObject nodeObj = nodesArray.getJSONObject(i);
                    nodes.add(ExtractedNode.builder()
                            .id(nodeObj.getString("id"))
                            .name(nodeObj.getString("name"))
                            .type(nodeObj.getString("type"))
                            .path(nodeObj.getString("path"))
                            .build());
                }
            }

            List<ExtractedRelation> relations = new ArrayList<>();
            JSONArray relationsArray = json.getJSONArray("relations");
            if (relationsArray != null) {
                for (int i = 0; i < relationsArray.size(); i++) {
                    JSONObject relObj = relationsArray.getJSONObject(i);
                    relations.add(ExtractedRelation.builder()
                            .from(relObj.getString("from"))
                            .to(relObj.getString("to"))
                            .type(relObj.getString("type"))
                            .build());
                }
            }

            return OntologyExtractResponse.builder()
                    .nodes(nodes)
                    .relations(relations)
                    .build();
        } catch (Exception e) {
            log.warn("解析提取响应失败", e);
            return OntologyExtractResponse.builder()
                    .nodes(List.of())
                    .relations(List.of())
                    .build();
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
