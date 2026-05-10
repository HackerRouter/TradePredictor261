package io;

import trade.RefreshResult;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TradeFileIO {

    private static final String DATA_DIR = "data";

    public static String getFilePath(long seed, String profession, int level) {
        return DATA_DIR + "/" + seed + "/" + profession + ".level_" + level;
    }


    public static boolean exists(long seed, String profession, int level) {
        return Files.exists(Paths.get(getFilePath(seed, profession, level)));
    }


    public static void write(long seed, String profession, int level, List<RefreshResult> results) throws IOException {
        Path dir = Paths.get(DATA_DIR + "/" + seed);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path filePath = Paths.get(getFilePath(seed, profession, level));
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println("#refresh_idx|slot_idx|description");
            for (RefreshResult refresh : results) {
                for (String line : refresh.toOutputLines()) {
                    writer.println(line);
                }
            }
        }
    }

    public static List<String> readLines(long seed, String profession, int level) throws IOException {
        Path filePath = Paths.get(getFilePath(seed, profession, level));
        List<String> allLines = Files.readAllLines(filePath);
        List<String> dataLines = new ArrayList<>();
        for (String line : allLines) {
            if (!line.startsWith("#") && !line.trim().isEmpty()) {
                dataLines.add(line);
            }
        }
        return dataLines;
    }


    public static Map<Integer, List<String>> parseByRefresh(List<String> lines) {
        Map<Integer, List<String>> result = new LinkedHashMap<>();
        for (String line : lines) {
            String[] parts = line.split("\\|", 3);
            if (parts.length >= 3) {
                try {
                    int refreshIdx = Integer.parseInt(parts[0].trim());
                    String description = parts[2].trim();
                    result.computeIfAbsent(refreshIdx, k -> new ArrayList<>()).add(description);
                } catch (NumberFormatException e) {
                    // skip malformed lines
                }
            }
        }
        return result;
    }

    public static int getMaxRefreshIndex(long seed, String profession, int level) throws IOException {
        List<String> lines = readLines(seed, profession, level);
        int max = 0;
        for (String line : lines) {
            String[] parts = line.split("\\|", 2);
            if (parts.length >= 1) {
                try {
                    int idx = Integer.parseInt(parts[0].trim());
                    if (idx > max) max = idx;
                } catch (NumberFormatException e) {
                    // skip
                }
            }
        }
        return max;
    }
}
