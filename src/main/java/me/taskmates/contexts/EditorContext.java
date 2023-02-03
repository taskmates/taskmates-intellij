package me.taskmates.contexts;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import me.taskmates.lib.utils.ThreadUtils;

import java.util.concurrent.CompletableFuture;

public class EditorContext {
    private final Editor editor;

    public EditorContext(Editor editor) {
        this.editor = editor;
    }

    public String getLinePrefix() {
        int lineNumber = this.editor.getDocument().getLineNumber(this.editor.getCaretModel().getOffset());
        int lineStartOffset = this.editor.getDocument().getLineStartOffset(lineNumber);
        return this.editor.getDocument().getText().substring(lineStartOffset, this.editor.getCaretModel().getOffset());
    }

    public String getLineSuffix() {
        int lineNumber = this.editor.getDocument().getLineNumber(this.editor.getCaretModel().getOffset());
        int lineEndOffset = this.editor.getDocument().getLineEndOffset(lineNumber);
        return this.editor.getDocument().getText().substring(this.editor.getCaretModel().getOffset(), lineEndOffset);
    }

    public FileType getFileType() {
        return PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(editor.getDocument()).getFileType();
    }

    public String getTextPrefix() {
        return editor.getDocument().getText().substring(0, editor.getCaretModel().getOffset());
    }

    public String getTextSuffix() {
        return editor.getDocument().getText().substring(editor.getCaretModel().getOffset());
    }

    public String getPath() {
        Document document = editor.getDocument();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        return virtualFile.getPath().replace(getProject().getBasePath() + "/", "");
        // PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
        // return psiFile.getVirtualFile().getCanonicalPath();
    }

    public CompletableFuture<String> getCanonicalPath() {
        return ThreadUtils.runInEdt(() -> {
            Document document = editor.getDocument();
            VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
            virtualFile.getCanonicalPath();
            PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
            return psiFile.getVirtualFile().getCanonicalPath();
        });
    }

    public String getLanguage() {
        Document document = editor.getDocument();
        VirtualFile virtualFile = FileDocumentManager.getInstance().getFile(document);
        virtualFile.getCanonicalPath();
        PsiFile psiFile = PsiDocumentManager.getInstance(editor.getProject()).getPsiFile(document);
        return psiFile.getLanguage().getDisplayName();
    }

    public String getDocumentText() {
        return editor.getDocument().getText();
    }

    public Project getProject() {
        return editor.getProject();
    }

    public Editor getEditor() {
        return editor;
    }

    public int getCaretOffset() {
        return editor.getCaretModel().getOffset();
    }

    public int getSelectionStart() {
        return editor.getSelectionModel().getSelectionStart();
    }

    public int getSelectionEnd() {
        return editor.getSelectionModel().getSelectionEnd();
    }
}
