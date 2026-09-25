package com.yourproject.agent_platform.tool;

import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ToolRegistry {

    private final Map<String, Tool> toolsByName;

    public ToolRegistry(List<Tool> tools) {
        toolsByName = tools.stream()
                .collect(Collectors.toMap(Tool::getName, t -> t));
    }

    public Optional<Tool> find(String name) {
        return Optional.ofNullable(toolsByName.get(name));
    }

    public List<Map<String, Object>> getToolDefinitions() {
        return toolsByName.values().stream()
                .map(tool -> {
                    Map<String, Object> def = new HashMap<>();
                    def.put("name", tool.getName());
                    def.put("description", tool.getDescription());
                    def.put("input_schema", tool.getInputSchema());
                    return def;
                })
                .collect(Collectors.toList());
    }
}
