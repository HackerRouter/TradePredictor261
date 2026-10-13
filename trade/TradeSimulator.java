package trade;

import enchant.EnchantRandomly;
import enchant.EnchantWithLevels;
import functions.SetRandomDyes;
import functions.SetRandomPotion;
import functions.SetStewEffect;
import gamedata.GameData;
import rng.Xoroshiro128PlusPlus;

import java.util.ArrayList;
import java.util.List;

public class TradeSimulator {

    private final Xoroshiro128PlusPlus rng;
    private final GameData data;
    private final SimulationContext context;

    public TradeSimulator(Xoroshiro128PlusPlus rng, GameData data, SimulationContext context) {
        this.rng = rng;
        this.data = data;
        this.context = context;
    }

    public static TradeSimulator forPool(TradePool pool, long seed, SimulationContext context) {
        Xoroshiro128PlusPlus rng = new Xoroshiro128PlusPlus(pool.identifier);
        rng.setSeed(seed);
        return new TradeSimulator(rng, GameData.get(), context);
    }

    public void skip(TradePool pool, int refreshes) {
        for (int i = 1; i <= refreshes; i++) simulateRefresh(pool, i);
    }

    public RefreshResult simulateRefresh(TradePool pool, int refreshIndex) {
        RefreshResult result = new RefreshResult(refreshIndex);
        List<TradeEntry> leftover = new ArrayList<>(pool.entries);
        int offersFound = 0;
        while (offersFound < pool.amount && !leftover.isEmpty()) {
            TradeEntry entry = leftover.remove(rng.nextInt(leftover.size()));
            TradeResult trade = getOffer(entry);
            result.addTrade(trade);
            if (!trade.discarded) offersFound++;
        }
        return result;
    }

    private TradeResult getOffer(TradeEntry entry) {
        if (entry.villagerTypes != null && !entry.villagerTypes.contains(context.villagerType)) {
            return new TradeResult(entry, TradeResult.DiscardReason.VILLAGER_TYPE, 0, null, null, null, null, null);
        }
        EnchantRandomly.Result enchantRandomly = null;
        EnchantWithLevels.Result enchantWithLevels = null;
        SetRandomDyes.Result dyes = null;
        SetStewEffect.Result stew = null;
        SetRandomPotion.Result potion = null;
        TradeResult.DiscardReason discard = null;
        int additionalCost = 0;
        if (entry.modifier != null) {
            switch (entry.type) {
                case ENCHANT_RANDOMLY:
                    enchantRandomly = EnchantRandomly.simulate(rng, entry.modifier, data);
                    additionalCost = enchantRandomly.totalAdditionalCost();
                    break;
                case ENCHANT_WITH_LEVELS:
                    enchantWithLevels = EnchantWithLevels.simulate(rng, entry.modifier, data);
                    if (enchantWithLevels.empty && entry.discardUnless.contains("enchantments")) {
                        discard = TradeResult.DiscardReason.NO_ENCHANTMENT;
                    } else if (entry.modifier.additionalCost && !enchantWithLevels.empty && enchantWithLevels.enchantmentCost > 0) {
                        additionalCost = enchantWithLevels.enchantmentCost;
                    }
                    break;
                case SET_RANDOM_DYES:
                    dyes = SetRandomDyes.simulate(rng, entry.modifier, data);
                    if (dyes.isEmpty() && entry.discardUnless.contains("dyed_color")) {
                        discard = TradeResult.DiscardReason.NO_DYE;
                    }
                    break;
                case SET_STEW_EFFECT:
                    stew = SetStewEffect.simulate(rng, entry.modifier);
                    break;
                case SET_RANDOM_POTION:
                    potion = SetRandomPotion.simulate(rng, entry.modifier);
                    break;
                case EXPLORATION_MAP:
                    if (context.unavailableEntries.contains(entry.index) && entry.discardUnless.contains("map_id")) {
                        discard = TradeResult.DiscardReason.MAP_UNAVAILABLE;
                    }
                    break;
                default:
                    break;
            }
        }
        int price = entry.wants.withAdditional(additionalCost);
        if (discard == null && (price < 1 || (entry.additionalWants != null && entry.additionalWants.withAdditional(0) < 1))) {
            discard = TradeResult.DiscardReason.ZERO_COST;
        }
        return new TradeResult(entry, discard, price, enchantRandomly, enchantWithLevels, dyes, stew, potion);
    }
}
