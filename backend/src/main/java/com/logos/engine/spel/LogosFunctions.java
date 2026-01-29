package com.logos.engine.spel;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

/**
 * LOGOS SpEL自定义函数库
 * 提供规则表达式中常用的工具函数
 */
public class LogosFunctions {
    
    /**
     * 获取属性值（模拟原系统的 getAttr 方法）
     * 
     * @param attrName 属性名（如 col1, attrib_125）
     * @param context 上下文数据
     * @return 属性值
     */
    public static Object getAttr(String attrName, Map<String, Object> context) {
        if (context == null || attrName == null) {
            return null;
        }
        
        // 直接匹配
        if (context.containsKey(attrName)) {
            return context.get(attrName);
        }
        
        // 尝试忽略大小写匹配
        for (Map.Entry<String, Object> entry : context.entrySet()) {
            if (attrName.equalsIgnoreCase(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        return null;
    }
    
    /**
     * 检查值是否在列表中
     * 
     * @param value 待检查的值
     * @param listStr 逗号分隔的列表字符串
     * @return 是否在列表中
     */
    public static boolean inList(String value, String listStr) {
        if (value == null || listStr == null) {
            return false;
        }
        
        String[] items = listStr.split(",");
        return Arrays.asList(items).contains(value.trim());
    }
    
    /**
     * 正则匹配
     * 
     * @param value 待匹配的值
     * @param pattern 正则表达式
     * @return 是否匹配
     */
    public static boolean matches(String value, String pattern) {
        if (value == null || pattern == null) {
            return false;
        }
        return value.matches(pattern);
    }
    
    /**
     * 判空
     */
    public static boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
            return ((String) value).isEmpty();
        }
        if (value instanceof java.util.Collection) {
            return ((java.util.Collection<?>) value).isEmpty();
        }
        if (value instanceof Map) {
            return ((Map<?, ?>) value).isEmpty();
        }
        return false;
    }
    
    /**
     * 非空判断
     */
    public static boolean isNotEmpty(Object value) {
        return !isEmpty(value);
    }
    
    /**
     * 相等比较（支持null安全）
     */
    public static boolean eq(Object a, Object b) {
        return Objects.equals(a, b);
    }
    
    /**
     * 字符串相等比较（忽略大小写）
     */
    public static boolean eqIgnoreCase(String a, String b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equalsIgnoreCase(b);
    }
    
    /**
     * 默认值
     */
    public static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }
}
