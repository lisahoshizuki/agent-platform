package com.yourproject.agent_platform.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.databind.JsonNode;

import java.util.List;

// Represents a response from the Claude API
public record ClaudeResponse(
        String model,
        List<ContentBlock> content,
        @JsonProperty("stop_reason") String stopReason
) {

    // Represents an individual content block
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record ContentBlock(
            String type,
            String text,
            String id,
            String name,
            JsonNode input
    ) {
    }
}