package gamedata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import enchant.EnchantDef;
import trade.Cost;
import trade.Modifier;
import trade.TradeEntry;
import trade.TradePool;
import util.Resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GameData {

    private static final String DIR = "versions";
    private static GameData current;

    public final String version;
    public final String dataId;
    public final List<String> villagerTypes;
    public final List<String> dyeColors;
    public final List<EnchantDef> enchantments;
    private final Map<String, Integer> enchantIndex = new HashMap<>();
    private final Map<String, Integer> tooltipOrder = new HashMap<>();
    private final Map<String, TradePool> pools = new LinkedHashMap<>();
    private final List<String> professions = new ArrayList<>();

    private GameData(String version, String dataId, JsonObject root) {
        this.version = version;
        this.dataId = dataId;
        this.villagerTypes = strings(root, "villager_types");
        this.dyeColors = strings(root, "dye_colors");
        List<EnchantDef> enchants = new ArrayList<>();
        for (JsonElement element : root.getAsJsonArray("enchantments")) {
            JsonObject e = element.getAsJsonObject();
            String name = e.get("id").getAsString();
            enchantIndex.put(name, enchants.size());
            enchants.add(new EnchantDef(enchants.size(), name, e.get("max_level").getAsInt(), e.get("weight").getAsInt(),
                e.getAsJsonArray("min_cost").get(0).getAsInt(), e.getAsJsonArray("min_cost").get(1).getAsInt(),
                e.getAsJsonArray("max_cost").get(0).getAsInt(), e.getAsJsonArray("max_cost").get(1).getAsInt(),
                new LinkedHashSet<>(strings(e, "exclusive"))));
        }
        this.enchantments = Collections.unmodifiableList(enchants);
        List<String> order = strings(root, "tooltip_order");
        for (int i = 0; i < order.size(); i++) tooltipOrder.put(order.get(i), i);
        for (JsonElement element : root.getAsJsonArray("pools")) {
            TradePool pool = parsePool(element.getAsJsonObject());
            pools.put(pool.profession + "/" + pool.level, pool);
            if (!professions.contains(pool.profession)) professions.add(pool.profession);
        }
    }

    private TradePool parsePool(JsonObject json) {
        List<TradeEntry> entries = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray("trades")) {
            JsonObject t = element.getAsJsonObject();
            JsonObject gives = t.getAsJsonObject("gives");
            entries.add(new TradeEntry(
                entries.size(),
                t.get("id").getAsString(),
                Cost.parse(t.getAsJsonObject("wants")),
                t.has("additional_wants") ? Cost.parse(t.getAsJsonObject("additional_wants")) : null,
                gives.get("item").getAsString(),
                gives.get("count").getAsInt(),
                t.has("villager_types") ? new LinkedHashSet<>(strings(t, "villager_types")) : null,
                t.has("modifier") ? Modifier.parse(t.getAsJsonObject("modifier"), this::indexOfEnchant) : null,
                t.has("discard_unless") ? new LinkedHashSet<>(strings(t, "discard_unless")) : null
            ));
        }
        return new TradePool(json.get("profession").getAsString(), json.get("level").getAsInt(),
            json.get("random_sequence").getAsString(), json.get("amount").getAsInt(), Collections.unmodifiableList(entries));
    }

    public int indexOfEnchant(String name) {
        Integer index = enchantIndex.get(name);
        return index == null ? -1 : index;
    }

    public static List<String> supportedVersions() {
        List<String> versions = new ArrayList<>();
        for (JsonElement element : readIndex().getAsJsonArray("versions")) {
            versions.add(element.getAsJsonObject().get("version").getAsString());
        }
        return versions;
    }

    public static boolean isSupported(String version) {
        return version != null && supportedVersions().contains(version);
    }

    public static GameData load(String version) {
        for (JsonElement element : readIndex().getAsJsonArray("versions")) {
            JsonObject entry = element.getAsJsonObject();
            if (entry.get("version").getAsString().equals(version)) {
                String dataId = entry.get("data").getAsString();
                return new GameData(version, dataId, read(DIR + "/" + dataId + ".json"));
            }
        }
        throw new IllegalArgumentException("Unsupported game version: " + version);
    }

    public static void select(String version) {
        current = load(version);
    }

    public static GameData get() {
        if (current == null) throw new IllegalStateException("No game version selected");
        return current;
    }

    public TradePool getPool(String profession, int level) {
        return pools.get(profession + "/" + level);
    }

    public List<String> getProfessions() {
        return Collections.unmodifiableList(professions);
    }

    public EnchantDef enchant(int index) {
        return enchantments.get(index);
    }

    public int tooltipRank(String enchantment) {
        return tooltipOrder.getOrDefault(enchantment, Integer.MAX_VALUE);
    }

    private static JsonObject readIndex() {
        return read(DIR + "/index.json");
    }

    private static JsonObject read(String path) {
        try {
            return JsonParser.parseString(Resources.readText(path)).getAsJsonObject();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read " + path + ": " + e.getMessage(), e);
        }
    }

    private static List<String> strings(JsonObject json, String key) {
        List<String> values = new ArrayList<>();
        for (JsonElement element : json.getAsJsonArray(key)) values.add(element.getAsString());
        return values;
    }
}
