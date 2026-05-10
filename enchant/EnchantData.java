package enchant;

import java.util.ArrayList;
import java.util.List;


public class EnchantData {

    // ==================== Enchantment Definition ====================

    public static class EnchantDef {
        public final int id;
        public final String name;
        public final int maxLevel;
        public final int weight;
        public final boolean isTreasure;
        public final int minCostBase;
        public final int minCostPerLevel;
        public final int maxCostBase;
        public final int maxCostPerLevel;
        // Exclusive groups for compatibility checking
        public final int exclusiveGroup;

        public EnchantDef(int id, String name, int maxLevel, int weight, boolean isTreasure,
                          int minCostBase, int minCostPerLevel, int maxCostBase, int maxCostPerLevel,
                          int exclusiveGroup) {
            this.id = id;
            this.name = name;
            this.maxLevel = maxLevel;
            this.weight = weight;
            this.isTreasure = isTreasure;
            this.minCostBase = minCostBase;
            this.minCostPerLevel = minCostPerLevel;
            this.maxCostBase = maxCostBase;
            this.maxCostPerLevel = maxCostPerLevel;
            this.exclusiveGroup = exclusiveGroup;
        }

        public int getMinCost(int level) {
            return minCostBase + minCostPerLevel * (level - 1);
        }

        public int getMaxCost(int level) {
            return maxCostBase + maxCostPerLevel * (level - 1);
        }
    }

    // Exclusive groups (for filterCompatibleEnchantments)
    public static final int GROUP_NONE = 0;
    public static final int GROUP_PROTECTION = 1;    // protection, fire_protection, blast_protection, projectile_protection
    public static final int GROUP_DAMAGE = 2;        // sharpness, smite, bane_of_arthropods, density, breach
    public static final int GROUP_DEPTH = 3;         // depth_strider, frost_walker
    public static final int GROUP_FORTUNE = 4;       // silk_touch, fortune, luck_of_the_sea
    public static final int GROUP_INFINITY = 5;      // infinity, mending
    public static final int GROUP_LOYALTY = 6;       // loyalty, riptide
    public static final int GROUP_CHANNELING = 7;    // channeling, riptide
    public static final int GROUP_MULTISHOT = 8;     // multishot, piercing

    // ==================== #minecraft:tradeable (40 enchantments) ====================
    // Order matches the tag file: #non_treasure (36) + binding_curse, vanishing_curse, frost_walker, mending

    public static final EnchantDef[] TRADEABLE = {
        // #minecraft:non_treasure (36)
        new EnchantDef(0,  "protection",            4, 10, false, 1,  11, 12, 11, GROUP_PROTECTION),
        new EnchantDef(1,  "fire_protection",       4, 5,  false, 10, 8,  18, 8,  GROUP_PROTECTION),
        new EnchantDef(2,  "feather_falling",       4, 5,  false, 5,  6,  11, 6,  GROUP_NONE),
        new EnchantDef(3,  "blast_protection",      4, 2,  false, 5,  8,  13, 8,  GROUP_PROTECTION),
        new EnchantDef(4,  "projectile_protection", 4, 5,  false, 3,  6,  9,  6,  GROUP_PROTECTION),
        new EnchantDef(5,  "respiration",           3, 2,  false, 10, 10, 40, 10, GROUP_NONE),
        new EnchantDef(6,  "aqua_affinity",         1, 2,  false, 1,  0,  41, 0,  GROUP_NONE),
        new EnchantDef(7,  "thorns",                3, 1,  false, 10, 20, 60, 20, GROUP_NONE),
        new EnchantDef(8,  "depth_strider",         3, 2,  false, 10, 10, 25, 10, GROUP_DEPTH),
        new EnchantDef(9,  "sharpness",             5, 10, false, 1,  11, 21, 11, GROUP_DAMAGE),
        new EnchantDef(10, "smite",                 5, 5,  false, 5,  8,  25, 8,  GROUP_DAMAGE),
        new EnchantDef(11, "bane_of_arthropods",    5, 5,  false, 5,  8,  25, 8,  GROUP_DAMAGE),
        new EnchantDef(12, "knockback",             2, 5,  false, 5,  20, 55, 20, GROUP_NONE),
        new EnchantDef(13, "fire_aspect",           2, 2,  false, 10, 20, 60, 20, GROUP_NONE),
        new EnchantDef(14, "looting",               3, 2,  false, 15, 9,  65, 9,  GROUP_NONE),
        new EnchantDef(15, "sweeping_edge",         3, 2,  false, 5,  9,  20, 9,  GROUP_NONE),
        new EnchantDef(16, "efficiency",            5, 10, false, 1,  10, 51, 10, GROUP_NONE),
        new EnchantDef(17, "silk_touch",            1, 1,  false, 15, 0,  65, 0,  GROUP_FORTUNE),
        new EnchantDef(18, "unbreaking",            3, 5,  false, 5,  8,  55, 8,  GROUP_NONE),
        new EnchantDef(19, "fortune",               3, 2,  false, 15, 9,  65, 9,  GROUP_FORTUNE),
        new EnchantDef(20, "power",                 5, 10, false, 1,  10, 16, 10, GROUP_NONE),
        new EnchantDef(21, "punch",                 2, 2,  false, 12, 20, 37, 20, GROUP_NONE),
        new EnchantDef(22, "flame",                 1, 2,  false, 20, 0,  50, 0,  GROUP_NONE),
        new EnchantDef(23, "infinity",              1, 1,  false, 20, 0,  50, 0,  GROUP_INFINITY),
        new EnchantDef(24, "luck_of_the_sea",       3, 2,  false, 15, 9,  65, 9,  GROUP_FORTUNE),
        new EnchantDef(25, "lure",                  3, 2,  false, 15, 9,  65, 9,  GROUP_NONE),
        new EnchantDef(26, "loyalty",               3, 5,  false, 12, 7,  50, 0,  GROUP_LOYALTY),
        new EnchantDef(27, "impaling",              5, 2,  false, 1,  8,  21, 8,  GROUP_NONE),
        new EnchantDef(28, "riptide",               3, 2,  false, 17, 7,  50, 0,  GROUP_LOYALTY),
        new EnchantDef(29, "channeling",            1, 1,  false, 25, 0,  50, 0,  GROUP_CHANNELING),
        new EnchantDef(30, "multishot",             1, 2,  false, 20, 0,  50, 0,  GROUP_MULTISHOT),
        new EnchantDef(31, "quick_charge",          3, 5,  false, 12, 20, 50, 0,  GROUP_NONE),
        new EnchantDef(32, "piercing",              4, 10, false, 1,  10, 50, 0,  GROUP_MULTISHOT),
        new EnchantDef(33, "density",               5, 5,  false, 5,  8,  25, 8,  GROUP_DAMAGE),
        new EnchantDef(34, "breach",                4, 2,  false, 15, 9,  65, 9,  GROUP_DAMAGE),
        new EnchantDef(35, "lunge",                 3, 5,  false, 5,  8,  25, 8,  GROUP_NONE),
        // Treasure enchantments in tradeable
        new EnchantDef(36, "binding_curse",         1, 1,  true,  25, 0,  50, 0,  GROUP_NONE),
        new EnchantDef(37, "vanishing_curse",       1, 1,  true,  25, 0,  50, 0,  GROUP_NONE),
        new EnchantDef(38, "frost_walker",          2, 2,  true,  10, 10, 25, 10, GROUP_DEPTH),
        new EnchantDef(39, "mending",               1, 2,  true,  25, 25, 75, 25, GROUP_INFINITY),
    };

    public static final int TRADEABLE_COUNT = 40;

    // ==================== #minecraft:on_traded_equipment (36 enchantments) ====================
    // Same as #minecraft:non_treasure - the 36 non-treasure enchantments
    // These are used for EnchantWithLevels on equipment trades

    public static final EnchantDef[] ON_TRADED_EQUIPMENT = new EnchantDef[36];
    static {
        System.arraycopy(TRADEABLE, 0, ON_TRADED_EQUIPMENT, 0, 36);
    }
    public static final int ON_TRADED_EQUIPMENT_COUNT = 36;

    // ==================== #minecraft:double_trade_price ====================
    // = #minecraft:treasure. Of those in tradeable: binding_curse(36), vanishing_curse(37), frost_walker(38), mending(39)
    // IS_DOUBLE_PRICE[id] = true if this enchantment doubles the trade cost

    public static boolean isDoublePriceEnchant(int enchantId) {
        return enchantId >= 36 && enchantId <= 39;
    }

    // ==================== Item Enchantability Values ====================
    // From DataComponents.ENCHANTABLE

    public static final int ENCHANTABILITY_IRON_ARMOR = 9;      // iron helmet/chestplate/leggings/boots
    public static final int ENCHANTABILITY_DIAMOND_ARMOR = 10;   // diamond helmet/chestplate/leggings/boots
    public static final int ENCHANTABILITY_CHAINMAIL_ARMOR = 12; // chainmail
    public static final int ENCHANTABILITY_IRON_TOOL = 14;       // iron sword/axe/pickaxe/shovel/hoe
    public static final int ENCHANTABILITY_DIAMOND_TOOL = 10;    // diamond sword/axe/pickaxe/shovel/hoe
    public static final int ENCHANTABILITY_FISHING_ROD = 1;
    public static final int ENCHANTABILITY_BOW = 1;
    public static final int ENCHANTABILITY_CROSSBOW = 1;
    public static final int ENCHANTABILITY_BOOK = 1;             // books accept all enchantments

    /**
     * Get enchantability for a given item type used in trades.
     */
    public static int getEnchantability(String itemName) {
        switch (itemName) {
            case "iron_sword":
            case "iron_axe":
            case "iron_pickaxe":
            case "iron_shovel":
            case "iron_hoe":
                return ENCHANTABILITY_IRON_TOOL;
            case "diamond_sword":
            case "diamond_axe":
            case "diamond_pickaxe":
            case "diamond_shovel":
            case "diamond_hoe":
                return ENCHANTABILITY_DIAMOND_TOOL;
            case "diamond_helmet":
            case "diamond_chestplate":
            case "diamond_leggings":
            case "diamond_boots":
                return ENCHANTABILITY_DIAMOND_ARMOR;
            case "iron_helmet":
            case "iron_chestplate":
            case "iron_leggings":
            case "iron_boots":
                return ENCHANTABILITY_IRON_ARMOR;
            case "chainmail_helmet":
            case "chainmail_chestplate":
            case "chainmail_leggings":
            case "chainmail_boots":
                return ENCHANTABILITY_CHAINMAIL_ARMOR;
            case "fishing_rod":
                return ENCHANTABILITY_FISHING_ROD;
            case "bow":
                return ENCHANTABILITY_BOW;
            case "crossbow":
                return ENCHANTABILITY_CROSSBOW;
            default:
                return ENCHANTABILITY_BOOK;
        }
    }

    // ==================== Item-specific enchantment filtering ====================
    // For EnchantWithLevels, we need to know which enchantments can go on which items.
    // Since the tag is #on_traded_equipment (= non_treasure, 36 enchants),
    // and the item filter is `isPrimaryItem(itemStack) || isBook`,
    // we need to define which enchantments are primary for each item type.

    // Item categories for primary item matching
    public static final int CAT_SWORD = 1;
    public static final int CAT_AXE = 2;
    public static final int CAT_PICKAXE = 3;
    public static final int CAT_SHOVEL = 4;
    public static final int CAT_HOE = 5;
    public static final int CAT_BOW = 6;
    public static final int CAT_CROSSBOW = 7;
    public static final int CAT_FISHING_ROD = 8;
    public static final int CAT_HELMET = 9;
    public static final int CAT_CHESTPLATE = 10;
    public static final int CAT_LEGGINGS = 11;
    public static final int CAT_BOOTS = 12;

    public static int getItemCategory(String itemName) {
        if (itemName.contains("sword")) return CAT_SWORD;
        if (itemName.contains("axe") && !itemName.contains("pickaxe")) return CAT_AXE;
        if (itemName.contains("pickaxe")) return CAT_PICKAXE;
        if (itemName.contains("shovel")) return CAT_SHOVEL;
        if (itemName.contains("hoe")) return CAT_HOE;
        if (itemName.equals("bow")) return CAT_BOW;
        if (itemName.equals("crossbow")) return CAT_CROSSBOW;
        if (itemName.equals("fishing_rod")) return CAT_FISHING_ROD;
        if (itemName.contains("helmet")) return CAT_HELMET;
        if (itemName.contains("chestplate")) return CAT_CHESTPLATE;
        if (itemName.contains("leggings")) return CAT_LEGGINGS;
        if (itemName.contains("boots")) return CAT_BOOTS;
        return 0; // book or unknown
    }

    public static boolean canApplyTo(EnchantDef enchant, int itemCategory) {
        switch (enchant.name) {
            // Protection enchantments - armor only (no primary restriction)
            case "protection":
            case "fire_protection":
            case "blast_protection":
            case "projectile_protection":
                return itemCategory >= CAT_HELMET && itemCategory <= CAT_BOOTS;
            case "thorns":
                // primary = chest_armor only
                return itemCategory == CAT_CHESTPLATE;
            case "feather_falling":
                return itemCategory == CAT_BOOTS;
            case "respiration":
            case "aqua_affinity":
                return itemCategory == CAT_HELMET;
            case "depth_strider":
            case "frost_walker":
                return itemCategory == CAT_BOOTS;

            // Weapon enchantments - primary = melee_weapon (sword+spear)
            case "sharpness":
            case "smite":
            case "bane_of_arthropods":
                return itemCategory == CAT_SWORD;
            case "knockback":
            case "fire_aspect":
            case "looting":
            case "sweeping_edge":
                return itemCategory == CAT_SWORD;

            // Lunge - spears only (not traded)
            case "lunge":
                return false;

            // Tool enchantments - no primary restriction
            case "efficiency":
                return itemCategory == CAT_PICKAXE || itemCategory == CAT_SHOVEL
                    || itemCategory == CAT_AXE || itemCategory == CAT_HOE;
            case "silk_touch":
            case "fortune":
                return itemCategory == CAT_PICKAXE || itemCategory == CAT_SHOVEL
                    || itemCategory == CAT_AXE || itemCategory == CAT_HOE;

            // Bow enchantments
            case "power":
            case "punch":
            case "flame":
            case "infinity":
                return itemCategory == CAT_BOW;

            // Crossbow enchantments
            case "multishot":
            case "quick_charge":
            case "piercing":
                return itemCategory == CAT_CROSSBOW;

            // Fishing rod enchantments
            case "luck_of_the_sea":
            case "lure":
                return itemCategory == CAT_FISHING_ROD;

            // Trident enchantments - not traded
            case "loyalty":
            case "impaling":
            case "riptide":
            case "channeling":
                return false;

            // Mace enchantments - not traded
            case "density":
            case "breach":
                return false;

            // Unbreaking applies to everything with durability
            case "unbreaking":
                return true;

            default:
                return false;
        }
    }

    public static List<EnchantDef> getApplicableEnchantments(String itemName) {
        int category = getItemCategory(itemName);
        List<EnchantDef> result = new ArrayList<>();
        for (EnchantDef def : ON_TRADED_EQUIPMENT) {
            if (canApplyToItem(def, itemName, category)) {
                result.add(def);
            }
        }
        return result;
    }

    private static boolean canApplyToItem(EnchantDef enchant, String itemName, int category) {
        switch (enchant.name) {
            // Protection enchantments - armor only (no primary_items restriction)
            case "protection":
            case "fire_protection":
            case "blast_protection":
            case "projectile_protection":
                return category >= CAT_HELMET && category <= CAT_BOOTS;
            case "feather_falling":
                return category == CAT_BOOTS;
            case "respiration":
            case "aqua_affinity":
                return category == CAT_HELMET;
            case "thorns":
                // supported = all armor, primary = chest_armor only
                return category == CAT_CHESTPLATE;
            case "depth_strider":
                return category == CAT_BOOTS;

            // Melee weapon enchantments
            // sharpness: supported = sharp_weapon (sword+spear+axe), primary = melee_weapon (sword+spear)
            // smite/bane: supported = weapon (sword+spear+axe+mace), primary = melee_weapon (sword+spear)
            // Result: only sword passes isPrimaryItem (spear not traded)
            case "sharpness":
            case "smite":
            case "bane_of_arthropods":
                return category == CAT_SWORD;
            // knockback/looting: supported = melee_weapon (sword+spear), no primary restriction
            case "knockback":
            case "looting":
                return category == CAT_SWORD;
            // fire_aspect: supported = fire_aspect (sword+spear+mace), primary = melee_weapon (sword+spear)
            case "fire_aspect":
                return category == CAT_SWORD;
            // sweeping_edge: supported = sweeping (sword only), no primary restriction
            case "sweeping_edge":
                return category == CAT_SWORD;
            // lunge: supported = lunge (spears only) - not applicable to any traded item
            case "lunge":
                return false;

            // Mining enchantments - no primary_items restriction
            case "efficiency":
                return category == CAT_PICKAXE || category == CAT_SHOVEL
                    || category == CAT_AXE || category == CAT_HOE;
            case "silk_touch":
            case "fortune":
                return category == CAT_PICKAXE || category == CAT_SHOVEL
                    || category == CAT_AXE || category == CAT_HOE;

            // Bow enchantments - no primary_items restriction
            case "power":
            case "punch":
            case "flame":
            case "infinity":
                return category == CAT_BOW;

            // Crossbow enchantments - no primary_items restriction
            case "multishot":
            case "quick_charge":
            case "piercing":
                return category == CAT_CROSSBOW;

            // Fishing rod enchantments - no primary_items restriction
            case "luck_of_the_sea":
            case "lure":
                return category == CAT_FISHING_ROD;

            // Trident enchantments - not traded
            case "loyalty":
            case "impaling":
            case "riptide":
            case "channeling":
                return false;

            // Mace enchantments - not traded
            case "density":
            case "breach":
                return false;

            // Universal enchantments - no primary_items restriction
            case "unbreaking":
                return true; // applies to all items with durability

            default:
                return false;
        }
    }

    /**
     * Check if two enchantments are compatible (can coexist on same item).
     * Returns false if they are in the same exclusive group.
     */
    public static boolean areCompatible(EnchantDef a, EnchantDef b) {
        if (a.id == b.id) return false; // same enchantment
        if (a.exclusiveGroup == GROUP_NONE || b.exclusiveGroup == GROUP_NONE) return true;
        return a.exclusiveGroup != b.exclusiveGroup;
    }

    // ==================== Potion Data ====================
    // #minecraft:potion/tradeable - 41 potions

    public static final String[] TRADEABLE_POTIONS = {
        "wind_charged",
        "oozing",
        "infested",
        "weaving",
        "night_vision",
        "long_night_vision",
        "invisibility",
        "long_invisibility",
        "fire_resistance",
        "long_fire_resistance",
        "leaping",
        "long_leaping",
        "strong_leaping",
        "slowness",
        "long_slowness",
        "strong_slowness",
        "turtle_master",
        "long_turtle_master",
        "strong_turtle_master",
        "swiftness",
        "long_swiftness",
        "strong_swiftness",
        "water_breathing",
        "long_water_breathing",
        "healing",
        "strong_healing",
        "harming",
        "strong_harming",
        "poison",
        "long_poison",
        "strong_poison",
        "regeneration",
        "long_regeneration",
        "strong_regeneration",
        "strength",
        "long_strength",
        "strong_strength",
        "weakness",
        "long_weakness",
        "slow_falling",
        "long_slow_falling",
    };

    public static final int TRADEABLE_POTION_COUNT = 41;

    // ==================== Stew Effects ====================
    // Farmer level 4 suspicious stew effects (6 effects)

    public static final String[] STEW_EFFECTS = {
        "night_vision",   // 5 seconds (100 ticks)
        "jump_boost",     // 8 seconds (160 ticks)
        "weakness",       // 7 seconds (140 ticks)
        "blindness",      // 6 seconds (120 ticks)
        "poison",         // 14 seconds (280 ticks)
        "saturation",     // 7 seconds (140 ticks)
    };

    public static final int[] STEW_DURATIONS_SECONDS = {5, 8, 7, 6, 14, 7};
    public static final int STEW_EFFECT_COUNT = 6;

    // ==================== DyeColor ====================
    // DyeColor.VALUES order (16 colors)

    public static final String[] DYE_COLORS = {
        "white",       // 0
        "orange",      // 1
        "magenta",     // 2
        "light_blue",  // 3
        "yellow",      // 4
        "lime",        // 5
        "pink",        // 6
        "gray",        // 7
        "light_gray",  // 8
        "cyan",        // 9
        "purple",      // 10
        "blue",        // 11
        "brown",       // 12
        "green",       // 13
        "red",         // 14
        "black",       // 15
    };

    public static final int DYE_COLOR_COUNT = 16;
}
