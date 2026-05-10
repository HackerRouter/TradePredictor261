package mode;

import enchant.EnchantWithLevels;
import io.ConfigManager;
import io.TradeFileIO;
import rng.Xoroshiro128PlusPlus;
import trade.*;
import util.I18n;
import util.InputHelper;
import util.ReferenceHelper;
import util.SeedSelector;

import java.util.ArrayList;
import java.util.List;

public class ModeMatch {

    public static void run(ConfigManager config) {
        System.out.println("\n" + I18n.get("match.title") + "\n");

        // 1. Select seed
        long seed = SeedSelector.select(config);
        if (seed == 0) return;

        // 2. Select profession and level
        String profession = InputHelper.selectProfession(TradePoolRegistry.getAllProfessions());
        int level = InputHelper.selectLevel();

        TradePool pool = TradePoolRegistry.getPool(profession, level);
        if (pool == null) {
            System.out.println(I18n.get("generate.pool_not_found", profession, level));
            return;
        }

        // 3. Show trade menu and reference tables
        pool.printTradeMenu();
        ReferenceHelper.printReferences(pool);

        // 4. Input observed trades
        System.out.println("\n" + I18n.get("match.input_instructions"));
        System.out.println(I18n.get("match.format_constant"));
        System.out.println(I18n.get("match.format_enchant"));
        System.out.println(I18n.get("match.format_equipment"));
        System.out.println(I18n.get("match.type_done") + "\n");

        List<int[][]> observedRefreshes = new ArrayList<>();
        int refreshNum = 1;

        while (true) {
            int[][] refresh = new int[pool.amount][];
            boolean valid = true;

            for (int slot = 0; slot < pool.amount; slot++) {
                System.out.print(I18n.get("match.refresh_slot", refreshNum, slot + 1));
                String input = InputHelper.readLine();
                if (input.equalsIgnoreCase("done")) {
                    if (slot == 0) {
                        valid = false;
                        break;
                    }
                    // partial refresh not allowed
                    System.out.println(I18n.get("match.complete_refresh"));
                    slot--;
                    continue;
                }
                if (input.isEmpty()) {
                    valid = false;
                    break;
                }

                String[] parts = input.split(",");
                int[] slotData = new int[parts.length];
                try {
                    for (int i = 0; i < parts.length; i++) {
                        slotData[i] = Integer.parseInt(parts[i].trim());
                    }
                } catch (NumberFormatException e) {
                    if (parts[0].trim().equalsIgnoreCase("done")) {
                        valid = false;
                        break;
                    }
                    System.out.println(I18n.get("match.invalid_input"));
                    slot--;
                    continue;
                }
                refresh[slot] = slotData;
            }

            if (!valid) break;
            observedRefreshes.add(refresh);
            refreshNum++;
        }

        if (observedRefreshes.isEmpty()) {
            System.out.println(I18n.get("match.no_observations"));
            return;
        }

        System.out.println("\n" + I18n.get("match.observed_count", observedRefreshes.size()));

        // 5. Generate or load data
        int searchLength = 1000;
        if (TradeFileIO.exists(seed, profession, level)) {
            if (InputHelper.askYesNo(I18n.get("match.data_exists_use"))) {
                // use existing
                try {
                    int maxIdx = TradeFileIO.getMaxRefreshIndex(seed, profession, level);
                    searchLength = maxIdx;
                    System.out.println(I18n.get("match.loaded_data", maxIdx));
                } catch (Exception e) {
                    System.out.println(I18n.get("match.error_reading", e.getMessage()));
                    return;
                }
            } else {
                searchLength = InputHelper.readInt(I18n.get("match.generate_how_many"), 1000);
            }
        } else {
            searchLength = InputHelper.readInt(I18n.get("match.no_data_generate"), 1000);
        }

        // 6. Generate sequence
        System.out.println(I18n.get("match.generating", searchLength));
        Xoroshiro128PlusPlus rng = new Xoroshiro128PlusPlus(pool.identifier);
        rng.setSeed(seed);
        TradeSimulator simulator = new TradeSimulator(rng);

        List<RefreshResult> generated = new ArrayList<>();
        for (int i = 1; i <= searchLength; i++) {
            generated.add(simulator.simulateRefresh(pool, i));
        }

        // Save to file
        try {
            TradeFileIO.write(seed, profession, level, generated);
        } catch (Exception e) {
            System.out.println("Warning: Could not save data file: " + e.getMessage());
        }

        // 7. Sliding window match
        System.out.println(I18n.get("match.searching") + "\n");
        List<Integer> matches = findMatches(observedRefreshes, generated, pool);

        // 8. Report results
        if (matches.isEmpty()) {
            System.out.println(I18n.get("match.no_match", searchLength));
            System.out.println(I18n.get("match.possible_causes"));
        } else if (matches.size() == 1) {
            int matchPos = matches.get(0);
            System.out.println(I18n.get("match.unique_match", matchPos));
            int currentIndex = matchPos + observedRefreshes.size() - 1;
            System.out.println(I18n.get("match.current_index", currentIndex));

            String key = profession + ".level_" + level;
            config.setTrackedIndex(key, currentIndex);
            System.out.println(I18n.get("match.saved_to_config", key, currentIndex));
        } else {
            System.out.println(I18n.get("match.multiple_matches", matches.size()));
            for (int pos : matches) {
                System.out.println(I18n.get("match.match_at", pos));
            }
            System.out.println(I18n.get("match.need_more"));
        }
    }

    private static List<Integer> findMatches(List<int[][]> observed, List<RefreshResult> generated, TradePool pool) {
        List<Integer> matches = new ArrayList<>();
        int windowSize = observed.size();

        for (int startIdx = 0; startIdx <= generated.size() - windowSize; startIdx++) {
            boolean allMatch = true;
            for (int offset = 0; offset < windowSize; offset++) {
                if (!refreshMatches(observed.get(offset), generated.get(startIdx + offset), pool)) {
                    allMatch = false;
                    break;
                }
            }
            if (allMatch) {
                matches.add(startIdx + 1); // 1-based
            }
        }
        return matches;
    }

    private static boolean refreshMatches(int[][] observed, RefreshResult generated, TradePool pool) {
        // Get non-discarded trades from generated
        List<TradeResult> genTrades = new ArrayList<>();
        for (TradeResult t : generated.trades) {
            if (!t.discarded) genTrades.add(t);
        }

        if (observed.length > genTrades.size()) return false;

        for (int slot = 0; slot < observed.length; slot++) {
            if (observed[slot] == null) continue;
            int[] obs = observed[slot];
            if (slot >= genTrades.size()) return false;
            TradeResult gen = genTrades.get(slot);

            // obs[0] = trade index in pool
            if (obs[0] != gen.poolIndex) return false;

            // For enchanted books (ENCHANT_RANDOMLY): obs = [tradeIdx, enchantIdx, level, cost]
            if (obs.length == 4 && gen.enchantRandomlyResult != null) {
                if (obs[1] != gen.enchantRandomlyResult.enchantIndex) return false;
                if (obs[2] != 0 && obs[2] != gen.enchantRandomlyResult.level) return false;
                if (obs[3] != 0 && obs[3] != gen.enchantRandomlyResult.finalCost) return false;
            }

            if (obs.length >= 4 && gen.enchantWithLevelsResult != null) {
                if (gen.enchantWithLevelsResult.empty) return false;

                List<EnchantWithLevels.EnchantInstance> genEnchants = gen.enchantWithLevelsResult.enchantments;
                int totalCostField;
                int enchantPairCount;

                if (obs.length == 4) {
                    // Format A: [tradeIdx, enchantIdx, level, totalCost]
                    enchantPairCount = 1;
                    totalCostField = obs[3];
                    // Match first enchantment
                    if (obs[1] != genEnchants.get(0).enchantIndex) return false;
                    if (obs[2] != 0 && obs[2] != genEnchants.get(0).level) return false;
                } else {
                    totalCostField = obs[obs.length - 1];
                    enchantPairCount = (obs.length - 2) / 2; // subtract tradeIdx and totalCost, divide by 2

                    // Must have exact number of enchantments
                    if (enchantPairCount != genEnchants.size()) return false;

                    // Match each enchantment in order
                    for (int i = 0; i < enchantPairCount; i++) {
                        int eIdx = obs[1 + i * 2];
                        int eLvl = obs[2 + i * 2];
                        if (i >= genEnchants.size()) return false;
                        if (eIdx != genEnchants.get(i).enchantIndex) return false;
                        if (eLvl != 0 && eLvl != genEnchants.get(i).level) return false;
                    }
                }

                // Match total cost (if provided, 0 = any)
                if (totalCostField != 0) {
                    int baseCost = TradeResult.parseBaseCost(gen.tradeName);
                    int totalCost = baseCost + gen.enchantWithLevelsResult.enchantmentCost;
                    if (totalCostField != totalCost) return false;
                }
            }
        }
        return true;
    }
}
