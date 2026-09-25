package com.yourproject.agent_platform.tool.impl;

import com.yourproject.agent_platform.tool.Tool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WebSearchTool implements Tool {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${tavily.api-key}")
    private String apiKey;

    public WebSearchTool(WebClient.Builder builder, ObjectMapper objectMapper) {
        this.webClient = builder
                .baseUrl("https://api.tavily.com")
                .build();
        this.objectMapper = objectMapper;
    }

    @Override
    public String getName() {
        return "web_search";
    }

    @Override
    public String getDescription() {
        return "Search the internet for real-time information. Useful for questions " +
                "that require up-to-date data, recent news, or information not in the " +
                "model's training data.";
    }

    @Override
    public JsonNode getInputSchema() {
        // Define the input schema expected by the tool
        Map<String, Object> tavRequestBody = new HashMap<>();
        Map<String, Object> propertiesBody = new HashMap<>();
        Map<String, Object> queryBody = new HashMap<>();

        queryBody.put("type", "string");
        queryBody.put("description", "search keyword");

        propertiesBody.put("query", queryBody);

        tavRequestBody.put("type", "object");
        tavRequestBody.put("properties", propertiesBody);
        tavRequestBody.put("required", List.of("query"));

        return objectMapper.valueToTree(tavRequestBody);
    }

    @Override
    public Mono<String> execute(JsonNode input) {
        String queryTxt = input.get("query").asText();

        Map<String, String> queryBody = new HashMap<>();
        queryBody.put("query", queryTxt);

        // Send the search request to Tavily
        return webClient.post()
                .uri("/search")
                .header("Authorization", "Bearer " + this.apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(queryBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}