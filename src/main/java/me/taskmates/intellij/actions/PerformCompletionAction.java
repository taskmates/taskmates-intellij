package me.taskmates.intellij.actions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import me.taskmates.assistances.markdown.MarkdownCompletionAssistance;
import me.taskmates.contexts.ContextInjector;
import me.taskmates.intellij.config.TaskmatesConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PerformCompletionAction extends AnAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(PerformCompletionAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
        assert editor != null;

        ModelSelectionDialog dialog = new ModelSelectionDialog();
        if (dialog.showAndGet()) {
            String selectedModel = dialog.getSelectedModel();
            if (selectedModel != null) {
                // Save the selected model
                TaskmatesConfig config = TaskmatesConfig.getInstance();
                config.lastSelectedModel = selectedModel;
                ContextInjector.createInjector(event).thenAccept((injector -> {
                    Project project = injector.getInstance(Project.class);
                    ProgressManager.getInstance().run(new Task.Backgroundable(project, "Performing Completion", true) {
                        public void run(@NotNull ProgressIndicator indicator) {
                            try {
                                MarkdownCompletionAssistance action = new MarkdownCompletionAssistance(project, editor, injector);
                                action.performCompletion(selectedModel, indicator);
                            } catch (Exception e) {
                                ApplicationManager.getApplication().invokeLater(() -> Messages.showMessageDialog(project, e.getMessage(), "Error", Messages.getErrorIcon()));
                                LOG.error(e);
                            }
                        }
                    });
                })).exceptionally((e) -> {
                    LOG.error(e);
                    return null;
                });
            }
        }
    }

    static class ModelSelectionDialog extends DialogWrapper {
        private static final Logger LOG = Logger.getInstance(ModelSelectionDialog.class);
        private JComboBox<String> modelComboBox;
        private List<String> models;

        protected ModelSelectionDialog() {
            super(true); // use current window as parent
            setTitle("Select Model");
            init();
        }

        @Nullable
        @Override
        protected JComponent createCenterPanel() {
            JPanel panel = new JPanel(new BorderLayout());
            models = fetchModels();
            modelComboBox = new JComboBox<>(models.toArray(new String[0]));
            
            // Restore the last selected model if it exists
            TaskmatesConfig config = TaskmatesConfig.getInstance();
            if (config.lastSelectedModel != null && models.contains(config.lastSelectedModel)) {
                modelComboBox.setSelectedItem(config.lastSelectedModel);
            }
            
            panel.add(modelComboBox, BorderLayout.CENTER);
            // Request focus for the JComboBox
            SwingUtilities.invokeLater(() -> modelComboBox.requestFocusInWindow());

            // Add key listener for Enter key
            modelComboBox.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                        if (getOKAction().isEnabled()) {
                            doOKAction();
                        }
                    }
                }
            });
            return panel;
        }

        List<String> fetchModels() {
            List<String> modelList = new ArrayList<>();
            try {
                TaskmatesConfig config = TaskmatesConfig.getInstance();
                URL url = URI.create(config.getHttpUrl() + "/v1/models").toURL();
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    JsonObject jsonObject = JsonParser.parseString(response.toString()).getAsJsonObject();
                    JsonObject modelsObject = jsonObject.getAsJsonObject("models");
                    if (modelsObject != null) {
                        for (Map.Entry<String, JsonElement> entry : modelsObject.entrySet()) {
                            modelList.add(entry.getKey());
                        }
                        // Sort models alphabetically
                        modelList.sort(String::compareTo);
                    }
                }
            } catch (Exception e) {
                LOG.error("Failed to fetch models from server", e);
                ApplicationManager.getApplication().invokeLater(() -> {
                    TaskmatesConfig config = TaskmatesConfig.getInstance();
                    Messages.showErrorDialog(
                        "Failed to fetch models from server: " + e.getMessage() + "\n\nPlease ensure the Taskmates server is running on " + config.getHttpUrl(),
                        "Connection Error"
                    );
                });
            }
            return modelList;
        }

        public String getSelectedModel() {
            return (String) modelComboBox.getSelectedItem();
        }
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());

        boolean visible = project != null && editor != null;
        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(visible);
    }
}
