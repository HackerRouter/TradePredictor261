package enchant;

import gamedata.GameData;
import rng.Xoroshiro128PlusPlus;
import trade.Modifier;
import util.Names;

public class EnchantRandomly {

    public static class Result {
        public final int enchantIndex;
        public final String enchantName;
        public final int level;
        public final int additionalCost;
        public final boolean doubled;

        public Result(int enchantIndex, String enchantName, int level, int additionalCost, boolean doubled) {
            this.enchantIndex = enchantIndex;
            this.enchantName = enchantName;
            this.level = level;
            this.additionalCost = additionalCost;
            this.doubled = doubled;
        }

        public int totalAdditionalCost() {
            return doubled ? additionalCost * 2 : additionalCost;
        }

        @Override
        public String toString() {
            return Names.enchant(enchantName) + (level > 1 ? " " + level : "");
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng, Modifier modifier, GameData data) {
        int enchantIndex = modifier.enchantOptions.get(rng.nextInt(modifier.enchantOptions.size()));
        EnchantDef def = data.enchant(enchantIndex);
        int level = rng.nextInt(1, def.maxLevel);
        int additionalCost = modifier.additionalCost ? 2 + rng.nextInt(5 + level * 10) + 3 * level : 0;
        return new Result(enchantIndex, def.name, level, additionalCost, modifier.doublePriceEnchants.contains(enchantIndex));
    }
}
