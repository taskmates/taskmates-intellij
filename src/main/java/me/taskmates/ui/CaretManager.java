package me.taskmates.ui;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretVisualAttributes;
import com.intellij.openapi.editor.Editor;
import me.taskmates.lib.utils.UIState;

public class CaretManager {
    private final Editor editor;

    public CaretManager(Editor editor) {
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
        CaretVisualAttributes originalCaretVisualAttributes = editor.getCaretModel().getCurrentCaret().getVisualAttributes();
        UIState.putOriginalCaretVisualAttributes(editor, originalCaretVisualAttributes);
        Caret currentCaret = editor.getCaretModel().getCurrentCaret();
        currentCaret.setVisualAttributes(UIState.AI_CARET_VISUAL_ATTRIBUTES);
    }
}
