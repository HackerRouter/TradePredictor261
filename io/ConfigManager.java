package io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private static final Path CONFIG_FILE = Paths.get("config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final List<Long> seeds = new ArrayList<>();
    private int activeSeedIndex = -1;
    private String language = "en";
    private String gameVersion = null;
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
        int existing = seeds.indexOf(seed);
        if (existing >= 0) {
            activeSeedIndex = existing;
        } else {
            seeds.add(seed);
            activeSeedIndex = seeds.size() - 1;
        }
        save();
        return activeSeedIndex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
        save();
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
        save();
    }

    public int getTrackedIndex(String key) {
        long seed = getActiveSeed();
        if (seed == 0) return -1;
        return trackedIndices.getOrDefault(seed + "/" + key, -1);
    }

    public void setTrackedIndex(String key, int index) {
        long seed = getActiveSeed();
        if (seed == 0) return;
        trackedIndices.put(seed + "/" + key, index);
        save();
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
        if (!Files.exists(CONFIG_FILE)) return;
        try {
            JsonObject root = JsonParser.parseString(new String(Files.readAllBytes(CONFIG_FILE), StandardCharsets.UTF_8)).getAsJsonObject();
            if (root.has("seeds")) {
                loadCurrentFormat(root);
            } else if (root.has("world_seed")) {
                loadLegacyFormat(root);
                save();
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to load config.json: " + e.getMessage());
        }
    }

    private void loadCurrentFormat(JsonObject root) {
        for (JsonElement seed : root.getAsJsonArray("seeds")) {
            seeds.add(seed.getAsLong());
        }
        activeSeedIndex = root.has("active_seed_index") ? root.get("active_seed_index").getAsInt() : (seeds.isEmpty() ? -1 : 0);
        if (activeSeedIndex >= seeds.size()) activeSeedIndex = seeds.isEmpty() ? -1 : 0;
        if (root.has("language")) language = root.get("language").getAsString();
        if (root.has("game_version")) gameVersion = root.get("game_version").getAsString();
        readTracked(root, "");
    }

    private void loadLegacyFormat(JsonObject root) {
        long seed = root.get("world_seed").getAsLong();
        if (seed != 0) {
            seeds.add(seed);
            activeSeedIndex = 0;
        }
        readTracked(root, seed != 0 ? seed + "/" : "");
    }

    private void readTracked(JsonObject root, String keyPrefix) {
        if (!root.has("tracked")) return;
        for (Map.Entry<String, JsonElement> entry : root.getAsJsonObject("tracked").entrySet()) {
            JsonElement value = entry.getValue();
            int index = value.isJsonObject() ? value.getAsJsonObject().get("index").getAsInt() : value.getAsInt();
            trackedIndices.put(keyPrefix + entry.getKey(), index);
        }
    }

    public void save() {
        JsonObject root = new JsonObject();
        JsonArray seedArray = new JsonArray();
        seeds.forEach(seedArray::add);
        root.add("seeds", seedArray);
        root.addProperty("active_seed_index", activeSeedIndex);
        root.addProperty("language", language);
        if (gameVersion != null) root.addProperty("game_version", gameVersion);
        JsonObject tracked = new JsonObject();
        for (Map.Entry<String, Integer> entry : trackedIndices.entrySet()) {
            JsonObject value = new JsonObject();
            value.addProperty("index", entry.getValue());
            tracked.add(entry.getKey(), value);
        }
        root.add("tracked", tracked);
        try {
            Files.write(CONFIG_FILE, (GSON.toJson(root) + "\n").getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            System.err.println("Warning: Failed to save config.json: " + e.getMessage());
        }
    }
}
