package enchant;

import java.util.Set;

public class EnchantDef {
    public final int index;
    public final String name;
    public final int maxLevel;
    public final int weight;
    public final int minCostBase;
    public final int minCostPerLevel;
    public final int maxCostBase;
    public final int maxCostPerLevel;
    public final Set<String> exclusiveSet;

    public EnchantDef(int index, String name, int maxLevel, int weight, int minCostBase, int minCostPerLevel,
                      int maxCostBase, int maxCostPerLevel, Set<String> exclusiveSet) {
        this.index = index;
        this.name = name;
        this.maxLevel = maxLevel;
        this.weight = weight;
        this.minCostBase = minCostBase;
        this.minCostPerLevel = minCostPerLevel;
        this.maxCostBase = maxCostBase;
        this.maxCostPerLevel = maxCostPerLevel;
        this.exclusiveSet = exclusiveSet;
    }

    public int getMinCost(int level) {
        return minCostBase + minCostPerLevel * (level - 1);
    }

    public int getMaxCost(int level) {
        return maxCostBase + maxCostPerLevel * (level - 1);
    }

    public boolean isCompatibleWith(EnchantDef other) {
        return this != other && !exclusiveSet.contains(other.name) && !other.exclusiveSet.contains(name);
    }
}
