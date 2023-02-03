package me.taskmates.runners;

import com.intellij.openapi.vfs.VirtualFile;
import me.taskmates.lib.utils.ThreadUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FunctionExecutionContext {
    @NotNull
    public static Map<String, Object> computeContext(Map<String, Object> metadata,
                                                     @NotNull VirtualFile projectDir,
                                                     @NotNull VirtualFile chatDir,
                                                     @NotNull VirtualFile chatFile) throws InterruptedException,
        ExecutionException {
        Map<String, Object> fullContext = new HashMap<>();

        // Use projectDir, chatDir, host, and cwd from metadata if available
        VirtualFile effectiveProjectDir = computeProjectDirPath(metadata, projectDir, chatDir);

        String chatDirPath = (String) metadata.getOrDefault("chat_dir", chatDir.getPath());

        fullContext.putAll(getDefaultContext());
        fullContext.putAll(getComputedMetadata());
        fullContext.putAll(getRuntimeContext(metadata, effectiveProjectDir, chatDirPath, chatFile.getPath()));
        fullContext.putAll(metadata);
        return fullContext;
    }

    @NotNull
    private static VirtualFile computeProjectDirPath(Map<String, Object> metadata,
                                                     @NotNull VirtualFile projectDir,
                                                     @NotNull VirtualFile chatDir) {
        if (metadata.containsKey("project_dir")) {
            String projectDirAsString = (String) metadata.get("project_dir");
            return Objects.requireNonNull(projectDir.getFileSystem().findFileByPath(projectDirAsString));
        }

        VirtualFile repoDir = chatDir.findChild("repo");
        if (repoDir != null) {
            return repoDir;
        }
        return projectDir;
    }

    private static Map<String, Object> getDefaultContext() {
        return Map.of(
            "container_name", "shell",
            "service_name", "shell"
        );
    }

    private static Map<String, String> getRuntimeContext(Map<String, Object> metadata,
                                                         VirtualFile effectiveProjectDir,
                                                         String chatDirPath,
                                                         String chatFilePath) throws ExecutionException, InterruptedException {
        String effectiveHost = computeHost(metadata, effectiveProjectDir);
        String effectiveCwd = computeCwd(metadata, effectiveProjectDir.getPath(), effectiveHost);

        return Map.of(
            "transclusions_base_dir", chatDirPath,
            "project_dir", effectiveProjectDir.getPath(),
            "chat_dir", chatDirPath,
            "chat_file", chatFilePath,
            "markdown_path", chatFilePath,
            "host", effectiveHost,
            "cwd", effectiveCwd
        );
    }

    private static String computeHost(Map<String, Object> metadata, VirtualFile projectDir) throws ExecutionException, InterruptedException {
        if (metadata.containsKey("host")) {
            return (String) metadata.get("host");
        }

        return ThreadUtils.runInReadAction(() -> {
            // if (projectDir.findChild("docker-compose.yaml") != null ||
            //     projectDir.findChild("docker-compose.yml") != null) {
            //     return "docker-compose";
            // } else if (projectDir.findChild("Dockerfile") != null) {
            //     return "docker";
            // }
            return "localhost";
        }).get();
    }

    private static String computeCwd(Map<String, Object> metadata, String effectiveProjectDir, String effectiveHost) {
        if (metadata.containsKey("cwd")) {
            return (String) metadata.get("cwd");
        }
        if (!"localhost".equals(effectiveHost)) {
            return "/app";
        }
        return effectiveProjectDir;
    }

    private static Map<String, Object> getComputedMetadata() {
        return Map.of();
    }
}
