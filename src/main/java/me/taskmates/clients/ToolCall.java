package me.taskmates.clients;

import me.taskmates.lib.utils.JsonUtils;

import java.util.Map;

public class ToolCall {

    final private String id;
    private String type;
    private Function function;

    public ToolCall() {
        this.id = "tool_call_" + System.currentTimeMillis();
    }

    public ToolCall(String id, String type, Function function) {
        this.id = id;
        this.type = type;
        this.function = function;
    }

    public ToolCall(String name, Map<String, Object> arguments) {
        this.id = "tool_call_" + System.currentTimeMillis();
        this.function = new Function(name, arguments);
    }

    @SuppressWarnings("unchecked")
    public ToolCall(Map<String, Object> toolCall) {
        this.id = "tool_call_" + System.currentTimeMillis();
        Map<String, Object> function = (Map<String, Object>) toolCall.get("function");
        this.function = new Function((String) function.get("name"), (Map<String, Object>) function.get("arguments"));
    }

    // getters and setters

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public static class Function {

        private String name;
        private String arguments;

        // getters and setters


        public Function() {
        }

        public Function(String name, Map<String, Object> arguments) {
            this.name = name;
            setArgumentsMap(arguments);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, Object> getArgumentsMap() {
            return JsonUtils.parseJson(arguments);
        }

        public void setArgumentsMap(Map<String, Object> argumentsMap) {
            this.arguments = JsonUtils.dump(argumentsMap);
        }
    }
}
