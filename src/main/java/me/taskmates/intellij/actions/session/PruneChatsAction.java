package me.taskmates.intellij.actions.session;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.lib.utils.ThreadUtils;
import me.taskmates.contexts.ChatSessionsContext;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PruneChatsAction extends AnAction implements DumbAware {

    private static final Logger LOG = Logger.getInstance(PruneChatsAction.class);

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) return;

        ThreadUtils.runInWriteCommand(project, () -> {
            ChatSessionsContext chatSessionsContext = new ChatSessionsContext(project);
            VirtualFile baseDir = chatSessionsContext.getChatsBaseDir();
            VfsUtil.processFilesRecursively(baseDir, dir -> {
                if (dir.isDirectory()) {
                    VirtualFile chatFile = dir.findChild("chat.md");
                    if (chatFile != null && chatFile.getLength() == 0) {
                        try {
                            // If chat.md is empty, delete the directory
                            dir.delete(this);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return true;
            });
            return null;
        });
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean visible = true;
        e.getPresentation().setVisible(visible);
        e.getPresentation().setEnabled(visible);
    }
}
