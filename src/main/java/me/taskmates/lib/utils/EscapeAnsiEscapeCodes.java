package me.taskmates.lib.utils;

public class EscapeAnsiEscapeCodes {
    public static String removeAnsiEscapeCodes(String string) {
        return string;

        // // System.out.println(string.toCharArray());
        // String escaped = string;
        // escaped = escaped.replaceAll("\u001B]\\d{4}", "");
        // escaped = escaped.replaceAll("\u0007", "");
        //
        // // return escaped.replaceAll("\u001B\\[[;\\d]*m", "")
        // //         .replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "")
        // //         .replaceAll("\u001B\\[[;\\d]*m", "")
        // //         .replaceAll("\u001B\\[[;\\d]*[ -/]*[@-~]", "")
        // //         .replaceAll("\u001B]1337;.*?\u0007", "");
        // return escaped;
    }
}
