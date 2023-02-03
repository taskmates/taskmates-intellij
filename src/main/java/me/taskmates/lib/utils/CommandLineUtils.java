package me.taskmates.lib.utils;

import org.apache.commons.exec.CommandLine;

import java.util.Map;

public class CommandLineUtils {
    public static CommandLine commandLine(String... command) {
        CommandLine commandLine = new CommandLine(command[0]);
        for (int i = 1; i < command.length; i++) {
            commandLine.addArgument(command[i], true);
        }
        return commandLine;
    }

    public static String commandLine(CommandLine commandLine, Map<String, Object> arguments) {
        arguments.forEach((argName, argValue) -> {
            if (argValue != null) {
                commandLine.addArgument("--" + argName);
                try {
                    commandLine.addArgument(argValue.toString(), true);
                    // TODO: get rid of this hack. this is a security issue
                } catch (IllegalArgumentException e) {
                    // This happens when there's both single and double quotes
                    commandLine.addArgument(escapeArgument(argValue.toString()), false);
                }
            }
        });
        return String.join(" ", commandLine.toStrings());
    }

    // TODO: review this in combination with handleQuaoting
    private static String escapeArgument(String argument) {
        String escaped = argument.replace("'", "'\\''");
        return "'" + escaped + "'";
    }
}
