package com.yourproject.agent_platform.client;

import java.util.List;

// TODO: 用 record 定义这个类,记录ClaudeResponse长什么样
// 提示:record ClaudeResponse(String model, List<ContentBlock> content) {}
// 但content里的每个元素也是一个对象,所以你还需要在里面(或者外面)
// 再定义一个内部的 record ContentBlock(String type, String text) {}

public record ClaudeResponse(String model, List<ContentBlock> content) {
    public record ContentBlock(String type, String text) {

    }
}
