package util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class I18n {

    private static final String LANG_DIR = "lang";
    private static Map<String, String> strings = new HashMap<>();
    private static String currentLanguage = "en";

    public static void init(String language) {
        currentLanguage = language;
        strings = new HashMap<>();
        try {
            JsonObject root = JsonParser.parseString(Resources.readText(LANG_DIR + "/" + language + ".json")).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                strings.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to load language file " + language + ": " + e.getMessage());
        }
    }

    public static String get(String key) {
        return strings.getOrDefault(key, key);
    }

    public static String get(String key, Object... args) {
        String result = strings.getOrDefault(key, key);
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return result;
    }

    public static boolean has(String key) {
        return strings.containsKey(key);
    }

    public static String getLanguage() {
        return currentLanguage;
    }

    public static String translateId(String prefix, String id) {
        String result = strings.get(prefix + "." + id);
        return result != null ? result : id;
    }

    public static String translateWithOriginal(String prefix, String id) {
        String result = strings.get(prefix + "." + id);
        if (result != null && !result.equals(id)) {
            return result + " (" + id + ")";
        }
        return id;
    }

    public static List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        for (String file : Resources.listFiles(LANG_DIR, ".json")) {
            languages.add(file.substring(0, file.length() - ".json".length()));
        }
        return languages;
    }
}
