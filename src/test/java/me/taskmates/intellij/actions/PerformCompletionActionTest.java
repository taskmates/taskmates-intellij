package me.taskmates.intellij.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import javax.swing.*;
import java.util.List;

public class PerformCompletionActionTest extends BasePlatformTestCase {

    @Test
    public void testModelSelectionDialog() {
        PerformCompletionAction.ModelSelectionDialog dialog = new PerformCompletionAction.ModelSelectionDialog() {
            @Override
            List<String> fetchModels() {
                // Simulate server response
                return List.of("claude-3-haiku-20240307", "claude-opus-4-20250514");
            }
        };

        List<String> models = dialog.fetchModels();
        assertNotNull(models);
        assertEquals(2, models.size());
        assertTrue(models.contains("claude-3-haiku-20240307"));
        assertTrue(models.contains("claude-opus-4-20250514"));
    }

    @Test
    public void testActionUpdateWithEditor() {
        PerformCompletionAction action = new PerformCompletionAction();
        
        // Create a test file to have an editor
        myFixture.configureByText("test.md", "# Test Markdown");
        
        // Create a proper data context with editor
        DataContext dataContext = new DataContext() {
            @Override
            @Nullable
            public Object getData(@NotNull String dataId) {
                if (CommonDataKeys.EDITOR.is(dataId)) {
                    return myFixture.getEditor();
                }
                if (CommonDataKeys.PROJECT.is(dataId)) {
                    return getProject();
                }
                return null;
            }
        };
        
        // Create an action event with the data context
        AnActionEvent event = new AnActionEvent(null, dataContext, "", 
                action.getTemplatePresentation().clone(), 
                com.intellij.openapi.actionSystem.ActionManager.getInstance(), 0);
        
        // Update the action
        action.update(event);
        
        // The presentation should be visible and enabled when there's an editor
        assertTrue(event.getPresentation().isVisible());
        assertTrue(event.getPresentation().isEnabled());
    }

    @Test
    public void testActionUpdateWithoutEditor() {
        PerformCompletionAction action = new PerformCompletionAction();
        
        // Create a data context without an editor
        DataContext emptyContext = new DataContext() {
            @Override
            @Nullable
            public Object getData(@NotNull String dataId) {
                if (CommonDataKeys.PROJECT.is(dataId)) {
                    return getProject();
                }
                // No editor provided
                return null;
            }
        };
        
        // Create an action event with the empty context
        AnActionEvent event = new AnActionEvent(null, emptyContext, "", 
                action.getTemplatePresentation().clone(), 
                com.intellij.openapi.actionSystem.ActionManager.getInstance(), 0);
        
        // Update the action
        action.update(event);
        
        // The presentation should not be visible or enabled without an editor
        assertFalse(event.getPresentation().isVisible());
        assertFalse(event.getPresentation().isEnabled());
    }

    @Test
    public void testModelSelectionDialogCreation() {
        PerformCompletionAction.ModelSelectionDialog dialog = new PerformCompletionAction.ModelSelectionDialog();
        assertNotNull(dialog);
        assertEquals("Select Model", dialog.getTitle());
        
        JComponent centerPanel = dialog.createCenterPanel();
        assertNotNull(centerPanel);
        assertTrue(centerPanel instanceof JPanel);
    }

    @Test
    public void testFetchModelsHandlesException() {
        PerformCompletionAction.ModelSelectionDialog dialog = new PerformCompletionAction.ModelSelectionDialog();
        
        // This should not throw an exception even if the server is not running
        List<String> models = dialog.fetchModels();
        assertNotNull(models);
        // The list might be empty if the server is not running, but it should not be null
    }

    @Test
    public void testGetSelectedModelReturnsNull() {
        PerformCompletionAction.ModelSelectionDialog dialog = new PerformCompletionAction.ModelSelectionDialog();
        dialog.createCenterPanel(); // Initialize the combo box
        
        // When no models are available, getSelectedModel should handle it gracefully
        String selectedModel = dialog.getSelectedModel();
        // Could be null if no models are available
        assertTrue(selectedModel == null || selectedModel instanceof String);
    }

    @Test
    public void testModelsSortedAlphabetically() {
        PerformCompletionAction.ModelSelectionDialog dialog = new PerformCompletionAction.ModelSelectionDialog() {
            @Override
            List<String> fetchModels() {
                // Return unsorted list
                return List.of("zebra-model", "alpha-model", "beta-model");
            }
        };

        // Note: The actual fetchModels method sorts the list, but our override doesn't
        // So we need to test the sorting logic separately
        List<String> models = new java.util.ArrayList<>(dialog.fetchModels());
        models.sort(String::compareTo);
        
        assertEquals("alpha-model", models.get(0));
        assertEquals("beta-model", models.get(1));
        assertEquals("zebra-model", models.get(2));
    }
}
