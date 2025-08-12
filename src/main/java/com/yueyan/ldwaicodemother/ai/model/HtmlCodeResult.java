package com.yueyan.ldwaicodemother.ai.model;

import jdk.jfr.Description;
import lombok.Data;

@Description("生成html代码文件的结果")
@Data
public class HtmlCodeResult {

    @Description("生成的html代码")
    private String htmlCode;

    @Description("生成的html代码的描述")
    private String description;
}
