package com.yourproject.agent_platform.tool;

// TODO: 需要哪些import? 提示:Mono, JsonNode(用来表示JSON结构的通用类型)

import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

public interface Tool {

    // TODO 1: 声明一个方法,返回工具的名字
    //         方法名建议叫 getName,返回类型String,没有方法体(接口方法不需要实现)
     String getName();

    // TODO 2: 声明一个方法,返回工具的描述(给模型看的,帮助模型判断什么时候该用这个工具)
    //         方法名建议叫 getDescription,返回类型String
     String getDescription();

    // TODO 3: 声明一个方法,返回这个工具接受的参数结构(JSON Schema)
    //         方法名建议叫 getInputSchema,返回类型 JsonNode
    JsonNode getInputSchema();


    // TODO 4: 声明一个方法,真正执行这个工具
    //         方法名建议叫 execute,参数是 JsonNode input(模型传来的参数)
    //         返回类型 Mono<String>(执行结果,异步返回)

    Mono<String> execute(JsonNode input);

}