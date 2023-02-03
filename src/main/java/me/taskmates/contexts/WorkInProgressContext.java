package me.taskmates.contexts;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WorkInProgressContext {

    public static List<VirtualFile> getUnversionedFiles(Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        List<FilePath> unversionedFilePaths = changeListManager.getUnversionedFilesPaths();

        return unversionedFilePaths.stream()
                .map(FilePath::getVirtualFile)
                .collect(Collectors.toList());
    }

    public static List<VirtualFile> getModifiedFiles(Project project) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(project);
        Collection<Change> changes = changeListManager.getAllChanges();

        return changes.stream()
                .filter(change -> change.getType() == Change.Type.MODIFICATION || change.getType() == Change.Type.NEW)
                .map(Change::getVirtualFile)
                .collect(Collectors.toList());
    }

    public static List<VirtualFile> getWorkInProgress(Project project) {
        List<VirtualFile> unversionedFiles = getUnversionedFiles(project)
                .stream()
                .toList();

        List<VirtualFile> modifiedFiles = getModifiedFiles(project)
                .stream()
                .toList();

        List<VirtualFile> result = new ArrayList<>();
        result.addAll(unversionedFiles);
        result.addAll(modifiedFiles);

        return result;
    }
}
