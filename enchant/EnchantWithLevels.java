package enchant;

import rng.Xoroshiro128PlusPlus;
import util.I18n;
import java.util.ArrayList;
import java.util.List;


public class EnchantWithLevels {

    public static class EnchantInstance {
        public final int enchantIndex;    // index in ON_TRADED_EQUIPMENT (0-35)
        public final String enchantName;
        public final int level;
        public final int weight;

        public EnchantInstance(int enchantIndex, String enchantName, int level, int weight) {
            this.enchantIndex = enchantIndex;
            this.enchantName = enchantName;
            this.level = level;
            this.weight = weight;
        }

        @Override
        public String toString() {
            String displayName = I18n.translateId("enchant", enchantName);
            return displayName + (level > 1 ? " " + level : "");
        }
    }

    public static class Result {
        public final List<EnchantInstance> enchantments;
        public final int enchantmentCost;  // the cost level used (for ADDITIONAL_TRADE_COST)
        public final boolean empty;        // true if no enchantments were applied (item discarded)

        public Result(List<EnchantInstance> enchantments, int enchantmentCost) {
            this.enchantments = enchantments;
            this.enchantmentCost = enchantmentCost;
            this.empty = enchantments.isEmpty();
        }

        @Override
        public String toString() {
            if (empty) return "[EMPTY - DISCARDED]";
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < enchantments.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(enchantments.get(i));
            }
            return sb.toString();
        }
    }


    public static Result simulate(Xoroshiro128PlusPlus rng, String itemName) {
        int enchantability = EnchantData.getEnchantability(itemName);
        List<EnchantData.EnchantDef> applicableEnchants = EnchantData.getApplicableEnchantments(itemName);

        // Step 1: Generate enchantment cost level (UniformGenerator.between(5, 19).getInt())
        // Mth.nextInt(random, 5, 19) = random.nextInt(15) + 5
        int enchantmentCost = rng.nextInt(5, 19);

        // Step 2: selectEnchantment
        List<EnchantInstance> results = selectEnchantment(rng, enchantmentCost, enchantability, applicableEnchants);

        return new Result(results, enchantmentCost);
    }

    private static List<EnchantInstance> selectEnchantment(
            Xoroshiro128PlusPlus rng, int enchantmentCost, int enchantability,
            List<EnchantData.EnchantDef> sourceEnchants) {

        List<EnchantInstance> results = new ArrayList<>();

        if (enchantability <= 0) {
            return results;
        }

        // Modify cost with enchantability
        // enchantmentCost += 1 + nextInt(enchantability/4 + 1) + nextInt(enchantability/4 + 1)
        int halfEnch = enchantability / 4 + 1;
        enchantmentCost += 1 + rng.nextInt(halfEnch) + rng.nextInt(halfEnch);

        // Random span: (nextFloat() + nextFloat() - 1.0) * 0.15
        float randomSpan = (rng.nextFloat() + rng.nextFloat() - 1.0F) * 0.15F;
        enchantmentCost = clamp(Math.round(enchantmentCost + enchantmentCost * randomSpan), 1, Integer.MAX_VALUE);

        // Get available enchantments at this cost level
        List<EnchantInstance> available = getAvailableEnchantments(enchantmentCost, sourceEnchants);

        if (!available.isEmpty()) {
            // First pick: WeightedRandom.getRandomItem(random, list)
            EnchantInstance first = weightedRandomPick(rng, available);
            if (first != null) {
                results.add(first);

                // Loop: while (nextInt(50) <= enchantmentCost)
                while (rng.nextInt(50) <= enchantmentCost) {
                    // Filter compatible enchantments
                    if (!results.isEmpty()) {
                        filterCompatible(available, results.get(results.size() - 1));
                    }

                    if (available.isEmpty()) {
                        break;
                    }

                    // Pick next enchantment
                    EnchantInstance next = weightedRandomPick(rng, available);
                    if (next != null) {
                        results.add(next);
                    }
                    enchantmentCost /= 2;
                }
            }
        }

        return results;
    }

    private static List<EnchantInstance> getAvailableEnchantments(int cost, List<EnchantData.EnchantDef> source) {
        List<EnchantInstance> results = new ArrayList<>();
        for (EnchantData.EnchantDef def : source) {
            // Check from max level down to min level (1)
            for (int level = def.maxLevel; level >= 1; level--) {
                if (cost >= def.getMinCost(level) && cost <= def.getMaxCost(level)) {
                    results.add(new EnchantInstance(def.id, def.name, level, def.weight));
                    break;
                }
            }
        }
        return results;
    }

    private static EnchantInstance weightedRandomPick(Xoroshiro128PlusPlus rng, List<EnchantInstance> items) {
        int totalWeight = 0;
        for (EnchantInstance item : items) {
            totalWeight += item.weight;
        }
        if (totalWeight <= 0) return null;

        int selection = rng.nextInt(totalWeight);
        for (EnchantInstance item : items) {
            selection -= item.weight;
            if (selection < 0) {
                return item;
            }
        }
        return null;
    }

    private static void filterCompatible(List<EnchantInstance> enchantments, EnchantInstance target) {
        enchantments.removeIf(e -> !areCompatible(target, e));
    }

    private static boolean areCompatible(EnchantInstance a, EnchantInstance b) {
        if (a.enchantIndex == b.enchantIndex) return false;
        EnchantData.EnchantDef defA = getDefById(a.enchantIndex);
        EnchantData.EnchantDef defB = getDefById(b.enchantIndex);
        if (defA == null || defB == null) return true;
        return EnchantData.areCompatible(defA, defB);
    }

    private static EnchantData.EnchantDef getDefById(int id) {
        if (id >= 0 && id < EnchantData.ON_TRADED_EQUIPMENT_COUNT) {
            return EnchantData.ON_TRADED_EQUIPMENT[id];
        }
        return null;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
