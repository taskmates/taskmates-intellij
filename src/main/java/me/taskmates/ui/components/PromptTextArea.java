package me.taskmates.ui.components;

import me.taskmates.ui.utils.Debouncer;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

public class PromptTextArea extends JTextArea {
    private Consumer<String> promptSubmittedListener = (s) -> {};
    private Consumer<String> promptChangedListener = (s) -> {};
    private Consumer<String> debouncedPromptChangedListener = (s) -> {};

    public PromptTextArea() {
        setLineWrap(true);
        requestFocus();
        // setOpaque(false);

        // enable SHIFT+ENTER key
        getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
        getInputMap().put(KeyStroke.getKeyStroke("control ENTER"), "insert-break");

        getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "ok");
        getActionMap().put("ok", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                promptSubmittedListener.accept(getText());
            }
        });

        getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                promptChangedListener.accept(getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                promptChangedListener.accept(getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                promptChangedListener.accept(getText());
            }
        });

        getDocument().addDocumentListener(new Debouncer(500) {
            @Override
            public void actionPerformed(ActionEvent e) {
                debouncedPromptChangedListener.accept(getText());
            }
        });
    }

    public void registerPromptSubmittedListener(Consumer<String> listener) {
        this.promptSubmittedListener = listener;
    }

    public void registerPromptChangedListener(Consumer<String> listener) {
        this.promptChangedListener = listener;
    }

    public void registerDebouncedPromptChangedListener(Consumer<String> listener) {
        this.debouncedPromptChangedListener = listener;
    }
}
