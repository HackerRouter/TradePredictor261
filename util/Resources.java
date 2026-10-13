package util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Resources {

    public static String readText(String path) throws IOException {
        try (InputStream in = Resources.class.getResourceAsStream("/" + path)) {
            if (in != null) {
                return stripBom(new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        Path file = Paths.get(path);
        if (Files.exists(file)) {
            return stripBom(new String(Files.readAllBytes(file), StandardCharsets.UTF_8));
        }
        throw new IOException("Resource not found: " + path);
    }

    public static Set<String> listFiles(String dir, String suffix) {
        Set<String> names = new TreeSet<>();
        try {
            Path codeSource = Paths.get(Resources.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isRegularFile(codeSource)) {
                try (ZipFile zip = new ZipFile(codeSource.toFile())) {
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements()) {
                        String name = entries.nextElement().getName();
                        if (name.startsWith(dir + "/") && name.endsWith(suffix) && name.indexOf('/', dir.length() + 1) < 0) {
                            names.add(name.substring(dir.length() + 1));
                        }
                    }
                }
            } else {
                addFromDirectory(codeSource.resolve(dir), suffix, names);
            }
        } catch (IOException | URISyntaxException | SecurityException ignored) {
        }
        addFromDirectory(Paths.get(dir), suffix, names);
        return names;
    }

    private static void addFromDirectory(Path dir, String suffix, Set<String> names) {
        if (!Files.isDirectory(dir)) return;
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*" + suffix)) {
            for (Path entry : stream) {
                names.add(entry.getFileName().toString());
            }
        } catch (IOException ignored) {
        }
    }

    private static String stripBom(String content) {
        return content.startsWith("﻿") ? content.substring(1) : content;
    }
}
