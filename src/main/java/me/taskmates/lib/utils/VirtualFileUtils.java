package me.taskmates.lib.utils;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

public class VirtualFileUtils {
    public static void tree(VirtualFile virtualFile) {
        tree(virtualFile, "");
    }

    private static void tree(VirtualFile virtualFile, String indentation) {
        if (virtualFile == null) {
            return;
        }

        System.out.println(indentation + virtualFile.getName());

        if (virtualFile.isDirectory()) {
            String newIndentation = indentation + "  ";
            virtualFile.refresh(false, true);
            for (VirtualFile child : virtualFile.getChildren()) {
                tree(child, newIndentation);
            }
        }
    }

    public static VirtualFile mkdirs(Object requestor, VirtualFile root, Path path) throws IOException {
        if (root == null || path == null) {
            throw new IllegalArgumentException("Root and path must not be null.");
        }

        VirtualFile currentDirectory = root;

        Path relativePath = Path.of(root.getPath()).relativize(path);

        for (Path component : relativePath) {
            String componentName = component.toString();
            VirtualFile subDirectory = currentDirectory.findFileByRelativePath(componentName);

            if (subDirectory == null) {
                subDirectory = currentDirectory.createChildDirectory(requestor, componentName);
            }

            currentDirectory = subDirectory;
        }

        return currentDirectory;
    }

    public static String read(VirtualFile chatFile) throws IOException {
        try (InputStream inputStream = chatFile.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        }
    }
}
