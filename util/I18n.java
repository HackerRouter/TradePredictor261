package util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class I18n {

    private static final String LANG_DIR = "lang";
    private static Map<String, String> strings = new HashMap<>();
    private static String currentLanguage = "en";

    public static void init(String language) {
        currentLanguage = language;
        strings = new HashMap<>();
        Path langFile = Paths.get(LANG_DIR, language + ".json");
        if (!Files.exists(langFile)) {
            System.err.println("Warning: Language file not found: " + langFile);
            return;
        }
        try {
            byte[] bytes = Files.readAllBytes(langFile);
            String content = new String(bytes, StandardCharsets.UTF_8);
            // Strip UTF-8 BOM if present
            if (content.startsWith("\uFEFF")) {
                content = content.substring(1);
            }
            strings = parseJson(content);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load language file: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return strings.getOrDefault(key, key);
    }

    public static String get(String key, Object... args) {
        String template = strings.getOrDefault(key, key);
        String result = template;
        for (int i = 0; i < args.length; i++) {
            result = result.replace("{" + i + "}", String.valueOf(args[i]));
        }
        return result;
    }

    public static String getLanguage() {
        return currentLanguage;
    }

    public static String translateId(String prefix, String id) {
        String key = prefix + "." + id;
        String result = strings.get(key);
        return result != null ? result : id;
    }

    public static String translateWithOriginal(String prefix, String id) {
        String key = prefix + "." + id;
        String result = strings.get(key);
        if (result != null && !result.equals(id)) {
            return result + " (" + id + ")";
        }
        return id;
    }

    public static List<String> getAvailableLanguages() {
        List<String> languages = new ArrayList<>();
        Path langDir = Paths.get(LANG_DIR);
        if (!Files.exists(langDir)) return languages;
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(langDir, "*.json");
            for (Path entry : stream) {
                String filename = entry.getFileName().toString();
                languages.add(filename.substring(0, filename.length() - 5)); // remove .json
            }
            stream.close();
        } catch (Exception e) {
            // ignore
        }
        Collections.sort(languages);
        return languages;
    }

    private static Map<String, String> parseJson(String json) {
        Map<String, String> map = new HashMap<>();
        json = json.trim();
        if (!json.startsWith("{") || !json.endsWith("}")) return map;

        // Remove outer braces
        json = json.substring(1, json.length() - 1);

        int pos = 0;
        while (pos < json.length()) {
            // Find key start (opening quote)
            int keyStart = json.indexOf('"', pos);
            if (keyStart < 0) break;

            // Find key end (closing quote, not escaped)
            int keyEnd = findClosingQuote(json, keyStart + 1);
            if (keyEnd < 0) break;

            String key = unescapeString(json.substring(keyStart + 1, keyEnd));

            // Find colon
            int colonIdx = json.indexOf(':', keyEnd + 1);
            if (colonIdx < 0) break;

            // Find value start (opening quote)
            int valStart = json.indexOf('"', colonIdx + 1);
            if (valStart < 0) break;

            // Find value end (closing quote, not escaped)
            int valEnd = findClosingQuote(json, valStart + 1);
            if (valEnd < 0) break;

            String value = unescapeString(json.substring(valStart + 1, valEnd));

            map.put(key, value);
            pos = valEnd + 1;
        }

        return map;
    }

    private static int findClosingQuote(String str, int from) {
        for (int i = from; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '\\') {
                i++; // skip escaped character
            } else if (c == '"') {
                return i;
            }
        }
        return -1;
    }

    private static String unescapeString(String s) {
        StringBuilder sb = new StringBuilder(s.length());
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < s.length()) {
                char next = s.charAt(i + 1);
                switch (next) {
                    case '"': sb.append('"'); i++; break;
                    case '\\': sb.append('\\'); i++; break;
                    case 'n': sb.append('\n'); i++; break;
                    case 't': sb.append('\t'); i++; break;
                    case 'r': sb.append('\r'); i++; break;
                    default: sb.append(c); break;
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
