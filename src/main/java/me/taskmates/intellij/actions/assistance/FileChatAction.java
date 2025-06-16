package me.taskmates.intellij.actions.assistance;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
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
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.assistances.markdown.MarkdownCompletionAssistance;
import me.taskmates.contexts.ChatSessionsContext;
import me.taskmates.contexts.ContextInjector;
import me.taskmates.io.chatdirs.ChatActions;
import me.taskmates.lib.utils.ThreadUtils;
import org.jetbrains.annotations.NotNull;


public class FileChatAction extends AnAction implements DumbAware {
    private static final Logger LOG = Logger.getInstance(FileChatAction.class);
    private final String model;

    public FileChatAction() {
        this("default");
    }

    public FileChatAction(String model) {
        this.model = model;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        Editor editor = CommonDataKeys.EDITOR.getData(event.getDataContext());
        assert editor != null;

        VirtualFile currentFile = editor.getVirtualFile();

        ThreadUtils.dumpThreadInfo(this.getClass().getSimpleName());

        if (ChatSessionsContext.isChatFile(currentFile)) {
            ContextInjector.createInjector(event).thenApplyAsync((injector -> {
                ThreadUtils.dumpThreadInfo(this.getClass().getSimpleName() + " injector.thenApplyAsync");

                ProgressManager.getInstance().run(new Task.Backgroundable(project, "FileChatAction", true) {
                    public void run(@NotNull ProgressIndicator indicator) {
                        System.out.println("entering " + indicator);
                        ThreadUtils.dumpThreadInfo(this.getClass().getSimpleName() + " Task.Backgroundable.run");
                        indicator.setText(FileChatAction.class.getCanonicalName());
                        MarkdownCompletionAssistance action = new MarkdownCompletionAssistance(project, editor, injector);
                        action.performCompletion(FileChatAction.this.model, indicator);
                        System.out.println("exiting " + indicator);
                    }
                });

                return null;
            })).exceptionally(e -> {
                LOG.error(e);
                ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred: " + e.getMessage(), "Error"));
                return null;
            });

        } else {
            ChatActions chatActions = new ChatActions(project);
            chatActions.createAndOpenChat(currentFile).exceptionally(e -> {
                LOG.error(e);
                ApplicationManager.getApplication().invokeLater(() -> Messages.showErrorDialog("An error occurred: " + e.getMessage(), "Error"));
                return null;
            });
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

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}

