package com.yourproject.agent_platform.tool;

// TODO: 需要哪些import? 提示:Mono, JsonNode(用来表示JSON结构的通用类型)

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

public interface Tool {

    String getName();

    String getDescription();

    JsonNode getInputSchema();

    Mono<String> execute(JsonNode input);
}