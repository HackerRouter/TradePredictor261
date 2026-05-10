package util;

import enchant.EnchantData;
import trade.TradeEntry;
import trade.TradePool;

public class ReferenceHelper {

    public static void printReferences(TradePool pool) {
        boolean hasEnchantRandomly = false;
        boolean hasEnchantWithLevels = false;
        boolean hasSetRandomDyes = false;
        boolean hasSetStewEffect = false;
        boolean hasSetRandomPotion = false;

        for (TradeEntry entry : pool.entries) {
            switch (entry.type) {
                case ENCHANT_RANDOMLY: hasEnchantRandomly = true; break;
                case ENCHANT_WITH_LEVELS: hasEnchantWithLevels = true; break;
                case SET_RANDOM_DYES: hasSetRandomDyes = true; break;
                case SET_STEW_EFFECT: hasSetStewEffect = true; break;
                case SET_RANDOM_POTION: hasSetRandomPotion = true; break;
                default: break;
            }
        }

        if (hasEnchantRandomly) {
            printEnchantmentReference();
        }
        if (hasEnchantWithLevels) {
            printEnchantWithLevelsReference(pool);
        }
        if (hasSetStewEffect) {
            printStewEffectReference();
        }
        if (hasSetRandomPotion) {
            printPotionReference();
        }
        if (hasSetRandomDyes) {
            printDyeColorReference();
        }
    }

    private static void printEnchantmentReference() {
        System.out.println("\n" + I18n.get("ref.enchant_title"));
        System.out.println(I18n.get("ref.enchant_header"));
        System.out.println(I18n.get("ref.enchant_separator"));
        for (int i = 0; i < EnchantData.TRADEABLE_COUNT; i++) {
            EnchantData.EnchantDef def = EnchantData.TRADEABLE[i];
            String displayName = I18n.translateWithOriginal("enchant", def.name);
            System.out.printf("%3d | %-30s | %4d   | %4d   | %s%n",
                i, displayName, def.maxLevel, def.weight,
                def.isTreasure ? I18n.get("ref.enchant_treasure") : "");
        }
        System.out.println();
    }

    private static void printEnchantWithLevelsReference(TradePool pool) {
        System.out.println("\n" + I18n.get("ref.equipment_title"));
        System.out.println(I18n.get("ref.equipment_header"));
        System.out.println(I18n.get("ref.equipment_separator"));
        for (TradeEntry entry : pool.entries) {
            if (entry.type == TradeEntry.TradeType.ENCHANT_WITH_LEVELS) {
                String item = entry.itemName;
                int ench = EnchantData.getEnchantability(item);
                java.util.List<EnchantData.EnchantDef> applicable = EnchantData.getApplicableEnchantments(item);
                StringBuilder names = new StringBuilder();
                for (int i = 0; i < applicable.size(); i++) {
                    if (i > 0) names.append(", ");
                    names.append(I18n.translateId("enchant", applicable.get(i).name));
                }
                System.out.printf("%-18s | %14d | %s%n", item, ench, names);
            }
        }
        System.out.println(I18n.get("ref.equipment_note1"));
        System.out.println(I18n.get("ref.equipment_note2"));
        System.out.println(I18n.get("ref.equipment_note3"));
        System.out.println(I18n.get("ref.equipment_note4"));

        // Print the ON_TRADED_EQUIPMENT index table
        System.out.println("\n" + I18n.get("ref.equipment_enchant_title"));
        System.out.println(I18n.get("ref.equipment_enchant_header"));
        System.out.println(I18n.get("ref.equipment_enchant_separator"));
        for (int i = 0; i < EnchantData.ON_TRADED_EQUIPMENT_COUNT; i++) {
            EnchantData.EnchantDef def = EnchantData.ON_TRADED_EQUIPMENT[i];
            String displayName = I18n.translateWithOriginal("enchant", def.name);
            System.out.printf("%3d | %-30s | %4d   | %4d%n",
                i, displayName, def.maxLevel, def.weight);
        }
        System.out.println();
    }

    private static void printStewEffectReference() {
        System.out.println("\n" + I18n.get("ref.stew_title"));
        System.out.println(I18n.get("ref.stew_header"));
        System.out.println(I18n.get("ref.stew_separator"));
        for (int i = 0; i < EnchantData.STEW_EFFECT_COUNT; i++) {
            String displayName = I18n.translateWithOriginal("stew", EnchantData.STEW_EFFECTS[i]);
            System.out.printf("%3d | %-22s | %ds%n",
                i, displayName, EnchantData.STEW_DURATIONS_SECONDS[i]);
        }
        System.out.println();
    }

    private static void printPotionReference() {
        System.out.println("\n" + I18n.get("ref.potion_title"));
        System.out.println(I18n.get("ref.potion_header"));
        System.out.println(I18n.get("ref.potion_separator"));
        for (int i = 0; i < EnchantData.TRADEABLE_POTION_COUNT; i++) {
            String displayName = I18n.translateWithOriginal("potion", EnchantData.TRADEABLE_POTIONS[i]);
            System.out.printf("%3d | %s%n", i, displayName);
        }
        System.out.println();
    }

    private static void printDyeColorReference() {
        System.out.println("\n" + I18n.get("ref.dye_title"));
        System.out.println(I18n.get("ref.dye_header"));
        System.out.println(I18n.get("ref.dye_separator"));
        for (int i = 0; i < EnchantData.DYE_COLOR_COUNT; i++) {
            String displayName = I18n.translateWithOriginal("dye", EnchantData.DYE_COLORS[i]);
            System.out.printf("%3d | %s%n", i, displayName);
        }
        System.out.println(I18n.get("ref.dye_note1"));
        System.out.println(I18n.get("ref.dye_note2"));
        System.out.println();
    }
}
