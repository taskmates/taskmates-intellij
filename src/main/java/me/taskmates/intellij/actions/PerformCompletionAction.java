package me.taskmates.intellij.actions;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.ex.ActionManagerEx;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import me.taskmates.assistances.markdown.MarkdownCompletionAssistance;
import me.taskmates.contexts.ContextInjector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;


public class PerformCompletionAction extends AnAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(PerformCompletionAction.class);

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
        assert editor != null;

        // event.getData()
        ActionManagerEx actionManager = (ActionManagerEx) event.getActionManager();
        String actionId = actionManager.getLastPreformedActionId();

        String selectedModel = Objects.requireNonNull(actionId).split(":")[1];


        ContextInjector.createInjector(event).thenAccept((injector -> {
            Project project = injector.getInstance(Project.class);
            ProgressManager.getInstance().run(new Task.Backgroundable(project, "FileChatAction", true) {
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

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(e.getDataContext());

        boolean visible = project != null && editor != null;
        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(visible);
    }
}

