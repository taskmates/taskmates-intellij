package me.taskmates.io.completions;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.io.EditorAppender;

import java.util.concurrent.CompletableFuture;

public class ChatCompletionEditorCompletion {
    private final EditorAppender editorAppender;

    public ChatCompletionEditorCompletion(Project instance, VirtualFile chatFile) {
        editorAppender = new EditorAppender(instance, chatFile);
    }

    public void processCompletionChunk(String chunk) {
        this.append(chunk);
    }

    public CompletableFuture<Void> append(String text) {
        return editorAppender.append(text);
    }
}
