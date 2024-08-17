package me.taskmates.actions.session;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import junit.framework.TestCase;
import me.taskmates.runners.ProcessRunner;

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

        TestCase.assertEquals("Process should exit with code 0", 0, exitCode);
        TestCase.assertTrue("Stderr should be empty", stderrQueue.isEmpty());
        TestCase.assertEquals("Stdout should contain the echoed message", "Hello, World!", stdoutQueue.poll());
    }


    public void testPwdCommand() throws InterruptedException {
        List<String> command = Collections.singletonList("pwd");
        Map<String, String> env = Collections.emptyMap();
        String workingDir = System.getProperty("user.dir");
        ConcurrentLinkedQueue<String> stdoutQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> stderrQueue = new ConcurrentLinkedQueue<>();

        int exitCode = ProcessRunner.runProcess(command, env, workingDir, stdoutQueue::add, stderrQueue::add);

        TestCase.assertEquals("Process should exit with code 0", 0, exitCode);
        TestCase.assertTrue("Stderr should be empty", stderrQueue.isEmpty());
        TestCase.assertEquals("Stdout should contain the current working directory", workingDir, stdoutQueue.poll());
    }


    public void testEnvCommand() throws InterruptedException {
        List<String> command = Collections.singletonList("env");
        Map<String, String> env = Collections.singletonMap("TEST_ENV_VAR", "12345");
        String workingDir = System.getProperty("user.dir");
        ConcurrentLinkedQueue<String> stdoutQueue = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<String> stderrQueue = new ConcurrentLinkedQueue<>();

        int exitCode = ProcessRunner.runProcess(command, env, workingDir, stdoutQueue::add, stderrQueue::add);

        TestCase.assertEquals("Process should exit with code 0", 0, exitCode);
        TestCase.assertTrue("Stderr should be empty", stderrQueue.isEmpty());
        boolean containsEnvVar = stdoutQueue.stream().anyMatch(line -> line.contains("TEST_ENV_VAR=12345"));
        TestCase.assertTrue("Stdout should contain the environment variable", containsEnvVar);
    }
}
