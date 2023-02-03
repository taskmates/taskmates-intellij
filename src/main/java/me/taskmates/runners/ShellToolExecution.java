package me.taskmates.runners;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.io.EditorAppender;
import me.taskmates.clients.Signals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("unchecked")
public class ShellToolExecution {
    public static CompletableFuture<Object> runShellBasedToolCall(Injector injector,
                                                                  String functionName,
                                                                  String command,
                                                                  Signals signals) {
        CompletableFuture<Object> future = new CompletableFuture<>();

        Project project = injector.getInstance(Project.class);
        VirtualFile chatFile = injector.getInstance(Key.get(VirtualFile.class, Names.named("chat_file")));

        EditorAppender editorCompletion = new EditorAppender(
            project,
            chatFile);

        String cwd = injector.getInstance(Key.get(String.class, Names.named("cwd")));
        String projectDir = injector.getInstance(Key.get(String.class, Names.named("project_dir")));

        List<CompletableFuture<Void>> completionFutures = new ArrayList<>();

        try {
            Map<String, String> env = new HashMap<>();

            env.put("TOOL_CALL_ID", injector.getInstance(Key.get(String.class, Names.named("tool_call_id"))));
            env.put("TOOL_CALL_CWD", cwd);
            env.put("CHAT_FILE", chatFile.getPath());
            env.put("CHAT_DIR", chatFile.getParent().getPath());
            env.put("PROJECT_DIR", projectDir);

            Map<String, String> context = injector.getInstance(Key.get(Map.class, Names.named(functionName)));
            for (Map.Entry<String, String> entry : context.entrySet()) {
                env.put(entry.getKey().toUpperCase(), entry.getValue());
            }

            ShellCommandRunner shellCommandRunner = new LocalShellCommandRunner();

            // result is a map with stdout, stderr, and exit_code
            int exitCode = shellCommandRunner.runShellCommand(
                command,
                cwd,
                env,
                line -> completionFutures.add(editorCompletion.append(line + "\n")),
                line -> completionFutures.add(editorCompletion.append(line + "\n")),
                signals);

            completionFutures.add(editorCompletion.append("\n"));

            CompletableFuture.allOf(completionFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    chatFile.getParent().refresh(false, true);
                    future.complete("Exit Code: " + exitCode);
                });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    public static CompletableFuture<Object> runShellCommand(Injector injector,
                                                            String command,
                                                            Signals signals) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        EditorAppender editorCompletionActions = new EditorAppender(injector.getInstance(Project.class), injector.getInstance(Key.get(VirtualFile.class, Names.named("chat_file"))));

        String cwd = injector.getInstance(Key.get(String.class, Names.named("cwd")));
        String host = injector.getInstance(Key.get(String.class, Names.named("host")));

        List<CompletableFuture<Void>> completionFutures = new ArrayList<>();

        try {
            Map<String, String> env = new HashMap<>();

            // TODO get ENV vars from context

            String projectDir = injector.getInstance(Key.get(String.class, Names.named("project_dir")));

            String toolCallId = injector.getInstance(Key.get(String.class, Names.named("tool_call_id")));
            env.put("TOOL_CALL_ID", toolCallId);

            // completionFutures.add(editorCompletionActions.appendCompletion("Running command: \n" +
            //     "cd \"" + cwd + "\" && " + command + "\n"));

            ShellCommandRunner shellCommandRunner;

            if (host.equals("docker")) {
                String containerName = injector.getInstance(Key.get(String.class, Names.named("container_name")));
                shellCommandRunner = new DockerShellCommandRunner(containerName);
            } else if (host.equals("docker-compose")) {
                String containerName = injector.getInstance(Key.get(String.class, Names.named("service_name")));
                shellCommandRunner = new DockerComposeShellCommandRunner(projectDir, containerName);
            } else {
                shellCommandRunner = new LocalShellCommandRunner();
            }

            System.out.println("ShellToolExecution. projectDir: " + projectDir);
            System.out.println("ShellToolExecution. cwd: " + cwd);
            System.out.println("ShellToolExecution. host: " + host);
            System.out.println("ShellToolExecution. ShellCommandRunner: " + shellCommandRunner.getClass().getName());

            // result is a map with stdout, stderr, and exit_code
            int exitCode = shellCommandRunner.runShellCommand(
                command,
                cwd,
                env,
                line -> completionFutures.add(editorCompletionActions.append(line + "\n")),
                line -> completionFutures.add(editorCompletionActions.append(line + "\n")),
                signals);

            completionFutures.add(editorCompletionActions.append("\n"));

            CompletableFuture.allOf(completionFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    future.complete("Exit Code: " + exitCode);
                });
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }
}
