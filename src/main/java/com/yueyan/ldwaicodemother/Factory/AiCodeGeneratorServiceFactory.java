package com.yueyan.ldwaicodemother.Factory;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.yueyan.ldwaicodemother.ai.AiCodeGeneratorService;
import com.yueyan.ldwaicodemother.ai.tools.FileWriteTool;
import com.yueyan.ldwaicodemother.model.enums.CodeGenTypeEnum;
import com.yueyan.ldwaicodemother.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.stream.Stream;

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

//    @Resource(name = "customOpenAiStreamingChatModel")
//    private StreamingChatModel openAiStreamingChatModel;

    @Resource
//    @Qualifier("reasoningStreamingChatModel")
    private StreamingChatModel reasoningStreamingChatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;


    private final Cache<String,AiCodeGeneratorService> serviceCache= Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause)->{
                log.debug("AI服务实例被移除，缓存键:{},原因：{}",key,cause);
            })
            .build();

    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId){
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }

    /**
     * 根据appid获取服务（缓存）
     *
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(Long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey,key->createAiCodeGeneratorService(appId,codeGenType));
         }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(Long appId,CodeGenTypeEnum codeGenType){
        return appId + "_" + codeGenType.getValue();
    }


    /**
     * 创建新的ai服务
     */
    public AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType) {
        //根据appid 创建一个ai服务
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        //从数据库加载历史
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,20);
        //根据代码生成类型选择不同的模型配置
        return switch (codeGenType){
            //vue项目使用推理模型
            case VUE_PROJECT -> AiServices.builder(AiCodeGeneratorService.class)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemoryProvider(memoryId->chatMemory)
                    .tools(new FileWriteTool())
                    .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                            toolExecutionRequest,"Error:there is no tool called"+toolExecutionRequest
                    ))
                    .build();
            case HTML, MULTI_FILE -> AiServices.builder(AiCodeGeneratorService.class)
                    .chatModel(chatModel)
                    .streamingChatModel(reasoningStreamingChatModel)
                    .chatMemory(chatMemory)
                    .build();
            default -> throw new IllegalArgumentException("Invalid code generation type: " + codeGenType);
        };
    }

    /**
     * 默认提供一个 Bean
     */
//    @Bean
//    public AiCodeGeneratorService aiCodeGeneratorService() {
//        return getAiCodeGeneratorService(0L);
//    }

}

