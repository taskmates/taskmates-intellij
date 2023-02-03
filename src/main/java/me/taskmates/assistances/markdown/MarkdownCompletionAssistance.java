package me.taskmates.assistances.markdown;

import com.google.inject.Injector;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.clients.Signals;
import me.taskmates.contexts.ChatSessionsContext;
import me.taskmates.contexts.ContextInjector;
import me.taskmates.lib.utils.ThreadUtils;
import me.taskmates.runners.FunctionExecutionContext;
import me.taskmates.runners.ProgressFeedback;
import me.taskmates.ui.EscBinding;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class MarkdownCompletionAssistance {
    private static final Logger LOG = Logger.getInstance(MarkdownCompletionAssistance.class);

    private final Project project;
    private final Editor editor;
    private final Injector injector;

    public MarkdownCompletionAssistance(Project project, Editor editor, Injector injector) {
        this.project = project;
        this.editor = editor;
        this.injector = injector;
    }

    public void performCompletion(String model, @NotNull ProgressIndicator indicator) {
        // TODO
        //  ideally we would move that to the backend

        ChatSessionsContext chatSessionsContext = new ChatSessionsContext(project);
        VirtualFile chatDir = chatSessionsContext.findChatDir(editor);
        VirtualFile chatFile = chatSessionsContext.findChatFile(editor);

        EscBinding escBinding = new EscBinding(chatFile);

        // TODO: this doesn't work with closing editors
        ProgressFeedback progressFeedback = new ProgressFeedback(this.editor);
        progressFeedback.showAICaret();

        try {

            String markdownChat = ThreadUtils.runInReadAction(() -> {
                FileDocumentManager documentManager = FileDocumentManager.getInstance();
                Document document = documentManager.getDocument(chatFile);
                assert document != null;
                return document.getText();
            }).get();

            @NotNull VirtualFile projectDir = Objects.requireNonNull(LocalFileSystem.getInstance().
                findFileByPath(Objects.requireNonNull(project.getBasePath())));

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("model", model);
            Map<String, Object> context = FunctionExecutionContext.computeContext(metadata, projectDir, chatDir, chatFile);

            Injector completionContextInjector = getCompletionContextInjector(context, chatDir, chatFile);

            MarkdownChatCompletionAssistance completionAssistance = new MarkdownChatCompletionAssistance();

            escBinding.bindEsc(project, completionAssistance::interruptOrKill);

            indicator.setText("Taskmates Assistance");

            Signals signals = new Signals();
            signals.on("error", (Object event) -> {
                // TODO what if the editor is closed? handle this situation.
                progressFeedback.handleCompletionException((Throwable) event, project, chatFile);
                indicator.cancel();
            });

            // Perform the completion
            CompletableFuture<Void> future = completionAssistance.performCompletion(
                    completionContextInjector,
                    context,
                    markdownChat,
                    signals)
                .exceptionally(throwable -> {
                    progressFeedback.handleCompletionException(throwable, project, chatFile);
                    indicator.cancel();
                    return null;
                });

            joinAndTrapCancellation(future, indicator, signals);
        } catch (Exception e) {
            progressFeedback.handleCompletionException(e, project, chatFile);
            LOG.error(e);
        } finally {
            LOG.info("Completion: Done");
            indicator.stop();
            escBinding.unbindEsc();
            progressFeedback.hideAICaret();
        }
    }

    private Injector getCompletionContextInjector(Map<String, Object> context,
                                                  @NotNull VirtualFile chatDir,
                                                  @NotNull VirtualFile chatFile) {

        Injector completionContextInjector;
        completionContextInjector = ContextInjector.createChildInjectorWithArguments(
            this.injector,
            context);
        completionContextInjector = ContextInjector.createChildInjectorWithArguments(
            completionContextInjector,
            Map.of(
                "chat_dir", chatDir,
                "chat_file", chatFile
            ));
        return completionContextInjector;
    }

    private void joinAndTrapCancellation(CompletableFuture<Void> future, @NotNull ProgressIndicator indicator, Signals signals) {
        // TODO: this doesn't look right.
        //  This is blocking the UI thread.
        //  Also, this join probably makes no sense since we are already looping until isDone

        while (!future.isDone()) {
            if (indicator.isCanceled()) {
                signals.send("kill", null);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        future.join();
    }
}
