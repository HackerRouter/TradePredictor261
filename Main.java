import io.ConfigManager;
import mode.ModeGenerate;
import mode.ModeMatch;
import mode.ModeOptimalRoute;
import mode.ModePredict;
import util.I18n;
import util.InputHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        ConfigManager config = new ConfigManager();
        I18n.init(config.getLanguage());

        System.out.println(I18n.get("app.title"));
        System.out.println(I18n.get("app.subtitle"));
        System.out.println();

        while (true) {
            System.out.println("\n" + I18n.get("menu.title"));
            int choice = InputHelper.selectFromMenu(I18n.get("menu.select_mode"), Arrays.asList(
                I18n.get("menu.generate"),
                I18n.get("menu.match"),
                I18n.get("menu.predict"),
                I18n.get("menu.optimal_route"),
                I18n.get("menu.show_config"),
                I18n.get("menu.exit")
            ));

            switch (choice) {
                case 0:
                    ModeGenerate.run(config);
                    break;
                case 1:
                    ModeMatch.run(config);
                    break;
                case 2:
                    ModePredict.run(config);
                    break;
                case 3:
                    ModeOptimalRoute.run(config);
                    break;
                case 4:
                    showConfig(config);
                    break;
                case 5:
                    System.out.println(I18n.get("common.bye"));
                    return;
            }
        }
    }

    private static void showConfig(ConfigManager config) {
        System.out.println("\n" + I18n.get("config.title"));

        // Show seeds
        var seeds = config.getSeeds();
        if (seeds.isEmpty()) {
            System.out.println(I18n.get("config.no_seeds"));
        } else {
            System.out.println(I18n.get("config.seeds_title"));
            int activeIdx = config.getActiveSeedIndex();
            for (int i = 0; i < seeds.size(); i++) {
                String marker = (i == activeIdx) ? I18n.get("config.active_marker") : "";
                System.out.println("  [" + i + "] " + seeds.get(i) + marker);
            }
        }

        // Show language
        System.out.println(I18n.get("config.language_label", I18n.getLanguage()));

        // Show tracked indices for active seed
        var tracked = config.getTrackedForActiveSeed();
        if (tracked.isEmpty()) {
            System.out.println(I18n.get("config.no_tracked"));
        } else {
            System.out.println(I18n.get("config.tracked_title", config.getActiveSeed()));
            for (var entry : tracked.entrySet()) {
                System.out.println("  " + entry.getKey() + " = " + entry.getValue());
            }
        }

        // Config actions
        System.out.println();
        int action = InputHelper.selectFromMenu(I18n.get("config.actions_title"), Arrays.asList(
            I18n.get("config.change_language"),
            I18n.get("config.back")
        ));

        if (action == 0) {
            changeLanguage(config);
        }
    }

    private static void changeLanguage(ConfigManager config) {
        List<String> available = I18n.getAvailableLanguages();
        if (available.isEmpty()) {
            available = Arrays.asList("en", "zh");
        }
        int choice = InputHelper.selectFromMenu(I18n.get("config.select_language"), available);
        if (choice >= 0 && choice < available.size()) {
            String newLang = available.get(choice);
            config.setLanguage(newLang);
            I18n.init(newLang);
            System.out.println(I18n.get("config.language_changed", newLang));
        }
    }
}
