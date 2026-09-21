package com.yourproject.agent_platform.orchestrator;

// TODO: 需要哪些import?
// 提示: Component, Mono, 以及你之前写的 ClaudeService, ToolRegistry

import com.yourproject.agent_platform.client.ClaudeService;
import com.yourproject.agent_platform.tool.ToolRegistry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AgentOrchestrator {

    // TODO 1: 加上让Spring管理这个类的注解

    // TODO 2: 声明两个字段: claudeService(类型ClaudeService), toolRegistry(类型ToolRegistry)
    private final ClaudeService claudeService;
    private final ToolRegistry toolRegistry;


    // TODO 3: 写构造函数,参数是这两个依赖,分别赋值给上面的字段
    //         (这个模式你已经写过很多次了,应该很熟悉)

    public AgentOrchestrator(ClaudeService claudeService, ToolRegistry toolRegistry) {
        this.claudeService = claudeService;
        this.toolRegistry = toolRegistry;
    }

    // TODO 4: 写一个方法,叫 run
    //         参数: String userMessage
    //         返回类型: Mono<String>
    //         方法体先留空,我们下一步再填(先随便return一个占位值,比如 Mono.just("todo"))

    public Mono<String> run(String userMessage) {
        return Mono.just("todo");
    }
}