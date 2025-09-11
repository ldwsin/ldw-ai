package com.yueyan.ldwaicodemother.langgraph4j.tools;

// ... existing code ...
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.system.SystemUtil;
import com.yueyan.ldwaicodemother.exception.BusinessException;
import com.yueyan.ldwaicodemother.exception.ErrorCode;
import com.yueyan.ldwaicodemother.langgraph4j.model.ImageCategoryEnum;
import com.yueyan.ldwaicodemother.langgraph4j.state.ImageResource;
import com.yueyan.ldwaicodemother.manager.CosManager;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class MermaidDiagramTool {

    @Resource
    private CosManager cosManager;

    @Tool("将 Mermaid 代码转换为架构图图片，用于展示系统结构和技术关系")
    public List<ImageResource> generateMermaidDiagram(@P("Mermaid 图表代码") String mermaidCode,
                                                      @P("架构图描述") String description) {
        if (StrUtil.isBlank(mermaidCode)) {
            return new ArrayList<>();
        }
        try {
            // 转换为SVG图片
            File diagramFile = convertMermaidToSvg(mermaidCode);
            // 上传到COS
            String keyName = String.format("/mermaid/%s/%s",
                    RandomUtil.randomString(5), diagramFile.getName());
            String cosUrl = cosManager.uploadFile(keyName, diagramFile);
            // 清理临时文件
            FileUtil.del(diagramFile);
            if (StrUtil.isNotBlank(cosUrl)) {
                return Collections.singletonList(ImageResource.builder()
                        .category(ImageCategoryEnum.ARCHITECTURE)
                        .description(description)
                        .url(cosUrl)
                        .build());
            }
        } catch (Exception e) {
            log.error("生成架构图失败: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    /**
     * 将Mermaid代码转换为SVG图片
     */
    private File convertMermaidToSvg(String mermaidCode) {
        // 创建临时输入文件
        File tempInputFile = FileUtil.createTempFile("mermaid_input_", ".mmd", true);
        FileUtil.writeUtf8String(mermaidCode, tempInputFile);
        // 创建临时输出文件
        File tempOutputFile = FileUtil.createTempFile("mermaid_output_", ".svg", true);

        // 使用 npx 执行 mermaid CLI，这种方式更可靠
        String cmdLine = String.format("npx -y @mermaid-js/mermaid-cli@11.9.0 mmdc -i \"%s\" -o \"%s\" -b transparent",
                tempInputFile.getAbsolutePath(),
                tempOutputFile.getAbsolutePath()
        );

        log.info("执行 Mermaid CLI 命令: {}", cmdLine);

        try {
            // 执行命令并获取结果
            String result = RuntimeUtil.execForStr(cmdLine);
            log.info("Mermaid CLI 执行输出: {}", result);

            // 等待一段时间确保文件写入完成
            Thread.sleep(3000);

            // 检查输出文件
            log.info("检查输出文件: {} (存在: {}, 大小: {})",
                    tempOutputFile.getAbsolutePath(),
                    tempOutputFile.exists(),
                    tempOutputFile.exists() ? tempOutputFile.length() : 0);

            if (!tempOutputFile.exists() || tempOutputFile.length() == 0) {
                // 检查是否有错误日志文件
                File errorLogFile = new File(tempOutputFile.getAbsolutePath() + ".log");
                if (errorLogFile.exists()) {
                    String errorLog = FileUtil.readUtf8String(errorLogFile);
                    log.error("Mermaid CLI 错误日志: {}", errorLog);
                }

                log.error("Mermaid CLI 执行失败，输出文件不存在或为空");
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                        String.format("Mermaid CLI 执行失败: 输出文件未生成或为空。命令: %s", cmdLine));
            }

            // 清理输入文件，保留输出文件供上传使用
            FileUtil.del(tempInputFile);
            return tempOutputFile;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("线程被中断", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行被中断");
        } catch (Exception e) {
            // 清理临时文件
            FileUtil.del(tempInputFile);
            FileUtil.del(tempOutputFile);
            log.error("执行 Mermaid CLI 失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    String.format("Mermaid CLI 执行失败: %s", e.getMessage()));
        }
    }
}

//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.RandomUtil;
//import cn.hutool.core.util.RuntimeUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.hutool.system.SystemUtil;
//import com.yueyan.ldwaicodemother.exception.BusinessException;
//import com.yueyan.ldwaicodemother.exception.ErrorCode;
//import com.yueyan.ldwaicodemother.langgraph4j.model.ImageCategoryEnum;
//import com.yueyan.ldwaicodemother.langgraph4j.state.ImageResource;
//import com.yueyan.ldwaicodemother.manager.CosManager;
//import dev.langchain4j.agent.tool.P;
//import dev.langchain4j.agent.tool.Tool;
//import jakarta.annotation.Resource;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//@Slf4j
//@Component
//public class MermaidDiagramTool {
//
//    @Resource
//    private CosManager cosManager;
//
//    @Tool("将 Mermaid 代码转换为架构图图片，用于展示系统结构和技术关系")
//    public List<ImageResource> generateMermaidDiagram(@P("Mermaid 图表代码") String mermaidCode,
//                                                      @P("架构图描述") String description) {
//        if (StrUtil.isBlank(mermaidCode)) {
//            return new ArrayList<>();
//        }
//        try {
//            // 转换为SVG图片
//            File diagramFile = convertMermaidToSvg(mermaidCode);
//            // 上传到COS
//            String keyName = String.format("/mermaid/%s/%s",
//                    RandomUtil.randomString(5), diagramFile.getName());
//            String cosUrl = cosManager.uploadFile(keyName, diagramFile);
//            // 清理临时文件
//            FileUtil.del(diagramFile);
//            if (StrUtil.isNotBlank(cosUrl)) {
//                return Collections.singletonList(ImageResource.builder()
//                        .category(ImageCategoryEnum.ARCHITECTURE)
//                        .description(description)
//                        .url(cosUrl)
//                        .build());
//            }
//        } catch (Exception e) {
//            log.error("生成架构图失败: {}", e.getMessage(), e);
//        }
//        return new ArrayList<>();
//    }
//
//    /**
//     * 将Mermaid代码转换为SVG图片
//     */
//    private File convertMermaidToSvg(String mermaidCode) {
//        // 创建临时输入文件
//        File tempInputFile = FileUtil.createTempFile("mermaid_input_", ".mmd", true);
//        FileUtil.writeUtf8String(mermaidCode, tempInputFile);
//        // 创建临时输出文件
//        File tempOutputFile = FileUtil.createTempFile("mermaid_output_", ".svg", true);
//        // 根据操作系统选择命令
//        String command = SystemUtil.getOsInfo().isWindows() ? "mmdc.cmd" : "mmdc";
//        // 构建命令
//        String cmdLine = String.format("%s -i %s -o %s -b transparent",
//                command,
//                tempInputFile.getAbsolutePath(),
//                tempOutputFile.getAbsolutePath()
//        );
//        // 执行命令
//        RuntimeUtil.execForStr(cmdLine);
//        // 检查输出文件
//        if (!tempOutputFile.exists() || tempOutputFile.length() == 0) {
//            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Mermaid CLI 执行失败");
//        }
//        // 清理输入文件，保留输出文件供上传使用
//        FileUtil.del(tempInputFile);
//        return tempOutputFile;
//    }
//}
