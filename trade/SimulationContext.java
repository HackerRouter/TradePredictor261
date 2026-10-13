package trade;

import java.util.Collections;
import java.util.Set;

public class SimulationContext {

    public static final SimulationContext DEFAULT = new SimulationContext(null, Collections.emptySet());

    public final String villagerType;
    public final Set<Integer> unavailableEntries;

    public SimulationContext(String villagerType, Set<Integer> unavailableEntries) {
        this.villagerType = villagerType;
        this.unavailableEntries = unavailableEntries;
    }

    public String describe() {
        StringBuilder sb = new StringBuilder();
        if (villagerType != null) sb.append("villager_type=").append(villagerType);
        if (!unavailableEntries.isEmpty()) {
            if (sb.length() > 0) sb.append(' ');
            sb.append("unavailable=").append(unavailableEntries);
        }
        return sb.toString();
    }
}
