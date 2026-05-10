package io;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class BackupManager {

    private static final String BACKUP_DIR = "backup";


    public static String backup(long seed, String profession, int level) throws IOException {
        String srcPath = TradeFileIO.getFilePath(seed, profession, level);
        Path src = Paths.get(srcPath);
        if (!Files.exists(src)) return null;

        Path backupDir = Paths.get(BACKUP_DIR);
        if (!Files.exists(backupDir)) {
            Files.createDirectories(backupDir);
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String backupName = seed + "_" + profession + ".level_" + level + "." + timestamp;
        Path dest = backupDir.resolve(backupName);

        Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
        return dest.toString();
    }
}
