package me.taskmates.ui.windows;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class TranslucentFrame extends JFrame {
    public TranslucentFrame() {
        super();

        setLayout(new MigLayout("insets 0"));
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        // setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // close on ESC
        getRootPane().registerKeyboardAction(e ->
                        // TODO move
                        setVisible(false),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void resizeToPercentage(double ratio) {
        setSize((int) (getToolkit().getScreenSize().getWidth() * ratio), (int) (getToolkit().getScreenSize().getHeight() * ratio));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->
        {
            TranslucentFrame frame = new TranslucentFrame();

            JPanel testContentPane = new JPanel();
            testContentPane.setLayout(new BoxLayout(testContentPane, BoxLayout.Y_AXIS));
            JLabel label = new JLabel("Hello World");
            label.setSize(new Dimension(300, 200));
            testContentPane.add(label);
            testContentPane.setSize(new Dimension(300, 200));

            frame.setContentPane(testContentPane);
            frame.pack();

            frame.setVisible(true);
        });
    }
}
