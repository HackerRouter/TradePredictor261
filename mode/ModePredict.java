package mode;

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

public class ModePredict {

    public static void run(ConfigManager config) {
        System.out.println("\n" + I18n.get("predict.title") + "\n");

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

        // 3. Get current index
        String key = profession + ".level_" + level;
        int currentIndex = config.getTrackedIndex(key);
        if (currentIndex < 0) {
            currentIndex = InputHelper.readInt(I18n.get("predict.enter_index"), 0);
            // No existing record, save directly
            if (currentIndex > 0) {
                config.setTrackedIndex(key, currentIndex);
                System.out.println(I18n.get("config.index_saved", key, currentIndex));
            }
        } else {
            System.out.println(I18n.get("predict.current_tracked", currentIndex));
            if (!InputHelper.askYesNo(I18n.get("predict.use_index"))) {
                int newIndex = InputHelper.readInt(I18n.get("predict.enter_index_prompt"), currentIndex);
                if (newIndex != currentIndex) {
                    // Record exists, ask to overwrite
                    if (InputHelper.askYesNo(I18n.get("config.overwrite_index", key, currentIndex))) {
                        config.setTrackedIndex(key, newIndex);
                        System.out.println(I18n.get("config.index_saved", key, newIndex));
                    }
                }
                currentIndex = newIndex;
            }
        }

        // 4. Show trade menu and reference tables, get targets
        pool.printTradeMenu();
        ReferenceHelper.printReferences(pool);
        System.out.println("\n" + I18n.get("predict.input_instructions"));
        System.out.println(I18n.get("predict.format_constant"));
        System.out.println(I18n.get("predict.format_enchant"));
        System.out.println(I18n.get("predict.level_any"));
        System.out.println(I18n.get("predict.type_done") + "\n");

        List<int[]> targets = new ArrayList<>();
        int targetNum = 1;
        while (true) {
            System.out.print(I18n.get("predict.target_prompt", targetNum));
            String input = InputHelper.readLine();
            if (input.equalsIgnoreCase("done") || input.isEmpty()) break;

            String[] parts = input.split(",");
            int[] target = new int[parts.length];
            try {
                for (int i = 0; i < parts.length; i++) {
                    target[i] = Integer.parseInt(parts[i].trim());
                }
                targets.add(target);
                targetNum++;
            } catch (NumberFormatException e) {
                System.out.println(I18n.get("predict.invalid_input"));
            }
        }

        if (targets.isEmpty()) {
            System.out.println(I18n.get("predict.no_targets"));
            return;
        }

        // 5. Search
        int searchLength = InputHelper.readInt(I18n.get("predict.search_length"), 1000);
        int startFrom = currentIndex + 1; // search from next refresh

        System.out.println("\n" + I18n.get("predict.searching", startFrom, searchLength));

        Xoroshiro128PlusPlus rng = new Xoroshiro128PlusPlus(pool.identifier);
        rng.setSeed(seed);
        TradeSimulator simulator = new TradeSimulator(rng);

        // Skip to startFrom position
        for (int i = 1; i < startFrom; i++) {
            simulator.simulateRefresh(pool, i);
        }

        // Search
        List<Integer> foundAt = new ArrayList<>();
        int maxResults = 10;

        for (int i = startFrom; i < startFrom + searchLength; i++) {
            RefreshResult refresh = simulator.simulateRefresh(pool, i);
            if (matchesAllTargets(refresh, targets, pool)) {
                foundAt.add(i);
                if (foundAt.size() >= maxResults) break;
            }
        }

        // 6. Report
        if (foundAt.isEmpty()) {
            System.out.println("\n" + I18n.get("predict.no_match", searchLength, startFrom));
            System.out.println(I18n.get("predict.try_increase"));
        } else {
            System.out.println("\n" + I18n.get("predict.results_title"));
            System.out.println(I18n.get("predict.found_count", foundAt.size()));
            for (int pos : foundAt) {
                int gap = pos - currentIndex;
                System.out.println(I18n.get("predict.refresh_gap", pos, gap));
            }

            // Show details of first match
            int firstMatch = foundAt.get(0);
            System.out.println("\n" + I18n.get("predict.first_match_details", firstMatch));
            Xoroshiro128PlusPlus rng2 = new Xoroshiro128PlusPlus(pool.identifier);
            rng2.setSeed(seed);
            TradeSimulator sim2 = new TradeSimulator(rng2);
            for (int i = 1; i < firstMatch; i++) {
                sim2.simulateRefresh(pool, i);
            }
            RefreshResult match = sim2.simulateRefresh(pool, firstMatch);
            System.out.print(match);
        }
    }

    /**
     * Check if a refresh contains all target trades.
     */
    private static boolean matchesAllTargets(RefreshResult refresh, List<int[]> targets, TradePool pool) {
        for (int[] target : targets) {
            if (!matchesTarget(refresh, target, pool)) return false;
        }
        return true;
    }

    /**
     * Check if a refresh contains a specific target trade.
     */
    private static boolean matchesTarget(RefreshResult refresh, int[] target, TradePool pool) {
        for (TradeResult trade : refresh.trades) {
            if (trade.discarded) continue;
            if (tradeMatchesTarget(trade, target)) return true;
        }
        return false;
    }

    /**
     * Check if a single trade matches a target specification.
     */
    private static boolean tradeMatchesTarget(TradeResult trade, int[] target) {
        // target[0] = trade index in pool
        if (trade.poolIndex != target[0]) return false;

        // For enchanted books: target = [tradeIdx, enchantIdx, level, maxCost]
        if (target.length >= 4 && trade.enchantRandomlyResult != null) {
            int enchantIdx = target[1];
            int targetLevel = target[2];
            int maxCost = target[3];

            if (trade.enchantRandomlyResult.enchantIndex != enchantIdx) return false;
            if (targetLevel != 0 && trade.enchantRandomlyResult.level != targetLevel) return false;
            if (maxCost != 0 && trade.enchantRandomlyResult.finalCost > maxCost) return false;
        }

        return true;
    }
}
