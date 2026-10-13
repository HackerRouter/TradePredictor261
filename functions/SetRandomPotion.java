package functions;

import rng.Xoroshiro128PlusPlus;
import trade.Modifier;
import util.Names;

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
            return Names.of("potion", potionName);
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng, Modifier modifier) {
        int index = rng.nextInt(modifier.potionOptions.size());
        return new Result(index, modifier.potionOptions.get(index));
    }
}
