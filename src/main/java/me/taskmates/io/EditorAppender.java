package me.taskmates.io;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.ScrollType;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import me.taskmates.lib.utils.EditorUtils;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class EditorAppender {
    private static final Logger LOG = Logger.getInstance(EditorAppender.class);
    private final StringBuilder appendedCompletions = new StringBuilder();
    private final VirtualFile chatFile;
    private final Project project;

    public EditorAppender(Project project, VirtualFile chatFile) {
        this.project = project;
        this.chatFile = chatFile;
    }


    public CompletableFuture<Void> append(String text) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        String sanitized = text.replace("\r", "");
        if (sanitized.isEmpty()) {
            future.complete(null);
            return future;
        }

        this.appendedCompletions.append(sanitized);

        ApplicationManager.getApplication().invokeLater(() -> {
            if (project.isDisposed()) {
                LOG.warn("Project is already disposed. Cannot append to editor.");
                LOG.warn("Text: \n" + text);
                return;
            }
            WriteCommandAction.runWriteCommandAction(project, () -> {
                FileDocumentManager documentManager = FileDocumentManager.getInstance();
                Document document = documentManager.getDocument(chatFile);
                assert document != null;

                String textBefore = document.getText();

                Editor editor = EditorUtils.getEditorForFile(project, chatFile);
                boolean updateCursorAndScroll = false;

                if (editor != null) {
                    updateCursorAndScroll = editor.getCaretModel().getOffset() >= textBefore.trim().length();
                }

                Objects.requireNonNull(document).insertString(textBefore.length(), sanitized);
                PsiDocumentManager.getInstance(project).commitDocument(document);

                String textAfter = document.getText();

                if (textBefore.equals(textAfter)) {
                    throw new IllegalStateException("Update failed");
                }

                if (updateCursorAndScroll) {
                    int endOffset = document.getTextLength();
                    editor.getCaretModel().getCurrentCaret().moveToOffset(endOffset);
                    editor.getScrollingModel().scrollToCaret(ScrollType.RELATIVE);
                }
                future.complete(null);
            });
        });

        return future;
    }

    public StringBuilder getAppendedCompletions() {
        return appendedCompletions;
    }
}
