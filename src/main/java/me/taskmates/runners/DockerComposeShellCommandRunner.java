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

public class DockerComposeShellCommandRunner implements ShellCommandRunner {
    private final String projectDir;
    private final String serviceName;

    public DockerComposeShellCommandRunner(String projectDir, String serviceName) {
        this.projectDir = projectDir;
        this.serviceName = serviceName;
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

            // Create the Docker Compose command
            CommandLine commandLine = CommandLineUtils.commandLine("/usr/local/bin/docker", "compose", "exec");

            // TODO: this is messing up with PATH
            // Add environment variables to the Docker Compose command
            // for (Map.Entry<String, String> entry : effectiveEnv.entrySet()) {
            //     commandLine.addArgument("-e");
            //     commandLine.addArgument(entry.getKey() + "=" + entry.getValue(), false);
            // }

            commandLine.addArgument("-w");
            commandLine.addArgument(cwd);

            // Add the service name to the Docker Compose command
            commandLine.addArgument(serviceName);

            // Add the actual command to be executed
            commandLine.addArgument("sh");
            commandLine.addArgument("-c");
            commandLine.addArgument(command, false);

            System.out.println("Running command: " + commandLine.toStrings());

            // Create the PTY process builder
            PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder(
                commandLine.toStrings()
            );

            // Set the working directory for the PTY process builder to the project directory
            ptyProcessBuilder.setDirectory(projectDir);

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
