package me.taskmates.clients;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatMessage {
    private final String role;
    private String name = null;
    private Object content = null;
    private String toolCallId = null;

    private List<ToolCall> toolCalls;

    public ChatMessage(String role, String content) {
        this(role, content, (String) null);
    }

    public ChatMessage(String role, String content, String name) {
        this(role, content, name, null);
    }

    public ChatMessage(String role, String content, Map<String, Object> attributes) {
        this.role = role;
        this.content = content;
        if (attributes != null) {
            this.name = (String) attributes.getOrDefault("name", null);
            this.toolCallId = (String) attributes.getOrDefault("tool_call_id", null);
        }
    }


    public ChatMessage(List<ToolCall> toolCalls) {
        this.role = "system";
        this.name = "ide";
        this.toolCalls = toolCalls;
    }

    public ChatMessage(String role, String content, String name, String toolCallId) {
        this.role = role;
        this.name = name;
        this.content = new StringModel(content);
        this.toolCallId = toolCallId;
    }

    @NotNull
    public static ChatMessage buildToolCallMessage(String functionName, String key, String value) {
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("key", key);
        arguments.put("value", value);
        return buildToolCallMessage(functionName, arguments);
    }

    @NotNull
    public static ChatMessage buildToolCallMessage(String functionName, Map<String, Object> arguments) {
        List<ToolCall> toolCalls = new ArrayList<>();
        ToolCall setChatDirPath = new ToolCall();
        setChatDirPath.setType("function");
        ToolCall.Function setChatDirPathFunction = new ToolCall.Function();
        setChatDirPath.setFunction(setChatDirPathFunction);
        setChatDirPathFunction.setName(functionName);
        setChatDirPathFunction.setArgumentsMap(arguments);
        toolCalls.add(setChatDirPath);
        return new ChatMessage(toolCalls);
    }

    public String getRole() {
        return role;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public Object getContent() {
        return content;
    }

    public List<ToolCall> getToolCalls() {
        return toolCalls;
    }

    public String getToolCallId() {
        return toolCallId;
    }

    public void append(String text) {
        content = ((String) content) + text;
    }

    public String getTextContent() {
        return (String) content;
    }

    public boolean isUserPrompt() {
        return getRole().equals("user") && (getContent() == null || getTextContent().trim().isEmpty());
    }
}
