package mode;

import io.BackupManager;
import io.ConfigManager;
import io.TradeFileIO;
import rng.Xoroshiro128PlusPlus;
import trade.*;
import util.I18n;
import util.InputHelper;
import util.SeedSelector;

import java.util.ArrayList;
import java.util.List;


public class ModeGenerate {

    public static void run(ConfigManager config) {
        System.out.println("\n" + I18n.get("generate.title") + "\n");

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

        // 3. Show pool info
        System.out.println("\n" + I18n.get("generate.pool_info", pool.getDisplayName(), pool.size(), pool.amount));
        pool.printTradeMenu();

        // 4. Get generation length
        int length = InputHelper.readInt(I18n.get("generate.num_refreshes"), 100);

        // 5. Check existing file
        if (TradeFileIO.exists(seed, profession, level)) {
            System.out.println("\n" + I18n.get("generate.file_exists_warning", pool.getDisplayName()));
            if (InputHelper.askYesNo(I18n.get("generate.backup_overwrite"))) {
                try {
                    String backupPath = BackupManager.backup(seed, profession, level);
                    if (backupPath != null) {
                        System.out.println(I18n.get("generate.backup_created", backupPath));
                    }
                } catch (Exception e) {
                    System.out.println(I18n.get("generate.backup_failed", e.getMessage()));
                }
            } else {
                System.out.println(I18n.get("generate.cancelled"));
                return;
            }
        }

        // 6. Generate
        System.out.println("\n" + I18n.get("generate.generating", length, pool.getDisplayName()));
        Xoroshiro128PlusPlus rng = new Xoroshiro128PlusPlus(pool.identifier);
        rng.setSeed(seed);
        TradeSimulator simulator = new TradeSimulator(rng);

        List<RefreshResult> results = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            RefreshResult refresh = simulator.simulateRefresh(pool, i);
            results.add(refresh);
        }

        // 7. Write to file
        try {
            TradeFileIO.write(seed, profession, level, results);
            System.out.println(I18n.get("generate.written_to", TradeFileIO.getFilePath(seed, profession, level)));
            System.out.println(I18n.get("generate.total_refreshes", length));
        } catch (Exception e) {
            System.out.println(I18n.get("generate.write_error", e.getMessage()));
            return;
        }

        // 8. Print first few results as preview
        System.out.println("\n" + I18n.get("generate.preview_title"));
        for (int i = 0; i < Math.min(5, results.size()); i++) {
            System.out.print(results.get(i));
        }
        System.out.println("...");
    }
}
