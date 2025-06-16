package me.taskmates.intellij.actions.assistance;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ui.UIUtil;
import me.taskmates.lib.utils.ThreadUtils;
import me.taskmates.runners.ProgressFeedback;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

@TestDataPath("$PROJECT_ROOT/src/test/testData/fileChatAction")
public class FileChatActionTest extends BasePlatformTestCase {
    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new LightProjectDescriptor();
    }

    @Override
    protected String getTestDataPath() {
        return "src/test/testData/fileChatAction";
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // Disable background indexing
        // IndexingDataKeys.SKIP_SLOW_INDEXING.put(getProject(), Boolean.TRUE);
        // Other setup code...
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    @Override
    protected boolean runInDispatchThread() {
        return false;
    }

    public void testEcho() throws ExecutionException, InterruptedException {
        String actionId = "FileChatActionTest";

        // Register the action dynamically
        AnAction fileChatAction = new FileChatAction("quote");
        ActionManager actionManager = registerAction(actionId, fileChatAction);

        // Get the file to be tested
        VirtualFile file = myFixture.configureByFile("echo_before.md").getVirtualFile();

        // Open the file and perform the completion
        openAndPerformCompletion(file, actionId);

        // Check the result against the expected after file
        checkResultWithDiff("echo_after.md", "testEcho");

        // Unregister the action
        actionManager.unregisterAction(actionId);
    }

    public void testEchoToolRunShellCommand() throws ExecutionException, InterruptedException {
        String actionId = "FileChatActionEchoToolRunShellCommandTest";

        // Register the action dynamically
        AnAction fileChatAction = new FileChatAction("quote");
        ActionManager actionManager = registerAction(actionId, fileChatAction);

        // Get the file to be tested
        VirtualFile file = myFixture.configureByFile("echoToolRunShellCommand_before.md").getVirtualFile();

        // Open the file and perform the completion
        openAndPerformCompletion(file, actionId);

        // Check the result against the expected after file
        checkResultWithDiff("echoToolRunShellCommand_after.md", "testEchoToolRunShellCommand");

        // Unregister the action
        actionManager.unregisterAction(actionId);
    }

    public void testEchoToolPythonFunction() throws ExecutionException, InterruptedException {
        String actionId = "FileChatActionEchoToolPythonFunctionTest";

        // Register the action dynamically
        AnAction fileChatAction = new FileChatAction("quote");
        ActionManager actionManager = registerAction(actionId, fileChatAction);

        // Get the file to be tested
        VirtualFile file = myFixture.configureByFile("echoToolPythonFunction_before.md").getVirtualFile();

        // Open the file and perform the completion
        openAndPerformCompletion(file, actionId);

        // Check the result against the expected after file
        checkResultWithDiff("echoToolPythonFunction_after.md", "testEchoToolPythonFunction");

        // Unregister the action
        actionManager.unregisterAction(actionId);
    }


    public void testEchoCodeCell() throws ExecutionException, InterruptedException {
        String actionId = "FileChatActionEchoCodeCellTest";

        // Register the action dynamically
        AnAction fileChatAction = new FileChatAction("quote");
        ActionManager actionManager = registerAction(actionId, fileChatAction);

        // Get the file to be tested
        VirtualFile file = myFixture.configureByFile("echoCodeCell_before.md").getVirtualFile();

        // Open the file and perform the completion
        openAndPerformCompletion(file, actionId);

        // Check the result against the expected after file
        checkResultWithDiff("echoCodeCell_after.md", "testEchoCodeCell");

        // Unregister the action
        actionManager.unregisterAction(actionId);
    }


    private void openAndPerformCompletion(VirtualFile file, String actionId) throws ExecutionException, InterruptedException {
        // Open the file in the editor and get contents
        final String initialEditorContent = ThreadUtils.runInEdt(() -> Objects.requireNonNull(
            FileEditorManager.getInstance(getProject())
                .openTextEditor(new OpenFileDescriptor(getProject(), file), true)).getDocument().getText()).get();

        // Trigger the action
        UIUtil.invokeAndWaitIfNeeded(() -> myFixture.performEditorAction(actionId));

        long startTime = System.currentTimeMillis();
        long timeoutMillis = 20000;

        ThrowableComputable<Boolean, RuntimeException> condition = () -> {
            try {
                Editor editor = ThreadUtils.runInEdt(() -> FileEditorManager.getInstance(getProject()).getSelectedTextEditor()).get();
                boolean isContentUpdated = !editor.getDocument().getText().equals(initialEditorContent);
                boolean isCompletionFinished = editor.getDocument().getText().endsWith("**user>** ");
                boolean isCompletionErrored = editor.getDocument().getText().contains("**error>**");
                boolean isCompletionInProgress = new ProgressFeedback(editor).isAICaretShowing().get();
                long elapsedTime = System.currentTimeMillis() - startTime;
                boolean hasTimeouted = elapsedTime >= timeoutMillis;
                return hasTimeouted || isCompletionErrored || (isContentUpdated && !isCompletionInProgress && isCompletionFinished);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };

        // Wait for the background process to complete
        ProgressIndicatorUtils.awaitWithCheckCanceled(condition);
    }

    private void checkResultWithDiff(String expectedFileName, String testName) {
        try {
            myFixture.checkResultByFile(expectedFileName);
        } catch (AssertionError e) {
            // Log the actual content when test fails
            String actualContent = myFixture.getEditor().getDocument().getText();
            System.err.println("=== TEST FAILED: " + testName + " ===");
            System.err.println("=== ACTUAL OUTPUT ===");
            System.err.println(actualContent);
            System.err.println("=== END ACTUAL OUTPUT ===");
            
            // Try to read expected content
            try {
                VirtualFile expectedFile = myFixture.findFileInTempDir("../" + expectedFileName);
                if (expectedFile == null) {
                    // Try alternative path
                    expectedFile = LocalFileSystem.getInstance().findFileByPath(
                        getTestDataPath() + "/" + expectedFileName
                    );
                }
                if (expectedFile != null) {
                    String expectedContent = new String(expectedFile.contentsToByteArray(), StandardCharsets.UTF_8);
                    System.err.println("=== EXPECTED OUTPUT ===");
                    System.err.println(expectedContent);
                    System.err.println("=== END EXPECTED OUTPUT ===");
                    
                    // Show character-by-character diff for the end of the file
                    System.err.println("=== DETAILED DIFF (last 100 chars) ===");
                    int actualLen = actualContent.length();
                    int expectedLen = expectedContent.length();
                    int startPos = Math.max(0, Math.min(actualLen, expectedLen) - 100);
                    
                    System.err.println("Actual length: " + actualLen + ", Expected length: " + expectedLen);
                    System.err.println("Actual ends with: " + actualContent.substring(Math.max(0, actualLen - 50))
                        .replace("\n", "\\n").replace("\r", "\\r"));
                    System.err.println("Expected ends with: " + expectedContent.substring(Math.max(0, expectedLen - 50))
                        .replace("\n", "\\n").replace("\r", "\\r"));
                }
            } catch (Exception ex) {
                System.err.println("Could not read expected file: " + ex.getMessage());
            }
            
            // Re-throw the original assertion error
            throw e;
        }
    }
    public static @NotNull ActionManager registerAction(String actionId, AnAction action) {
        ActionManager actionManager = ActionManager.getInstance();
        actionManager.registerAction(actionId, action);
        return actionManager;
    }
}
