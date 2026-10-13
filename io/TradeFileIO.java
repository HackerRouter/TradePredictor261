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


    public static void write(long seed, String profession, int level, String header, List<RefreshResult> results) throws IOException {
        Path dir = Paths.get(DATA_DIR + "/" + seed);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }

        Path filePath = Paths.get(getFilePath(seed, profession, level));
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(filePath))) {
            writer.println("#" + header);
            writer.println("#refresh_idx|slot_idx|description");
            for (RefreshResult refresh : results) {
                for (String line : refresh.toOutputLines()) {
                    writer.println(line);
                }
            }
        }
    }
}
