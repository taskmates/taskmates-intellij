package me.taskmates.runners;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import me.taskmates.clients.Signals;
import me.taskmates.lib.utils.CommandLineUtils;
import me.taskmates.lib.utils.EscapeAnsiEscapeCodes;
import org.apache.commons.exec.CommandLine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class DockerShellCommandRunner implements ShellCommandRunner {
    private final String containerName;

    public DockerShellCommandRunner(String containerName) {
        this.containerName = containerName;
    }

    public int runShellCommand(String command,
                               String cwd,
                               Map<String, String> env,
                               Consumer<String> outputConsumer,
                               Consumer<String> errorConsumer,
                               Signals signals) {
        int exitCode = 0;
        final PtyProcess process;

        try {
            // Set the environment variables and working directory
            Map<String, String> effectiveEnv = new HashMap<>(System.getenv());
            effectiveEnv.putAll(env);
            effectiveEnv.put("TERM", "xterm");

            // Create the Docker command
            CommandLine commandLine = CommandLineUtils.commandLine("/usr/local/bin/docker", "exec");

            // Add environment variables to the Docker command
            for (Map.Entry<String, String> entry : effectiveEnv.entrySet()) {
                commandLine.addArgument("-e");
                commandLine.addArgument(entry.getKey() + "=" + entry.getValue(), false);
            }

            // Add the working directory to the Docker command
            commandLine.addArgument("-w");
            commandLine.addArgument(cwd);

            // Add the container name to the Docker command
            commandLine.addArgument(containerName);

            // Add the actual command to be executed
            commandLine.addArgument("sh");
            commandLine.addArgument("-c");
            commandLine.addArgument(command, false);

            // Create the PTY process builder
            PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder(
                commandLine.toStrings()
            );

            // Start the PTY process
            process = ptyProcessBuilder.start();

            // Read output from the process
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            Thread readerThread = new Thread(() -> {
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        String cleanLine = EscapeAnsiEscapeCodes.removeAnsiEscapeCodes(line);
                        outputConsumer.accept(cleanLine);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            Thread errorReaderThread = new Thread(() -> {
                String line;
                try {
                    while ((line = errorReader.readLine()) != null) {
                        String cleanLine = EscapeAnsiEscapeCodes.removeAnsiEscapeCodes(line);
                        errorConsumer.accept(cleanLine);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            readerThread.start();
            errorReaderThread.start();

            signals.on("kill", (Object unused) -> {
                process.destroy();
            });

            // Get the exit code of the process
            exitCode = process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return exitCode;
    }
}
