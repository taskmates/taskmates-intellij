package me.taskmates.runners;

import me.taskmates.lib.utils.JsonUtils;
import me.taskmates.clients.ToolCall;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ToolCallExtractor {
    public static final Pattern TOOL_CALL_PATTERN = Pattern.compile(
            "^## \\[(?<functionName>.+?)#(?<toolCallId>.+?)]\\s*\\n```json\\s*\\n(?<arguments>\\{.*?})\\s*\\n```",
            Pattern.MULTILINE | Pattern.DOTALL);

    public static List<ToolCall> extractToolCalls(String content) {
        String[] contents = content.split("(?m)^# .*(?:\\r?\\n|$)");
        String lastContent = contents[contents.length - 1];

        List<ToolCall> toolCalls = new ArrayList<>();
        Matcher matcher = TOOL_CALL_PATTERN.matcher(lastContent);
        while (matcher.find()) {
            String stringArguments = matcher.group("arguments");
            Map<String, Object> arguments = JsonUtils.parseJson(stringArguments);
            ToolCall toolCall = new ToolCall(
                    matcher.group("toolCallId"),
                    "function",
                    new ToolCall.Function(matcher.group("functionName"), arguments)
            );
            toolCalls.add(toolCall);
        }
        return toolCalls;
    }
}
