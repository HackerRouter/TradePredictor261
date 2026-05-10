package trade;

public class TradeEntry {

    public enum TradeType {
        CONSTANT,           // No random consumption (paper->emerald, etc.)
        ENCHANT_RANDOMLY,   // EnchantRandomlyFunction (librarian enchanted books)
        ENCHANT_WITH_LEVELS,// EnchantWithLevelsFunction (equipment trades)
        SET_RANDOM_DYES,    // SetRandomDyesFunction (leatherworker)
        SET_STEW_EFFECT,    // SetStewEffectFunction (farmer suspicious stew)
        SET_RANDOM_POTION,  // SetRandomPotionFunction (fletcher tipped arrows)
        EXPLORATION_MAP     // ExplorationMapFunction (cartographer maps - 0 random consumption)
    }

    public final String name;
    public final TradeType type;
    public final String itemName;
    public final boolean hasVillagerTypePredicate;

    public TradeEntry(String name, TradeType type) {
        this(name, type, null, false);
    }

    public TradeEntry(String name, TradeType type, String itemName) {
        this(name, type, itemName, false);
    }

    public TradeEntry(String name, TradeType type, String itemName, boolean hasVillagerTypePredicate) {
        this.name = name;
        this.type = type;
        this.itemName = itemName;
        this.hasVillagerTypePredicate = hasVillagerTypePredicate;
    }

    @Override
    public String toString() {
        return name;
    }
}
