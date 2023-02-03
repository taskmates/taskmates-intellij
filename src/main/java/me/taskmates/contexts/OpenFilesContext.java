package me.taskmates.contexts;

import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OpenFilesContext {
    public static List<VirtualFile> getOpenFiles(Project project) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] openFiles = fileEditorManager.getOpenFiles();
        List<VirtualFile> filteredFiles = new ArrayList<>();
        for (VirtualFile file : openFiles) {
            if (!file.isDirectory()
                    && !file.getFileType().isBinary()
            ) {
                filteredFiles.add(file);
            }
        }
        return Arrays.asList(openFiles);
    }


}
