package com.yourproject.agent_platform.tool;

// TODO: 需要哪些import?
// 提示: Component, List, Map, Optional, Collectors(用来把List转成Map)

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ToolRegistry {

    // TODO 1: 声明一个字段,类型是 Map<String, Tool>,叫 toolsByName
    //         (这就是我们的"登记表":工具名字 -> 工具实例)
    private final Map<String, Tool> toolsByName;

    // TODO 2: 加上让Spring管理这个类的注解

    // TODO 3: 写构造函数,参数是 List<Tool> tools
    //         在构造函数里,把这个List转换成Map,存到toolsByName里
    //         提示: 用 tools.stream().collect(Collectors.toMap(Tool::getName, t -> t))
    //         这行代码的意思是:"把tools这个List转成一个Map,
    //         key是每个工具调用getName()的结果,value是工具本身"

    public ToolRegistry(List<Tool> tools) {
        toolsByName = tools.stream().collect(Collectors.toMap(Tool::getName, t -> t));
    }

    // TODO 4: 写一个方法,叫 find,参数是String name,
    //         返回类型是 Optional<Tool>
    //         方法体:返回 toolsByName.get(name) 包装成的 Optional
    //         提示: Optional.ofNullable(toolsByName.get(name))

    public Optional<Tool> find(String name) {
        return Optional.ofNullable(toolsByName.get(name));

    }
}