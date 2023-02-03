package me.taskmates.ui.components;

import com.intellij.ui.JBColor;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class PromptPanel extends JPanel {
    private final PromptTextArea promptTextArea;

    public PromptPanel() {
        setLayout(new MigLayout("fill, insets 5 5 5 5"));
        setFinished();

        promptTextArea = new PromptTextArea();

        add(promptTextArea, "grow, push, width 900!");

        // add(sendButton);
    }

    public PromptTextArea getPromptTextArea() {
        return promptTextArea;
    }

    public void setInProgress() {
        setBackground(JBColor.YELLOW);
    }

    public void setFinished() {
        setBackground(JBColor.BLUE);
    }

    public void setErrored() {
        setBackground(JBColor.RED);
    }
}
