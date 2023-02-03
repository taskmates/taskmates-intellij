package me.taskmates.utils;

import me.taskmates.lib.utils.CsvUtils;
import me.taskmates.lib.utils.JsonUtils;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;

public class CsvUtilsTest extends TestCase {

    public void testCsvToMap() {
        // define a sample csv contents as a string
        String csvContents = "name,color,size,isFruit\napple,red,small,true\nbanana,yellow,long,true\ncarrot,orange,cylindrical,false";

        // call the helper method with the csv contents and store the result in a variable
        List<Map<String, Object>> result = CsvUtils.csvToMap(csvContents);

        assertEquals(result.size(), 3);
        assertEquals(result.get(0).get("name"), "apple");
        assertEquals(result.get(1).get("color"), "yellow");
        assertEquals(result.get(2).get("isFruit"), false);
    }

    public void testJsonToCsv() {
        // define a sample json contents as a string
        String jsonContents = "[{\"name\":\"apple\",\"color\":\"red\",\"size\":\"small\",\"isFruit\":true},{\"name\":\"banana\",\"color\":\"yellow\",\"size\":\"long\",\"isFruit\":true},{\"name\":\"carrot\",\"color\":\"orange\",\"size\":\"cylindrical\",\"isFruit\":false}]";

        List<Map<String, Object>> json = JsonUtils.parseJsonList(jsonContents);

        // call the helper method with the json object and store the result in a variable
        String result = CsvUtils.jsonToCsv(json);

        assertEquals("\"name\",\"color\",\"size\",\"isFruit\"\n\"apple\",\"red\",\"small\",true\n\"banana\",\"yellow\",\"long\",true\n\"carrot\",\"orange\",\"cylindrical\",false\n", result);
    }
}
