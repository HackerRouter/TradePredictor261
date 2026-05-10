package functions;

import rng.Xoroshiro128PlusPlus;
import enchant.EnchantData;
import util.I18n;

public class SetRandomPotion {

    public static class Result {
        public final int potionIndex;
        public final String potionName;

        public Result(int potionIndex, String potionName) {
            this.potionIndex = potionIndex;
            this.potionName = potionName;
        }

        @Override
        public String toString() {
            return I18n.translateId("potion", potionName);
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng) {
        int idx = rng.nextInt(EnchantData.TRADEABLE_POTION_COUNT); // nextInt(41)
        return new Result(idx, EnchantData.TRADEABLE_POTIONS[idx]);
    }
}
