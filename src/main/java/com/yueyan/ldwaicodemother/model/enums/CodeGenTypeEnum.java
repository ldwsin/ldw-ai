package com.yueyan.ldwaicodemother.model.enums;

import lombok.Getter;

@Getter
public enum CodeGenTypeEnum {
    HTML("原生HTML模式","html"),
    MULTI_FILE("多文件模式","multi-file");

    private final String text;
    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据value获取枚举
     *
     * @param value
     * @return
     */
    public static CodeGenTypeEnum genEnumByValue(String value) {
        for (CodeGenTypeEnum valueEnum : CodeGenTypeEnum.values()) {
            if (valueEnum.value.equals(value)) {
                return valueEnum;
            }
        }
        return null;
    }
}
