package functions;

import rng.Xoroshiro128PlusPlus;
import enchant.EnchantData;
import util.I18n;


public class SetStewEffect {

    public static class Result {
        public final int effectIndex;     // 0-5
        public final String effectName;
        public final int durationSeconds;

        public Result(int effectIndex, String effectName, int durationSeconds) {
            this.effectIndex = effectIndex;
            this.effectName = effectName;
            this.durationSeconds = durationSeconds;
        }

        @Override
        public String toString() {
            String displayName = I18n.translateId("stew", effectName);
            return displayName + " " + durationSeconds + "s";
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng) {
        int idx = rng.nextInt(EnchantData.STEW_EFFECT_COUNT); // nextInt(6)
        return new Result(idx, EnchantData.STEW_EFFECTS[idx], EnchantData.STEW_DURATIONS_SECONDS[idx]);
    }
}
