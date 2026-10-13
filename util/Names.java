package util;

public class Names {

    public static String of(String prefix, String id) {
        String key = prefix + "." + id;
        return I18n.has(key) ? I18n.get(key) : titleCase(id);
    }

    public static String item(String id) {
        return of("item", id);
    }

    public static String enchant(String id) {
        return of("enchant", id);
    }

    public static String titleCase(String id) {
        StringBuilder sb = new StringBuilder();
        for (String word : id.split("_")) {
            if (word.isEmpty()) continue;
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return sb.toString();
    }

    public static String ticks(int ticks) {
        if (ticks % 20 == 0) return (ticks / 20) + "s";
        return ticks + " ticks";
    }
}
