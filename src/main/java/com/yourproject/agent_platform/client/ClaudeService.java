package com.yourproject.agent_platform.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for interacting with the Claude API.
 */
@Service
public class ClaudeService {

    private WebClient webClient;

    @Value("${anthropic.api-key}")
    private String apiKey;

    @Value("${anthropic.model}")
    private String model;

    public ClaudeService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.anthropic.com/v1")
                .build();
    }

    public Mono<String> sendMessage(String userMessage) {
        // Build the request body for a simple chat request
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", this.model);
        requestBody.put("max_tokens", 1024);
        requestBody.put(
                "messages",
                List.of(
                        Map.of(
                                "role", "user",
                                "content", userMessage
                        )
                )
        );

        // Send the request to the Claude Messages API
        return webClient.post()
                .uri("/messages")
                .header("x-api-key", this.apiKey)
                .header("anthropic-version", "2023-06-01")
                .header("content-type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(ClaudeResponse.class)
                .map(response -> response.content().get(0).text());
    }

    public Mono<ClaudeResponse> call(
            List<Map<String, Object>> messages,
            List<Map<String, Object>> tools
    ) {
        // Build the request body with conversation history and available tools
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", this.model);
        requestBody.put("max_tokens", 1024);
        requestBody.put("messages", messages);
        requestBody.put("tools", tools);

        // Send the request and return the complete Claude response
        return webClient.post()
                .uri("/messages")
                .header("x-api-key", this.apiKey)
                .header("anthropic-version", "2023-06-01")
                .header("content-type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.out.println(
                                            "Claude API error response: " + errorBody
                                    );
                                    return Mono.error(
                                            new RuntimeException(
                                                    "Claude API error: " + errorBody
                                            )
                                    );
                                })
                )
                .bodyToMono(ClaudeResponse.class);
    }
}
