package me.taskmates.runners;

import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;
import me.taskmates.clients.Signals;
import me.taskmates.lib.utils.EscapeAnsiEscapeCodes;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class LocalShellCommandRunner implements ShellCommandRunner {
    @Override
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

            // Write the commands to a temporary script file
            File tempScript = File.createTempFile("script", null);
            try (Writer streamWriter = new OutputStreamWriter(new FileOutputStream(tempScript));
                 PrintWriter printWriter = new PrintWriter(streamWriter)) {
                printWriter.println("#!/usr/bin/env bash");
                printWriter.println("source ~/.bash_profile");
                printWriter.println("(" + command + ") | cat");
            }

            // Make the script file executable
            ProcessBuilder chmodProcessBuilder = new ProcessBuilder("chmod", "755", tempScript.getAbsolutePath());
            chmodProcessBuilder.start().waitFor();

            // Create the PTY process builder
            PtyProcessBuilder ptyProcessBuilder = new PtyProcessBuilder(new String[]{
                tempScript.getAbsolutePath()
            })
                .setDirectory(cwd)
                .setEnvironment(effectiveEnv);

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

            // Delete the script file when done
            boolean deleted = tempScript.delete();
            if (!deleted) {
                throw new IOException("Failed to delete temporary script file: " + tempScript.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return exitCode;
    }
}
