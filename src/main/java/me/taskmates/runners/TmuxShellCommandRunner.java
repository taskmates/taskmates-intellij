package me.taskmates.runners;

import com.intellij.openapi.progress.ProgressIndicator;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class TmuxShellCommandRunner {
    public static int runShellCommandOnTmux(String command,
                                            String cwd,
                                            Map<String, String> env,
                                            Consumer<String> outputConsumer,
                                            Consumer<String> errorConsumer,
                                            ProgressIndicator indicator) {
        int exitCode = 0;
        try {
            // Set the environment variables and working directory
            Map<String, String> effectiveEnv = new HashMap<>(System.getenv());
            effectiveEnv.putAll(env);

            // Define a fixed tmux session name
            String tmuxSessionName = "persistent_session";

            // Check if the tmux session exists
            ProcessBuilder tmuxHasSessionBuilder = new ProcessBuilder("tmux", "has-session", "-t", tmuxSessionName);
            Process tmuxHasSessionProcess = tmuxHasSessionBuilder.start();
            boolean sessionExists = (tmuxHasSessionProcess.waitFor() == 0);

            // Start a new tmux session if it doesn't exist
            if (!sessionExists) {
                ProcessBuilder tmuxStartBuilder = new ProcessBuilder("tmux", "new-session", "-d", "-s", tmuxSessionName);
                tmuxStartBuilder.environment().putAll(effectiveEnv);
                tmuxStartBuilder.directory(new File(cwd));
                tmuxStartBuilder.start().waitFor();
            }

            // Send the command to the tmux session
            ProcessBuilder tmuxSendBuilder = new ProcessBuilder("tmux", "send-keys", "-t", tmuxSessionName, command, "C-m");
            tmuxSendBuilder.environment().putAll(effectiveEnv);
            tmuxSendBuilder.directory(new File(cwd));
            Process tmuxSendProcess = tmuxSendBuilder.start();
            tmuxSendProcess.waitFor();

            // Capture any error output from the send-keys command
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(tmuxSendProcess.getErrorStream()))) {
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorConsumer.accept(errorLine);
                }
            }

            // Create a unique identifier for the 'wait-for' command
            String waitIdentifier = "cmd_done_" + System.currentTimeMillis();

            // Send the 'wait-for' trigger command to tmux after the original command
            ProcessBuilder tmuxSendWaitTriggerBuilder = new ProcessBuilder("tmux", "send-keys", "-t", tmuxSessionName, "tmux wait-for -T " + waitIdentifier, "C-m");
            tmuxSendWaitTriggerBuilder.environment().putAll(effectiveEnv);
            tmuxSendWaitTriggerBuilder.directory(new File(cwd));
            tmuxSendWaitTriggerBuilder.start().waitFor();

            // Wait for the 'wait-for' command to complete, indicating the command has finished
            ProcessBuilder tmuxWaitBuilder = new ProcessBuilder("tmux", "wait-for", waitIdentifier);
            tmuxWaitBuilder.environment().putAll(effectiveEnv);
            tmuxWaitBuilder.directory(new File(cwd));
            Process tmuxWaitProcess = tmuxWaitBuilder.start();
            exitCode = tmuxWaitProcess.waitFor();

            // Capture the output from the tmux session
            ProcessBuilder tmuxCaptureBuilder = new ProcessBuilder("tmux", "capture-pane", "-p", "-t", tmuxSessionName);
            tmuxCaptureBuilder.environment().putAll(effectiveEnv);
            tmuxCaptureBuilder.directory(new File(cwd));
            Process tmuxCaptureProcess = tmuxCaptureBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(tmuxCaptureProcess.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (indicator.isCanceled()) {
                    break;
                }
                outputConsumer.accept(line);
            }

            // Optionally kill the tmux session if it's no longer needed
            // ProcessBuilder tmuxKillBuilder = new ProcessBuilder("tmux", "kill-session", "-t", tmuxSessionName);
            // tmuxKillBuilder.start().waitFor();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return exitCode;
    }

    public static void main(String[] args) {
        String command = "cd /tmp && ls -l"; // The command to run
        String cwd = System.getProperty("user.dir"); // Use the current directory as the working directory

        try {
            // Start a new tmux session if it doesn't exist
            ProcessBuilder tmuxStartBuilder = new ProcessBuilder("tmux", "new-session", "-d", "-s", "simple_session");
            tmuxStartBuilder.start().waitFor();

            // Send the command to the tmux session
            ProcessBuilder tmuxSendBuilder = new ProcessBuilder("tmux", "send-keys", "-t", "simple_session", command, "C-m");
            tmuxSendBuilder.start().waitFor();

            // Capture the output from the tmux session
            ProcessBuilder tmuxCaptureBuilder = new ProcessBuilder("tmux", "capture-pane", "-p", "-t", "simple_session");
            Process tmuxCaptureProcess = tmuxCaptureBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(tmuxCaptureProcess.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append(System.lineSeparator());
            }

            // Kill the tmux session
            ProcessBuilder tmuxKillBuilder = new ProcessBuilder("tmux", "kill-session", "-t", "simple_session");
            tmuxKillBuilder.start().waitFor();

            // Print the output
            System.out.println("Output from tmux session:");
            System.out.println(outputBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
    }
}
