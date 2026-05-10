package functions;

import rng.Xoroshiro128PlusPlus;
import enchant.EnchantData;
import util.I18n;


public class SetRandomDyes {

    public static class Result {
        public final int numberOfDyes;
        public final int[] dyeIndices;  // indices into DyeColor.VALUES (0-15)

        public Result(int numberOfDyes, int[] dyeIndices) {
            this.numberOfDyes = numberOfDyes;
            this.dyeIndices = dyeIndices;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Dyes[");
            for (int i = 0; i < numberOfDyes; i++) {
                if (i > 0) sb.append("+");
                sb.append(I18n.translateId("dye", EnchantData.DYE_COLORS[dyeIndices[i]]));
            }
            sb.append("]");
            return sb.toString();
        }
    }


    public static Result simulate(Xoroshiro128PlusPlus rng) {
        // Binomial(n=2, p=0.75): 2 trials
        int binomialResult = 0;
        if (rng.nextFloat() < 0.75F) binomialResult++;
        if (rng.nextFloat() < 0.75F) binomialResult++;

        // Sum(1, binomial) = 1 + binomialResult
        int numberOfDyes = 1 + binomialResult;

        // Pick dye colors
        int[] dyeIndices = new int[numberOfDyes];
        for (int i = 0; i < numberOfDyes; i++) {
            dyeIndices[i] = rng.nextInt(EnchantData.DYE_COLOR_COUNT); // nextInt(16)
        }

        return new Result(numberOfDyes, dyeIndices);
    }
}
