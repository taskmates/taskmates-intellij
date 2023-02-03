package me.taskmates.utils;

import me.taskmates.lib.utils.JsonUtils;
import junit.framework.TestCase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtilsTest extends TestCase {
    public void testRoundtripParseDumpJsonArray() {
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("boolean_value", true);

        List<Map<String, Object>> expectedList = List.of(expectedMap);
        String jsonString = JsonUtils.dump(expectedList);
        List<Map<String, Object>> parsedList = JsonUtils.parseJsonList(jsonString);

        assertEquals(JsonUtils.dump(expectedList), JsonUtils.dump(parsedList));
    }


    public void testRoundtripParseDumpJsonObject() {
        // Define the expected map structure
        Map<String, Object> expectedMap = new HashMap<>();
        expectedMap.put("string_value", "a");
        expectedMap.put("int_value", 2);
        expectedMap.put("float_value", 3.4);
        expectedMap.put("array_value", new String[]{"a", "b", "c"});
        expectedMap.put("list_value", List.of("a", "b", "c"));
        expectedMap.put("boolean_value", true);

        Map<String, Object> nestedMap = new HashMap<>();
        nestedMap.put("int_value", 3);

        expectedMap.put("nested_value", nestedMap);

        // Serialize the expected map to a JSON string
        String expectedJsonString = JsonUtils.dump(expectedMap);

        // Parse the expected JSON string back into a map
        Map<String, Object> parsedMap = JsonUtils.parseJson(expectedJsonString);

        // Compare the dumped JSON string of the parsed map with the dumped JSON string of the expected map
        assertEquals(JsonUtils.dump(expectedMap), JsonUtils.dump(parsedMap));
    }

    public void testParseJsonObjectToMap() {
        String jsonString = "{\"key1\":\"value1\", \"key2\":123, \"nestedObj\":{\"nestedKey\":\"nestedValue\"}, \"array\":[1, 2, 3]}";
        Map<String, Object> resultMap = JsonUtils.parseJson(jsonString);

        assertEquals("value1", resultMap.get("key1"));
        assertEquals(123, resultMap.get("key2"));
        assertTrue(resultMap.get("nestedObj") instanceof Map);
        assertTrue(resultMap.get("array") instanceof List);

        Map<String, Object> nestedMap = (Map<String, Object>) resultMap.get("nestedObj");
        assertEquals("nestedValue", nestedMap.get("nestedKey"));

        List<Object> arrayList = (List<Object>) resultMap.get("array");
        assertEquals(3, arrayList.size());
        assertEquals(1, arrayList.get(0));
    }

    public void testParseJsonArrayToList() {
        String jsonString = "[{\"key1\":\"value1\"}, {\"key2\":123}, [\"a\", \"b\", \"c\"]]";
        List<Map<String, Object>> resultList = JsonUtils.parseJsonList(jsonString);

        assertTrue(resultList.get(0) instanceof Map);
        assertTrue(resultList.get(1) instanceof Map);
        assertTrue(resultList.get(2) instanceof List);

        Map<String, Object> map1 = (Map<String, Object>) resultList.get(0);
        assertEquals("value1", map1.get("key1"));

        Map<String, Object> map2 = (Map<String, Object>) resultList.get(1);
        assertEquals(123, map2.get("key2"));

        List<Object> innerList = (List<Object>) resultList.get(2);
        assertEquals("a", innerList.get(0));
        assertEquals("b", innerList.get(1));
        assertEquals("c", innerList.get(2));
    }
}
