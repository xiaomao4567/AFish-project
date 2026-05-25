package com.example.yitiaoyu.config;


import com.alibaba.cloud.ai.agent.studio.loader.AgentLoader;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Studio UI 配置类
 * 解决 AgentLoader Bean 找不到的问题
 */
@Configuration
public class StudioConfig {

    /**
     * 创建 AgentLoader Bean
     * Studio UI 需要这个 Bean 来加载 Agent
     */
    @Bean
    public AgentLoader agentLoader(ReactAgent reactAgent) {
        // 返回一个简单的 AgentLoader 实现
        return new AgentLoader() {
            @NotNull
            @Override
            public List<String> listAgents() {
                return List.of();
            }

            @Override
            public ReactAgent loadAgent(String agentId) {
                // 根据 agentId 返回对应的 Agent
                // 这里简单处理，只返回默认的 weather_agent
                return reactAgent;
            }
        };
    }
}