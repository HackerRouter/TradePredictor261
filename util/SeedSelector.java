package util;

import io.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class SeedSelector {

    public static long select(ConfigManager config) {
        List<Long> seeds = config.getSeeds();

        if (seeds.isEmpty()) {
            // No seeds at all - require input
            System.out.println(I18n.get("seed.no_seed"));
            long seed = InputHelper.readLong(I18n.get("seed.enter_seed"), 0);
            if (seed == 0) {
                System.out.println(I18n.get("seed.invalid_seed"));
                return 0;
            }
            config.addSeed(seed);
            return seed;
        }

        if (seeds.size() == 1) {
            // Single seed - show and ask
            long existing = seeds.get(0);
            System.out.println(I18n.get("seed.current_seed", existing));
            List<String> options = new ArrayList<>();
            options.add(I18n.get("seed.use_seed", existing));
            options.add(I18n.get("seed.input_new"));
            int choice = InputHelper.selectFromMenu(I18n.get("seed.select_title"), options);
            if (choice == 0) {
                config.setActiveSeedIndex(0);
                return existing;
            } else {
                long seed = InputHelper.readLong(I18n.get("seed.enter_new"), 0);
                if (seed == 0) {
                    System.out.println(I18n.get("seed.invalid_using_existing"));
                    config.setActiveSeedIndex(0);
                    return existing;
                }
                config.addSeed(seed);
                return seed;
            }
        }

        // Multiple seeds - show list
        List<String> options = new ArrayList<>();
        int activeIdx = config.getActiveSeedIndex();
        for (int i = 0; i < seeds.size(); i++) {
            String label = String.valueOf(seeds.get(i));
            if (i == activeIdx) label += I18n.get("seed.active_marker");
            options.add(label);
        }
        options.add(I18n.get("seed.input_new"));

        int choice = InputHelper.selectFromMenu(I18n.get("seed.select_title"), options);
        if (choice < seeds.size()) {
            config.setActiveSeedIndex(choice);
            return seeds.get(choice);
        } else {
            long seed = InputHelper.readLong(I18n.get("seed.enter_new"), 0);
            if (seed == 0) {
                System.out.println(I18n.get("seed.invalid_using_active"));
                return config.getActiveSeed();
            }
            config.addSeed(seed);
            return seed;
        }
    }
}
