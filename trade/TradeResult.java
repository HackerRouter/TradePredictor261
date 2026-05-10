package trade;

import enchant.EnchantRandomly;
import enchant.EnchantWithLevels;
import functions.SetRandomDyes;
import functions.SetStewEffect;
import functions.SetRandomPotion;

public class TradeResult {
    public final int poolIndex;          // original index in the pool (before removal)
    public final String tradeName;       // human-readable trade name
    public final TradeEntry.TradeType type;
    public final boolean discarded;      // true if FilteredFunction discarded this trade

    public EnchantRandomly.Result enchantRandomlyResult;
    public EnchantWithLevels.Result enchantWithLevelsResult;
    public SetRandomDyes.Result dyeResult;
    public SetStewEffect.Result stewResult;
    public SetRandomPotion.Result potionResult;

    public TradeResult(int poolIndex, String tradeName, TradeEntry.TradeType type, boolean discarded) {
        this.poolIndex = poolIndex;
        this.tradeName = tradeName;
        this.type = type;
        this.discarded = discarded;
    }

    public String getCostDescription() {
        if (discarded) return "[DISCARDED]";
        switch (type) {
            case ENCHANT_RANDOMLY:
                if (enchantRandomlyResult != null) {
                    return enchantRandomlyResult.finalCost + " EM + 1 Book";
                }
                break;
            case ENCHANT_WITH_LEVELS:
                if (enchantWithLevelsResult != null && !enchantWithLevelsResult.empty) {
                    int base = parseBaseCost(tradeName);
                    int total = base + enchantWithLevelsResult.enchantmentCost;
                    return total + " EM";
                }
                break;
            default:
                break;
        }
        return tradeName.split("->")[0].trim();
    }

    public String getResultDescription() {
        if (discarded) return "[DISCARDED]";
        switch (type) {
            case ENCHANT_RANDOMLY:
                if (enchantRandomlyResult != null) {
                    return enchantRandomlyResult.toString();
                }
                break;
            case ENCHANT_WITH_LEVELS:
                if (enchantWithLevelsResult != null) {
                    return enchantWithLevelsResult.toString();
                }
                break;
            case SET_RANDOM_DYES:
                if (dyeResult != null) {
                    return dyeResult.toString();
                }
                break;
            case SET_STEW_EFFECT:
                if (stewResult != null) {
                    return stewResult.toString();
                }
                break;
            case SET_RANDOM_POTION:
                if (potionResult != null) {
                    return potionResult.toString();
                }
                break;
            default:
                break;
        }
        return tradeName;
    }

    public String getFullDescription() {
        if (discarded) return "[DISCARDED]";
        switch (type) {
            case ENCHANT_RANDOMLY:
                if (enchantRandomlyResult != null) {
                    return enchantRandomlyResult.finalCost + " EM + 1 Book -> " + enchantRandomlyResult.toString();
                }
                break;
            case ENCHANT_WITH_LEVELS:
                if (enchantWithLevelsResult != null && !enchantWithLevelsResult.empty) {
                    int baseCost = parseBaseCost(tradeName);
                    int totalCost = baseCost + enchantWithLevelsResult.enchantmentCost;
                    return totalCost + " EM -> " + extractItemName(tradeName) + " -> "
                        + enchantWithLevelsResult.toString()
                        + " (" + baseCost + "+" + enchantWithLevelsResult.enchantmentCost + "=" + totalCost + "E)";
                }
                break;
            case SET_RANDOM_DYES:
                if (dyeResult != null) {
                    return tradeName + " " + dyeResult.toString();
                }
                break;
            case SET_STEW_EFFECT:
                if (stewResult != null) {
                    return tradeName + " -> " + stewResult.toString();
                }
                break;
            case SET_RANDOM_POTION:
                if (potionResult != null) {
                    return tradeName + " -> " + potionResult.toString();
                }
                break;
            default:
                break;
        }
        return tradeName;
    }

    public static int parseBaseCost(String tradeName) {
        try {
            int emIdx = tradeName.indexOf(" EM");
            if (emIdx > 0) {
                return Integer.parseInt(tradeName.substring(0, emIdx).trim());
            }
        } catch (NumberFormatException e) {
            // fall through
        }
        return 0;
    }

    private static String extractItemName(String tradeName) {
        int arrowIdx = tradeName.indexOf("-> ");
        if (arrowIdx >= 0) {
            return tradeName.substring(arrowIdx + 3);
        }
        return tradeName;
    }
}
