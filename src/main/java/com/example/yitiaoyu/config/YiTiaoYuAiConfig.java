package com.example.yitiaoyu.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class YiTiaoYuAiConfig {
    @Bean
    public VectorStore simpleVectorStore(EmbeddingModel embeddingModel){
        return SimpleVectorStore.builder(embeddingModel).build();
    }
    @Bean
    public ChatClient client(ChatClient.Builder builder){
        return builder.build();
    }
}
