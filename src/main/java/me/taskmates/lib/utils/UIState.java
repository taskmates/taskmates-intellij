package me.taskmates.lib.utils;

import com.intellij.openapi.editor.CaretVisualAttributes;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.ui.EditorTextField;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class UIState {
    public static Key<CaretVisualAttributes> ORIGINAL_CARET_VISUAL_ATTRIBUTES_KEY = Key.create("ORIGINAL_CARET_VISUAL_ATTRIBUTES");

    public static final CaretVisualAttributes AI_CARET_VISUAL_ATTRIBUTES = new CaretVisualAttributes(Color.GRAY,
            CaretVisualAttributes.Weight.HEAVY, CaretVisualAttributes.Shape.BLOCK,
            1);

    public static void putOriginalCaretVisualAttributes(Editor editor, CaretVisualAttributes originalCaretVisualAttributes) {
        editor.putUserData(ORIGINAL_CARET_VISUAL_ATTRIBUTES_KEY, originalCaretVisualAttributes);
    }

    public static @Nullable CaretVisualAttributes getOriginalCaretVisualAttributes(Editor editor) {
        return editor.getUserData(ORIGINAL_CARET_VISUAL_ATTRIBUTES_KEY);
    }
}
