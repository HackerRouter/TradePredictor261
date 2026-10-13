package util;

import io.ConfigManager;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;

public class SeedSelector {

    public static Long select(ConfigManager config) {
        List<Long> seeds = config.getSeeds();

        if (seeds.isEmpty()) {
            System.out.println(I18n.get("seed.no_seed"));
            OptionalLong seed = readSeed(I18n.get("seed.enter_seed"));
            if (seed.isEmpty()) {
                System.out.println(I18n.get("seed.invalid_seed"));
                return null;
            }
            config.addSeed(seed.getAsLong());
            return seed.getAsLong();
        }

        List<String> options = new ArrayList<>();
        int activeIdx = config.getActiveSeedIndex();
        for (int i = 0; i < seeds.size(); i++) {
            String label = seeds.size() == 1 ? I18n.get("seed.use_seed", seeds.get(i)) : String.valueOf(seeds.get(i));
            if (seeds.size() > 1 && i == activeIdx) label += I18n.get("seed.active_marker");
            options.add(label);
        }
        options.add(I18n.get("seed.input_new"));
        if (seeds.size() == 1) System.out.println(I18n.get("seed.current_seed", seeds.get(0)));

        int choice = InputHelper.selectFromMenu(I18n.get("seed.select_title"), options);
        if (choice < seeds.size()) {
            config.setActiveSeedIndex(choice);
            return seeds.get(choice);
        }
        OptionalLong seed = readSeed(I18n.get("seed.enter_new"));
        if (seed.isEmpty()) {
            int fallback = activeIdx >= 0 ? activeIdx : 0;
            System.out.println(I18n.get("seed.invalid_using_active"));
            config.setActiveSeedIndex(fallback);
            return seeds.get(fallback);
        }
        config.addSeed(seed.getAsLong());
        return seed.getAsLong();
    }

    private static OptionalLong readSeed(String prompt) {
        System.out.print(prompt + ": ");
        OptionalLong seed = parseSeed(InputHelper.readLine());
        if (seed.isPresent()) System.out.println(I18n.get("seed.parsed", seed.getAsLong()));
        return seed;
    }

    public static OptionalLong parseSeed(String input) {
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return OptionalLong.empty();
        try {
            return OptionalLong.of(Long.parseLong(trimmed));
        } catch (NumberFormatException e) {
            return OptionalLong.of(trimmed.hashCode());
        }
    }
}
