package functions;

import rng.Xoroshiro128PlusPlus;
import trade.Modifier;
import util.Names;

public class SetStewEffect {

    public static class Result {
        public final int effectIndex;
        public final Modifier.StewEffect effect;

        public Result(int effectIndex, Modifier.StewEffect effect) {
            this.effectIndex = effectIndex;
            this.effect = effect;
        }

        @Override
        public String toString() {
            return Names.of("stew", effect.effect) + " " + Names.ticks(effect.ticks);
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng, Modifier modifier) {
        int index = rng.nextInt(modifier.stewEffects.size());
        return new Result(index, modifier.stewEffects.get(index));
    }
}
