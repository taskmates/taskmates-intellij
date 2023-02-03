package me.taskmates.ui.components;

import com.intellij.ui.jcef.JBCefBrowser;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class BrowserWidget extends JPanel {
    private JBCefBrowser browser;

    public BrowserWidget() {
        setLayout(new MigLayout("fill, insets 0"));
        browser = new JBCefBrowser();
        add(browser.getComponent(), "grow, push");
    }

    public void loadURL(String url) {
        browser.loadURL(url);
    }

    public void loadHTML(String html) {
        browser.loadHTML(html);
    }

    public static BrowserWidget preview(BrowserWidget browserWidget) {
        browserWidget.setPreferredSize(new Dimension(500, 500));
        browserWidget.loadURL("https://www.google.com");
        return browserWidget;
    }
}
