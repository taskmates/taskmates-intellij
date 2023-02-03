package me.taskmates.contexts;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ContextManager {
    public static final String CURRENT_FILE = "Current File";
    public static final String SELECTION = "Selection";
    public static final String OPEN_FILES = "Open Files";
    public static final String UNVERSIONED_FILES = "Unversioned Files";
    public static final String MODIFIED_FILES = "Modified Files";
    public static final String ALL = "All";
    public static final List<String> CONTEXTS_NAMES = Arrays.asList(
            CURRENT_FILE,
            SELECTION,
            OPEN_FILES,
            UNVERSIONED_FILES,
            MODIFIED_FILES,
            ALL
    );
    public static final String APPEND_TO_CHAT_FILE_AS_TRANSCLUSION = "Append as Transclusion";
    public static final String APPEND_TO_CHAT_FILE_AS_TEXT = "Append as Text";
    public static final String LINK = "Link File";
    public static final String SYMLINK = "Symlink File";
    public static final String COPY = "Copy File";
    public static final List<String> ADD_METHODS = Arrays.asList(
            APPEND_TO_CHAT_FILE_AS_TRANSCLUSION,
            APPEND_TO_CHAT_FILE_AS_TEXT,
            LINK,
            SYMLINK,
            COPY
    );
    static final Logger LOG = Logger.getInstance(ContextManager.class);

    public static CompletableFuture<Void> addFilesToContext(Project project, VirtualFile chatFile,
                                                            String contextName,
                                                            VirtualFile[] files,
                                                            String addMethod) {
        VirtualFile chatDir = chatFile.getParent();
        Path contextPath = Paths.get(chatDir.getPath(), contextName);
        CompletableFuture<Void> future = new CompletableFuture<>();

        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    switch (addMethod) {
                        case APPEND_TO_CHAT_FILE_AS_TRANSCLUSION -> {
                            VfsUtil.saveText(chatFile, VfsUtil.loadText(chatFile) + "\n## " + contextName + "\n\n");
                            for (VirtualFile file : files) {
                                String filePath = "#[[" + file.getPath() + "]]\n\n";
                                VfsUtil.saveText(chatFile, VfsUtil.loadText(chatFile) + filePath);
                            }
                        }
                        case APPEND_TO_CHAT_FILE_AS_TEXT -> {
                            VfsUtil.saveText(chatFile, VfsUtil.loadText(chatFile) + "\n## " + contextName + "\n\n");
                            for (VirtualFile file : files) {
                                String fileContent = new String(Files.readAllBytes(file.toNioPath()));
                                VfsUtil.saveText(chatFile, VfsUtil.loadText(chatFile) + fileContent + "\n\n");
                            }
                        }
                        case LINK, SYMLINK, COPY -> {
                            for (VirtualFile file : files) {
                                Path targetPath = Paths.get(contextPath.toString(), file.getName());
                                if (Files.exists(targetPath)) {
                                    byte[] targetContent = Files.readAllBytes(targetPath);
                                    byte[] sourceContent = Files.readAllBytes(file.toNioPath());
                                    if (Arrays.equals(sourceContent, targetContent)) {
                                        continue;
                                    } else {
                                        int result = Messages.showYesNoDialog(project, "The file " + file.getName() + " already exists. Do you want to overwrite it?", "File Exists", Messages.getQuestionIcon());
                                        if (result != Messages.YES) {
                                            continue;
                                        } else {
                                            Files.delete(targetPath);
                                        }
                                    }
                                }

                                switch (addMethod) {
                                    case LINK -> Files.createLink(targetPath, file.toNioPath());
                                    case SYMLINK -> Files.createSymbolicLink(targetPath, file.toNioPath());
                                    case COPY -> Files.copy(file.toNioPath(), targetPath);
                                }
                                chatDir.refresh(false, true);
                            }
                        }
                    }
                    future.complete(null);
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            });
        });
        return future;
    }

    public static void addTextToContext(VirtualFile chatFile,
                                        String contextName,
                                        String text,
                                        String addMethod) throws IOException {
        VirtualFile chatDir = chatFile.getParent();
        Path contextPath = Paths.get(chatDir.getPath(), contextName);

        if (COPY.equals(addMethod)) {
            Path targetPath = Paths.get(contextPath.toString(), contextName + ".txt");
            Files.write(targetPath, text.getBytes());
            chatDir.refresh(false, true);
        } else if (APPEND_TO_CHAT_FILE_AS_TEXT.equals(addMethod)) {
            VfsUtil.saveText(chatFile, VfsUtil.loadText(chatFile) + "\n## " + contextName + "\n\n" + text + "\n\n");
        }
    }
}
