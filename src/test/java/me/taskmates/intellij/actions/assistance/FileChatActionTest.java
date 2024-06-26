package me.taskmates.intellij.actions.assistance;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.util.ThrowableComputable;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestDataPath;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.util.ui.UIUtil;
import me.taskmates.lib.utils.ThreadUtils;
import me.taskmates.runners.ProgressFeedback;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

@TestDataPath("$PROJECT_ROOT/src/test/testData/fileChatAction")
public class FileChatActionTest extends BasePlatformTestCase {
    public static final LightProjectDescriptor LIGHT_PROJECT_DESCRIPTOR = new LightProjectDescriptor();

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return LIGHT_PROJECT_DESCRIPTOR;
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
        myFixture.checkResultByFile("echo_after.md");

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
        myFixture.checkResultByFile("echoToolRunShellCommand_after.md");

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
        myFixture.checkResultByFile("echoToolPythonFunction_after.md");

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
        myFixture.checkResultByFile("echoCodeCell_after.md");

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
                boolean isCompletionFinished = editor.getDocument().getText().endsWith(">** ");
                boolean isCompletionInProgress = new ProgressFeedback(editor).isAICaretShowing().get();
                long elapsedTime = System.currentTimeMillis() - startTime;
                boolean hasTimeouted = elapsedTime >= timeoutMillis;
                return hasTimeouted || (isContentUpdated && !isCompletionInProgress && isCompletionFinished);
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        };

        // Wait for the background process to complete
        ProgressIndicatorUtils.awaitWithCheckCanceled(condition);
    }

    public static @NotNull ActionManager registerAction(String actionId, AnAction action) {
        ActionManager actionManager = ActionManager.getInstance();
        actionManager.registerAction(actionId, action);
        return actionManager;
    }
}
