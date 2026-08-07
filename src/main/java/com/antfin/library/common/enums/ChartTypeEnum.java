package com.antfin.library.common.enums;

/**
 * 前端图表类型枚举
 */
public enum ChartTypeEnum {

    LINE("LINE", "折线图（趋势）"),
    PIE("PIE", "饼图（占比）"),
    BAR("BAR", "柱状图（对比）");

    private final String code;
    private final String desc;

    ChartTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
