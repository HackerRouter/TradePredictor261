package trade;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

public class Modifier {

    public static class StewEffect {
        public final String effect;
        public final int ticks;

        public StewEffect(String effect, int ticks) {
            this.effect = effect;
            this.ticks = ticks;
        }
    }

    public final TradeEntry.TradeType type;
    public final List<Integer> enchantOptions;
    public final Set<Integer> doublePriceEnchants;
    public final boolean additionalCost;
    public final IntProvider levels;
    public final int enchantability;
    public final IntProvider dyeCount;
    public final List<StewEffect> stewEffects;
    public final List<String> potionOptions;
    public final String destination;

    private Modifier(TradeEntry.TradeType type, List<Integer> enchantOptions, Set<Integer> doublePriceEnchants,
                     boolean additionalCost, IntProvider levels, int enchantability, IntProvider dyeCount,
                     List<StewEffect> stewEffects, List<String> potionOptions, String destination) {
        this.type = type;
        this.enchantOptions = enchantOptions;
        this.doublePriceEnchants = doublePriceEnchants;
        this.additionalCost = additionalCost;
        this.levels = levels;
        this.enchantability = enchantability;
        this.dyeCount = dyeCount;
        this.stewEffects = stewEffects;
        this.potionOptions = potionOptions;
        this.destination = destination;
    }

    public static Modifier parse(JsonObject json, ToIntFunction<String> enchantIndex) {
        String type = json.get("type").getAsString();
        List<Integer> enchantOptions = Collections.emptyList();
        Set<Integer> doublePrice = Collections.emptySet();
        if (json.has("options") && (type.equals("enchant_randomly") || type.equals("enchant_with_levels"))) {
            enchantOptions = new ArrayList<>();
            for (JsonElement option : json.getAsJsonArray("options")) {
                enchantOptions.add(enchantIndex.applyAsInt(option.getAsString()));
            }
        }
        if (json.has("double_price")) {
            doublePrice = new HashSet<>();
            for (JsonElement enchant : json.getAsJsonArray("double_price")) {
                int index = enchantIndex.applyAsInt(enchant.getAsString());
                if (index >= 0) doublePrice.add(index);
            }
        }
        boolean additionalCost = json.has("additional_cost") && json.get("additional_cost").getAsBoolean();
        switch (type) {
            case "enchant_randomly":
                return new Modifier(TradeEntry.TradeType.ENCHANT_RANDOMLY, enchantOptions, doublePrice, additionalCost,
                    null, 0, null, null, null, null);
            case "enchant_with_levels":
                return new Modifier(TradeEntry.TradeType.ENCHANT_WITH_LEVELS, enchantOptions, doublePrice, additionalCost,
                    IntProvider.parse(json.getAsJsonObject("levels")), json.get("enchantability").getAsInt(), null, null, null, null);
            case "set_random_dyes":
                return new Modifier(TradeEntry.TradeType.SET_RANDOM_DYES, enchantOptions, doublePrice, false,
                    null, 0, IntProvider.parse(json.getAsJsonObject("count")), null, null, null);
            case "set_stew_effect": {
                List<StewEffect> effects = new ArrayList<>();
                for (JsonElement element : json.getAsJsonArray("effects")) {
                    JsonObject effect = element.getAsJsonObject();
                    effects.add(new StewEffect(effect.get("effect").getAsString(), effect.get("ticks").getAsInt()));
                }
                return new Modifier(TradeEntry.TradeType.SET_STEW_EFFECT, enchantOptions, doublePrice, false,
                    null, 0, null, effects, null, null);
            }
            case "set_random_potion": {
                List<String> potions = new ArrayList<>();
                for (JsonElement potion : json.getAsJsonArray("options")) potions.add(potion.getAsString());
                return new Modifier(TradeEntry.TradeType.SET_RANDOM_POTION, enchantOptions, doublePrice, false,
                    null, 0, null, null, potions, null);
            }
            case "exploration_map":
                return new Modifier(TradeEntry.TradeType.EXPLORATION_MAP, enchantOptions, doublePrice, false,
                    null, 0, null, null, null, json.get("destination").getAsString());
            default:
                throw new IllegalArgumentException("Unknown trade modifier: " + type);
        }
    }
}
