package io;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigManager {

    private static final String CONFIG_FILE = "config.json";
    private final List<Long> seeds = new ArrayList<>();
    private int activeSeedIndex = -1;
    private String language = "en";
    private final Map<String, Integer> trackedIndices = new LinkedHashMap<>();

    public ConfigManager() {
        load();
    }


    public List<Long> getSeeds() {
        return Collections.unmodifiableList(seeds);
    }

    public int getActiveSeedIndex() {
        return activeSeedIndex;
    }


    public long getActiveSeed() {
        if (activeSeedIndex >= 0 && activeSeedIndex < seeds.size()) {
            return seeds.get(activeSeedIndex);
        }
        return 0;
    }

    public void setActiveSeedIndex(int index) {
        if (index >= 0 && index < seeds.size()) {
            this.activeSeedIndex = index;
            save();
        }
    }


    public int addSeed(long seed) {
        for (int i = 0; i < seeds.size(); i++) {
            if (seeds.get(i) == seed) {
                activeSeedIndex = i;
                save();
                return i;
            }
        }
        seeds.add(seed);
        activeSeedIndex = seeds.size() - 1;
        save();
        return activeSeedIndex;
    }


    public void removeSeed(int index) {
        if (index >= 0 && index < seeds.size()) {
            long removedSeed = seeds.remove(index);
            // Remove tracked entries for this seed
            trackedIndices.entrySet().removeIf(e -> e.getKey().startsWith(removedSeed + "/"));
            // Adjust active index
            if (seeds.isEmpty()) {
                activeSeedIndex = -1;
            } else if (activeSeedIndex >= seeds.size()) {
                activeSeedIndex = seeds.size() - 1;
            }
            save();
        }
    }



    @Deprecated
    public long getWorldSeed() {
        return getActiveSeed();
    }


    @Deprecated
    public void setWorldSeed(long seed) {
        addSeed(seed);
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        save();
    }

    public int getTrackedIndex(String key) {
        long seed = getActiveSeed();
        if (seed == 0) return -1;
        String fullKey = seed + "/" + key;
        return trackedIndices.getOrDefault(fullKey, -1);
    }

    public void setTrackedIndex(String key, int index) {
        long seed = getActiveSeed();
        if (seed == 0) return;
        String fullKey = seed + "/" + key;
        trackedIndices.put(fullKey, index);
        save();
    }

    public Map<String, Integer> getAllTracked() {
        return Collections.unmodifiableMap(trackedIndices);
    }


    public Map<String, Integer> getTrackedForActiveSeed() {
        long seed = getActiveSeed();
        if (seed == 0) return Collections.emptyMap();
        String prefix = seed + "/";
        Map<String, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : trackedIndices.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                result.put(entry.getKey().substring(prefix.length()), entry.getValue());
            }
        }
        return result;
    }


    private void load() {
        Path path = Paths.get(CONFIG_FILE);
        if (!Files.exists(path)) return;

        try {
            String content = new String(Files.readAllBytes(path));
            parseJson(content);
        } catch (Exception e) {
            System.err.println("Warning: Failed to load config.json: " + e.getMessage());
        }
    }

    public void save() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("{\n");

            // Seeds array
            sb.append("  \"seeds\": [");
            for (int i = 0; i < seeds.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(seeds.get(i));
            }
            sb.append("],\n");

            // Active seed index
            sb.append("  \"active_seed_index\": ").append(activeSeedIndex).append(",\n");

            // Language
            sb.append("  \"language\": \"").append(language).append("\",\n");

            // Tracked indices
            sb.append("  \"tracked\": {\n");
            int i = 0;
            for (Map.Entry<String, Integer> entry : trackedIndices.entrySet()) {
                sb.append("    \"").append(entry.getKey()).append("\": { \"index\": ").append(entry.getValue()).append(" }");
                if (i < trackedIndices.size() - 1) sb.append(",");
                sb.append("\n");
                i++;
            }
            sb.append("  }\n");
            sb.append("}\n");

            Files.write(Paths.get(CONFIG_FILE), sb.toString().getBytes());
        } catch (Exception e) {
            System.err.println("Warning: Failed to save config.json: " + e.getMessage());
        }
    }


    private void parseJson(String json) {
        json = json.trim();
        if (!json.startsWith("{")) return;

        // Check if this is old format (has "world_seed") or new format (has "seeds")
        if (json.contains("\"seeds\"")) {
            parseNewFormat(json);
        } else if (json.contains("\"world_seed\"")) {
            parseOldFormat(json);
        }
    }

    private void parseNewFormat(String json) {
        // Parse seeds array
        int seedsIdx = json.indexOf("\"seeds\"");
        if (seedsIdx >= 0) {
            int bracketStart = json.indexOf("[", seedsIdx);
            int bracketEnd = json.indexOf("]", bracketStart);
            if (bracketStart >= 0 && bracketEnd >= 0) {
                String seedsContent = json.substring(bracketStart + 1, bracketEnd).trim();
                if (!seedsContent.isEmpty()) {
                    String[] seedParts = seedsContent.split(",");
                    for (String part : seedParts) {
                        try {
                            seeds.add(Long.parseLong(part.trim()));
                        } catch (NumberFormatException e) {
                            // skip
                        }
                    }
                }
            }
        }

        // Parse active_seed_index
        int activeIdx = json.indexOf("\"active_seed_index\"");
        if (activeIdx >= 0) {
            int colonIdx = json.indexOf(":", activeIdx);
            int endIdx = indexOfAny(json, colonIdx + 1, ',', '}', '\n');
            if (colonIdx >= 0 && endIdx >= 0) {
                String indexStr = json.substring(colonIdx + 1, endIdx).trim();
                try {
                    activeSeedIndex = Integer.parseInt(indexStr);
                } catch (NumberFormatException e) {
                    activeSeedIndex = seeds.isEmpty() ? -1 : 0;
                }
            }
        }

        // Parse language
        int langIdx = json.indexOf("\"language\"");
        if (langIdx >= 0) {
            int colonIdx = json.indexOf(":", langIdx);
            if (colonIdx >= 0) {
                int quoteStart = json.indexOf("\"", colonIdx + 1);
                if (quoteStart >= 0) {
                    int quoteEnd = json.indexOf("\"", quoteStart + 1);
                    if (quoteEnd >= 0) {
                        language = json.substring(quoteStart + 1, quoteEnd);
                    }
                }
            }
        }

        // Parse tracked
        parseTrackedSection(json);
    }

    private void parseOldFormat(String json) {
        // Parse world_seed
        int seedIdx = json.indexOf("\"world_seed\"");
        if (seedIdx >= 0) {
            int colonIdx = json.indexOf(":", seedIdx);
            int endIdx = indexOfAny(json, colonIdx + 1, ',', '}');
            if (colonIdx >= 0 && endIdx >= 0) {
                String seedStr = json.substring(colonIdx + 1, endIdx).trim();
                try {
                    long seed = Long.parseLong(seedStr);
                    if (seed != 0) {
                        seeds.add(seed);
                        activeSeedIndex = 0;
                    }
                } catch (NumberFormatException e) {
                    // ignore
                }
            }
        }

        // Parse old tracked format (keys without seed prefix)
        int trackedIdx = json.indexOf("\"tracked\"");
        if (trackedIdx >= 0) {
            int braceStart = json.indexOf("{", trackedIdx + 9);
            if (braceStart >= 0) {
                int braceEnd = findMatchingBrace(json, braceStart);
                if (braceEnd >= 0) {
                    String trackedContent = json.substring(braceStart + 1, braceEnd);
                    // Parse old format and migrate keys to include seed prefix
                    long seed = getActiveSeed();
                    parseTrackedOldFormat(trackedContent, seed);
                }
            }
        }

        // Save in new format
        save();
    }

    private void parseTrackedSection(String json) {
        int trackedIdx = json.indexOf("\"tracked\"");
        if (trackedIdx >= 0) {
            int braceStart = json.indexOf("{", trackedIdx + 9);
            if (braceStart >= 0) {
                int braceEnd = findMatchingBrace(json, braceStart);
                if (braceEnd >= 0) {
                    String trackedContent = json.substring(braceStart + 1, braceEnd);
                    parseTrackedEntries(trackedContent);
                }
            }
        }
    }

    private void parseTrackedEntries(String content) {
        int pos = 0;
        while (pos < content.length()) {
            int keyStart = content.indexOf("\"", pos);
            if (keyStart < 0) break;
            int keyEnd = content.indexOf("\"", keyStart + 1);
            if (keyEnd < 0) break;
            String key = content.substring(keyStart + 1, keyEnd);

            int indexKeyPos = content.indexOf("\"index\"", keyEnd);
            if (indexKeyPos < 0) break;
            int colonPos = content.indexOf(":", indexKeyPos);
            if (colonPos < 0) break;

            int numStart = colonPos + 1;
            while (numStart < content.length() && (content.charAt(numStart) == ' ' || content.charAt(numStart) == '\t')) {
                numStart++;
            }
            int numEnd = numStart;
            while (numEnd < content.length() && (Character.isDigit(content.charAt(numEnd)) || content.charAt(numEnd) == '-')) {
                numEnd++;
            }
            if (numEnd > numStart) {
                try {
                    int index = Integer.parseInt(content.substring(numStart, numEnd));
                    trackedIndices.put(key, index);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            pos = numEnd;
        }
    }

    private void parseTrackedOldFormat(String content, long seed) {
        int pos = 0;
        while (pos < content.length()) {
            int keyStart = content.indexOf("\"", pos);
            if (keyStart < 0) break;
            int keyEnd = content.indexOf("\"", keyStart + 1);
            if (keyEnd < 0) break;
            String key = content.substring(keyStart + 1, keyEnd);

            int indexKeyPos = content.indexOf("\"index\"", keyEnd);
            if (indexKeyPos < 0) break;
            int colonPos = content.indexOf(":", indexKeyPos);
            if (colonPos < 0) break;

            int numStart = colonPos + 1;
            while (numStart < content.length() && (content.charAt(numStart) == ' ' || content.charAt(numStart) == '\t')) {
                numStart++;
            }
            int numEnd = numStart;
            while (numEnd < content.length() && (Character.isDigit(content.charAt(numEnd)) || content.charAt(numEnd) == '-')) {
                numEnd++;
            }
            if (numEnd > numStart) {
                try {
                    int index = Integer.parseInt(content.substring(numStart, numEnd));
                    // Add seed prefix for migration
                    String fullKey = (seed != 0) ? seed + "/" + key : key;
                    trackedIndices.put(fullKey, index);
                } catch (NumberFormatException e) {
                    // ignore
                }
            }

            pos = numEnd;
        }
    }

    private int findMatchingBrace(String json, int openPos) {
        int depth = 1;
        for (int i = openPos + 1; i < json.length(); i++) {
            if (json.charAt(i) == '{') depth++;
            else if (json.charAt(i) == '}') {
                depth--;
                if (depth == 0) return i;
            }
        }
        return -1;
    }

    private int indexOfAny(String str, int from, char... chars) {
        for (int i = from; i < str.length(); i++) {
            for (char c : chars) {
                if (str.charAt(i) == c) return i;
            }
        }
        return -1;
    }
}
