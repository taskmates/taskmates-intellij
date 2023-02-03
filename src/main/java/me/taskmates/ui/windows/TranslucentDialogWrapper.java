package me.taskmates.ui.windows;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.Producer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class TranslucentDialogWrapper extends DialogWrapper {
    private Producer<JPanel> centerPanelProducer;

    public TranslucentDialogWrapper(Producer<JPanel> centerPanelProducer) {
        super(true); // use current window as parent
        this.centerPanelProducer = centerPanelProducer;
        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return centerPanelProducer.produce();
    }

    @Override
    protected void init() {
        super.init();
        setUndecorated(true);
        setResizable(true);
        getWindow().setOpacity(0.8f);
        getRootPane().setOpaque(false);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{};
    }
}
