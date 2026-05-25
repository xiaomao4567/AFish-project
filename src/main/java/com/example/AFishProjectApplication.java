package com.example;

import org.springframework.ai.vectorstore.elasticsearch.autoconfigure.ElasticsearchVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientAutoConfiguration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {
        ElasticsearchVectorStoreAutoConfiguration.class,
        ElasticsearchRestClientAutoConfiguration.class
})
@EnableTransactionManagement
public class AFishProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AFishProjectApplication.class, args);
        System.out.println("🚀 访问地址: http://localhost:8080/chatui/index.html");
    }

}
