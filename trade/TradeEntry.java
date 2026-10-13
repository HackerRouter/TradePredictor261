package trade;

import util.Names;

import java.util.Collections;
import java.util.Set;

public class TradeEntry {

    public enum TradeType {
        CONSTANT,
        ENCHANT_RANDOMLY,
        ENCHANT_WITH_LEVELS,
        SET_RANDOM_DYES,
        SET_STEW_EFFECT,
        SET_RANDOM_POTION,
        EXPLORATION_MAP
    }

    public final int index;
    public final String id;
    public final Cost wants;
    public final Cost additionalWants;
    public final String givesItem;
    public final int givesCount;
    public final Set<String> villagerTypes;
    public final Modifier modifier;
    public final Set<String> discardUnless;
    public final TradeType type;

    public TradeEntry(int index, String id, Cost wants, Cost additionalWants, String givesItem, int givesCount,
                      Set<String> villagerTypes, Modifier modifier, Set<String> discardUnless) {
        this.index = index;
        this.id = id;
        this.wants = wants;
        this.additionalWants = additionalWants;
        this.givesItem = givesItem;
        this.givesCount = givesCount;
        this.villagerTypes = villagerTypes;
        this.modifier = modifier;
        this.discardUnless = discardUnless == null ? Collections.emptySet() : discardUnless;
        this.type = modifier == null ? TradeType.CONSTANT : modifier.type;
    }

    public boolean hasVillagerTypeCondition() {
        return villagerTypes != null;
    }

    public boolean isMapThatMayBeUnavailable() {
        return type == TradeType.EXPLORATION_MAP && discardUnless.contains("map_id");
    }

    public String costLabel(int wantsCount) {
        StringBuilder sb = new StringBuilder();
        if (wantsCount > 0) sb.append(wantsCount).append(' ').append(Names.item(wants.item));
        if (additionalWants != null) {
            if (sb.length() > 0) sb.append(" + ");
            sb.append(additionalWants.withAdditional(0)).append(' ').append(Names.item(additionalWants.item));
        }
        return sb.toString();
    }

    public String givesLabel() {
        return givesCount + " " + Names.item(givesItem);
    }

    public String label() {
        String cost = type == TradeType.ENCHANT_RANDOMLY ? "? " + Names.item(wants.item)
            + (additionalWants != null ? " + " + additionalWants.withAdditional(0) + " " + Names.item(additionalWants.item) : "")
            : type == TradeType.ENCHANT_WITH_LEVELS ? wants.count + "+? " + Names.item(wants.item)
            : costLabel(wants.withAdditional(0));
        return cost + " -> " + givesLabel();
    }

    @Override
    public String toString() {
        return label();
    }
}
