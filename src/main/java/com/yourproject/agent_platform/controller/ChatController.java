package com.yourproject.agent_platform.controller;

import com.yourproject.agent_platform.client.ClaudeService;
import com.yourproject.agent_platform.orchestrator.AgentOrchestrator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ChatController {
    private final AgentOrchestrator agentOrchestrator;

    // Field of type ClaudeService
    private ClaudeService claudeService;
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    // Constructor: injects ClaudeService and AgentOrchestrator via Spring's dependency injection
    public ChatController(ClaudeService claudeService, AgentOrchestrator agentOrchestrator) {
        this.claudeService = claudeService;
        this.agentOrchestrator = agentOrchestrator;
    }

    // POST /api/chat: takes the raw request body as the user's message,
    // calls ClaudeService.sendMessage(...) directly, and returns the reply
    @PostMapping("/api/chat")
    public Mono<String> chat(@RequestBody String userMessage) {
        return claudeService.sendMessage(userMessage);
    }

    // POST /api/agent: takes the raw request body as the user's message,
    // delegates to AgentOrchestrator.run(...) to run the full tool-calling loop
    @PostMapping("/api/agent")
    public Mono<String> agentChat(@RequestBody String userMessage) {
        log.info("userMessage is {}", userMessage);
        return agentOrchestrator.run(userMessage);
    }
}