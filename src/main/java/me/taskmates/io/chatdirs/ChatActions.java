package me.taskmates.io.chatdirs;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.contexts.ChatSessionsContext;
import me.taskmates.contexts.ContextManager;
import me.taskmates.lib.utils.ThreadUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class ChatActions {
    private static final Logger LOG = Logger.getInstance(ChatActions.class);

    private final Project project;
    private final ChatSessionsContext chatSessionsContext;

    public ChatActions(Project project) {
        this.project = project;
        this.chatSessionsContext = new ChatSessionsContext(project);
    }

    public CompletableFuture<VirtualFile> createAndOpenChat(VirtualFile sourceFile) {
        String chatId = sourceFile.getName();
        if (chatSessionsContext.chatFileExists(chatId)) {
            return this.openChatFile(chatId).thenApplyAsync(chatFile -> {
                        openChatFileInEditor(chatFile);
                        return chatFile;
                    }
            );
        } else {
            return this.createChatFile(this, chatId).thenApplyAsync(chatFile -> {
                ContextManager.addFilesToContext(project, chatFile,
                        ContextManager.CURRENT_FILE,
                        new VirtualFile[]{VfsUtil.findFile(sourceFile.toNioPath(), true)},
                        ContextManager.APPEND_TO_CHAT_FILE_AS_TRANSCLUSION);

                openChatFileInEditor(chatFile);
                return chatFile;
            });

        }
    }


    public CompletableFuture<VirtualFile> openChatFile(String chatDirName) {
        return ThreadUtils.runInEdt(() -> {
            VirtualFile chatsBaseDir = chatSessionsContext.getChatsBaseDir();
            if (chatsBaseDir == null) throw new RuntimeException("Chats base directory not found.");
            VirtualFile chatDir = chatsBaseDir.findChild(chatDirName);
            if (chatDir == null) throw new RuntimeException("Chat directory not found for chatDirName: " + chatDirName);

            VirtualFile chatFile = chatDir.findChild("chat.md");
            if (chatFile == null) throw new RuntimeException("Chat file not found in directory: " + chatDirName);
            openChatFileInEditor(chatFile);
            return chatFile;
        });
    }

    public CompletableFuture<Editor> openChatFileInEditor(VirtualFile chatFile) {
        return ThreadUtils.runInEdt(() -> Objects.requireNonNull(FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, chatFile), true)));
    }


    public @NotNull CompletableFuture<VirtualFile> createChatFile(Object requestor) throws IOException {
        String timestamp = String.format("%1$tY-%1$tm-%1$td_%1$tH-%1$tM-%1$tS-%1$tL", System.currentTimeMillis());
        String chatFileName = "chat@" + timestamp;

        return createChatFile(requestor, chatFileName);
    }

    @NotNull
    public CompletableFuture<VirtualFile> createChatFile(Object requestor, String chatFileName) {
        String directoryPath = project.getBasePath();
        Path chatFolderPath = Path.of(Objects.requireNonNull(directoryPath), ".taskmates", chatFileName);

        return ThreadUtils.runInWriteCommand(project, () -> {
            try {
                VirtualFile chatFolderVirtualFile;
                chatFolderVirtualFile = VfsUtil.createDirectoryIfMissing(chatFolderPath.toString());
                Objects.requireNonNull(chatFolderVirtualFile).refresh(false, true);
                VirtualFile chatFile = chatFolderVirtualFile.createChildData(requestor, "chat.md");

                chatFile.refresh(false, true);
                return chatFile;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public VirtualFile initializeChatDir(String dirName) throws IOException {
        String directoryPath = project.getBasePath();
        Path chatFolderPath = Path.of(Objects.requireNonNull(directoryPath), ".taskmates", dirName);
        VirtualFile chatFolderVirtualFile = VfsUtil.createDirectoryIfMissing(chatFolderPath.toString());
        Objects.requireNonNull(chatFolderVirtualFile).refresh(false, true);
        return chatFolderVirtualFile;
    }


    public @NotNull CompletableFuture<VirtualFile> getOrCreateChatFile(Object requestor, Editor editor) throws IOException {
        try {
            VirtualFile chatFile = chatSessionsContext.findChatFile(editor);
            return CompletableFuture.completedFuture(chatFile);
        } catch (NoSuchElementException e) {
            return createChatFile(requestor);
        }
    }
}
