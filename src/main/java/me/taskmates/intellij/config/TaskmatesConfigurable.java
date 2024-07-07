package me.taskmates.intellij.config;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class TaskmatesConfigurable implements Configurable {
    private JPanel myMainPanel;
    // private JTextField apiKeyField;
    private JTextField serverUrlField;
    private TaskmatesConfig taskmatesConfig;

    public TaskmatesConfigurable() {
        taskmatesConfig = TaskmatesConfig.getInstance();
    }

    @NlsContexts.ConfigurableName
    @Override
    public String getDisplayName() {
        return "Taskmates";
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        myMainPanel = new JPanel(new GridBagLayout());
        serverUrlField = new JTextField(30);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insets(5, 10);
        gbc.weightx = 1.0;

        gbc.gridx = 0;
        gbc.gridy = 0;
        myMainPanel.add(new JLabel("Server URL:"), gbc);

        gbc.gridy = 1;
        myMainPanel.add(serverUrlField, gbc);

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        myMainPanel.add(Box.createVerticalGlue(), gbc);

        return myMainPanel;
    }

    @Override
    public boolean isModified() {
        return !serverUrlField.getText().equals(taskmatesConfig.serverUrl);
    }

    @Override
    public void apply() {
        taskmatesConfig.serverUrl = serverUrlField.getText();
    }

    @Override
    public void reset() {
        serverUrlField.setText(taskmatesConfig.serverUrl);
    }
}
