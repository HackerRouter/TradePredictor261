package trade;

import rng.Xoroshiro128PlusPlus;
import enchant.EnchantRandomly;
import enchant.EnchantWithLevels;
import functions.SetRandomDyes;
import functions.SetStewEffect;
import functions.SetRandomPotion;

import java.util.ArrayList;
import java.util.List;

public class TradeSimulator {

    private final Xoroshiro128PlusPlus rng;

    public TradeSimulator(Xoroshiro128PlusPlus rng) {
        this.rng = rng;
    }

    public RefreshResult simulateRefresh(TradePool pool, int refreshIndex) {
        RefreshResult result = new RefreshResult(refreshIndex);

        // Create mutable copy of pool indices
        List<Integer> leftoverIndices = new ArrayList<>();
        for (int i = 0; i < pool.size(); i++) {
            leftoverIndices.add(i);
        }

        int offersFound = 0;
        int amount = pool.amount;

        while (offersFound < amount && !leftoverIndices.isEmpty()) {
            // Pick random entry from remaining pool
            int pickIdx = rng.nextInt(leftoverIndices.size());
            int entryIndex = leftoverIndices.remove(pickIdx);
            TradeEntry entry = pool.entries.get(entryIndex);

            // Simulate getOffer() for this entry
            TradeResult tradeResult = simulateGetOffer(entry, entryIndex);
            result.addTrade(tradeResult);

            if (!tradeResult.discarded) {
                offersFound++;
            }
            // If discarded: offersFound NOT incremented, loop continues with smaller pool
        }

        return result;
    }


    private TradeResult simulateGetOffer(TradeEntry entry, int poolIndex) {
        switch (entry.type) {
            case CONSTANT:
                // No random consumption, always succeeds
                return new TradeResult(poolIndex, entry.name, entry.type, false);

            case ENCHANT_RANDOMLY:
                return simulateEnchantRandomly(entry, poolIndex);

            case ENCHANT_WITH_LEVELS:
                return simulateEnchantWithLevels(entry, poolIndex);

            case SET_RANDOM_DYES:
                return simulateSetRandomDyes(entry, poolIndex);

            case SET_STEW_EFFECT:
                return simulateSetStewEffect(entry, poolIndex);

            case SET_RANDOM_POTION:
                return simulateSetRandomPotion(entry, poolIndex);

            case EXPLORATION_MAP:
                return new TradeResult(poolIndex, entry.name, entry.type, false);

            default:
                return new TradeResult(poolIndex, entry.name, entry.type, false);
        }
    }

    private TradeResult simulateEnchantRandomly(TradeEntry entry, int poolIndex) {
        EnchantRandomly.Result enchResult = EnchantRandomly.simulate(rng);
        TradeResult result = new TradeResult(poolIndex, entry.name, entry.type, false);
        result.enchantRandomlyResult = enchResult;
        return result;
    }

    private TradeResult simulateEnchantWithLevels(TradeEntry entry, int poolIndex) {
        EnchantWithLevels.Result enchResult = EnchantWithLevels.simulate(rng, entry.itemName);
        boolean discarded = enchResult.empty;
        TradeResult result = new TradeResult(poolIndex, entry.name, entry.type, discarded);
        result.enchantWithLevelsResult = enchResult;
        return result;
    }

    private TradeResult simulateSetRandomDyes(TradeEntry entry, int poolIndex) {
        SetRandomDyes.Result dyeResult = SetRandomDyes.simulate(rng);
        TradeResult result = new TradeResult(poolIndex, entry.name, entry.type, false);
        result.dyeResult = dyeResult;
        return result;
    }

    private TradeResult simulateSetStewEffect(TradeEntry entry, int poolIndex) {
        SetStewEffect.Result stewResult = SetStewEffect.simulate(rng);
        TradeResult result = new TradeResult(poolIndex, entry.name, entry.type, false);
        result.stewResult = stewResult;
        return result;
    }

    private TradeResult simulateSetRandomPotion(TradeEntry entry, int poolIndex) {
        SetRandomPotion.Result potionResult = SetRandomPotion.simulate(rng);
        TradeResult result = new TradeResult(poolIndex, entry.name, entry.type, false);
        result.potionResult = potionResult;
        return result;
    }
}
