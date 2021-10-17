package de.mwvb.cruncher.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class CopiedFile {
    public static String root;
    public static String rootTarget;
    public static List<CopiedFile> copied;
    
    private final Path source;
    private final String shortName;
    
    private CopiedFile link;
    private Path target;
    
    public CopiedFile(Path file) {
        String filename = file.toFile().getAbsolutePath().replace("\\", "/");
        source = Paths.get(filename);
        shortName = filename.substring(root.length() + 1);
    }
    
    public String getShortName() {
        return shortName;
    }
    
    public String getLastName() {
        int o = shortName.lastIndexOf("/");
        if (o >= 0) {
            return shortName.substring(o + 1);
        }
        return shortName;
    }
    
    public long length() {
        return source.toFile().length();
    }

    public static void setRoot(Path rootSourceDir, Path rootTargetDir) {
        root = rootSourceDir.toFile().getAbsolutePath().replace("\\", "/");
        rootTarget = rootTargetDir.toFile().getAbsolutePath().replace("\\", "/");
    }
    
    public void copy(File targetDir, int index) {
        link = null;
        File target0 = new File(targetDir, shortName);
        target = target0.toPath();
        for (int i = 0; i < index; i++) {
            CopiedFile j = copied.get(i);
            if (getLastName().equals(j.getLastName())) {
                if (source.toFile().length() == j.length()) {
                    link = j;
                    return;
                }
            }
        }
        try {
            target0.getParentFile().mkdirs();
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    /** FOR SCRIPT */
    public boolean hasLink() {
        return link != null;
    }
    
    /** FOR SCRIPT */
    public String getSource() {
        return link.getTarget();
    }
    
    /** FOR SCRIPT */
    public String getTarget() {
        return target.toFile().getAbsolutePath().replace("\\", "/").substring(rootTarget.length() +1 );
    }

    public static void cleanup() {
        root = null;
        rootTarget = null;
        copied = null;
    }

    public String getTargetFolder() {
        String t = getTarget();
        int o = t.lastIndexOf("/");
        if (o >= 0) {
            return t.substring(0, o);
        }
        return null;
    }
}
