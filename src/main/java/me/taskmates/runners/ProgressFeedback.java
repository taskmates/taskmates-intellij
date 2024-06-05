package me.taskmates.runners;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretVisualAttributes;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.io.completions.ChatCompletionEditorCompletion;
import me.taskmates.lib.utils.ThreadUtils;
import me.taskmates.lib.utils.UIState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;

public class ProgressFeedback {
    private static final Logger LOG = Logger.getInstance(ProgressFeedback.class);
    private final Editor editor;

    public ProgressFeedback(Editor editor) {
        this.editor = editor;
    }

    public void hideAICaret() {
        ApplicationManager.getApplication().invokeLater(() -> {
            CaretVisualAttributes originalCaretVisualAttributes = UIState.getOriginalCaretVisualAttributes(editor);
            Caret currentCaret = editor.getCaretModel().getCurrentCaret();
            assert originalCaretVisualAttributes != null;
            currentCaret.setVisualAttributes(originalCaretVisualAttributes);
        });
    }

    public void showAICaret() {
        ApplicationManager.getApplication().invokeLater(() -> {
            CaretVisualAttributes originalCaretVisualAttributes = editor.getCaretModel().getCurrentCaret().getVisualAttributes();
            UIState.putOriginalCaretVisualAttributes(editor, originalCaretVisualAttributes);
            Caret currentCaret = editor.getCaretModel().getCurrentCaret();
            currentCaret.setVisualAttributes(UIState.AI_CARET_VISUAL_ATTRIBUTES);
        });
    }

    public CompletableFuture<Boolean> isAICaretShowing() {
        return ThreadUtils.runInEdt(() -> {
            CaretVisualAttributes currentCaretVisualAttributes = editor.getCaretModel().getCurrentCaret().getVisualAttributes();
            return UIState.AI_CARET_VISUAL_ATTRIBUTES.equals(currentCaretVisualAttributes);
        });
    }


    public void handleCompletionException(Throwable throwable, Project project, VirtualFile chatFile) {
        if (throwable instanceof CancellationException) {
            return;
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        ChatCompletionEditorCompletion completion = new ChatCompletionEditorCompletion(project, chatFile);
        LOG.error(throwable);
        String errorMessage = throwable.getMessage() + "\n\nCheck Logs for more details\n\n";
        completion.append("**error:** " + errorMessage);

        // completion.append("**error:** " + throwable.getMessage() + "\n\n<pre style=\"display: none\">\n" + sw + "\n</pre>\n");
        // ApplicationManager.getApplication().invokeLater(() -> {
        //     Messages.showErrorDialog("Error", errorMessage);
        //     //     HintManager.getInstance().showErrorHint(editor, throwable.getClass().getName() + "\n" + throwable.getMessage());
        // });
    }
}
