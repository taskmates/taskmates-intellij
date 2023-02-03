package me.taskmates.ui.components;

import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.speedSearch.SpeedSearch;
import com.intellij.util.Consumer;
import com.intellij.util.ui.UIUtil;
import me.taskmates.lib.utils.UIBindings;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;

public class EditorPreview extends EditorTextField {
    public static class State {
        public static final Border HIGHLIGHTED_BORDER = BorderFactory.createLineBorder(Color.black);
        public static final Border NON_HIGHLIGHTED_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
        public static final Dimension INSERTION_PREVIEW_SIZE = new Dimension(300, 200);
        public static final Dimension EDIT_PREVIEW_SIZE = new Dimension(900, 200);
        public static final Color BACKGROUND_CANCELLED = Color.gray;
    }

    public EditorPreview() {
        init();
    }


    // TODO test only
    public EditorPreview(String text) {
        super(text);
        init();
    }

    public EditorPreview(Project project,
                         FileType fileType) {
        super("",
                project,
                fileType
        );
        init();
    }


    public void init() {
        // setFocusable(true);
        setOneLineMode(false);
        setViewer(true);
        // setBackground(Color.GRAY);
        UIBindings.addHighlightOnFocusListeners(this);


        // getActionMap().put("EditorDown", null);

        //
        //
        // // Set the focus traversal keys for each component
        // Set<AWTKeyStroke> forwardKeys = new HashSet<>();
        // forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0));
        // forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0));
        //
        // Set<AWTKeyStroke> backwardKeys = new HashSet<>();
        // backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0));
        // backwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0));
        //
        // setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);
        // setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);
    }

    @Override
    protected EditorEx createEditor() {
        EditorEx editorEx = super.createEditor();

        editorEx.setHorizontalScrollbarVisible(true);
        editorEx.setVerticalScrollbarVisible(true);
        return editorEx;
    }

    public KeyEventDispatcher addNavigationKeyListener(KeyboardFocusManager currentKeyboardFocusManager, Runnable onEnter, Consumer<KeyEvent> onTyping) {
        KeyEventDispatcher keyEventDispatcher = e -> {
            if (!(e.getSource() instanceof EditorComponentImpl)) {
                return false;
            }

            Container container = ((EditorComponentImpl) e.getSource()).getEditor().getComponent().getParent();

            if (!(container instanceof EditorPreview)) {
                return false;
            }

            EditorPreview editorPreview = (EditorPreview) container;

            if (editorPreview != this) {
                return false;
            }

            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_RIGHT:
                        currentKeyboardFocusManager.focusNextComponent();
                        e.consume();
                        return true;
                    case KeyEvent.VK_LEFT:
                        currentKeyboardFocusManager.focusPreviousComponent();
                        e.consume();
                        return true;
                    case KeyEvent.VK_ENTER:
                        onEnter.run();
                        e.consume();
                        return true;
                }
            } else if (e.getID() == KeyEvent.KEY_TYPED) {
                if (isTyping(e)) {
                    onTyping.consume(e);
                    return false;
                }
            }
            return false;
        };

        currentKeyboardFocusManager.addKeyEventDispatcher(keyEventDispatcher);

        return keyEventDispatcher;
    }

    private boolean isTyping(KeyEvent e) {
        if (e.isAltDown()) return false;
        // if (e.isShiftDown() && isNavigationKey(e.getKeyCode())) return;
        // if (true) {
        if (e.getID() == KeyEvent.KEY_TYPED) {
            if (!UIUtil.isReallyTypedEvent(e)) return false;
            char c = e.getKeyChar();
            if (Character.isLetterOrDigit(c) || !Character.isWhitespace(c) && SpeedSearch.PUNCTUATION_MARKS.indexOf(c) != -1) {
                return true;
            }
        }

        return false;
    }

    public void setInProgress() {
        setBackground(Color.YELLOW);
    }

    public static EditorPreview preview_completion(EditorPreview choicePreview) {
        choicePreview.setText("Hello World");
        choicePreview.setPreferredSize(State.INSERTION_PREVIEW_SIZE);
        choicePreview.setMinimumSize(State.INSERTION_PREVIEW_SIZE);
        choicePreview.setSize(State.INSERTION_PREVIEW_SIZE);
        return choicePreview;
    }

    public static EditorPreview preview_edit(EditorPreview choicePreview) {
        String text = "";
        for (int i = 0; i < 100; i++) {
            text += "This is line " + i + "\n";
        }
        choicePreview.setText(text);
        choicePreview.setPreferredSize(State.EDIT_PREVIEW_SIZE);
        choicePreview.setMinimumSize(State.EDIT_PREVIEW_SIZE);
        choicePreview.setSize(State.EDIT_PREVIEW_SIZE);

        return choicePreview;
    }
}
