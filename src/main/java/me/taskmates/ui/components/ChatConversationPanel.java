package me.taskmates.ui.components;

import com.intellij.ui.jcef.JBCefBrowser;
import me.taskmates.lib.utils.HTMLUtils;
import me.taskmates.lib.utils.MarkdownUtils;
import me.taskmates.lib.utils.ModelUtils;
import me.taskmates.clients.ChatMessage;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class ChatConversationPanel extends JPanel {
    final private JBCefBrowser conversationBrowser;
    final private ChatSessionsPanel chatSessionsPanel;
    private DefaultListModel<ChatMessage> chatMessagesModel = new DefaultListModel<>();

    public ChatConversationPanel() {
        setLayout(new MigLayout("fill, nogrid, insets 0, gap 0"));
        conversationBrowser = new JBCefBrowser();
        chatSessionsPanel = new ChatSessionsPanel();
        add(chatSessionsPanel, "growx, push, wrap");
        add(conversationBrowser.getComponent(), "grow, push");

        String html = "<html style='margin: 0; padding: 0;'>" +
                "<body style='background-color: blue; padding: 0 10px; margin: 0'></body>" +
                "</html>";
        conversationBrowser.loadHTML(html);
    }

    public void setChatMessagesModel(DefaultListModel<ChatMessage> chatMessagesModel) {
        this.chatMessagesModel = chatMessagesModel;
        chatMessagesModel.addListDataListener(new ListDataListener() {
            @Override
            public void intervalAdded(ListDataEvent e) {
                for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                    ChatMessage incomingChatMessageModel = chatMessagesModel.getElementAt(i);
                    // incomingChatMessageModel.getContent().addTextChangeListener(evt ->
                    //         updateConversation()
                    // );
                }

                updateConversation();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                // TODO
                // for (int i = e.getIndex0(); i <= e.getIndex1(); i++) {
                //     ChatMessage incomingChatMessageModel = chatMessagesModel.getElementAt(i);
                //     incomingChatMessageModel.getContent().removeTextChangeListener(listener);
                // }

                updateConversation();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                updateConversation();
            }
        });

        updateConversation();
    }

    public void setExistingChatSessionsModel(DefaultComboBoxModel<String> chatSessionsModel) {
        chatSessionsPanel.setExistingChatSessionsModel(chatSessionsModel);
    }

    // public void setIncomingChatMessageModel(ChatMessage incomingChatMessageModel) {
    //     incomingChatMessageModel.getContent().addTextChangeListener(evt -> updateConversation());
    // }

    private void updateConversation() {
        StringBuilder contents = new StringBuilder();
        for (ChatMessage message : ModelUtils.toList(chatMessagesModel)) {
            String messageContent = message.getContent().toString();
            switch (message.getRole()) {
                case "system" -> {
                    contents.append("<div style='background-color: grey; padding: 10px;'>");
                    contents.append(MarkdownUtils.markdownToHTML(HTMLUtils.escapeHTML(messageContent)));
                    contents.append("</div>");
                }
                case "user" -> {
                    contents.append("<div style='background-color: #bbb; padding: 10px;'>");
                    contents.append("<div>[").append(message.getRole()).append("]</div>");
                    contents.append(MarkdownUtils.markdownToHTML(HTMLUtils.escapeHTML(messageContent)));
                    contents.append("</div>");
                }
                default -> {
                    contents.append("<div style='background-color: #e0e0e0; padding: 10px;'>");
                    contents.append("<div>[").append(message.getRole()).append("]</div>");
                    contents.append(MarkdownUtils.markdownToHTML(messageContent));
                    contents.append("</div>");
                }
            }
        }

        conversationBrowser.getCefBrowser().executeJavaScript("document.body.innerHTML = \""
                        + escapeJavascript(contents.toString()) + "\"",
                "https://localhost",
                0
        );
        conversationBrowser.getCefBrowser().executeJavaScript(
                "window.scrollTo(0, document.body.scrollHeight)",
                "https://localhost",
                0
        );
    }

    private String escapeJavascript(String string) {
        return string.replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    public void registerChatSessionSelectedListener(ActionListener listener) {
        chatSessionsPanel.registerChatSessionSelectedListener(listener);
    }

    public void setSelectedChatSession(int index) {
        chatSessionsPanel.setSelectedChatSession(index);
    }

    public static ChatConversationPanel preview_empty(ChatConversationPanel chatConversationPanel) {
        return chatConversationPanel;
    }

    public static ChatConversationPanel preview_withMessages(ChatConversationPanel chatConversationPanel) {
        List<ChatMessage> messagesModel = new ArrayList<>();
        messagesModel.add(new ChatMessage("You're an AI Assistant", "system"));
        messagesModel.add(new ChatMessage("How can I help you?", "assistant"));
        messagesModel.add(new ChatMessage("Hello. \n\n List 3 colors", "user"));
        messagesModel.add(new ChatMessage("Red\n- Green\n- Blue", "assistant"));
        messagesModel.add(new ChatMessage("List 1 to 50", "user"));

        String message = "";
        for (int i = 1; i < 51; i++) {
            message += i + "<br />";
        }

        messagesModel.add(new ChatMessage(message, "assistant"));

        return chatConversationPanel;
    }

    public static ChatConversationPanel preview_appendingChunks(ChatConversationPanel chatConversationPanel) {
        List<ChatMessage> messagesModel = new ArrayList<>();
        messagesModel.add(new ChatMessage("You're an AI Assistant", "system"));
        messagesModel.add(new ChatMessage("How can I help you?", "assistant"));
        messagesModel.add(new ChatMessage("Hello. \n\n List 3 colors", "user"));
        messagesModel.add(new ChatMessage(" Red\n- Green\n- Blue", "assistant"));

        return chatConversationPanel;
    }
}
