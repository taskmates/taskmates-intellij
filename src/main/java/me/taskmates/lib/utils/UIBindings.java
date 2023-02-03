package me.taskmates.lib.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.ui.components.EditorPreview;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;

public class UIBindings {

    public static void registerEscapeKeyAction(JFrame frame, ActionListener anAction) {
        frame.getRootPane().registerKeyboardAction(anAction,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public static void addHighlightOnFocusListeners(JComponent editorPreview) {
        editorPreview.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // editorPreview.setPreferredSize(new Dimension(440, 220));
                // editorPreview.setSize(new Dimension(440, 200));
                editorPreview.setBorder(EditorPreview.State.HIGHLIGHTED_BORDER);
            }

            @Override
            public void focusLost(FocusEvent e) {
                // editorPreview.setPreferredSize(new Dimension(400, 200));
                // editorPreview.setSize(new Dimension(400, 200));
                editorPreview.setBorder(EditorPreview.State.NON_HIGHLIGHTED_BORDER);
                // editorPreview.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 5, 5),
                //         null,
                //         TitledBorder.DEFAULT_JUSTIFICATION,
                //         TitledBorder.DEFAULT_POSITION,
                //         null,
                //         null));

            }
        });
    }


    @NotNull
    public static KeyEventDispatcher bindEsc(Project project,
                                             KeyboardFocusManager keyboardFocusManager,
                                             VirtualFile file,
                                             Runnable callback) {
        KeyEventDispatcher binding = e1 -> {
            if (e1.getKeyCode() == 27 // ESC
                && e1.getID() == KeyEvent.KEY_RELEASED) {
                Editor editor = EditorUtils.getCurrentActiveEditor(project);
                if (editor != null) {
                    VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
                    if (currentFile != null && currentFile.equals(file)) {
                        callback.run();
                        e1.consume();
                    }
                }
            }
            return false;
        };
        keyboardFocusManager.addKeyEventDispatcher(binding);
        return binding;
    }

    public static void unbindEsc(KeyboardFocusManager keyboardFocusManager, KeyEventDispatcher escBinding) {
        keyboardFocusManager.removeKeyEventDispatcher(escBinding);
    }

    @NotNull
    public static KeyEventDispatcher tempBindEsc(KeyboardFocusManager keyboardFocusManager, Runnable callback) {
        KeyEventDispatcher binding = e1 -> {
            if (e1.getKeyCode() == 27 // ESC
                && e1.getID() == KeyEvent.KEY_RELEASED) {
                callback.run();
                e1.consume();
//                unbindEsc(keyboardFocusManager, binding);
            }
            return false;
        };
        keyboardFocusManager.addKeyEventDispatcher(binding);
        return binding;
    }
}
