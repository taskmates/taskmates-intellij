package me.taskmates.assistances.contexts;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.openapi.vcs.changes.LocalChangeList;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixture4TestCase;
import me.taskmates.contexts.WorkInProgressContext;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class WorkInProgressContextTest extends LightPlatformCodeInsightFixture4TestCase {

    @Override
    protected String getTestDataPath() {
        return "src/testData/WorkInProgressContextTest";
    }

    @Ignore("Not properly implemented")
    @Test
    public void testGetUnstagedModifiedFiles() throws IOException {
        // Set up the project with the test data
        myFixture.configureByFiles("file1.txt", "file2.txt", "file3.txt");

        // Modify the files
        WriteAction.run(() -> {
            myFixture.findFileInTempDir("file1.txt").setBinaryContent("Modified content 1".getBytes());
            myFixture.findFileInTempDir("file2.txt").setBinaryContent("Modified content 2".getBytes());
        });

        // Stage one of the files (file1.txt)
        stageFile(myFixture.findFileInTempDir("file1.txt"));

        // Get the unstaged modified files
        List<VirtualFile> unstagedModifiedFiles = WorkInProgressContext.getModifiedFiles(getProject());

        // Verify that only file2.txt is in the list of unstaged modified files
        assertEquals(1, unstagedModifiedFiles.size());
        assertEquals("file2.txt", unstagedModifiedFiles.get(0).getName());
    }

    private void stageFile(VirtualFile file) {
        ChangeListManager changeListManager = ChangeListManager.getInstance(getProject());
        LocalChangeList defaultChangeList = changeListManager.getDefaultChangeList();

        // // TODO
        // GitRepositoryManager repositoryManager = GitUtil.getRepositoryManager(getProject());
        // GitRepository repository = repositoryManager.getRepositoryForFileQuick(file);
        // if (repository == null) {
        //     throw new IllegalStateException("Repository not found for the file: " + file.getPath());
        // }
        // GitUtil.addFiles(getProject(), repository.getRoot(), List.of(file));
    }
}
