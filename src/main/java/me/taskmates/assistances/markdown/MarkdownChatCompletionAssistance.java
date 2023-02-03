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
import java.util.concurrent.CompletableFuture;

public class MarkdownChatCompletionAssistance {
    private TaskmatesCompletionRequest currentRequest;
    private int interruptCount = 0;

    public void interruptOrKill() {
        if (currentRequest == null) {
            return;
        }

        interruptCount++;

        if (interruptCount == 1) {
            currentRequest.interrupt();
        } else {
            currentRequest.kill();
        }
    }


    public CompletableFuture<Void> performCompletion(Injector injector,
                                                     Map<String, Object> context,
                                                     String markdownChat,
                                                     @NotNull Signals signals) {
        ChatCompletionEditorCompletion editorCompletion = new ChatCompletionEditorCompletion(injector.getInstance(Project.class),
            injector.getInstance(Key.get(VirtualFile.class, Names.named("chat_file"))));

        signals.on("completion", editorCompletion::processCompletionChunk)
            .on("kill", (Object unused) -> this.currentRequest.kill());

        String jsonDump = JsonUtils.dump(
            Map.of(
                "markdown_chat", markdownChat,
                "context", context
            )
        );
        Map<String, Object> payload = JsonUtils.parseJson(jsonDump);
        currentRequest = new TaskmatesCompletionRequest(
            payload,
            signals);
        return currentRequest.performRequest();
    }
}
