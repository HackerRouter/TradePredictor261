package mode;

import enchant.EnchantDef;
import gamedata.GameData;
import io.ConfigManager;
import trade.*;
import util.I18n;
import util.InputHelper;
import util.Names;
import util.ReferenceHelper;
import util.SeedSelector;

import java.util.*;

public class ModeOptimalRoute {

    // Target specification
    private static class Target {
        final int enchantIdx;
        final int level;
        final int maxCost;
        final String displayName;

        Target(int enchantIdx, int level, int maxCost) {
            this.enchantIdx = enchantIdx;
            this.level = level;
            this.maxCost = maxCost;
            EnchantDef def = GameData.get().enchant(enchantIdx);
            String levelStr = level > 0 ? " " + level : " (any lvl)";
            String costStr = maxCost > 0 ? " \u2264" + maxCost + "E" : "";
            this.displayName = Names.enchant(def.name) + levelStr + costStr;
        }
    }

    // Route assignment result
    private static class RouteResult {
        final int totalRefreshes;
        final int[] assignment;  // assignment[targetIdx] = levelIdx (0-3)
        final int[] positions;   // positions[targetIdx] = refresh position at assigned level

        RouteResult(int totalRefreshes, int[] assignment, int[] positions) {
            this.totalRefreshes = totalRefreshes;
            this.assignment = assignment;
            this.positions = positions;
        }
    }

    public static void run(ConfigManager config) {
        System.out.println("\n" + I18n.get("optimal.title") + "\n");

        // 1. Select seed
        Long selectedSeed = SeedSelector.select(config);
        if (selectedSeed == null) return;
        long seed = selectedSeed;

        // 2. Select sub-mode
        List<String> subModes = Arrays.asList(
            I18n.get("optimal.mode_a"),
            I18n.get("optimal.mode_b")
        );
        int subMode = InputHelper.selectFromMenu(I18n.get("optimal.select_submode"), subModes);

        // 3. Print enchantment reference
        System.out.println();
        ReferenceHelper.printReferences(TradePoolRegistry.getPool("librarian", 1));

        // 4. Input targets
        System.out.println(I18n.get("optimal.input_targets"));
        System.out.println(I18n.get("optimal.format_info"));
        System.out.println(I18n.get("optimal.enchant_idx_range"));
        System.out.println(I18n.get("optimal.level_info"));
        System.out.println(I18n.get("optimal.cost_info"));
        System.out.println(I18n.get("optimal.type_done") + "\n");

        List<Target> targets = new ArrayList<>();
        int targetNum = 1;
        int maxTargets = (subMode == 0) ? 4 : 20; // mode a: max 4, mode b: practical limit

        while (targets.size() < maxTargets) {
            System.out.print(I18n.get("optimal.target_prompt", targetNum));
            String input = InputHelper.readLine();
            if (input.equalsIgnoreCase("done") || input.isEmpty()) break;

            String[] parts = input.split(",");
            if (parts.length != 3) {
                System.out.println(I18n.get("optimal.invalid_format"));
                continue;
            }

            try {
                int enchantIdx = Integer.parseInt(parts[0].trim());
                int level = Integer.parseInt(parts[1].trim());
                int maxCost = Integer.parseInt(parts[2].trim());

                if (enchantIdx < 0 || enchantIdx >= GameData.get().enchantments.size()) {
                    System.out.println(I18n.get("optimal.invalid_enchant_idx"));
                    continue;
                }
                if (level < 0 || level > GameData.get().enchant(enchantIdx).maxLevel) {
                    System.out.println(I18n.get("optimal.invalid_level", GameData.get().enchant(enchantIdx).maxLevel));
                    continue;
                }

                targets.add(new Target(enchantIdx, level, maxCost));
                System.out.println(I18n.get("optimal.added", targets.get(targets.size() - 1).displayName));
                targetNum++;
            } catch (NumberFormatException e) {
                System.out.println(I18n.get("optimal.invalid_number"));
            }
        }

        if (targets.isEmpty()) {
            System.out.println(I18n.get("optimal.no_targets"));
            return;
        }

        if (subMode == 0 && targets.size() > 4) {
            System.out.println(I18n.get("optimal.mode_a_max_error"));
            return;
        }

        // 5. Input search depth
        int searchDepth = InputHelper.readInt(I18n.get("optimal.search_depth"), 1000);

        // 6. Input starting positions for each level
        int[] startPositions = new int[4]; // level 1-4 start positions

        // Check if any tracked librarian indices exist
        int[] trackedIndices = new int[4];
        boolean hasAnyTracked = false;
        for (int lvl = 1; lvl <= 4; lvl++) {
            String key = "librarian.level_" + lvl;
            trackedIndices[lvl - 1] = config.getTrackedIndex(key);
            if (trackedIndices[lvl - 1] >= 0) hasAnyTracked = true;
        }

        if (hasAnyTracked) {
            // Show tracked indices and ask if user wants to use them
            System.out.println("\n" + I18n.get("optimal.has_tracked"));
            for (int lvl = 1; lvl <= 4; lvl++) {
                if (trackedIndices[lvl - 1] >= 0) {
                    System.out.println(I18n.get("optimal.level_tracked", lvl, trackedIndices[lvl - 1]));
                }
            }
            if (InputHelper.askYesNo(I18n.get("optimal.use_tracked"))) {
                for (int lvl = 0; lvl < 4; lvl++) {
                    startPositions[lvl] = Math.max(0, trackedIndices[lvl]);
                }
            } else {
                // User wants to input manually
                System.out.println(I18n.get("optimal.enter_positions"));
                for (int lvl = 1; lvl <= 4; lvl++) {
                    int inputPos = InputHelper.readInt(I18n.get("optimal.level_start", lvl), 0);
                    startPositions[lvl - 1] = inputPos;
                    // Save to config: existing record -> ask overwrite; no record -> save directly
                    String key = "librarian.level_" + lvl;
                    if (inputPos > 0) {
                        int existing = trackedIndices[lvl - 1];
                        if (existing >= 0) {
                            if (inputPos != existing) {
                                if (InputHelper.askYesNo(I18n.get("config.overwrite_index", key, existing))) {
                                    config.setTrackedIndex(key, inputPos);
                                    System.out.println(I18n.get("config.index_saved", key, inputPos));
                                }
                            }
                        } else {
                            config.setTrackedIndex(key, inputPos);
                            System.out.println(I18n.get("config.index_saved", key, inputPos));
                        }
                    }
                }
            }
        } else {
            // No tracked indices - ask user to input each level
            System.out.println("\n" + I18n.get("optimal.enter_positions"));
            for (int lvl = 1; lvl <= 4; lvl++) {
                int inputPos = InputHelper.readInt(I18n.get("optimal.level_start", lvl), 0);
                startPositions[lvl - 1] = inputPos;
                // No existing record, save directly
                if (inputPos > 0) {
                    String key = "librarian.level_" + lvl;
                    config.setTrackedIndex(key, inputPos);
                    System.out.println(I18n.get("config.index_saved", key, inputPos));
                }
            }
        }

        // 7. Generate and search
        System.out.println("\n" + I18n.get("optimal.generating"));

        int[][] positionMap = new int[4][targets.size()];
        TradeResult[][] matchDetails = new TradeResult[4][targets.size()];

        for (int lvl = 0; lvl < 4; lvl++) {
            int startPos = startPositions[lvl];
            TradePool pool = TradePoolRegistry.getPool("librarian", lvl + 1);
            if (pool == null) {
                System.out.println(I18n.get("optimal.pool_not_found", lvl + 1));
                return;
            }

            System.out.println(I18n.get("optimal.searching_level", lvl + 1, startPos));

            TradeSimulator simulator = TradeSimulator.forPool(pool, seed, SimulationContext.DEFAULT);

            // Initialize all positions as not found
            for (int t = 0; t < targets.size(); t++) {
                positionMap[lvl][t] = -1;
            }

            simulator.skip(pool, startPos);

            // Search
            boolean[] found = new boolean[targets.size()];
            int foundCount = 0;

            for (int i = startPos + 1; i <= startPos + searchDepth; i++) {
                RefreshResult refresh = simulator.simulateRefresh(pool, i);

                // Check each unfound target
                for (int t = 0; t < targets.size(); t++) {
                    if (found[t]) continue;
                    Target target = targets.get(t);

                    // Find the ENCHANT_RANDOMLY result in this refresh
                    for (TradeResult trade : refresh.trades) {
                        if (trade.discarded) continue;
                        if (trade.enchantRandomlyResult != null) {
                            if (matchesTarget(trade, target)) {
                                positionMap[lvl][t] = i - startPos; // relative position from start
                                matchDetails[lvl][t] = trade;
                                found[t] = true;
                                foundCount++;
                                break;
                            }
                        }
                    }
                }

                if (foundCount == targets.size()) break; // all found at this level
            }
        }

        // 8. Print search results summary
        System.out.println("\n" + I18n.get("optimal.results_title"));
        System.out.println(I18n.get("optimal.results_header"));
        System.out.printf("%-30s | Lv1    | Lv2    | Lv3    | Lv4%n", I18n.get("optimal.target_header"));
        System.out.println("-------------------------------|--------|--------|--------|--------");
        for (int t = 0; t < targets.size(); t++) {
            System.out.printf("%-30s |", targets.get(t).displayName);
            for (int lvl = 0; lvl < 4; lvl++) {
                int pos = positionMap[lvl][t];
                if (pos < 0) {
                    System.out.printf(" %-6s |", I18n.get("optimal.na"));
                } else {
                    System.out.printf(" %-6d |", pos);
                }
            }
            System.out.println();
        }

        // 9. Optimize
        RouteResult best;
        if (subMode == 0) {
            best = optimizeModeA(targets, positionMap);
        } else {
            best = optimizeModeB(targets, positionMap);
        }

        // 10. Output results
        if (best == null) {
            System.out.println("\n" + I18n.get("optimal.no_valid_route"));
            System.out.println(I18n.get("optimal.try_increase"));
            return;
        }

        if (subMode == 0) {
            printResultModeA(best, targets, positionMap, matchDetails, startPositions);
        } else {
            printResultModeB(best, targets, positionMap, matchDetails);
        }
    }

    /**
     * Check if an enchant result matches a target specification.
     */
    private static boolean matchesTarget(TradeResult trade, Target target) {
        if (trade.enchantRandomlyResult.enchantIndex != target.enchantIdx) return false;
        if (target.level != 0 && trade.enchantRandomlyResult.level != target.level) return false;
        if (target.maxCost != 0 && trade.price > target.maxCost) return false;
        return true;
    }

    // ==================== Mode A Optimization ====================

    /**
     * Mode a: Each level gets at most 1 target. K targets, 4 levels.
     * Enumerate all permutations of assigning K targets to 4 levels (P(4,K) <= 24).
     * Total cost = sum of each target's position at its assigned level.
     */
    private static RouteResult optimizeModeA(List<Target> targets, int[][] positionMap) {
        int K = targets.size();
        int[] bestAssignment = null;
        int[] bestPositions = null;
        int bestTotal = Integer.MAX_VALUE;

        // Generate all permutations of choosing K levels from 4
        int[] levels = new int[K];
        if (enumerateModeA(targets, positionMap, levels, 0, new boolean[4], K, bestTotal) != Integer.MAX_VALUE) {
            // Use iterative approach instead
        }

        // Iterative enumeration
        bestTotal = Integer.MAX_VALUE;
        int[] currentLevels = new int[K];
        Arrays.fill(currentLevels, -1);

        bestTotal = findBestModeA(positionMap, K, currentLevels, 0, new boolean[4]);
        if (bestTotal == Integer.MAX_VALUE) return null;

        // Reconstruct best assignment
        bestAssignment = new int[K];
        bestPositions = new int[K];
        reconstructBestModeA(positionMap, K, bestAssignment, bestPositions, 0, new boolean[4], bestTotal);

        return new RouteResult(bestTotal, bestAssignment, bestPositions);
    }

    private static int findBestModeA(int[][] positionMap, int K, int[] assignment, int targetIdx, boolean[] usedLevels) {
        if (targetIdx == K) {
            return 0;
        }

        int best = Integer.MAX_VALUE;
        for (int lvl = 0; lvl < 4; lvl++) {
            if (usedLevels[lvl]) continue;
            int pos = positionMap[lvl][targetIdx];
            if (pos < 0) continue; // target not found at this level

            usedLevels[lvl] = true;
            assignment[targetIdx] = lvl;
            int rest = findBestModeA(positionMap, K, assignment, targetIdx + 1, usedLevels);
            if (rest != Integer.MAX_VALUE) {
                int total = pos + rest;
                if (total < best) {
                    best = total;
                }
            }
            usedLevels[lvl] = false;
        }
        return best;
    }

    private static boolean reconstructBestModeA(int[][] positionMap, int K, int[] assignment, int[] positions,
                                                 int targetIdx, boolean[] usedLevels, int targetTotal) {
        if (targetIdx == K) {
            return targetTotal == 0;
        }

        for (int lvl = 0; lvl < 4; lvl++) {
            if (usedLevels[lvl]) continue;
            int pos = positionMap[lvl][targetIdx];
            if (pos < 0) continue;
            if (pos > targetTotal) continue;

            usedLevels[lvl] = true;
            assignment[targetIdx] = lvl;
            positions[targetIdx] = pos;
            if (reconstructBestModeA(positionMap, K, assignment, positions, targetIdx + 1, usedLevels, targetTotal - pos)) {
                return true;
            }
            usedLevels[lvl] = false;
        }
        return false;
    }

    // Unused helper from initial approach
    private static int enumerateModeA(List<Target> targets, int[][] positionMap, int[] levels,
                                       int idx, boolean[] used, int K, int currentBest) {
        return Integer.MAX_VALUE;
    }

    // ==================== Mode B Optimization ====================

    /**
     * Mode b: Each level can have multiple targets.
     * Level cost = max(positions of all targets assigned to that level).
     * Total cost = sum of level costs.
     * Enumerate all 4^K assignments (K <= 8 -> 65536 max).
     */
    private static RouteResult optimizeModeB(List<Target> targets, int[][] positionMap) {
        int targetCount = targets.size();
        int[][] candidates = new int[4][];
        for (int lvl = 0; lvl < 4; lvl++) {
            TreeSet<Integer> values = new TreeSet<>();
            values.add(0);
            for (int t = 0; t < targetCount; t++) {
                if (positionMap[lvl][t] > 0) values.add(positionMap[lvl][t]);
            }
            candidates[lvl] = values.stream().mapToInt(Integer::intValue).toArray();
        }

        int bestTotal = Integer.MAX_VALUE;
        int[] bestThresholds = null;
        int[] thresholds = new int[4];
        for (int a : candidates[0]) {
            thresholds[0] = a;
            for (int b : candidates[1]) {
                thresholds[1] = b;
                for (int c : candidates[2]) {
                    thresholds[2] = c;
                    for (int d : candidates[3]) {
                        thresholds[3] = d;
                        int total = a + b + c + d;
                        if (total < bestTotal && coversAll(thresholds, positionMap, targetCount)) {
                            bestTotal = total;
                            bestThresholds = thresholds.clone();
                        }
                    }
                }
            }
        }
        if (bestThresholds == null) return null;

        int[] assignment = new int[targetCount];
        int[] positions = new int[targetCount];
        for (int t = 0; t < targetCount; t++) {
            int bestLevel = -1;
            for (int lvl = 0; lvl < 4; lvl++) {
                int pos = positionMap[lvl][t];
                if (pos > 0 && pos <= bestThresholds[lvl] && (bestLevel < 0 || pos < positionMap[bestLevel][t])) {
                    bestLevel = lvl;
                }
            }
            assignment[t] = bestLevel;
            positions[t] = positionMap[bestLevel][t];
        }
        int total = 0;
        for (int lvl = 0; lvl < 4; lvl++) {
            int maxPos = 0;
            for (int t = 0; t < targetCount; t++) {
                if (assignment[t] == lvl) maxPos = Math.max(maxPos, positions[t]);
            }
            total += maxPos;
        }
        return new RouteResult(total, assignment, positions);
    }

    private static boolean coversAll(int[] thresholds, int[][] positionMap, int targetCount) {
        for (int t = 0; t < targetCount; t++) {
            boolean covered = false;
            for (int lvl = 0; lvl < 4 && !covered; lvl++) {
                int pos = positionMap[lvl][t];
                covered = pos > 0 && pos <= thresholds[lvl];
            }
            if (!covered) return false;
        }
        return true;
    }

    // ==================== Output ====================

    private static void printResultModeA(RouteResult result, List<Target> targets,
                                          int[][] positionMap, TradeResult[][] matchDetails,
                                          int[] startPositions) {
        System.out.println("\n" + I18n.get("optimal.result_a_title"));
        System.out.println(I18n.get("optimal.total_refreshes", result.totalRefreshes));
        System.out.println();

        // Group by level
        for (int lvl = 0; lvl < 4; lvl++) {
            int assignedTarget = -1;
            for (int t = 0; t < targets.size(); t++) {
                if (result.assignment[t] == lvl) {
                    assignedTarget = t;
                    break;
                }
            }

            if (assignedTarget < 0) {
                System.out.println(I18n.get("optimal.level_no_target", lvl + 1));
            } else {
                int pos = result.positions[assignedTarget];
                System.out.println(I18n.get("optimal.level_refresh", lvl + 1, pos));
                TradeResult detail = matchDetails[lvl][assignedTarget];
                String detailStr = (detail != null) ? detail.getFullDescription() : targets.get(assignedTarget).displayName;
                System.out.println(I18n.get("optimal.obtain", detailStr));
            }
            System.out.println();
        }
    }

    private static void printResultModeB(RouteResult result, List<Target> targets,
                                          int[][] positionMap, TradeResult[][] matchDetails) {
        System.out.println("\n" + I18n.get("optimal.result_b_title"));
        System.out.println(I18n.get("optimal.total_refreshes", result.totalRefreshes));
        System.out.println();

        for (int lvl = 0; lvl < 4; lvl++) {
            // Collect targets assigned to this level
            List<int[]> levelTargets = new ArrayList<>(); // [targetIdx, position]
            for (int t = 0; t < targets.size(); t++) {
                if (result.assignment[t] == lvl) {
                    levelTargets.add(new int[]{t, result.positions[t]});
                }
            }

            if (levelTargets.isEmpty()) {
                System.out.println(I18n.get("optimal.level_no_target", lvl + 1));
            } else {
                // Sort by position
                levelTargets.sort(Comparator.comparingInt(a -> a[1]));

                int maxPos = levelTargets.get(levelTargets.size() - 1)[1];
                System.out.println(I18n.get("optimal.level_refresh_targets", lvl + 1, maxPos, levelTargets.size()));

                int prevPos = 0;
                for (int[] lt : levelTargets) {
                    int targetIdx = lt[0];
                    int pos = lt[1];
                    int increment = pos - prevPos;

                    TradeResult detail = matchDetails[lvl][targetIdx];
                    String detailStr = (detail != null) ? detail.getFullDescription() : targets.get(targetIdx).displayName;

                    if (prevPos == 0) {
                        System.out.println(I18n.get("optimal.refresh_obtain", pos, detailStr));
                    } else {
                        System.out.println(I18n.get("optimal.more_refresh_obtain", increment, detailStr));
                    }
                    prevPos = pos;
                }
            }
            System.out.println();
        }
    }
}
