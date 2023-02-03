package me.taskmates.ui.components;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AssistanceExecutionOptionsPanel extends JPanel {
    private List<EditorPreview> choicePreviews = new ArrayList<>();

    public AssistanceExecutionOptionsPanel() {
        // make height grow to fit content
        this.setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[grow]"));
        setBackground(Color.BLUE);
    }


    public EditorPreview appendNewCompletionPreview(EditorPreview choicePreview) {
        super.add(choicePreview, "grow, width " +
                EditorPreview.State.INSERTION_PREVIEW_SIZE.getWidth() +
                "!, height " + EditorPreview.State.INSERTION_PREVIEW_SIZE.getHeight() + "!");
        choicePreviews.add(choicePreview);

        return choicePreview;
    }

    public EditorPreview appendNewEditPreview(EditorPreview choicePreview) {
        super.add(choicePreview, "grow, width " +
                EditorPreview.State.EDIT_PREVIEW_SIZE.getWidth() +
                "!, height " + EditorPreview.State.EDIT_PREVIEW_SIZE.getHeight() + "!");
        choicePreviews.add(choicePreview);

        return choicePreview;
    }


    public static void preview_threeCompletionResults(AssistanceExecutionOptionsPanel editorPreviewSection) {
        editorPreviewSection.appendNewCompletionPreview(new EditorPreview("Hello World"));
        editorPreviewSection.appendNewCompletionPreview(new EditorPreview("Hello World 2"));
        editorPreviewSection.appendNewCompletionPreview(new EditorPreview("Hello World 3"));
    }

    public static void preview_oneCompletionResult(AssistanceExecutionOptionsPanel editorPreviewSection) {
        editorPreviewSection.appendNewCompletionPreview(new EditorPreview("Hello World"));
    }

    public static void preview_oneEditResult(AssistanceExecutionOptionsPanel editorPreviewSection) {
        EditorPreview editorPreview = new EditorPreview();
        String text = "";
        for (int i = 0; i < 100; i++) {
            text += "This is line " + i + "\n";
        }
        editorPreview.setText(text);
        editorPreviewSection.appendNewEditPreview(editorPreview);
    }
}
