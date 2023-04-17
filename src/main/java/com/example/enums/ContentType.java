package com.example.enums;

public enum ContentType {
    // 文本段落
    TEXT("text"),
    // 图片段落
    IMAGE("image"),
    // 公式段落
    FORMULA("formula"),
    TABLE("table"),
    ;

    // 段落类型的字符串表示
    private final String type;

    // 构造函数，初始化段落类型的字符串表示
    ContentType(String type) {
        this.type = type;
    }

    // 获取段落类型的字符串表示
    public String getType() {
        return type;
    }
}