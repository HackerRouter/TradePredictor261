package mode;

import enchant.EnchantData;
import enchant.EnchantRandomly;
import io.ConfigManager;
import rng.Xoroshiro128PlusPlus;
import trade.*;
import util.I18n;
import util.InputHelper;
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
            EnchantData.EnchantDef def = EnchantData.TRADEABLE[enchantIdx];
            String levelStr = level > 0 ? " " + level : " (any lvl)";
            String costStr = maxCost > 0 ? " \u2264" + maxCost + "E" : "";
            this.displayName = def.name + levelStr + costStr;
        }
    }

    // Result of searching one level for one target
    private static class SearchResult {
        final int targetIdx;
        final int levelIdx;      // 0-3 (representing level 1-4)
        final int position;      // refresh position (1-based) where target first appears, or -1 if not found
        final EnchantRandomly.Result matchDetail;

        SearchResult(int targetIdx, int levelIdx, int position, EnchantRandomly.Result matchDetail) {
            this.targetIdx = targetIdx;
            this.levelIdx = levelIdx;
            this.position = position;
            this.matchDetail = matchDetail;
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
        long seed = SeedSelector.select(config);
        if (seed == 0) return;

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

                if (enchantIdx < 0 || enchantIdx >= EnchantData.TRADEABLE_COUNT) {
                    System.out.println(I18n.get("optimal.invalid_enchant_idx"));
                    continue;
                }
                if (level < 0 || level > EnchantData.TRADEABLE[enchantIdx].maxLevel) {
                    System.out.println(I18n.get("optimal.invalid_level", EnchantData.TRADEABLE[enchantIdx].maxLevel));
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
        EnchantRandomly.Result[][] matchDetails = new EnchantRandomly.Result[4][targets.size()];

        for (int lvl = 0; lvl < 4; lvl++) {
            int startPos = startPositions[lvl];
            TradePool pool = TradePoolRegistry.getPool("librarian", lvl + 1);
            if (pool == null) {
                System.out.println(I18n.get("optimal.pool_not_found", lvl + 1));
                return;
            }

            System.out.println(I18n.get("optimal.searching_level", lvl + 1, startPos));

            Xoroshiro128PlusPlus rng = new Xoroshiro128PlusPlus(pool.identifier);
            rng.setSeed(seed);
            TradeSimulator simulator = new TradeSimulator(rng);

            // Initialize all positions as not found
            for (int t = 0; t < targets.size(); t++) {
                positionMap[lvl][t] = -1;
            }

            // Skip to start position
            for (int i = 1; i <= startPos; i++) {
                simulator.simulateRefresh(pool, i);
            }

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
                            if (matchesTarget(trade.enchantRandomlyResult, target)) {
                                positionMap[lvl][t] = i - startPos; // relative position from start
                                matchDetails[lvl][t] = trade.enchantRandomlyResult;
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
    private static boolean matchesTarget(EnchantRandomly.Result result, Target target) {
        if (result.enchantIndex != target.enchantIdx) return false;
        if (target.level != 0 && result.level != target.level) return false;
        if (target.maxCost != 0 && result.finalCost > target.maxCost) return false;
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
        int K = targets.size();

        if (K > 12) {
            System.out.println(I18n.get("optimal.warning_large_k", K, (int)Math.pow(4, K)));
        }

        int[] bestAssignment = null;
        int bestTotal = Integer.MAX_VALUE;

        // Enumerate all 4^K assignments
        int totalCombinations = (int) Math.pow(4, K);
        int[] assignment = new int[K];

        for (int combo = 0; combo < totalCombinations; combo++) {
            // Decode combo into assignment
            int temp = combo;
            boolean valid = true;
            for (int t = 0; t < K; t++) {
                assignment[t] = temp % 4;
                temp /= 4;
                // Check if target exists at assigned level
                if (positionMap[assignment[t]][t] < 0) {
                    valid = false;
                    break;
                }
            }
            if (!valid) continue;

            // Calculate total cost for this assignment
            int total = 0;
            for (int lvl = 0; lvl < 4; lvl++) {
                int maxPos = 0;
                for (int t = 0; t < K; t++) {
                    if (assignment[t] == lvl) {
                        maxPos = Math.max(maxPos, positionMap[lvl][t]);
                    }
                }
                total += maxPos;
            }

            if (total < bestTotal) {
                bestTotal = total;
                bestAssignment = assignment.clone();
            }
        }

        if (bestAssignment == null) return null;

        // Build positions array
        int[] positions = new int[K];
        for (int t = 0; t < K; t++) {
            positions[t] = positionMap[bestAssignment[t]][t];
        }

        return new RouteResult(bestTotal, bestAssignment, positions);
    }

    // ==================== Output ====================

    private static void printResultModeA(RouteResult result, List<Target> targets,
                                          int[][] positionMap, EnchantRandomly.Result[][] matchDetails,
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
                EnchantRandomly.Result detail = matchDetails[lvl][assignedTarget];
                String detailStr = (detail != null) ? detail.toString() : targets.get(assignedTarget).displayName;
                System.out.println(I18n.get("optimal.obtain", detailStr));
            }
            System.out.println();
        }
    }

    private static void printResultModeB(RouteResult result, List<Target> targets,
                                          int[][] positionMap, EnchantRandomly.Result[][] matchDetails) {
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

                    EnchantRandomly.Result detail = matchDetails[lvl][targetIdx];
                    String detailStr = (detail != null) ? detail.toString() : targets.get(targetIdx).displayName;

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
