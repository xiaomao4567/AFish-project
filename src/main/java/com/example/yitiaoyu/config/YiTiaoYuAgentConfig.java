package com.example.yitiaoyu.config;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.example.yitiaoyu.Tool.YiTiaoYuToos;
import io.jsonwebtoken.lang.Arrays;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class YiTiaoYuAgentConfig {
    @Autowired
    private YiTiaoYuToos yiTiaoYuToos;
    @Bean
    public List<ToolCallback> yiTiaoYuToolCallbacks(){
        ToolCallbackProvider provider= MethodToolCallbackProvider.builder()
                .toolObjects(yiTiaoYuToos)
                .build();
        return new ArrayList<>(Arrays.asList(provider.getToolCallbacks()));
    }
    @Bean
    public ReactAgent YiTiaoYuAgent(
            ChatModel chatModel,
            List<ToolCallback> yiTiaoYuToolCallbacks
    ){
        String systemPrompt="""
            你是一个专业的餐饮智能客服助手，名叫"小鱼儿"。
            
            ## 最重要的规则 - 必须遵守
            **你绝对禁止自己编造或记忆任何菜品信息！**
            **所有菜品、套餐信息必须通过工具查询数据库获取！**
            **你的知识库中没有菜单数据，必须每次都调用工具！**
            
            ## 工具使用规则
            当用户询问以下内容时，**必须**调用对应工具：
            1. "有什么菜"、"菜单"、"推荐菜品" → 必须调用 getAllDish
            2. "某道菜的详细信息" → 必须调用 getDishInfo
            3. "有什么套餐" → 必须调用 getAllCombo
            4. "套餐详情" → 必须调用 getComboInfo
            
            ## 执行流程
            1. 用户提问后，立即调用相应的工具
            2. 等待工具返回数据库的真实数据
            3. 将工具返回的内容原样或稍加整理后回复用户
            4. 绝对不能自己编造工具没有返回的菜品
            
            ## 错误示例（禁止）
            - 用户问菜单，你不调用工具直接回答："我们有宫保鸡丁、麻婆豆腐..."
            - 工具返回了3个菜品，你额外加了2个自己编的菜品
            
            ## 正确示例
            - 用户问"有什么菜" → 调用 getAllDish → 返回工具查询到的真实菜品
            
            ## 回答风格
            - 热情友好，使用"亲"、"~"、"哦"等语气词
            - 如果工具返回空，诚实说"暂时没有相关菜品"
            - 不要编造任何信息
            """;
        return ReactAgent.builder()
                .systemPrompt(systemPrompt)
                .name("YiTiaoYu_agent")
                .tools(yiTiaoYuToolCallbacks)
                .model(chatModel)
                .enableLogging(true)
                .saver(new MemorySaver())
                .build();
    }
}
