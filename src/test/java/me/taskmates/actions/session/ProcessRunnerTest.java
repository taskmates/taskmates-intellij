package me.taskmates.actions.session;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import me.taskmates.runners.ProcessRunner;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ProcessRunnerTest extends BasePlatformTestCase {
    public void testEchoCommand() throws InterruptedException {
        List<String> command = Arrays.asList("echo", "Hello, World!");
        Map<String, String> env = Collections.emptyMap();
        String workingDir = System.getProperty("user.dir");
        ConcurrentLinkedQueue<String> stdoutQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> stderrQueue = new ConcurrentLinkedQueue<>();

        int exitCode = ProcessRunner.runProcess(command, env, workingDir, stdoutQueue::add, stderrQueue::add);

        Assertions.assertEquals(0, exitCode, "Process should exit with code 0");
        Assertions.assertTrue(stderrQueue.isEmpty(), "Stderr should be empty");
        Assertions.assertEquals("Hello, World!", stdoutQueue.poll(), "Stdout should contain the echoed message");
    }


    public void testPwdCommand() throws InterruptedException {
        List<String> command = Collections.singletonList("pwd");
        Map<String, String> env = Collections.emptyMap();
        String workingDir = System.getProperty("user.dir");
        ConcurrentLinkedQueue<String> stdoutQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> stderrQueue = new ConcurrentLinkedQueue<>();

        int exitCode = ProcessRunner.runProcess(command, env, workingDir, stdoutQueue::add, stderrQueue::add);

        Assertions.assertEquals(0, exitCode, "Process should exit with code 0");
        Assertions.assertTrue(stderrQueue.isEmpty(), "Stderr should be empty");
        Assertions.assertEquals(workingDir, stdoutQueue.poll(), "Stdout should contain the current working directory");
    }


    public void testEnvCommand() throws InterruptedException {
        List<String> command = Collections.singletonList("env");
        Map<String, String> env = Collections.singletonMap("TEST_ENV_VAR", "12345");
        String workingDir = System.getProperty("user.dir");
        ConcurrentLinkedQueue<String> stdoutQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> stderrQueue = new ConcurrentLinkedQueue<>();

        int exitCode = ProcessRunner.runProcess(command, env, workingDir, stdoutQueue::add, stderrQueue::add);

        Assertions.assertEquals(0, exitCode, "Process should exit with code 0");
        Assertions.assertTrue(stderrQueue.isEmpty(), "Stderr should be empty");
        boolean containsEnvVar = stdoutQueue.stream().anyMatch(line -> line.contains("TEST_ENV_VAR=12345"));
        Assertions.assertTrue(containsEnvVar, "Stdout should contain the environment variable");
    }
}
