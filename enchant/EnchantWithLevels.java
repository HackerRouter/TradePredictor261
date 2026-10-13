package enchant;

import gamedata.GameData;
import rng.Xoroshiro128PlusPlus;
import trade.Modifier;
import util.Names;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EnchantWithLevels {

    public static class EnchantInstance {
        public final EnchantDef def;
        public final int level;

        public EnchantInstance(EnchantDef def, int level) {
            this.def = def;
            this.level = level;
        }

        public int enchantIndex() {
            return def.index;
        }

        @Override
        public String toString() {
            return Names.enchant(def.name) + (level > 1 ? " " + level : "");
        }
    }

    public static class Result {
        public final List<EnchantInstance> enchantments;
        public final int enchantmentCost;
        public final boolean empty;
        private final GameData data;

        public Result(List<EnchantInstance> enchantments, int enchantmentCost, GameData data) {
            this.enchantments = enchantments;
            this.enchantmentCost = enchantmentCost;
            this.empty = enchantments.isEmpty();
            this.data = data;
        }

        public List<EnchantInstance> inTooltipOrder() {
            List<EnchantInstance> sorted = new ArrayList<>(enchantments);
            sorted.sort(Comparator.comparingInt(e -> data.tooltipRank(e.def.name)));
            return sorted;
        }

        @Override
        public String toString() {
            if (empty) return "[EMPTY - DISCARDED]";
            StringBuilder sb = new StringBuilder();
            for (EnchantInstance enchant : inTooltipOrder()) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(enchant);
            }
            return sb.toString();
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng, Modifier modifier, GameData data) {
        int enchantmentCost = modifier.levels.getInt(rng);
        List<EnchantInstance> results = new ArrayList<>();
        if (modifier.enchantability > 0) {
            int cost = enchantmentCost;
            int bonusBound = modifier.enchantability / 4 + 1;
            cost += 1 + rng.nextInt(bonusBound) + rng.nextInt(bonusBound);
            float randomSpan = (rng.nextFloat() + rng.nextFloat() - 1.0F) * 0.15F;
            cost = Math.max(1, Math.round(cost + cost * randomSpan));
            List<EnchantInstance> available = availableAt(cost, modifier, data);
            if (!available.isEmpty()) {
                EnchantInstance first = weightedPick(rng, available);
                if (first != null) results.add(first);
                while (rng.nextInt(50) <= cost) {
                    if (!results.isEmpty()) {
                        EnchantInstance last = results.get(results.size() - 1);
                        available.removeIf(e -> !last.def.isCompatibleWith(e.def));
                    }
                    if (available.isEmpty()) break;
                    EnchantInstance next = weightedPick(rng, available);
                    if (next != null) results.add(next);
                    cost /= 2;
                }
            }
        }
        return new Result(results, enchantmentCost, data);
    }

    private static List<EnchantInstance> availableAt(int cost, Modifier modifier, GameData data) {
        List<EnchantInstance> results = new ArrayList<>();
        for (int index : modifier.enchantOptions) {
            EnchantDef def = data.enchant(index);
            for (int level = def.maxLevel; level >= 1; level--) {
                if (cost >= def.getMinCost(level) && cost <= def.getMaxCost(level)) {
                    results.add(new EnchantInstance(def, level));
                    break;
                }
            }
        }
        return results;
    }

    private static EnchantInstance weightedPick(Xoroshiro128PlusPlus rng, List<EnchantInstance> items) {
        int totalWeight = 0;
        for (EnchantInstance item : items) totalWeight += item.def.weight;
        if (totalWeight <= 0) return null;
        int selection = rng.nextInt(totalWeight);
        for (EnchantInstance item : items) {
            selection -= item.def.weight;
            if (selection < 0) return item;
        }
        return null;
    }
}
