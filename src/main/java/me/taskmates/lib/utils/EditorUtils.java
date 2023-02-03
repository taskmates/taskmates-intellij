package me.taskmates.lib.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class EditorUtils {
    public static boolean isFileOpen(Project project, VirtualFile fileToCheck) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        VirtualFile[] openFiles = fileEditorManager.getOpenFiles();
        for (VirtualFile openFile : openFiles) {
            if (openFile.equals(fileToCheck)) {
                return true;
            }
        }
        return false;
    }

    public static Editor getCurrentActiveEditor(Project project) {
        return FileEditorManager.getInstance(project).getSelectedTextEditor();
    }

    public static Editor getEditorForFile(Project project, VirtualFile file) {
        FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
        FileEditor[] fileEditors = fileEditorManager.getEditors(file);
        for (FileEditor fileEditor : fileEditors) {
            if (fileEditor instanceof TextEditor) {
                return ((TextEditor) fileEditor).getEditor();
            }
        }
        return null;
    }
}
