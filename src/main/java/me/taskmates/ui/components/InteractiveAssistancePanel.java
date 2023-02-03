package me.taskmates.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class InteractiveAssistancePanel extends JPanel {
    private ChatConversationPanel chatConversationPanel;
    private PromptPanel promptPanel;
    private AssistanceExecutionOptionsPanel assistanceExecutionOptionsPanel;

    public InteractiveAssistancePanel() {
        setLayout(new MigLayout("fill, wrap 1, insets 0, hidemode 3"));
        setBackground(Color.BLACK);
        add(buildChatConversationPanel(), "growx, push, height 300!");
        add(buildPromptPanel(), "growx, push");
        add(buildAssistanceExecutionOptionsPanel(), "growx, push");
    }

    private JPanel buildAssistanceExecutionOptionsPanel() {
        assistanceExecutionOptionsPanel = new AssistanceExecutionOptionsPanel();
        assistanceExecutionOptionsPanel.setVisible(false);
        return assistanceExecutionOptionsPanel;
    }

    private JPanel buildPromptPanel() {
        promptPanel = new PromptPanel();
        return promptPanel;
    }


    private ChatConversationPanel buildChatConversationPanel() {
        chatConversationPanel = new ChatConversationPanel();
        // dialogPanel.setVisible(false);
        return chatConversationPanel;
    }

    public PromptTextArea getPromptTextArea() {
        return promptPanel.getPromptTextArea();
    }

    public AssistanceExecutionOptionsPanel getAssistanceExecutionOptionsPanel() {
        return assistanceExecutionOptionsPanel;
    }

    public AssistanceExecutionOptionsPanel openAssistanceExecutionOptionsPanel() {
        getAssistanceExecutionOptionsPanel().setVisible(true);
        return assistanceExecutionOptionsPanel;
    }

    public AssistanceExecutionOptionsPanel closeAssistanceExecutionOptionsPanel() {
        getAssistanceExecutionOptionsPanel().setVisible(false);
        return assistanceExecutionOptionsPanel;
    }

    public ChatConversationPanel getChatConversationPanel() {
        return chatConversationPanel;
    }

    public PromptPanel getPromptPanel() {
        return promptPanel;
    }

    public static void preview_withOneCompletionPreview(InteractiveAssistancePanel interactiveAssistancePanel) {
        interactiveAssistancePanel.openAssistanceExecutionOptionsPanel();
        AssistanceExecutionOptionsPanel.preview_oneCompletionResult(interactiveAssistancePanel.getAssistanceExecutionOptionsPanel());
    }

    public static void preview_withThreeCompletionPreviews(InteractiveAssistancePanel interactiveAssistancePanel) {
        interactiveAssistancePanel.openAssistanceExecutionOptionsPanel();
        AssistanceExecutionOptionsPanel.preview_threeCompletionResults(interactiveAssistancePanel.getAssistanceExecutionOptionsPanel());
    }

    public static void preview_withOneEditPreview(InteractiveAssistancePanel interactiveAssistancePanel) {
        interactiveAssistancePanel.openAssistanceExecutionOptionsPanel();
        AssistanceExecutionOptionsPanel.preview_oneEditResult(interactiveAssistancePanel.getAssistanceExecutionOptionsPanel());
    }

    public static void preview_withChatMessages(InteractiveAssistancePanel interactiveAssistancePanel) {
        interactiveAssistancePanel.openAssistanceExecutionOptionsPanel();
        ChatConversationPanel.preview_withMessages(interactiveAssistancePanel.chatConversationPanel);
    }
}
