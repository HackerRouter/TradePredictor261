package trade;

import util.I18n;

import java.util.List;

public class TradePool {
    public final String profession;
    public final int level;
    public final String identifier;
    public final int amount;
    public final List<TradeEntry> entries;

    public TradePool(String profession, int level, int amount, List<TradeEntry> entries) {
        this.profession = profession;
        this.level = level;
        this.identifier = "minecraft:trade_set/" + profession + "/level_" + level;
        this.amount = amount;
        this.entries = entries;
    }

    public int size() {
        return entries.size();
    }

    public String getDisplayName() {
        return profession + "/level_" + level;
    }

    public void printTradeMenu() {
        System.out.println(I18n.get("trade.menu_title", getDisplayName()));
        for (int i = 0; i < entries.size(); i++) {
            TradeEntry entry = entries.get(i);
            String typeHint = "";
            switch (entry.type) {
                case ENCHANT_RANDOMLY:
                    typeHint = " [enchant_idx,level,cost]";
                    break;
                case ENCHANT_WITH_LEVELS:
                    typeHint = " [enchant_names]";
                    break;
                case SET_RANDOM_DYES:
                    typeHint = " [dyed]";
                    break;
                case SET_STEW_EFFECT:
                    typeHint = " [effect_idx]";
                    break;
                case SET_RANDOM_POTION:
                    typeHint = " [potion_idx]";
                    break;
                case EXPLORATION_MAP:
                    typeHint = " [map]";
                    break;
                default:
                    break;
            }
            System.out.println("  [" + i + "] " + entry.name + typeHint);
        }
    }
}
