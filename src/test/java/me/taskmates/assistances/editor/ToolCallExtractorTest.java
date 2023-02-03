package me.taskmates.assistances.editor;

import me.taskmates.runners.ToolCallExtractor;
import me.taskmates.clients.ToolCall;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ToolCallExtractorTest {

    @Test
    public void testProcessMessagesWithSingleToolCall() {
        String content = "Message content\n## [encode_numbers#tool_call_1701267816102]\n```json\n{\"a\":1,\"b\":2}\n```\n";
        List<ToolCall> toolCalls = ToolCallExtractor.extractToolCalls(content);
        assertNotNull(toolCalls);
        assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        assertEquals("tool_call_1701267816102", toolCall.getId());
        assertEquals("encode_numbers", toolCall.getFunction().getName());
        Map<String, Object> expectedArguments = Map.of("a", 1, "b", 2);
        assertEquals(expectedArguments, toolCall.getFunction().getArgumentsMap());
    }

    @Test
    public void testProcessMessagesWithMultipleToolCalls() {
        String content = ("Message content\n"
                + "## [some_function#some_id]\n```json\n{\"a\": 1, \"b\": 2}\n```\n"
                + "## [another_function#another_id]\n```json\n{\"a\": 3, \"b\": 4}\n```\n");
        List<ToolCall> toolCalls = ToolCallExtractor.extractToolCalls(content);
        assertNotNull(toolCalls);
        assertEquals(2, toolCalls.size());
        ToolCall firstToolCall = toolCalls.get(0);
        ToolCall secondToolCall = toolCalls.get(1);
        assertEquals("some_id", firstToolCall.getId());
        assertEquals("some_function", firstToolCall.getFunction().getName());
        assertEquals(Map.of("a", 1, "b", 2), firstToolCall.getFunction().getArgumentsMap());
        assertEquals("another_id", secondToolCall.getId());
        assertEquals("another_function", secondToolCall.getFunction().getName());
        assertEquals(Map.of("a", 3, "b", 4), secondToolCall.getFunction().getArgumentsMap());
    }

    @Test
    public void testProcessMessagesWithNoToolCalls() {
        String content = "Message content without tool calls\n";
        List<ToolCall> toolCalls = ToolCallExtractor.extractToolCalls(content);
        assertTrue(toolCalls.isEmpty());
    }

    @Test
    public void testProcessMessagesWithToolCallsAndExtraContent() {
        String content = ("Message content\nSome extra content\n"
                + "## [some_function#some_id]\n```json\n{\"a\": 1, \"b\": 2}\n```\n");
        List<ToolCall> toolCalls = ToolCallExtractor.extractToolCalls(content);
        assertNotNull(toolCalls);
        assertEquals(1, toolCalls.size());
        ToolCall toolCall = toolCalls.get(0);
        assertEquals("some_id", toolCall.getId());
        assertEquals("some_function", toolCall.getFunction().getName());
        assertEquals(Map.of("a", 1, "b", 2), toolCall.getFunction().getArgumentsMap());
    }
}
