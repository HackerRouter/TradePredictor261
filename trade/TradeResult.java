package trade;

import enchant.EnchantRandomly;
import enchant.EnchantWithLevels;
import functions.SetRandomDyes;
import functions.SetRandomPotion;
import functions.SetStewEffect;
import util.I18n;
import util.Names;

public class TradeResult {

    public enum DiscardReason {
        VILLAGER_TYPE,
        NO_ENCHANTMENT,
        NO_DYE,
        MAP_UNAVAILABLE,
        ZERO_COST
    }

    public final TradeEntry entry;
    public final int poolIndex;
    public final TradeEntry.TradeType type;
    public final DiscardReason discardReason;
    public final boolean discarded;
    public final int price;

    public final EnchantRandomly.Result enchantRandomlyResult;
    public final EnchantWithLevels.Result enchantWithLevelsResult;
    public final SetRandomDyes.Result dyeResult;
    public final SetStewEffect.Result stewResult;
    public final SetRandomPotion.Result potionResult;

    TradeResult(TradeEntry entry, DiscardReason discardReason, int price, EnchantRandomly.Result enchantRandomlyResult,
                EnchantWithLevels.Result enchantWithLevelsResult, SetRandomDyes.Result dyeResult,
                SetStewEffect.Result stewResult, SetRandomPotion.Result potionResult) {
        this.entry = entry;
        this.poolIndex = entry.index;
        this.type = entry.type;
        this.discardReason = discardReason;
        this.discarded = discardReason != null;
        this.price = price;
        this.enchantRandomlyResult = enchantRandomlyResult;
        this.enchantWithLevelsResult = enchantWithLevelsResult;
        this.dyeResult = dyeResult;
        this.stewResult = stewResult;
        this.potionResult = potionResult;
    }

    public String getFullDescription() {
        if (discarded) {
            return "[" + I18n.get("trade.discarded_" + discardReason.name().toLowerCase()) + ": " + entry.label() + "]";
        }
        String cost = entry.costLabel(price);
        switch (type) {
            case ENCHANT_RANDOMLY:
                return cost + " -> " + Names.item(entry.givesItem) + " [" + enchantRandomlyResult + "]"
                    + (enchantRandomlyResult.doubled ? " (x2)" : "");
            case ENCHANT_WITH_LEVELS:
                return cost + " -> " + Names.item(entry.givesItem) + " [" + enchantWithLevelsResult + "]"
                    + " (" + entry.wants.count + "+" + (price - entry.wants.count) + ")";
            case SET_RANDOM_DYES:
                return cost + " -> " + entry.givesLabel() + " " + dyeResult;
            case SET_STEW_EFFECT:
                return cost + " -> " + entry.givesLabel() + " [" + stewResult + "]";
            case SET_RANDOM_POTION:
                return cost + " -> " + entry.givesLabel() + " [" + potionResult + "]";
            default:
                return cost + " -> " + entry.givesLabel();
        }
    }

    @Override
    public String toString() {
        return getFullDescription();
    }
}
