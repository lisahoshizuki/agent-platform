package com.yourproject.agent_platform.controller;

// TODO: import需要用到的东西
// 提示: RestController, PostMapping, RequestBody(接收前端传来的JSON), Mono, 还有你的ClaudeService

import com.yourproject.agent_platform.client.ClaudeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class ChatController {

    // TODO: 声明一个ClaudeService类型的字段

    private ClaudeService claudeService;

    // TODO: 写一个构造函数,参数是ClaudeService,把它赋值给上面的字段
    //       (这就是"依赖注入"——Spring会自动把ClaudeService的实例传进来)
    public ChatController(ClaudeService claudeService) {
        this.claudeService = claudeService;
    }

    // TODO: 写一个方法:
    //   - 用 @PostMapping("/api/chat") 标注
    //   - 参数用 @RequestBody 标注,类型是String(先简化,直接接收纯文本消息体,不用额外的DTO)
    //   - 返回类型 Mono<String>
    //   - 方法体:直接调用 claudeService.sendMessage(参数),把结果return出去
    @PostMapping("/api/chat")
    public Mono<String> chat(@RequestBody String userMessage) {
        return claudeService.sendMessage(userMessage);
    }
}