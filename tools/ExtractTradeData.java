import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExtractTradeData {

    private static final List<String> PROFESSIONS = List.of(
        "farmer", "fisherman", "shepherd", "fletcher", "librarian", "cartographer",
        "cleric", "armorer", "weaponsmith", "toolsmith", "butcher", "leatherworker", "mason"
    );

    private static final List<String> VILLAGER_TYPES = List.of("desert", "jungle", "plains", "savanna", "snow", "swamp", "taiga");

    private static final List<String> DYE_COLORS = List.of(
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
        "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );

    private static final Set<String> INSTANT_EFFECTS = Set.of("instant_health", "instant_damage", "saturation");

    private static final Map<String, Integer> ENCHANTABILITY = Map.ofEntries(
        Map.entry("iron_sword", 14), Map.entry("iron_axe", 14), Map.entry("iron_pickaxe", 14),
        Map.entry("iron_shovel", 14), Map.entry("iron_hoe", 14),
        Map.entry("diamond_sword", 10), Map.entry("diamond_axe", 10), Map.entry("diamond_pickaxe", 10),
        Map.entry("diamond_shovel", 10), Map.entry("diamond_hoe", 10),
        Map.entry("iron_helmet", 9), Map.entry("iron_chestplate", 9), Map.entry("iron_leggings", 9), Map.entry("iron_boots", 9),
        Map.entry("chainmail_helmet", 12), Map.entry("chainmail_chestplate", 12),
        Map.entry("chainmail_leggings", 12), Map.entry("chainmail_boots", 12),
        Map.entry("diamond_helmet", 10), Map.entry("diamond_chestplate", 10),
        Map.entry("diamond_leggings", 10), Map.entry("diamond_boots", 10),
        Map.entry("bow", 1), Map.entry("crossbow", 1), Map.entry("fishing_rod", 1)
    );

    private static final Map<String, Integer> MAX_STACK = Map.ofEntries(
        Map.entry("writable_book", 1), Map.entry("lava_bucket", 1), Map.entry("water_bucket", 1), Map.entry("milk_bucket", 1),
        Map.entry("oak_boat", 1), Map.entry("spruce_boat", 1), Map.entry("birch_boat", 1), Map.entry("jungle_boat", 1),
        Map.entry("acacia_boat", 1), Map.entry("dark_oak_boat", 1), Map.entry("mangrove_boat", 1),
        Map.entry("cherry_boat", 1), Map.entry("pale_oak_boat", 1), Map.entry("bamboo_raft", 1)
    );

    private final Path data;
    private final Set<String> bookEnchantments = new LinkedHashSet<>();
    private final Set<String> equipmentEnchantments = new LinkedHashSet<>();

    private ExtractTradeData(Path resourcesRoot) {
        this.data = resourcesRoot.resolve("data/minecraft");
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java -cp lib/gson-2.13.1.jar tools/ExtractTradeData.java <resources-root> <group> <output-file>");
            System.exit(1);
        }
        JsonObject out = new ExtractTradeData(Paths.get(args[0])).extract(args[1]);
        Files.writeString(Paths.get(args[2]), new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(out) + "\n");
        System.out.println("Wrote " + args[2]);
    }

    private JsonObject extract(String group) throws IOException {
        JsonArray pools = new JsonArray();
        for (String profession : PROFESSIONS) {
            for (int level = 1; level <= 5; level++) {
                Path file = data.resolve("trade_set/" + profession + "/level_" + level + ".json");
                if (Files.exists(file)) {
                    pools.add(extractPool(profession, level, read(file)));
                }
            }
        }
        JsonObject out = new JsonObject();
        out.addProperty("group", group);
        out.add("villager_types", strings(VILLAGER_TYPES));
        out.add("dye_colors", strings(DYE_COLORS));
        out.add("enchantments", extractEnchantments());
        out.add("tooltip_order", strings(stripNamespace(resolveTag("enchantment", "minecraft:tooltip_order"))));
        out.add("pools", pools);
        return out;
    }

    private JsonObject extractPool(String profession, int level, JsonObject tradeSet) throws IOException {
        if (tradeSet.has("allow_duplicates") && tradeSet.get("allow_duplicates").getAsBoolean()) {
            throw fail(profession + "/" + level + ": allow_duplicates is not supported");
        }
        JsonObject pool = new JsonObject();
        pool.addProperty("profession", profession);
        pool.addProperty("level", level);
        pool.addProperty("amount", constantInt(tradeSet.get("amount"), "amount"));
        pool.addProperty("random_sequence", tradeSet.get("random_sequence").getAsString());
        JsonArray trades = new JsonArray();
        for (String id : resolveHolderSet("villager_trade", tradeSet.get("trades"))) {
            trades.add(extractTrade(id, read(data.resolve("villager_trade/" + path(id) + ".json"))));
        }
        pool.add("trades", trades);
        return pool;
    }

    private JsonObject extractTrade(String id, JsonObject trade) throws IOException {
        normalize(trade);
        JsonObject out = new JsonObject();
        out.addProperty("id", path(id));
        String gives = path(trade.getAsJsonObject("gives").get("id").getAsString());
        out.add("wants", cost(trade.getAsJsonObject("wants"), id));
        if (trade.has("additional_wants")) out.add("additional_wants", cost(trade.getAsJsonObject("additional_wants"), id));
        JsonObject givesOut = new JsonObject();
        givesOut.addProperty("item", gives);
        givesOut.addProperty("count", trade.getAsJsonObject("gives").has("count") ? trade.getAsJsonObject("gives").get("count").getAsInt() : 1);
        out.add("gives", givesOut);
        if (trade.has("merchant_predicate")) out.add("villager_types", villagerTypes(trade.get("merchant_predicate"), id));
        if (trade.has("given_item_modifier")) {
            JsonElement modifiers = trade.get("given_item_modifier");
            JsonArray list = modifiers.isJsonArray() ? modifiers.getAsJsonArray() : single(modifiers);
            extractModifiers(list, gives, trade, out, id);
        }
        return out;
    }

    private void extractModifiers(JsonArray list, String gives, JsonObject trade, JsonObject out, String id) throws IOException {
        JsonObject modifier = null;
        JsonArray filters = new JsonArray();
        for (JsonElement element : list) {
            JsonObject function = element.getAsJsonObject();
            if (function.has("conditions")) throw fail(id + ": conditional item functions are not supported");
            String type = path(function.get("type").getAsString());
            switch (type) {
                case "set_name":
                    break;
                case "filtered":
                    filters.add(filter(function, gives, id));
                    break;
                default:
                    if (modifier != null) throw fail(id + ": more than one random item function");
                    modifier = modifier(type, function, gives, trade, id);
            }
        }
        if (modifier != null) out.add("modifier", modifier);
        if (filters.size() > 0) out.add("discard_unless", filters);
    }

    private JsonObject modifier(String type, JsonObject function, String gives, JsonObject trade, String id) throws IOException {
        JsonObject out = new JsonObject();
        out.addProperty("type", type);
        switch (type) {
            case "enchant_randomly": {
                boolean onlyCompatible = !function.has("only_compatible") || function.get("only_compatible").getAsBoolean();
                if (onlyCompatible && !gives.equals("book")) throw fail(id + ": only_compatible enchant_randomly is not supported");
                List<String> options = stripNamespace(resolveHolderSet("enchantment", function.get("options")));
                bookEnchantments.addAll(options);
                out.add("options", strings(options));
                out.addProperty("additional_cost", flag(function, "include_additional_cost_component"));
                List<String> doublePrice = trade.has("double_trade_price_enchantments")
                    ? stripNamespace(resolveHolderSet("enchantment", trade.get("double_trade_price_enchantments")))
                    : List.of();
                out.add("double_price", strings(doublePrice));
                break;
            }
            case "enchant_with_levels": {
                Integer enchantability = ENCHANTABILITY.get(gives);
                if (enchantability == null) throw fail(id + ": unknown enchantability for " + gives);
                List<String> options = new ArrayList<>();
                for (String enchantment : stripNamespace(resolveHolderSet("enchantment", function.get("options")))) {
                    if (isPrimaryItem(enchantment, gives)) options.add(enchantment);
                }
                equipmentEnchantments.addAll(options);
                out.add("levels", provider(function.get("levels"), id));
                out.add("options", strings(options));
                out.addProperty("enchantability", enchantability);
                out.addProperty("additional_cost", flag(function, "include_additional_cost_component"));
                break;
            }
            case "set_random_dyes":
                out.add("count", provider(function.get("number_of_dyes"), id));
                break;
            case "set_stew_effect": {
                JsonArray effects = new JsonArray();
                for (JsonElement element : function.getAsJsonArray("effects")) {
                    JsonObject effect = element.getAsJsonObject();
                    String effectId = path(effect.get("type").getAsString());
                    int duration = constantInt(effect.get("duration"), id + " duration");
                    JsonObject effectOut = new JsonObject();
                    effectOut.addProperty("effect", effectId);
                    effectOut.addProperty("ticks", INSTANT_EFFECTS.contains(effectId) ? duration : duration * 20);
                    effects.add(effectOut);
                }
                out.add("effects", effects);
                break;
            }
            case "set_random_potion":
                if (!function.has("options")) throw fail(id + ": set_random_potion without options is not supported");
                out.add("options", strings(stripNamespace(resolveHolderSet("potion", function.get("options")))));
                break;
            case "exploration_map":
                out.addProperty("destination", path(function.get("destination").getAsString().replace("#", "")));
                break;
            default:
                throw fail(id + ": unsupported item function " + type);
        }
        return out;
    }

    private String filter(JsonObject function, String gives, String id) {
        if (!path(function.getAsJsonObject("on_fail").get("type").getAsString()).equals("discard")) {
            throw fail(id + ": filtered without discard is not supported");
        }
        JsonObject itemFilter = function.getAsJsonObject("item_filter");
        if (itemFilter.has("items")) {
            String items = path(itemFilter.get("items").getAsString());
            if (!items.equals(gives) && !(gives.equals("map") && items.equals("filled_map")) && !(gives.equals("book") && items.equals("enchanted_book"))) {
                throw fail(id + ": filter item " + items + " does not match " + gives);
            }
        }
        JsonObject predicates = itemFilter.getAsJsonObject("minecraft:predicates");
        if (predicates == null || predicates.size() != 1) throw fail(id + ": unsupported item filter");
        String key = path(predicates.keySet().iterator().next());
        if (!Set.of("enchantments", "stored_enchantments", "dyed_color", "map_id").contains(key)) {
            throw fail(id + ": unsupported item filter " + key);
        }
        return key;
    }

    private JsonArray villagerTypes(JsonElement predicate, String id) {
        JsonObject condition = predicate.getAsJsonObject();
        if (!path(condition.get("type").getAsString()).equals("entity_properties") || !condition.get("entity").getAsString().equals("this")) {
            throw fail(id + ": unsupported merchant predicate");
        }
        JsonObject predicates = condition.getAsJsonObject("predicate").getAsJsonObject("minecraft:predicates");
        if (predicates == null || predicates.size() != 1 || !predicates.has("minecraft:villager/variant")) {
            throw fail(id + ": unsupported merchant predicate");
        }
        JsonElement variants = predicates.get("minecraft:villager/variant");
        List<String> types = new ArrayList<>();
        for (JsonElement variant : variants.isJsonArray() ? variants.getAsJsonArray() : single(variants)) {
            String type = path(variant.getAsString());
            if (!VILLAGER_TYPES.contains(type)) throw fail(id + ": unknown villager type " + type);
            types.add(type);
        }
        return strings(types);
    }

    private JsonObject cost(JsonObject cost, String id) {
        if (cost.has("components")) throw fail(id + ": cost components are not supported");
        String item = path(cost.get("id").getAsString());
        JsonObject out = new JsonObject();
        out.addProperty("item", item);
        out.addProperty("count", cost.has("count") ? constantInt(cost.get("count"), id + " count") : 1);
        out.addProperty("max_stack", MAX_STACK.getOrDefault(item, 64));
        return out;
    }

    private JsonObject provider(JsonElement element, String id) {
        JsonObject out = new JsonObject();
        if (element.isJsonPrimitive()) {
            out.addProperty("type", "constant");
            out.addProperty("value", exactInt(element.getAsDouble(), id));
            return out;
        }
        JsonObject object = element.getAsJsonObject();
        String type = path(object.get("type").getAsString());
        switch (type) {
            case "constant":
                out.addProperty("type", "constant");
                out.addProperty("value", exactInt(object.get("value").getAsDouble(), id));
                break;
            case "uniform":
                out.addProperty("type", "uniform");
                out.addProperty("min", constantInt(object.get("min"), id));
                out.addProperty("max", constantInt(object.get("max"), id));
                break;
            case "binomial":
                out.addProperty("type", "binomial");
                out.addProperty("n", constantInt(object.get("n"), id));
                out.addProperty("p", object.get("p").getAsFloat());
                break;
            case "sum":
            case "add": {
                JsonArray values = new JsonArray();
                for (JsonElement value : object.getAsJsonArray(type.equals("sum") ? "summands" : "inputs")) {
                    values.add(provider(value, id));
                }
                out.addProperty("type", "sum");
                out.add("values", values);
                break;
            }
            default:
                throw fail(id + ": unsupported number provider " + type);
        }
        return out;
    }

    private int constantInt(JsonElement element, String what) {
        if (element.isJsonPrimitive()) return exactInt(element.getAsDouble(), what);
        JsonObject object = element.getAsJsonObject();
        if (path(object.get("type").getAsString()).equals("constant")) return exactInt(object.get("value").getAsDouble(), what);
        throw fail(what + ": expected a constant");
    }

    private int exactInt(double value, String what) {
        if (value != Math.rint(value)) throw fail(what + ": expected an integer, got " + value);
        return (int) value;
    }

    private boolean flag(JsonObject object, String key) {
        return object.has(key) && object.get(key).getAsBoolean();
    }

    private JsonArray extractEnchantments() throws IOException {
        Set<String> order = new LinkedHashSet<>(bookEnchantments);
        order.addAll(equipmentEnchantments);
        JsonArray out = new JsonArray();
        for (String id : order) {
            JsonObject definition = enchantment(id);
            JsonObject entry = new JsonObject();
            entry.addProperty("id", id);
            entry.addProperty("max_level", definition.get("max_level").getAsInt());
            entry.addProperty("weight", definition.get("weight").getAsInt());
            entry.add("min_cost", costFormula(definition.getAsJsonObject("min_cost")));
            entry.add("max_cost", costFormula(definition.getAsJsonObject("max_cost")));
            List<String> exclusive = definition.has("exclusive_set")
                ? stripNamespace(resolveHolderSet("enchantment", definition.get("exclusive_set")))
                : List.of();
            entry.add("exclusive", strings(exclusive));
            out.add(entry);
        }
        return out;
    }

    private JsonArray costFormula(JsonObject cost) {
        JsonArray out = new JsonArray();
        out.add(cost.get("base").getAsInt());
        out.add(cost.get("per_level_above_first").getAsInt());
        return out;
    }

    private boolean isPrimaryItem(String enchantment, String item) throws IOException {
        JsonObject definition = enchantment(enchantment);
        Set<String> supported = new LinkedHashSet<>(stripNamespace(resolveHolderSet("item", definition.get("supported_items"))));
        if (!supported.contains(item)) return false;
        if (!definition.has("primary_items")) return true;
        return stripNamespace(resolveHolderSet("item", definition.get("primary_items"))).contains(item);
    }

    private JsonObject enchantment(String id) throws IOException {
        return read(data.resolve("enchantment/" + id + ".json"));
    }

    private List<String> resolveHolderSet(String registry, JsonElement element) throws IOException {
        if (element.isJsonArray()) {
            List<String> out = new ArrayList<>();
            for (JsonElement value : element.getAsJsonArray()) out.add(value.getAsString());
            return out;
        }
        String value = element.getAsString();
        return value.startsWith("#") ? resolveTag(registry, value.substring(1)) : List.of(value);
    }

    private List<String> resolveTag(String registry, String tag) throws IOException {
        Set<String> out = new LinkedHashSet<>();
        JsonObject file = read(data.resolve("tags/" + registry + "/" + path(tag) + ".json"));
        for (JsonElement element : file.getAsJsonArray("values")) {
            String value = element.isJsonObject() ? element.getAsJsonObject().get("id").getAsString() : element.getAsString();
            if (value.startsWith("#")) {
                out.addAll(resolveTag(registry, value.substring(1)));
            } else {
                out.add(value.contains(":") ? value : "minecraft:" + value);
            }
        }
        return new ArrayList<>(out);
    }

    private void normalize(JsonObject object) {
        rename(object, "given_item_modifiers", "given_item_modifier");
        rename(object, "function", "type");
        rename(object, "condition", "type");
        rename(object, "predicates", "minecraft:predicates");
        for (Map.Entry<String, JsonElement> entry : new ArrayList<>(object.entrySet())) {
            normalizeValue(entry.getValue());
        }
    }

    private void normalizeValue(JsonElement element) {
        if (element.isJsonObject()) {
            normalize(element.getAsJsonObject());
        } else if (element.isJsonArray()) {
            for (JsonElement child : element.getAsJsonArray()) normalizeValue(child);
        }
    }

    private void rename(JsonObject object, String from, String to) {
        if (object.has(from) && !object.has(to)) {
            Map<String, JsonElement> ordered = new LinkedHashMap<>();
            for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                ordered.put(entry.getKey().equals(from) ? to : entry.getKey(), entry.getValue());
            }
            for (String key : new ArrayList<>(object.keySet())) object.remove(key);
            ordered.forEach(object::add);
        }
    }

    private JsonObject read(Path file) throws IOException {
        if (!Files.exists(file)) throw fail("missing file " + file);
        return JsonParser.parseString(Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
    }

    private static JsonArray single(JsonElement element) {
        JsonArray array = new JsonArray();
        array.add(element);
        return array;
    }

    private static JsonArray strings(List<String> values) {
        JsonArray array = new JsonArray();
        values.forEach(value -> array.add(new JsonPrimitive(value)));
        return array;
    }

    private static List<String> stripNamespace(List<String> ids) {
        List<String> out = new ArrayList<>();
        for (String id : ids) out.add(path(id));
        return out;
    }

    private static String path(String id) {
        int colon = id.indexOf(':');
        return colon >= 0 ? id.substring(colon + 1) : id;
    }

    private static IllegalStateException fail(String message) {
        return new IllegalStateException(message);
    }
}
