package me.taskmates.runners;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

public class ProcessRunner {
    // static final Logger LOG = Logger.getInstance(ProcessRunner.class);


    public static String runProcess(List<String> command) {
        return runProcess(command, Map.of(), "/tmp");
    }

    public static String runProcess(List<String> command, Map<String, String> env, String workingDir) {
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();

        try {
            int exitCode = ProcessRunner.runProcess(command, env, workingDir, stdout::append, stderr::append);

            if (exitCode != 0 || !stderr.isEmpty()) {
                throw new RuntimeException("Process failed with exit code: " + exitCode + ", stderr: \n" + stderr.toString());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Process was interrupted", e);
        }

        return stdout.toString();
    }

    public static int runProcess(List<String> command, Map<String, String> env, String workingDir, Consumer<String> stdoutConsumer, Consumer<String> stderrConsumer) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final int[] exitCode = new int[1];

        try {
            // LOG.info("Running process: " + command);

            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.directory(new File(workingDir));

            // Get the current environment and merge it with the provided environment variables
            Map<String, String> processEnvironment = processBuilder.environment();
            processEnvironment.putAll(System.getenv()); // Include all current environment variables
            processEnvironment.putAll(env); // Apply custom environment variables, potentially overriding system ones

            Process process = processBuilder.start();

            // Read the output from the script
            try (BufferedReader stdOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
                 BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = stdOut.readLine()) != null) {
                    stdoutConsumer.accept(line);
                }
                while ((line = stdErr.readLine()) != null) {
                    stderrConsumer.accept(line);
                }
            }

            // Wait for the process to complete
            exitCode[0] = process.waitFor();
        } catch (IOException e) {
            // LOG.error("Failed to run process", e);
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
        }

        latch.await(); // Wait for the process to complete
        return exitCode[0];
    }
}
