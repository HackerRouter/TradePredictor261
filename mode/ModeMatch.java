package mode;

import enchant.EnchantWithLevels;
import gamedata.GameData;
import io.ConfigManager;
import io.TradeFileIO;
import trade.*;
import util.ContextSelector;
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
        SimulationContext context = ContextSelector.select(pool);
        ReferenceHelper.printReferences(pool);

        // 4. Input observed trades
        System.out.println("\n" + I18n.get("match.input_instructions"));
        System.out.println(I18n.get("match.format_constant"));
        System.out.println(I18n.get("match.format_enchant"));
        System.out.println(I18n.get("match.format_equipment"));
        System.out.println(I18n.get("match.cost_note"));
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
        TradeSimulator simulator = TradeSimulator.forPool(pool, seed, context);

        List<RefreshResult> generated = new ArrayList<>();
        for (int i = 1; i <= searchLength; i++) {
            generated.add(simulator.simulateRefresh(pool, i));
        }

        // Save to file
        try {
            String header = "game_version=" + GameData.get().version + " seed=" + seed + " pool=" + pool.getDisplayName() + " " + context.describe();
            TradeFileIO.write(seed, profession, level, header.trim(), generated);
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
        List<TradeResult> offers = generated.getOffers();
        if (observed.length > offers.size()) return false;

        for (int slot = 0; slot < observed.length; slot++) {
            int[] obs = observed[slot];
            if (obs == null) continue;
            TradeResult gen = offers.get(slot);
            if (obs[0] != gen.poolIndex) return false;

            if (gen.enchantRandomlyResult != null && obs.length == 4) {
                if (obs[1] != gen.enchantRandomlyResult.enchantIndex) return false;
                if (obs[2] != 0 && obs[2] != gen.enchantRandomlyResult.level) return false;
                if (obs[3] != 0 && obs[3] != gen.price) return false;
            }

            if (gen.enchantWithLevelsResult != null && obs.length >= 4) {
                if (!enchantmentsMatch(obs, gen.enchantWithLevelsResult.enchantments)) return false;
                int totalCost = obs[obs.length - 1];
                if (totalCost != 0 && totalCost != gen.price) return false;
            }
        }
        return true;
    }

    private static boolean enchantmentsMatch(int[] obs, List<EnchantWithLevels.EnchantInstance> generated) {
        int pairCount = (obs.length - 2) / 2;
        if ((obs.length - 2) % 2 != 0 || pairCount != generated.size()) return false;
        boolean[] used = new boolean[generated.size()];
        for (int i = 0; i < pairCount; i++) {
            int enchantIdx = obs[1 + i * 2];
            int level = obs[2 + i * 2];
            boolean found = false;
            for (int j = 0; j < generated.size(); j++) {
                EnchantWithLevels.EnchantInstance enchant = generated.get(j);
                if (!used[j] && enchant.enchantIndex() == enchantIdx && (level == 0 || level == enchant.level)) {
                    used[j] = true;
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
