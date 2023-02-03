package me.taskmates.runners;

import me.taskmates.clients.Signals;

import java.util.Map;
import java.util.function.Consumer;

public interface ShellCommandRunner {
    int runShellCommand(String command,
                        String cwd,
                        Map<String, String> env,
                        Consumer<String> outputConsumer,
                        Consumer<String> errorConsumer,
                        Signals signals);
}
