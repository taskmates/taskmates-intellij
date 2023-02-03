package me.taskmates.lib.utils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.taskmates.clients.Chat;
import me.taskmates.clients.StringModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static final Type JSON_OBJECT_TYPE = new TypeToken<Map<String, Object>>() {
    }.getType();
    public static final Type JSON_LIST_TYPE = new TypeToken<List<Map<String, Object>>>() {
    }.getType();
    public static GsonBuilder GSON_BUILDER = new GsonBuilder()
        .registerTypeAdapter(JSON_OBJECT_TYPE, new MapDeserializer())
        .registerTypeAdapter(JSON_LIST_TYPE, new ListDeserializer())
        .registerTypeAdapter(StringModel.class, new StringModelTypeAdapter())
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .disableHtmlEscaping();

    public static final Gson GSON = GSON_BUILDER.create();

    public static String prettyPrint(Object json) {
        return GSON_BUILDER
            .setPrettyPrinting()
            .create().toJson(json);
    }

    @SuppressWarnings("unchecked")
    public static String dump(Object object) {
        if (object instanceof Map) {
            return dump((Map<String, Object>) object);
        } else if (object instanceof List) {
            return dump((List<Map<String, Object>>) object);
        } else if (object instanceof String) {
            return object.toString();
        } else {
            return GSON.toJson(object);
        }
    }

    public static String dump(Map<String, Object> json) {
        return GSON.toJson(json);
    }

    public static String dump(List<Map<String, Object>> json) {
        return GSON.toJson(json);
    }

    public static String escapeJson(String input) {
        if (input == null) {
            return null;
        }

        StringBuilder escaped = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '"':
                    escaped.append("\\\"");
                    break;
                case '\\':
                    escaped.append("\\\\");
                    break;
                case '\b':
                    escaped.append("\\b");
                    break;
                case '\f':
                    escaped.append("\\f");
                    break;
                case '\n':
                    escaped.append("\\n");
                    break;
                case '\r':
                    escaped.append("\\r");
                    break;
                case '\t':
                    escaped.append("\\t");
                    break;
                default:
                    if (c <= 0x1F) {
                        escaped.append(String.format("\\u%04x", (int) c));
                    } else {
                        escaped.append(c);
                    }
                    break;
            }
        }
        return escaped.toString();
    }

    public static Map<String, Object> parseJson(String string) {
        return GSON.fromJson(string, JSON_OBJECT_TYPE);
    }

    public static List<Map<String, Object>> parseJsonList(String string) {
        if (string.isEmpty()) {
            return new ArrayList<>();
        }

        return GSON.fromJson(string, JSON_LIST_TYPE);
    }

    public static Chat parseJsonToChat(String string) {
        try {
            return GSON.fromJson(string, Chat.class);

        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON:\n" + string, e);
        }
    }

    private static class ListDeserializer implements JsonDeserializer<List<Object>> {
        @Override
        public List<Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (!json.isJsonArray()) {
                throw new JsonParseException("Expected a JsonArray, but was " + json.getClass().getSimpleName());
            }

            List<Object> list = new ArrayList<>();
            JsonArray jsonArray = json.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    list.add(context.deserialize(element, JSON_OBJECT_TYPE));
                } else if (element.isJsonArray()) {
                    list.add(context.deserialize(element, JSON_LIST_TYPE));
                } else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
                    if (element.getAsNumber().toString().equals(Integer.toString(element.getAsInt()))) {
                        list.add(context.deserialize(element, Integer.class));
                    } else {
                        list.add(context.deserialize(element, Float.class));
                    }

                } else {
                    list.add(context.deserialize(element, Object.class));
                }
            }

            return list;
        }
    }

    private static class MapDeserializer implements JsonDeserializer<Map<String, Object>> {
        @Override
        public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                Map<String, Object> map = new LinkedHashMap<>();
                JsonObject jsonObject = json.getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                    JsonElement element = entry.getValue();
                    // Recursively deserialize the element based on its type
                    Object value = context.deserialize(element, getTypeForJsonElement(element));
                    map.put(entry.getKey(), value);
                }

                return map;
            } else if (json.isJsonPrimitive()) {
                // Handle the JsonPrimitive case here
                // Depending on your use case, you might want to return a singleton map with a special key,
                // or throw a different exception indicating the unexpected type.
                throw new JsonParseException("Expected a JsonObject, but was JsonPrimitive");
            } else {
                // Handle other unexpected types (JsonArray, JsonNull)
                throw new JsonParseException("Expected a JsonObject, but was " + json.getClass().getSimpleName());
            }
        }

        private Type getTypeForJsonElement(JsonElement element) {
            if (element.isJsonObject()) {
                return JSON_OBJECT_TYPE;
            } else if (element.isJsonArray()) {
                return JSON_LIST_TYPE;
            } else if (element.isJsonPrimitive()) {
                JsonPrimitive primitive = element.getAsJsonPrimitive();
                if (primitive.isNumber()) {
                    // Check if it's an integer or a floating-point number
                    if (primitive.getAsNumber().toString().equals(Integer.toString(primitive.getAsInt()))) {
                        return Integer.class;
                    } else {
                        return Float.class;
                    }
                } else if (primitive.isBoolean()) {
                    return Boolean.class;
                } else if (primitive.isString()) {
                    return String.class;
                }
            }
            // Fallback for other types or JsonNull
            return Object.class;
        }
    }

    private static class StringModelTypeAdapter extends TypeAdapter<StringModel> {
        @Override
        public void write(JsonWriter out, StringModel value) throws IOException {
            if (value == null) {
                out.nullValue();
                return;
            }
            out.value(value.getText());
        }

        @Override
        public StringModel read(JsonReader in) throws IOException {
            if (in.peek() == JsonToken.NULL) {
                in.nextNull(); // Consume the null token
                return null; // Return null or a new StringModel with a null or empty string
            }
            return new StringModel(in.nextString());
        }
    }
}
