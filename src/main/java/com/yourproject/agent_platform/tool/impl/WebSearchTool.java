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

    public WebSearchTool(WebClient.Builder builder,ObjectMapper objectMapper) {
        this.webClient  = builder.baseUrl("https://api.tavily.com").build();
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
        // TODO: 用 Map 构造出上面那段JSON Schema对应的结构
        //   提示: 最外层是一个Map,包含 "type"、"properties"、"required" 三个key
        //   "properties" 的值又是一个Map,里面装 "query" 这个key,
        //     对应的值又是一个Map,包含 "type" 和 "description"
        //   "required" 的值是一个List,里面装字符串 "query"

        Map<String,Object> tavRequestBody = new HashMap<>();
        Map<String,Object> propertiesBody = new HashMap<>();
        Map<String,Object> queryBody = new HashMap<>();
        queryBody.put("type", "string");
        queryBody.put("description","search keyword");

        propertiesBody.put("query",queryBody);

        tavRequestBody.put("type","object");
        tavRequestBody.put("properties",propertiesBody);
        tavRequestBody.put("required", List.of("query"));

        //   构造好这个Map之后,需要把它"转换"成JsonNode类型返回
        //   这里需要用到 ObjectMapper 这个类的 valueToTree(...) 方法
        //   (它能把一个普通的Java对象/Map,转换成JsonNode)
        //   所以你需要在类里再声明一个 ObjectMapper 类型的字段,
        //   并且通过构造函数注入进来(跟WebClient.Builder一样的注入方式)
        return objectMapper.valueToTree(tavRequestBody);
    }

    @Override
    public Mono<String> execute(JsonNode input) {
        // TODO 1: 从input这个JsonNode里,取出"query"这个字段的值(字符串)
        //         提示: input.get("query").asText()
        String queryTxt = input.get("query").asText();

        // TODO 2: 构造请求体Map,只需要一个key: "query",值就是上面取出来的搜索词
        Map<String,String> queryBody = new HashMap<>();
        queryBody.put("query",queryTxt);

        // TODO 3: 用webClient发起POST请求
        //   - uri是 "/search"
        //   - header: "Authorization", 值是 "Bearer " + this.apiKey (注意Bearer后面有个空格)
        //   - header: "Content-Type", 值是 "application/json"
        //   - bodyValue放请求体
        //   - retrieve()
        //   - bodyToMono(String.class)  <- 先直接拿原始JSON字符串,不做进一步解析

        return webClient.post().uri("/search")
                .header("Authorization","Bearer "+ this.apiKey)
                .header("Content-Type","application/json")
                .bodyValue(queryBody)
                .retrieve()
                .bodyToMono(String.class);
    }

}
