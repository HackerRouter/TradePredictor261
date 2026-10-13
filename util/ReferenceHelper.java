package util;

import enchant.EnchantDef;
import gamedata.GameData;
import trade.Modifier;
import trade.TradeEntry;
import trade.TradePool;

import java.util.Set;
import java.util.TreeSet;

public class ReferenceHelper {

    public static void printReferences(TradePool pool) {
        GameData data = GameData.get();
        for (TradeEntry entry : pool.entries) {
            if (entry.type == TradeEntry.TradeType.ENCHANT_RANDOMLY) {
                printBookEnchantments(entry.modifier, data);
                break;
            }
        }
        if (pool.has(TradeEntry.TradeType.ENCHANT_WITH_LEVELS)) {
            printEquipment(pool, data);
        }
        for (TradeEntry entry : pool.entries) {
            if (entry.type == TradeEntry.TradeType.SET_STEW_EFFECT) printStewEffects(entry.modifier);
            if (entry.type == TradeEntry.TradeType.SET_RANDOM_POTION) printPotions(entry.modifier);
        }
        if (pool.has(TradeEntry.TradeType.SET_RANDOM_DYES)) {
            printDyeColors(data);
        }
    }

    public static void printBookEnchantments(Modifier modifier, GameData data) {
        System.out.println("\n" + I18n.get("ref.enchant_title", modifier.enchantOptions.size()));
        System.out.println(I18n.get("ref.enchant_header"));
        System.out.println(I18n.get("ref.enchant_separator"));
        for (int index : modifier.enchantOptions) {
            EnchantDef def = data.enchant(index);
            System.out.printf("%3d | %-30s | %4d   | %4d   | %s%n",
                index, I18n.translateWithOriginal("enchant", def.name), def.maxLevel, def.weight,
                modifier.doublePriceEnchants.contains(index) ? I18n.get("ref.enchant_double_price") : "");
        }
        System.out.println();
    }

    private static void printEquipment(TradePool pool, GameData data) {
        System.out.println("\n" + I18n.get("ref.equipment_title"));
        System.out.println(I18n.get("ref.equipment_header"));
        System.out.println(I18n.get("ref.equipment_separator"));
        Set<Integer> used = new TreeSet<>();
        for (TradeEntry entry : pool.entries) {
            if (entry.type != TradeEntry.TradeType.ENCHANT_WITH_LEVELS) continue;
            StringBuilder names = new StringBuilder();
            for (int index : entry.modifier.enchantOptions) {
                if (names.length() > 0) names.append(", ");
                names.append(Names.enchant(data.enchant(index).name));
                used.add(index);
            }
            System.out.printf("%-18s | %14d | %s%n", entry.givesItem, entry.modifier.enchantability, names);
        }
        System.out.println(I18n.get("ref.equipment_note1"));
        System.out.println(I18n.get("ref.equipment_note2"));
        System.out.println(I18n.get("ref.equipment_note3"));

        System.out.println("\n" + I18n.get("ref.equipment_enchant_title"));
        System.out.println(I18n.get("ref.equipment_enchant_header"));
        System.out.println(I18n.get("ref.equipment_enchant_separator"));
        for (int index : used) {
            EnchantDef def = data.enchant(index);
            System.out.printf("%3d | %-30s | %4d   | %4d%n", index, I18n.translateWithOriginal("enchant", def.name), def.maxLevel, def.weight);
        }
        System.out.println();
    }

    private static void printStewEffects(Modifier modifier) {
        System.out.println("\n" + I18n.get("ref.stew_title", modifier.stewEffects.size()));
        System.out.println(I18n.get("ref.stew_header"));
        System.out.println(I18n.get("ref.stew_separator"));
        for (int i = 0; i < modifier.stewEffects.size(); i++) {
            Modifier.StewEffect effect = modifier.stewEffects.get(i);
            System.out.printf("%3d | %-22s | %s%n", i, I18n.translateWithOriginal("stew", effect.effect), Names.ticks(effect.ticks));
        }
        System.out.println();
    }

    private static void printPotions(Modifier modifier) {
        System.out.println("\n" + I18n.get("ref.potion_title", modifier.potionOptions.size()));
        System.out.println(I18n.get("ref.potion_header"));
        System.out.println(I18n.get("ref.potion_separator"));
        for (int i = 0; i < modifier.potionOptions.size(); i++) {
            System.out.printf("%3d | %s%n", i, I18n.translateWithOriginal("potion", modifier.potionOptions.get(i)));
        }
        System.out.println();
    }

    private static void printDyeColors(GameData data) {
        System.out.println("\n" + I18n.get("ref.dye_title", data.dyeColors.size()));
        System.out.println(I18n.get("ref.dye_header"));
        System.out.println(I18n.get("ref.dye_separator"));
        for (int i = 0; i < data.dyeColors.size(); i++) {
            System.out.printf("%3d | %s%n", i, I18n.translateWithOriginal("dye", data.dyeColors.get(i)));
        }
        System.out.println(I18n.get("ref.dye_note1"));
        System.out.println(I18n.get("ref.dye_note2"));
        System.out.println();
    }
}
