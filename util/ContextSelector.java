package util;

import gamedata.GameData;
import trade.SimulationContext;
import trade.TradeEntry;
import trade.TradePool;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ContextSelector {

    public static SimulationContext select(TradePool pool) {
        String villagerType = null;
        if (pool.hasVillagerTypeConditions()) {
            List<String> types = GameData.get().villagerTypes;
            List<String> options = new ArrayList<>();
            for (String type : types) options.add(I18n.translateWithOriginal("villager_type", type));
            villagerType = types.get(InputHelper.selectFromMenu(I18n.get("context.select_villager_type"), options));
        }
        Set<Integer> unavailable = new LinkedHashSet<>();
        if (pool.hasMapsThatMayBeUnavailable()) {
            System.out.println("\n" + I18n.get("context.map_note"));
            if (!InputHelper.askYesNo(I18n.get("context.all_maps_available"))) {
                for (TradeEntry entry : pool.entries) {
                    if (entry.isMapThatMayBeUnavailable()) {
                        System.out.println("  [" + entry.index + "] " + entry.label());
                    }
                }
                System.out.print(I18n.get("context.unavailable_maps"));
                for (String part : InputHelper.readLine().split(",")) {
                    try {
                        int index = Integer.parseInt(part.trim());
                        if (index >= 0 && index < pool.size() && pool.entries.get(index).isMapThatMayBeUnavailable()) {
                            unavailable.add(index);
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }
        return new SimulationContext(villagerType, unavailable);
    }
}
