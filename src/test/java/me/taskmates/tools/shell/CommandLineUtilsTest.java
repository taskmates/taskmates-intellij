package me.taskmates.tools.shell;

import me.taskmates.lib.utils.CommandLineUtils;
import org.apache.commons.exec.CommandLine;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@Ignore
public class CommandLineUtilsTest {

    @Test
    public void testCommandLine_Simple() {
        CommandLine cmdLine = CommandLine.parse("/bin/echo");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("message", "simple text");
        String result = CommandLineUtils.commandLine(cmdLine, arguments);
        assertEquals("/bin/echo --message \"simple text\"", result.trim());
    }

    @Test
    public void testCommandLine_SingleQuotes() {
        CommandLine cmdLine = CommandLine.parse("/bin/echo");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("message", "text with 'single quotes'");
        String result = CommandLineUtils.commandLine(cmdLine, arguments);
        assertEquals("/bin/echo --message \"text with 'single quotes'\"", result);
    }

    @Test
    public void testCommandLine_DoubleQuotes() {
        CommandLine cmdLine = CommandLine.parse("/bin/echo");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("message", "text with \"double quotes\"");
        String result = CommandLineUtils.commandLine(cmdLine, arguments);
        assertEquals("/bin/echo --message \"text with \\\"double quotes\\\"\"", result.trim());
    }

    @Test
    public void testCommandLine_MixedQuotes() {
        CommandLine cmdLine = CommandLine.parse("/bin/echo");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("message", "text with 'single' and \"double\" quotes");
        String result = CommandLineUtils.commandLine(cmdLine, arguments);
        assertEquals("/bin/echo --message \"text with 'single' and \\\"double\\\" quotes\"", result.trim());
    }

    @Test
    public void testCommandLine_SpecialCharacters() {
        CommandLine cmdLine = CommandLine.parse("/bin/echo");
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("message", "text with special characters \\ $ `\"");
        String result = CommandLineUtils.commandLine(cmdLine, arguments);
        assertEquals("/bin/echo --message \"text with special characters \\ \\$ `\"", result.trim());
    }
}
