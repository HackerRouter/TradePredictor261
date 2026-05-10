package enchant;

import rng.Xoroshiro128PlusPlus;
import util.I18n;

public class EnchantRandomly {

    public static class Result {
        public final int enchantIndex;    // index in TRADEABLE (0-39)
        public final String enchantName;
        public final int level;
        public final int baseCost;        // before double price
        public final int finalCost;       // after double price and clamping

        public Result(int enchantIndex, String enchantName, int level, int baseCost, int finalCost) {
            this.enchantIndex = enchantIndex;
            this.enchantName = enchantName;
            this.level = level;
            this.baseCost = baseCost;
            this.finalCost = finalCost;
        }

        @Override
        public String toString() {
            String displayName = I18n.translateId("enchant", enchantName);
            String levelStr = level > 1 ? " " + level : "";
            return displayName + levelStr + " (" + finalCost + "E)";
        }
    }


    public static Result simulate(Xoroshiro128PlusPlus rng) {
        // 1. Pick enchantment: Util.getRandomSafe(list, random) → nextInt(40)
        int enchantIdx = rng.nextInt(EnchantData.TRADEABLE_COUNT);
        EnchantData.EnchantDef def = EnchantData.TRADEABLE[enchantIdx];

        // 2. Pick level: Mth.nextInt(random, minLevel, maxLevel)
        //    minLevel is always 1 for all enchantments
        //    If min >= max (i.e., maxLevel == 1), returns min without consuming random
        int level;
        if (def.maxLevel > 1) {
            level = rng.nextInt(def.maxLevel) + 1; // nextInt(maxLevel - 1 + 1) + 1
        } else {
            level = 1; // no random consumed
        }

        // 3. Additional trade cost: 2 + nextInt(5 + level * 10) + 3 * level
        int baseCost = 2 + rng.nextInt(5 + level * 10) + 3 * level;

        // 4. Double price for treasure enchantments
        int finalCost = EnchantData.isDoublePriceEnchant(enchantIdx) ? baseCost * 2 : baseCost;

        // 5. Clamp to [1, 64]
        finalCost = Math.max(1, Math.min(64, finalCost));

        return new Result(enchantIdx, def.name, level, baseCost, finalCost);
    }
}
