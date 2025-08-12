package com.yueyan.ldwaicodemother.ai.model;

import jdk.jfr.Description;
import lombok.Data;

@Description("多文件代码生成结果")
@Data
public class MultiFileCodeResult {

    @Description("生成的html代码")
    private String htmlCode;

    @Description("生成的css代码")
    private String cssCode;

    @Description("生成的js代码")
    private String jsCode;

    @Description("生成代码的描述")
    private String description;
}
