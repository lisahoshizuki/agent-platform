package com.yourproject.agent_platform.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *Service can be used for other classes
 */

@Service
public class ClaudeService {
    // TODO 5: 声明一个WebClient类型的成员变量,叫webClient
    private WebClient webClient;

    // TODO 6: 用 @Value 注解,从配置文件读取 anthropic.api-key,
    // 声明一个String类型的成员变量,叫apiKey
    @Value("${anthropic.api-key}")
    private String apiKey;

    // TODO 7: 用 @Value 注解,从配置文件读取 anthropic.model,
    //         声明一个String类型的成员变量,叫model
    @Value("${anthropic.model}")
    private String model;

    // TODO 8: 写一个构造函数,参数是 WebClient.Builder,
    //         在构造函数里,用这个builder构建出webClient,
    //         并设置baseUrl为 "https://api.anthropic.com/v1"

    public ClaudeService(WebClient.Builder builder) {
        this.webClient= builder.baseUrl("https://api.anthropic.com/v1").build();

    }

    public Mono<String> sendMessage(String userMessage) {
        // TODO 1: 构造请求体,用 Map<String, Object> 表示这段JSON:
        //   {
        //     "model": "...",  <- 用this.model
        //     "max_tokens": 1024,
        //     "messages": [ { "role": "user", "content": userMessage } ]
        //   }
        // 提示:messages字段的值是一个List,里面装一个Map
        // 可以用 Map.of(...) 和 List.of(...) 来构造不可变的Map/List
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", this.model);
        requestBody.put("max_tokens", 1024);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", userMessage)));

        // TODO 2: 用webClient发起POST请求
        //   - uri是 "/messages"
        //   - 需要三个header: x-api-key, anthropic-version(值固定是"2023-06-01"), content-type
        //   - bodyValue放刚才构造的requestBody
        //   - retrieve()
        //   - bodyToMono(String.class)

        return webClient.post().uri("/messages")
                .header("x-api-key",this.apiKey)
                .header("anthropic-version","2023-06-01")
                .header("content-type","application/json")
                .bodyValue(requestBody)
                .retrieve().bodyToMono(ClaudeResponse.class)
                .map(response -> response.content().get(0).text());


    }

}
