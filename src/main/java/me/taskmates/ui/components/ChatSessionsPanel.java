package me.taskmates.ui.components;

import com.intellij.openapi.ui.ComboBox;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ChatSessionsPanel extends JPanel {
    private JComboBox<String> chatSessionsComboBox;

    public ChatSessionsPanel() {
        setLayout(new MigLayout("fillx, insets 10 10 10 20"));

        setBackground(Color.BLUE);

        // chatSessions.addElement("New chat...");

        chatSessionsComboBox = new ComboBox<>();

        // Add the JComboBox to the JPanel with the constraint to occupy all available width
        add(chatSessionsComboBox, "growx");
    }

    public void setExistingChatSessionsModel(DefaultComboBoxModel<String> chatSessionsModel) {
        chatSessionsComboBox.setModel(chatSessionsModel);
    }

    public void registerChatSessionSelectedListener(ActionListener listener) {
        chatSessionsComboBox.addActionListener(listener);
    }

    public void setSelectedChatSession(int index) {
        chatSessionsComboBox.setSelectedIndex(index);
    }
}

