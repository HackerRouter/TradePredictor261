package trade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradePoolRegistry {

    // Profession name constants
    public static final String FARMER = "farmer";
    public static final String FISHERMAN = "fisherman";
    public static final String SHEPHERD = "shepherd";
    public static final String FLETCHER = "fletcher";
    public static final String LIBRARIAN = "librarian";
    public static final String CARTOGRAPHER = "cartographer";
    public static final String CLERIC = "cleric";
    public static final String ARMORER = "armorer";
    public static final String WEAPONSMITH = "weaponsmith";
    public static final String TOOLSMITH = "toolsmith";
    public static final String BUTCHER = "butcher";
    public static final String LEATHERWORKER = "leatherworker";
    public static final String MASON = "mason";

    private static final List<String> ALL_PROFESSIONS = Arrays.asList(
        FARMER, FISHERMAN, SHEPHERD, FLETCHER, LIBRARIAN, CARTOGRAPHER,
        CLERIC, ARMORER, WEAPONSMITH, TOOLSMITH, BUTCHER, LEATHERWORKER, MASON
    );

    private static final Map<String, TradePool> pools = new HashMap<>();

    static {
        pools.put("farmer/level_1", new TradePool("farmer", 1, 2, List.of(
                new TradeEntry("20 Wheat -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("26 Potato -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("22 Carrot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("15 Beetroot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 6 Bread", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("farmer/level_2", new TradePool("farmer", 2, 2, List.of(
                new TradeEntry("6 Pumpkin -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Pumpkin Pie", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Apple", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("farmer/level_3", new TradePool("farmer", 3, 2, List.of(
                new TradeEntry("3 EM -> 18 Cookie", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 Melon -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("farmer/level_4", new TradePool("farmer", 4, 2, List.of(
                new TradeEntry("1 EM -> 1 Cake", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> Suspicious Stew", TradeEntry.TradeType.SET_STEW_EFFECT)
        )));

        pools.put("farmer/level_5", new TradePool("farmer", 5, 2, List.of(
                new TradeEntry("3 EM -> 3 Golden Carrot", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 EM -> 3 Glistering Melon Slice", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fisherman/level_1", new TradePool("fisherman", 1, 2, List.of(
                new TradeEntry("20 String -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("10 Coal -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("6 Raw Cod + 1 EM -> 6 Cooked Cod", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Cod Bucket", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fisherman/level_2", new TradePool("fisherman", 2, 2, List.of(
                new TradeEntry("15 Cod -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("6 Salmon + 1 EM -> 6 Cooked Salmon", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("2 EM -> 1 Campfire", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fisherman/level_3", new TradePool("fisherman", 3, 2, List.of(
                new TradeEntry("13 Salmon -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> Fishing Rod (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "fishing_rod")
        )));

        pools.put("fisherman/level_4", new TradePool("fisherman", 4, 2, List.of(
                new TradeEntry("6 Tropical Fish -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fisherman/level_5", new TradePool("fisherman", 5, 2, List.of(
                new TradeEntry("4 Pufferfish -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 Oak Boat -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 Spruce Boat -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 Jungle Boat -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 Acacia Boat -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 Dark Oak Boat -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("shepherd/level_1", new TradePool("shepherd", 1, 2, List.of(
                new TradeEntry("18 White Wool -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("18 Brown Wool -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("18 Gray Wool -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("18 Black Wool -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("2 EM -> 1 Shears", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("shepherd/level_2", new TradePool("shepherd", 2, 2, List.of(
                new TradeEntry("12 White Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Gray Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Black Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Light Blue Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Lime Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 White Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Orange Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Magenta Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Light Blue Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Yellow Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Lime Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Pink Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Gray Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Light Gray Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Cyan Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Purple Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Blue Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Brown Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Green Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Red Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Black Wool", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 White Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Orange Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Magenta Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Light Blue Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Yellow Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Lime Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Pink Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Gray Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Light Gray Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Cyan Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Purple Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Blue Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Brown Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Green Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Red Carpet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Black Carpet", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("shepherd/level_3", new TradePool("shepherd", 3, 2, List.of(
                new TradeEntry("12 Yellow Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Light Gray Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Orange Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Red Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Pink Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 White Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Orange Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Magenta Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Light Blue Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Yellow Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Lime Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Pink Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Gray Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Light Gray Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Cyan Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Purple Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Blue Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Brown Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Green Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Red Bed", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Black Bed", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("shepherd/level_4", new TradePool("shepherd", 4, 2, List.of(
                new TradeEntry("12 Brown Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Purple Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Blue Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Green Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Magenta Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("12 Cyan Dye -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 White Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Orange Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Magenta Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Light Blue Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Yellow Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Lime Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Pink Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Gray Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Light Gray Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Cyan Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Purple Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Blue Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Brown Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Green Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Red Banner", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Black Banner", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("shepherd/level_5", new TradePool("shepherd", 5, 2, List.of(
                new TradeEntry("2 EM -> 1 Painting", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fletcher/level_1", new TradePool("fletcher", 1, 2, List.of(
                new TradeEntry("32 Stick -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 16 Arrow", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("10 Gravel + 1 EM -> 10 Flint", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fletcher/level_2", new TradePool("fletcher", 2, 2, List.of(
                new TradeEntry("26 Flint -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("2 EM -> 1 Bow", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fletcher/level_3", new TradePool("fletcher", 3, 2, List.of(
                new TradeEntry("14 String -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Crossbow", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("fletcher/level_4", new TradePool("fletcher", 4, 2, List.of(
                new TradeEntry("24 Feather -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("2 EM -> Bow (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "bow")
        )));

        pools.put("fletcher/level_5", new TradePool("fletcher", 5, 2, List.of(
                new TradeEntry("8 Tripwire Hook -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> Crossbow (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "crossbow"),
                new TradeEntry("5 Arrow + 2 EM -> 5 Tipped Arrow", TradeEntry.TradeType.SET_RANDOM_POTION)
        )));

        pools.put("librarian/level_1", new TradePool("librarian", 1, 2, List.of(
                new TradeEntry("24 Paper -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("EM + Book -> Enchanted Book", TradeEntry.TradeType.ENCHANT_RANDOMLY),
                new TradeEntry("9 EM -> 1 Bookshelf", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("librarian/level_2", new TradePool("librarian", 2, 2, List.of(
                new TradeEntry("4 Book -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("EM + Book -> Enchanted Book", TradeEntry.TradeType.ENCHANT_RANDOMLY),
                new TradeEntry("1 EM -> 1 Lantern", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("librarian/level_3", new TradePool("librarian", 3, 2, List.of(
                new TradeEntry("5 Ink Sac -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("EM + Book -> Enchanted Book", TradeEntry.TradeType.ENCHANT_RANDOMLY),
                new TradeEntry("1 EM -> 4 Glass", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("librarian/level_4", new TradePool("librarian", 4, 2, List.of(
                new TradeEntry("2 Writable Book -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("EM + Book -> Enchanted Book", TradeEntry.TradeType.ENCHANT_RANDOMLY),
                new TradeEntry("5 EM -> 1 Clock", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 EM -> 1 Compass", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("librarian/level_5", new TradePool("librarian", 5, 3, List.of(
                new TradeEntry("1 EM -> 1 Yellow Candle", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Red Candle", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("cartographer/level_1", new TradePool("cartographer", 1, 2, List.of(
                new TradeEntry("24 Paper -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("7 EM -> 1 Map", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("cartographer/level_2", new TradePool("cartographer", 2, 2, List.of(
                new TradeEntry("11 Glass Pane -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("8 EM + 1 Compass -> Village Taiga Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true),
                new TradeEntry("8 EM + 1 Compass -> Explorer Swamp Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true),
                new TradeEntry("8 EM + 1 Compass -> Village Snowy Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true),
                new TradeEntry("8 EM + 1 Compass -> Village Savanna Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true),
                new TradeEntry("8 EM + 1 Compass -> Village Plains Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true),
                new TradeEntry("8 EM + 1 Compass -> Explorer Jungle Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true),
                new TradeEntry("8 EM + 1 Compass -> Village Desert Map", TradeEntry.TradeType.EXPLORATION_MAP, null, true)
        )));

        pools.put("cartographer/level_3", new TradePool("cartographer", 3, 2, List.of(
                new TradeEntry("1 Compass -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("13 EM + 1 Compass -> Ocean Explorer Map", TradeEntry.TradeType.EXPLORATION_MAP),
                new TradeEntry("12 EM + 1 Compass -> Trial Chamber Map", TradeEntry.TradeType.EXPLORATION_MAP)
        )));

        pools.put("cartographer/level_4", new TradePool("cartographer", 4, 2, List.of(
                new TradeEntry("7 EM -> 1 Item Frame", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 White Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Orange Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Magenta Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Blue Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Light Blue Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Yellow Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Lime Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Pink Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Gray Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Cyan Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Purple Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Brown Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Green Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Red Banner", TradeEntry.TradeType.CONSTANT, null, true),
                new TradeEntry("3 EM -> 1 Black Banner", TradeEntry.TradeType.CONSTANT, null, true)
        )));

        pools.put("cartographer/level_5", new TradePool("cartographer", 5, 2, List.of(
                new TradeEntry("8 EM -> 1 Globe Banner Pattern", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("14 EM + 1 Compass -> Woodland Mansion Map", TradeEntry.TradeType.EXPLORATION_MAP)
        )));

        pools.put("cleric/level_1", new TradePool("cleric", 1, 2, List.of(
                new TradeEntry("32 Rotten Flesh -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 2 Redstone", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("cleric/level_2", new TradePool("cleric", 2, 2, List.of(
                new TradeEntry("3 Gold Ingot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Lapis Lazuli", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("cleric/level_3", new TradePool("cleric", 3, 2, List.of(
                new TradeEntry("2 Rabbit Foot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 EM -> 1 Glowstone", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("cleric/level_4", new TradePool("cleric", 4, 2, List.of(
                new TradeEntry("4 Turtle Scute -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("9 Glass Bottle -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("5 EM -> 1 Ender Pearl", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("cleric/level_5", new TradePool("cleric", 5, 2, List.of(
                new TradeEntry("22 Nether Wart -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Experience Bottle", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("armorer/level_1", new TradePool("armorer", 1, 2, List.of(
                new TradeEntry("15 Coal -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("7 EM -> 1 Iron Leggings", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 EM -> 1 Iron Boots", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("5 EM -> 1 Iron Helmet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("9 EM -> 1 Iron Chestplate", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("armorer/level_2", new TradePool("armorer", 2, 2, List.of(
                new TradeEntry("4 Iron Ingot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("36 EM -> 1 Bell", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Chainmail Boots", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Chainmail Leggings", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("armorer/level_3", new TradePool("armorer", 3, 2, List.of(
                new TradeEntry("1 Lava Bucket -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Chainmail Helmet", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 EM -> 1 Chainmail Chestplate", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("5 EM -> 1 Shield", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 Diamond -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("armorer/level_4", new TradePool("armorer", 4, 2, List.of(
                new TradeEntry("14 EM -> Diamond Leggings (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_leggings"),
                new TradeEntry("8 EM -> Diamond Boots (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_boots")
        )));

        pools.put("armorer/level_5", new TradePool("armorer", 5, 2, List.of(
                new TradeEntry("8 EM -> Diamond Helmet (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_helmet"),
                new TradeEntry("16 EM -> Diamond Chestplate (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_chestplate")
        )));

        pools.put("weaponsmith/level_1", new TradePool("weaponsmith", 1, 2, List.of(
                new TradeEntry("15 Coal -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> 1 Iron Axe", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("2 EM -> Iron Sword (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "iron_sword")
        )));

        pools.put("weaponsmith/level_2", new TradePool("weaponsmith", 2, 2, List.of(
                new TradeEntry("4 Iron Ingot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("36 EM -> 1 Bell", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("weaponsmith/level_3", new TradePool("weaponsmith", 3, 2, List.of(
                new TradeEntry("24 Flint -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("weaponsmith/level_4", new TradePool("weaponsmith", 4, 2, List.of(
                new TradeEntry("12 EM -> Diamond Axe (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_axe"),
                new TradeEntry("1 Diamond -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("weaponsmith/level_5", new TradePool("weaponsmith", 5, 2, List.of(
                new TradeEntry("8 EM -> Diamond Sword (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_sword")
        )));

        pools.put("toolsmith/level_1", new TradePool("toolsmith", 1, 2, List.of(
                new TradeEntry("15 Coal -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Stone Axe", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Stone Shovel", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Stone Pickaxe", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Stone Hoe", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("toolsmith/level_2", new TradePool("toolsmith", 2, 2, List.of(
                new TradeEntry("4 Iron Ingot -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("36 EM -> 1 Bell", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("toolsmith/level_3", new TradePool("toolsmith", 3, 2, List.of(
                new TradeEntry("30 Flint -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> Iron Axe (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "iron_axe"),
                new TradeEntry("2 EM -> Iron Shovel (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "iron_shovel"),
                new TradeEntry("3 EM -> Iron Pickaxe (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "iron_pickaxe"),
                new TradeEntry("4 EM -> 1 Diamond Hoe", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("toolsmith/level_4", new TradePool("toolsmith", 4, 2, List.of(
                new TradeEntry("12 EM -> Diamond Axe (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_axe"),
                new TradeEntry("5 EM -> Diamond Shovel (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_shovel"),
                new TradeEntry("1 Diamond -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("toolsmith/level_5", new TradePool("toolsmith", 5, 2, List.of(
                new TradeEntry("13 EM -> Diamond Pickaxe (enchanted)", TradeEntry.TradeType.ENCHANT_WITH_LEVELS, "diamond_pickaxe")
        )));

        pools.put("butcher/level_1", new TradePool("butcher", 1, 2, List.of(
                new TradeEntry("14 Chicken -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("7 Porkchop -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("4 Rabbit -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Rabbit Stew", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("butcher/level_2", new TradePool("butcher", 2, 2, List.of(
                new TradeEntry("15 Coal -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 5 Cooked Porkchop", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 8 Cooked Chicken", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("butcher/level_3", new TradePool("butcher", 3, 2, List.of(
                new TradeEntry("7 Mutton -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("10 Beef -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("butcher/level_4", new TradePool("butcher", 4, 2, List.of(
                new TradeEntry("10 Dried Kelp Block -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("butcher/level_5", new TradePool("butcher", 5, 2, List.of(
                new TradeEntry("10 Sweet Berries -> 1 EM", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("leatherworker/level_1", new TradePool("leatherworker", 1, 2, List.of(
                new TradeEntry("6 Leather -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("3 EM -> Leather Leggings (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES),
                new TradeEntry("7 EM -> Leather Chestplate (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES)
        )));

        pools.put("leatherworker/level_2", new TradePool("leatherworker", 2, 2, List.of(
                new TradeEntry("26 Flint -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("5 EM -> Leather Helmet (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES),
                new TradeEntry("4 EM -> Leather Boots (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES)
        )));

        pools.put("leatherworker/level_3", new TradePool("leatherworker", 3, 2, List.of(
                new TradeEntry("9 Rabbit Hide -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("7 EM -> Leather Chestplate (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES)
        )));

        pools.put("leatherworker/level_4", new TradePool("leatherworker", 4, 2, List.of(
                new TradeEntry("4 Turtle Scute -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("6 EM -> Leather Horse Armor (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES)
        )));

        pools.put("leatherworker/level_5", new TradePool("leatherworker", 5, 2, List.of(
                new TradeEntry("6 EM -> 1 Saddle", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("5 EM -> Leather Helmet (dyed)", TradeEntry.TradeType.SET_RANDOM_DYES)
        )));

        pools.put("mason/level_1", new TradePool("mason", 1, 2, List.of(
                new TradeEntry("10 Clay Ball -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 10 Brick", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("mason/level_2", new TradePool("mason", 2, 2, List.of(
                new TradeEntry("20 Stone -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Chiseled Stone Bricks", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("mason/level_3", new TradePool("mason", 3, 2, List.of(
                new TradeEntry("16 Granite -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("16 Andesite -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("16 Diorite -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Dripstone Block", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Polished Andesite", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Polished Diorite", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 4 Polished Granite", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("mason/level_4", new TradePool("mason", 4, 2, List.of(
                new TradeEntry("12 Quartz -> 1 EM", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Orange Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 White Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Blue Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Light Blue Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Gray Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Light Gray Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Black Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Red Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Pink Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Magenta Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Lime Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Green Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Cyan Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Purple Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Yellow Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Brown Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Orange Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 White Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Blue Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Light Blue Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Gray Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Light Gray Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Black Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Red Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Pink Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Magenta Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Lime Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Green Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Cyan Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Purple Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Yellow Glazed Terracotta", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Brown Glazed Terracotta", TradeEntry.TradeType.CONSTANT)
        )));

        pools.put("mason/level_5", new TradePool("mason", 5, 2, List.of(
                new TradeEntry("1 EM -> 1 Quartz Pillar", TradeEntry.TradeType.CONSTANT),
                new TradeEntry("1 EM -> 1 Quartz Block", TradeEntry.TradeType.CONSTANT)
        )));

    }

    public static TradePool getPool(String profession, int level) {
        return pools.get(profession + "/level_" + level);
    }

    public static List<String> getAllProfessions() {
        return ALL_PROFESSIONS;
    }

    public static Map<String, TradePool> getAllPools() {
        return pools;
    }
}
