package util;

import java.util.List;
import java.util.Scanner;

public class InputHelper {

    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine() {
        return scanner.nextLine().trim();
    }

    public static long readLong(String prompt, long defaultValue) {
        System.out.print(prompt + I18n.get("input.default", defaultValue));
        String input = readLine();
        if (input.isEmpty()) return defaultValue;
        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println(I18n.get("input.invalid_number_default", defaultValue));
            return defaultValue;
        }
    }

    public static int readInt(String prompt, int defaultValue) {
        System.out.print(prompt + I18n.get("input.default", defaultValue));
        String input = readLine();
        if (input.isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println(I18n.get("input.invalid_number_default", defaultValue));
            return defaultValue;
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt + ": ");
            String input = readLine();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println(I18n.get("input.invalid_number_retry"));
            }
        }
    }

    public static int selectFromMenu(String title, List<String> options) {
        System.out.println("\n" + title);
        for (int i = 0; i < options.size(); i++) {
            System.out.println("  [" + i + "] " + options.get(i));
        }
        while (true) {
            System.out.print(I18n.get("input.select_range", options.size() - 1));
            String input = readLine();
            try {
                int choice = Integer.parseInt(input);
                if (choice >= 0 && choice < options.size()) {
                    return choice;
                }
            } catch (NumberFormatException e) {
                // fall through
            }
            System.out.println(I18n.get("input.invalid_choice"));
        }
    }

    public static boolean askYesNo(String prompt) {
        System.out.print(prompt + I18n.get("input.yes_no_suffix"));
        String input = readLine().toLowerCase();
        return input.startsWith("y");
    }

    public static String selectProfession(List<String> professions) {
        List<String> displayNames = new java.util.ArrayList<>();
        for (String p : professions) {
            displayNames.add(I18n.translateWithOriginal("profession", p));
        }
        return professions.get(selectFromMenu(I18n.get("input.select_profession"), displayNames));
    }

    public static int selectLevel() {
        while (true) {
            System.out.print(I18n.get("input.select_level"));
            String input = readLine();
            try {
                int level = Integer.parseInt(input);
                if (level >= 1 && level <= 5) return level;
            } catch (NumberFormatException e) {
                // fall through
            }
            System.out.println(I18n.get("input.invalid_level"));
        }
    }
}
