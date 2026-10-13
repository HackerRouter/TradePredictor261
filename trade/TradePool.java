package trade;

import util.I18n;
import util.Names;

import java.util.List;

public class TradePool {
    public final String profession;
    public final int level;
    public final String identifier;
    public final int amount;
    public final List<TradeEntry> entries;

    public TradePool(String profession, int level, String identifier, int amount, List<TradeEntry> entries) {
        this.profession = profession;
        this.level = level;
        this.identifier = identifier;
        this.amount = amount;
        this.entries = entries;
    }

    public int size() {
        return entries.size();
    }

    public String getDisplayName() {
        return profession + "/level_" + level;
    }

    public String getKey() {
        return profession + ".level_" + level;
    }

    public boolean hasVillagerTypeConditions() {
        for (TradeEntry entry : entries) {
            if (entry.hasVillagerTypeCondition()) return true;
        }
        return false;
    }

    public boolean hasMapsThatMayBeUnavailable() {
        for (TradeEntry entry : entries) {
            if (entry.isMapThatMayBeUnavailable()) return true;
        }
        return false;
    }

    public boolean has(TradeEntry.TradeType type) {
        for (TradeEntry entry : entries) {
            if (entry.type == type) return true;
        }
        return false;
    }

    public void printTradeMenu() {
        System.out.println(I18n.get("trade.menu_title", getDisplayName()));
        for (TradeEntry entry : entries) {
            String hint;
            switch (entry.type) {
                case ENCHANT_RANDOMLY: hint = " [enchant_idx,level,cost]"; break;
                case ENCHANT_WITH_LEVELS: hint = " [enchant_idx,level,...,total_cost]"; break;
                case SET_RANDOM_DYES: hint = " [dyed]"; break;
                case SET_STEW_EFFECT: hint = " [stew]"; break;
                case SET_RANDOM_POTION: hint = " [potion]"; break;
                case EXPLORATION_MAP: hint = " [map]"; break;
                default: hint = ""; break;
            }
            String condition = "";
            if (entry.hasVillagerTypeCondition()) {
                StringBuilder types = new StringBuilder();
                for (String type : entry.villagerTypes) {
                    if (types.length() > 0) types.append('/');
                    types.append(Names.of("villager_type", type));
                }
                condition = " " + I18n.get("trade.villager_types", types);
            }
            System.out.println("  [" + entry.index + "] " + entry.label() + hint + condition);
        }
    }
}
