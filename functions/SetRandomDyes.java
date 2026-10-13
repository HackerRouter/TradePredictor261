package functions;

import gamedata.GameData;
import rng.Xoroshiro128PlusPlus;
import trade.Modifier;
import util.Names;

public class SetRandomDyes {

    public static class Result {
        public final int[] dyeIndices;
        private final GameData data;

        public Result(int[] dyeIndices, GameData data) {
            this.dyeIndices = dyeIndices;
            this.data = data;
        }

        public boolean isEmpty() {
            return dyeIndices.length == 0;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Dyes[");
            for (int i = 0; i < dyeIndices.length; i++) {
                if (i > 0) sb.append("+");
                sb.append(Names.of("dye", data.dyeColors.get(dyeIndices[i])));
            }
            return sb.append("]").toString();
        }
    }

    public static Result simulate(Xoroshiro128PlusPlus rng, Modifier modifier, GameData data) {
        int rolls = Math.max(0, modifier.dyeCount.getInt(rng));
        int[] dyeIndices = new int[rolls];
        for (int i = 0; i < rolls; i++) {
            dyeIndices[i] = rng.nextInt(data.dyeColors.size());
        }
        return new Result(dyeIndices, data);
    }
}
