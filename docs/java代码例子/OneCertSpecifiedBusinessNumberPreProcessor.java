import com.iwhalecloud.bss.kite.common.util.KiteStringUtils;
import com.iwhalecloud.bss.kite.ctcc.rule.dataplug.order.CtccAttrModifyLinkCheckPlugin;
import com.iwhalecloud.bss.kite.dataservice.cache.DcPublicCache;
import com.iwhalecloud.bss.kite.dataservice.entity.DcPublic;
import com.iwhalecloud.bss.kite.local.common.consts.LocalKeyValues;
import com.iwhalecloud.bss.kite.manager.api.IOperateSceneInstService;
import com.iwhalecloud.bss.kite.manager.api.IQuerySceneInstService;
import com.iwhalecloud.bss.kite.manager.enums.ActionType;
import com.iwhalecloud.bss.kite.manager.inst.KiteProdInst;
import com.ztesoft.bss.rul.core.client.dto.instspec.rel.AccessProdInstSpecRel;
import com.ztesoft.bss.rul.core.engine.api.IRuleActionBusiProcessor;
import com.ztesoft.bss.rul.core.engine.event.CommonRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 一证N宽预校验前置处理规则，注意需要配合规则 OneCertSpecifiedBusinessNumberPlugin 使用，这边收集数据，那边做校验
 *
 *
 * <p>触发场景：宽带和固话产品，在新装、过户、改使用人、客户归并场景
 * <p>触发时机：去结算前
 * <p>规则ID：1700300102779221
 * <p>规则名称：一证N宽预校验前置处理规则
 *
 * @author 0027024085
 * @see com.iwhalecloud.bss.kite.local.rule.dataplug.OneCertSpecifiedBusinessNumberPlugin
 */
@Component
public class OneCertSpecifiedBusinessNumberPreProcessor implements IRuleActionBusiProcessor {

    public static final String JUDGE_FLAG = "OneCertSpecifiedBusinessNumberPreProcessor";

    @Autowired
    private IQuerySceneInstService querySceneInstService;

    @Autowired
    private IOperateSceneInstService operateSceneInstService;

    @Autowired
    private CtccAttrModifyLinkCheckPlugin ctccAttrModifyLinkCheckPlugin;

    @Override
    public void execute(CommonRequestDTO request, Map<String, Object> map) {
        if (request.getInstSpecRel() instanceof AccessProdInstSpecRel && isOpenPreJudge()) {
            String reqId = KiteStringUtils.defaultIfEmpty(request.getReqId(), LocalKeyValues.STRING_TRUE);
            // REQ_ID->客户ID->产品ID->产品实例
            Map<String, Map<String, Map<String, Set<String>>>> collectCheckMap = getCollectCheckMap(request, reqId);
            // 客户ID->产品ID->产品实例
            Map<String, Map<String, Set<String>>> custIdToProdInstId = collectCheckMap.get(reqId);
            DcPublic config = getProdConfig(request.getInstSpecRel().getSpecId());
            KiteProdInst prodInst = querySceneInstService.getProdInst(request.getCustId(), request.getInstSpecRel().getInstId());
            if (prodInst == null || ActionType.D.equals(prodInst.getActionType())) {
                return;
            }
            // 通用配置，校验类别 1 产权客户 2 使用客户
            String checkType = getCheckType();
            String serviceOfferId = request.getInstSpecRel().getServiceOfferId();
            String custIdToCheck = getCustIdToCheck(checkType, serviceOfferId, prodInst);
            if (KiteStringUtils.isNotEmpty(custIdToCheck)) {
                boolean isPassAttrCheck = isPassAttrCheck(config, prodInst);
                if (isPassAttrCheck) {
                    custIdToProdInstId.computeIfAbsent(custIdToCheck, (s) -> new HashMap<>()).computeIfAbsent(prodInst.getProdId(), (s) -> new HashSet<>()).add(prodInst.getProdInstId());
                }
            }
            operateSceneInstService.putExtParameter(request.getSceneInstId(), JUDGE_FLAG, collectCheckMap);
        }
    }

    public boolean isPassAttrCheck(DcPublic config, KiteProdInst prodInst) {
        boolean isPassAttrCheck = false;
        if (config != null) {
            String attrCheck = config.getCoded();
            // 因为其它的方法没提供成public的，所以只先调用这个 checkParamsRoot，单纯做些 以@开头的 属性的相关校验
            isPassAttrCheck = KiteStringUtils.isNotEmpty(attrCheck) && ctccAttrModifyLinkCheckPlugin.checkParamsRoot(attrCheck, new HashMap<>(), prodInst);
        }
        return isPassAttrCheck;
    }

    private Map<String, Map<String, Map<String, Set<String>>>> getCollectCheckMap(CommonRequestDTO request, String reqId) {
        Map<String, Map<String, Map<String, Set<String>>>> collectCheckMap = querySceneInstService.getExtParameterValue(request.getSceneInstId(), JUDGE_FLAG);
        if (collectCheckMap == null) {
            collectCheckMap = new HashMap<>();
            collectCheckMap.put(reqId, new HashMap<>());
        }
        if (!collectCheckMap.containsKey(reqId)) {
            // 另外一次点击了
            collectCheckMap.clear();
            collectCheckMap.put(reqId, new HashMap<>());
        }
        return collectCheckMap;
    }

    public static String getCustIdToCheck(String checkType, String serviceOfferId, KiteProdInst prodInst) {
        String custIdToCheck = "";
        if (isCheckUseCustomer(checkType)) {
            // 使用客户
            if (LocalKeyValues.SERVICE_OFFER_INSTALL.equals(serviceOfferId) ||
                LocalKeyValues.SERVICE_OFFER_GUOHU.equals(serviceOfferId) || LocalKeyValues.XSP_MEGER_CUST_ID.equals(serviceOfferId) ||
                LocalKeyValues.SERVICE_OFFER_GSYZXX.equals(serviceOfferId)) {
                // 新装 过户、客户归并 改使用者
                custIdToCheck = prodInst.getUseCustId();
            }
        }
        else {
            // 产权客户
            if (LocalKeyValues.SERVICE_OFFER_INSTALL.equals(serviceOfferId)) {
                // 新装
                custIdToCheck = prodInst.getOwnerCustId();
            }
            else if (LocalKeyValues.SERVICE_OFFER_GUOHU.equals(serviceOfferId) || LocalKeyValues.XSP_MEGER_CUST_ID.equals(serviceOfferId)) {
                // 过户、客户归并
                custIdToCheck = prodInst.getNewOwnerCustId();
            }
        }
        return custIdToCheck;
    }

    /**
     * 是否校验使用者
     */
    public static boolean isCheckUseCustomer(String checkType) {
        return KiteStringUtils.isEqual(checkType, "2");
    }

    /**
     * 是否开启预校验
     */
    public static boolean isOpenPreJudge() {
        return KiteStringUtils.isEqual(DcPublicCache.getCodeb("2024110401", "CERT_NO_NUMBER_INST_SWITCH_COMMON"), "T");
    }

    /**
     * 是否开启写中间表
     */
    public static boolean isOpenWriteData() {
        return KiteStringUtils.isEqual(DcPublicCache.getCodec("2024110401", "CERT_NO_NUMBER_INST_SWITCH_COMMON"), "T");
    }

    public static String getCheckType() {
        return KiteStringUtils.defaultIfEmpty(DcPublicCache.getCodea("2024110401", "CERT_NO_NUMBER_INST_SWITCH_COMMON"), "1");
    }

    public static DcPublic getProdConfig(String prodId) {
        // 产品配置
        DcPublic config = null;
        List<DcPublic> configs = DcPublicCache.getByPcode("2024110401", "CERT_NO_NUMBER_INST_SWITCH", prodId);
        if (configs != null) {
            for (DcPublic configItem : configs) {
                if (KiteStringUtils.isEqual("T", configItem.getCodea())) {
                    // 开关启用，就处理
                    config = configItem;
                }
            }
        }
        return config;
    }

    public static Map<String, DcPublic> getProdConfigs() {
        // 产品配置
        Map<String, DcPublic> configMap = new HashMap<>();
        List<DcPublic> configs = DcPublicCache.getByPkey("2024110401", "CERT_NO_NUMBER_INST_SWITCH");
        if (configs != null) {
            for (DcPublic configItem : configs) {
                if (KiteStringUtils.isEqual("T", configItem.getCodea())) {
                    // 开关启用，就处理
                    configMap.put(configItem.getPcode(), configItem);
                }
            }
        }
        return configMap;
    }

}