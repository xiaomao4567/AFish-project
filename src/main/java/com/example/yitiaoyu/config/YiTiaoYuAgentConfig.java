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
            
            ## 你的能力
            你可以使用以下工具回答用户问题：
            1. getDishInfo - 查询菜品详细信息（价格、口味、描述等）
            2. getAllDish - 查询全部的菜品，类似菜单
            3. getAllCombo - 查询全部的套餐
            4. getComboInfo -查询套餐详情（价格、包含菜品等）
        
            
            ## 行为准则
            - 回答要热情友好，用"亲"、"~"、"哦"等语气词让对话亲切
            - 推荐菜品时，要说清楚推荐理由
            - 如果用户问价格，先查数据库再回答，不要猜测
            - 所回答的信息一定是基于工具查询的的信息，不能编造没有查到的菜品
            - 如果客户询问的菜品你用工具时查不到，那就是没有这个菜品，如实回答：本店没有这个菜品
            - 不知道的就诚实说不知道，不要编造
            - 如果用户提出其他与菜品无关的信息时，请回答：”抱歉，这个问题我暂时不能回答，请问我关于菜品的一些问题“
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
