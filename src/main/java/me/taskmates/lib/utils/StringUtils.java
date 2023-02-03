package me.taskmates.lib.utils;

public class StringUtils {
    public static String snakeCaseToTitleCase(String snakeCaseString) {
        String[] words = snakeCaseString.split("_");
        StringBuilder titleCaseString = new StringBuilder();

        for (String word : words) {
            String firstLetter = word.substring(0, 1).toUpperCase();
            String restOfWord = word.substring(1).toLowerCase();
            titleCaseString.append(firstLetter).append(restOfWord).append(" ");
        }

        return titleCaseString.toString().trim();
    }

    public static String titleCaseToSnakeCase(String titleCaseString) {
        String[] words = titleCaseString.split("\\s+");
        StringBuilder snakeCaseString = new StringBuilder();

        for (String word : words) {
            String lowerCaseWord = word.toLowerCase();
            snakeCaseString.append(lowerCaseWord).append("_");
        }

        return snakeCaseString.toString().trim().replaceAll("_$", "");
    }
}
