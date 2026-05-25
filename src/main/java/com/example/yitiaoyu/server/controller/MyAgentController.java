package com.example.yitiaoyu.server.controller;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/agent")
public class MyAgentController {
    @Autowired
    private ReactAgent yiTiaoYuAgent;
    private final Map<String,String> sessionThreadsIds=new HashMap<>();
    @PostMapping
    public Map<String,String> chat(@RequestBody Map<String,String> request) throws GraphRunnerException {
        String message = request.get("message");
        String sessionId = request.getOrDefault("sessionId", UUID.randomUUID().toString());
        String threadId = sessionThreadsIds.computeIfAbsent(sessionId, id -> UUID.randomUUID().toString());
        RunnableConfig config = RunnableConfig.builder().threadId(threadId).build();
        AssistantMessage response = yiTiaoYuAgent.call(message, config);
        Map<String,String> result=new HashMap<>();
        result.put("response",response.getText());
        result.put("sessionId",sessionId);
        return result;
    }

}
