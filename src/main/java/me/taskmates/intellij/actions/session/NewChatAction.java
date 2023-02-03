package me.taskmates.intellij.actions.session;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import me.taskmates.io.chatdirs.ChatActions;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class NewChatAction extends AnAction implements DumbAware {
    static final Logger LOG = Logger.getInstance(NewChatAction.class);

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            Messages.showErrorDialog("No project selected", "No project selected");
            return;
        }

        ApplicationManager.getApplication().invokeLater(() -> {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    ChatActions chatActions = new ChatActions(project);
                    chatActions.createChatFile(this).thenAccept(chatActions::openChatFileInEditor);
                } catch (IOException e) {
                    LOG.error(e);
                    Messages.showErrorDialog(project, "An error occurred: " + e.getMessage(), "Error");
                }
            });
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean visible = true;
        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(visible);
    }
}

