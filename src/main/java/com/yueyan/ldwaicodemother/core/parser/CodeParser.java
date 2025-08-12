package com.yueyan.ldwaicodemother.core.parser;

public interface CodeParser<T> {
    /**
     * 解析代码内容
     */
    T parseCode(String codeContent);
}
