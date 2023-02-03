package me.taskmates.lib.utils;

public class HTMLUtils {
    public static String escapeHTML(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }

        StringBuilder escaped = new StringBuilder(input.length());
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            switch (c) {
                case '<':
                    escaped.append("&lt;");
                    break;
                case '>':
                    escaped.append("&gt;");
                    break;
                case '&':
                    escaped.append("&amp;");
                    break;
                case '"':
                    escaped.append("&quot;");
                    break;
                case '\'':
                    escaped.append("&#x27;");
                    break;
                case '/':
                    escaped.append("&#x2F;");
                    break;
                default:
                    escaped.append(c);
            }
        }

        return escaped.toString();
    }

    public static void main(String[] args) {
        String input = "<div class=\"test\">Hello, 'World'!</div>";
        String escaped = escapeHTML(input);
        System.out.println("Escaped HTML: " + escaped);
    }
}
