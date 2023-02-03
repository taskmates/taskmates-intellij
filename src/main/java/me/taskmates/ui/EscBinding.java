package me.taskmates.ui;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.lib.utils.UIBindings;

import java.awt.*;

public class EscBinding {
    private final VirtualFile file;
    private KeyEventDispatcher escBinding;

    public EscBinding(VirtualFile file) {
        this.file = file;
    }

    public void bindEsc(Project project, Runnable callback) {
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            this.escBinding = UIBindings.bindEsc(project, keyboardFocusManager, file, callback);
        }
    }

    public void unbindEsc() {
        KeyboardFocusManager keyboardFocusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        UIBindings.unbindEsc(keyboardFocusManager, this.escBinding);
    }
}
