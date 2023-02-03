package me.taskmates.lib.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvUtils {

    // define a helper method that takes a csv contents as a parameter and returns a List<Map<String, Object>>
    public static List<Map<String, Object>> csvToMap(String csvContents) {
        // create a List<Map<String, Object>> to store the csv contents
        List<Map<String, Object>> list = new ArrayList<>();
        // create a BufferedReader to read the csv contents from the string
        try (BufferedReader br = new BufferedReader(new StringReader(csvContents))) {
            // read the first line and store it in an array of headers
            String[] headers = br.readLine().split(",");
            // read each line from the string
            String line;
            while ((line = br.readLine()) != null) {
                // split the line by commas
                String[] parts = line.split(",");
                // create a new map for each line
                Map<String, Object> map = new HashMap<>();
                // use a loop to iterate over the headers and use them as keys in the map
                for (int i = 0; i < headers.length; i++) {
                    // check if the value is "true" or "false"
                    if (parts[i].equals("true") || parts[i].equals("false")) {
                        // convert the value to a boolean and put it in the map
                        map.put(headers[i], Boolean.parseBoolean(parts[i]));
                    } else {
                        // otherwise, put the value as a string in the map
                        map.put(headers[i], parts[i]);
                    }
                }
                // add the map to the list
                list.add(map);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // return the list
        return list;
    }

    public static String jsonToCsv(List<Map<String, Object>> json) {
        if (json.size() == 0) return "\n";

        StringBuilder csv = new StringBuilder();

        // extract header
        json.get(0).keySet().forEach(key -> csv.append(quoteIfString(key)).append(","));

        // remove last comma
        csv.deleteCharAt(csv.length() - 1);

        // add new line
        csv.append("\n");

        // extract values
        json.forEach(map -> {
            map.values().forEach(value -> csv.append(quoteIfString(value)).append(","));
            // remove last comma
            csv.deleteCharAt(csv.length() - 1);
            // add new line
            csv.append("\n");
        });

        return csv.toString();
    }

    public static String quoteIfString(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof String) {
            return "\"" + escapeCsvString((String) value) + "\"";
        } else {
            return value.toString();
        }
    }

    public static String escapeCsvString(String string) {
        return string.replace("\"", "\"\"");
    }
}
