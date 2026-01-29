package com.logos.engine.spel;

import java.util.Arrays;

/**
 * LOGOS 工具类 - 提供 SpEL 中使用的工具函数
 */
public final class LogosUtils {

    private LogosUtils() {
        // 工具类不允许实例化
    }

    /**
     * 判断是否应该跳过检查
     *
     * @param operType      操作类型
     * @param exemptTypes   豁免的操作类型列表
     * @return 是否跳过
     */
    public static boolean shouldSkip(String operType, String... exemptTypes) {
        if (operType == null || exemptTypes == null) {
            return false;
        }
        return Arrays.asList(exemptTypes).contains(operType);
    }

    /**
     * 判断值是否在列表中
     *
     * @param value  要检查的值
     * @param list   列表
     * @return 是否在列表中
     */
    public static boolean inList(String value, String... list) {
        if (value == null || list == null) {
            return false;
        }
        return Arrays.asList(list).contains(value);
    }

    /**
     * 正则匹配
     *
     * @param value   要检查的值
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
     * 判断字符串是否为空
     *
     * @param value 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param value 字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    /**
     * 相等判断
     *
     * @param a 值1
     * @param b 值2
     * @return 是否相等
     */
    public static boolean equals(Object a, Object b) {
        if (a == null && b == null) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return a.equals(b);
    }
}
