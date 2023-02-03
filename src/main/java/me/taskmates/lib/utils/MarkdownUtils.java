package me.taskmates.lib.utils;

import java.awt.*;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownUtils {
    public static String markdownToHTML(String markdown) {

        Parser parser = Parser.builder().build();
        Node document = parser.parse(markdown);
        HtmlRenderer renderer = HtmlRenderer.builder().softbreak("<br />").build();
        return renderer.render(document);
    }
}
