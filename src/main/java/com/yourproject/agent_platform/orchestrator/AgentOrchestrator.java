package com.yourproject.agent_platform.orchestrator;

import com.yourproject.agent_platform.client.ClaudeResponse;
import com.yourproject.agent_platform.client.ClaudeService;
import com.yourproject.agent_platform.tool.Tool;
import com.yourproject.agent_platform.tool.ToolRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Executes the agent loop and coordinates Claude with available tools.
 */
@Service
public class AgentOrchestrator {

    private static final Logger log = LoggerFactory.getLogger(AgentOrchestrator.class);

    private static final int MAX_ITERATIONS = 8;

    private final ClaudeService claudeService;
    private final ToolRegistry toolRegistry;

    public AgentOrchestrator(ClaudeService claudeService, ToolRegistry toolRegistry) {
        this.claudeService = claudeService;
        this.toolRegistry = toolRegistry;
    }

    public Mono<String> run(String userMessage) {
        List<Map<String, Object>> messages =
                List.of(Map.of("role", "user", "content", userMessage));

        log.info("messages is {}", messages);

        return runIteration(messages, 0);
    }

    private Mono<String> runIteration(
            List<Map<String, Object>> messages,
            int iteration
    ) {
        log.info("=== Iteration {} started ===", iteration);

        if (iteration >= MAX_ITERATIONS) {
            log.warn("Maximum iterations reached, stopping.");
            return Mono.just("Maximum tool-call iterations reached");
        }

        List<Map<String, Object>> allTools = toolRegistry.getToolDefinitions();

        return claudeService.call(messages, allTools).flatMap(response -> {
            log.info("Claude responded with stopReason: {}", response.stopReason());

            if (!response.stopReason().equals("tool_use")) {
                String text = response.content().stream()
                        .filter(block -> "text".equals(block.type()))
                        .findFirst()
                        .map(block -> block.text())
                        .orElse("(no text response)");

                log.info("Final answer: {}", text);
                return Mono.just(text);
            }

            // Preserve Claude's tool requests in the conversation history.
            List<Map<String, Object>> newMessages = new ArrayList<>(messages);
            newMessages.add(Map.of(
                    "role", "assistant",
                    "content", response.content()
            ));

            List<ClaudeResponse.ContentBlock> toolUseBlocks =
                    response.content().stream()
                            .filter(block -> "tool_use".equals(block.type()))
                            .collect(Collectors.toList());

            log.info("Claude requested {} tool call(s)", toolUseBlocks.size());

            List<Mono<Map<String, Object>>> toolResultMonos =
                    toolUseBlocks.stream()
                            .map(toolUseBlock -> {
                                log.info(
                                        "Calling tool: name='{}', input={}",
                                        toolUseBlock.name(),
                                        toolUseBlock.input()
                                );

                                Optional<Tool> toolOpt =
                                        toolRegistry.find(toolUseBlock.name());

                                if (toolOpt.isEmpty()) {
                                    log.warn(
                                            "Tool not found: '{}'",
                                            toolUseBlock.name()
                                    );
                                }

                                Mono<String> resultMono = toolOpt
                                        .map(tool -> tool.execute(toolUseBlock.input()))
                                        .orElse(Mono.just("Error: tool not found"));

                                return resultMono.map(resultText -> {
                                    log.info(
                                            "Tool '{}' returned result (first 200 chars): {}",
                                            toolUseBlock.name(),
                                            resultText.length() > 200
                                                    ? resultText.substring(0, 200) + "..."
                                                    : resultText
                                    );

                                    Map<String, Object> toolResultMessage =
                                            new HashMap<>();
                                    toolResultMessage.put("type", "tool_result");
                                    toolResultMessage.put(
                                            "tool_use_id",
                                            toolUseBlock.id()
                                    );
                                    toolResultMessage.put("content", resultText);

                                    return toolResultMessage;
                                });
                            })
                            .collect(Collectors.toList());

            return Flux.fromIterable(toolResultMonos)
                    .flatMap(mono -> mono)
                    .collectList()
                    .flatMap(toolResults -> {
                        log.info(
                                "All {} tool call(s) completed, proceeding to iteration {}",
                                toolResults.size(),
                                iteration + 1
                        );

                        newMessages.add(Map.of(
                                "role", "user",
                                "content", toolResults
                        ));

                        return runIteration(newMessages, iteration + 1);
                    });
        });
    }
}