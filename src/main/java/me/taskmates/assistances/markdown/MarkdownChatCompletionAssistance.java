package me.taskmates.assistances.markdown;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.name.Names;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.clients.Signals;
import me.taskmates.clients.taskmates.TaskmatesCompletionRequest;
import me.taskmates.io.completions.ChatCompletionEditorCompletion;
import me.taskmates.lib.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MarkdownChatCompletionAssistance {
    private TaskmatesCompletionRequest currentRequest;

    public void interrupt() {
        currentRequest.interrupt();
    }

    // Example payload:
    /*
        {
          "context": {
            "cwd": "/private/var/demos/taskmates-demo",
            "container_name": "shell",
            "service_name": "shell",
            "chat_dir": "/projects/taskmates/taskmates-intellij/build/idea-sandbox/config/scratches",
            "project_dir": "/private/var/demos/taskmates-demo",
            "host": "localhost",
            "chat_file": "/projects/taskmates/taskmates-intellij/build/idea-sandbox/config/scratches/scratch_1.md",
            "model": "claude-3-haiku-20240307",
            "transclusions_base_dir": "/projects/taskmates/taskmates-intellij/build/idea-sandbox/config/scratches",
            "markdown_path": "/projects/taskmates/taskmates-intellij/build/idea-sandbox/config/scratches/scratch_1.md"
          },
          "markdown_chat": "Hey @shell what's the current date?\n"
        }
     */


    public CompletableFuture<Void> performCompletion(Injector injector,
                                                     Map<String, Object> context,
                                                     String markdownChat,
                                                     @NotNull Signals signals) {
        ChatCompletionEditorCompletion editorCompletion = new ChatCompletionEditorCompletion(injector.getInstance(Project.class),
            injector.getInstance(Key.get(VirtualFile.class, Names.named("chat_file"))));

        signals.on("completion", editorCompletion::processCompletionChunk)
            .on("kill", (Object unused) -> this.currentRequest.kill());

        // Extract fields from context
        String cwd = (String) context.get("cwd");
        String markdownPath = (String) context.get("markdown_path");
        String model = (String) context.get("model");

        // Create CompletionContext
        Map<String, Object> completionContext = Map.of(
            "request_id", UUID.randomUUID().toString(),
            "cwd", cwd,
            "markdown_path", markdownPath
        );

        // Create CompletionOpts with defaults
        Map<String, Object> completionOpts = Map.of(
            "model", model
        );

        // Create CompletionPayload
        Map<String, Object> payload = Map.of(
            "type", "markdown_chat_completion",
            "version", "0.1.0",
            "markdown_chat", markdownChat,
            "completion_context", completionContext,
            "completion_opts", completionOpts
        );

        String jsonPayload = JsonUtils.dump(payload);

        currentRequest = new TaskmatesCompletionRequest(
            JsonUtils.parseJson(jsonPayload),
            signals);
        return currentRequest.performRequest();
    }
}
