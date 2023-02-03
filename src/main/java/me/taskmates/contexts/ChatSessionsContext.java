package me.taskmates.contexts;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ChatSessionsContext {

    private final Project project;

    public ChatSessionsContext(Project project) {
        this.project = project;
    }

    public VirtualFile getChatsBaseDir() {
        // Get the root directory of your project
        VirtualFile rootDir = LocalFileSystem.getInstance().findFileByPath(
            Objects.requireNonNull(project.getBasePath())
        );
        if (rootDir == null) {
            throw new NoSuchElementException("Project base path not found.");
        }
        return rootDir.findChild(".taskmates");
    }

    public VirtualFile getLastActiveChatDir() {
        VirtualFile TaskmatesDir = getChatsBaseDir();

        if (TaskmatesDir != null && TaskmatesDir.isDirectory()) {
            Ref<VirtualFile> mostRecentChatDir = new Ref<>();
            Ref<Long> mostRecentTimestamp = new Ref<>(Long.MIN_VALUE);

            VfsUtilCore.processFilesRecursively(TaskmatesDir, file -> {
                if (!file.isDirectory() && "chat.md".equals(file.getName())) {
                    long timeStamp = file.getTimeStamp();
                    if (timeStamp > mostRecentTimestamp.get()) {
                        mostRecentTimestamp.set(timeStamp);
                        mostRecentChatDir.set(file.getParent());
                    }
                }
                return true; // Continue processing
            });

            if (!mostRecentChatDir.isNull()) {
                return mostRecentChatDir.get();
            }
        }

        throw new NoSuchElementException("No active assistance session found.");
    }

    public static boolean isChatFile(VirtualFile file) {
        String fileName = file.getName();
        return fileName.endsWith(".md");
    }

    public boolean chatFileExists(String chatId) {
        VirtualFile chatsBaseDir = getChatsBaseDir();
        if (chatsBaseDir == null) return false;
        VirtualFile chatDir = chatsBaseDir.findChild(chatId);
        return chatDir != null && chatDir.findChild("chat.md") != null;
    }

    public VirtualFile findChatDir(@NotNull Editor editor) {
        return findChatFile(editor).getParent();
    }

    @NotNull
    public VirtualFile findChatFile(@NotNull Editor editor) {
        VirtualFile currentFile = FileDocumentManager.getInstance().getFile(editor.getDocument());
        assert currentFile != null;

        if (isChatFile(currentFile)) {
            return currentFile;
        }

        throw new NoSuchElementException("Chat file not found");

        // VirtualFile chatFolderVirtualFile = findChatDir(editor);
        // VirtualFile found = chatFolderVirtualFile.findChild("chat.md");
        // if (found == null) {
        //     throw new NoSuchElementException("Chat file not found");
        // }
        // return found;
    }

    public VirtualFile getProjectDir() {
        return VfsUtil.findFile(Path.of(project.getBasePath()), true);
    }

    // TODO: this doesn't take into account the current chat
    public VirtualFile getCwd() {
        return VfsUtil.findFile(Path.of(project.getBasePath()), true);
    }
}


