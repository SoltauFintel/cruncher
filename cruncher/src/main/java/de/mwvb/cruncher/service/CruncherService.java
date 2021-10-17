package de.mwvb.cruncher.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class CruncherService {
    private final Path dir;
    private final File targetDir;
    
    public CruncherService(Path dir, Path targetDir) {
        this.dir = dir;
        this.targetDir = targetDir.toFile();
    }

    public void crunch() {
        try {
            CopiedFile.setRoot(dir, targetDir.toPath());
            CopiedFile.copied = new ArrayList<>();
            find();
            for (int i = 0; i < CopiedFile.copied.size(); i++) {
                CopiedFile f = CopiedFile.copied.get(i);
                f.copy(targetDir, i);
            }
            StringBuilder script = new StringBuilder();
            Set<String> md = new HashSet<>();
            for (int i = 0; i < CopiedFile.copied.size(); i++) {
                CopiedFile f = CopiedFile.copied.get(i);
                if (f.hasLink()) {
                    String targetFolder = f.getTargetFolder();
                    if (targetFolder != null && !md.contains(targetFolder)) {
                        script.append("md ");
                        script.append(targetFolder);
                        script.append("\r\n");
                        md.add(targetFolder);
                        // TODO Man könnte noch die md Befehle sortieren und an den Anfang stellen.
                    }
                    
                    script.append("copy ");
                    script.append(f.getSource());
                    script.append(" ");
                    script.append(f.getTarget());
                    script.append("\r\n");
                }
            }
            if (!script.toString().isEmpty()) {
                try (FileWriter w = new FileWriter(new File(targetDir, "uncrunch.bat"))) {
                    w.write(script.toString().replace("/", "\\"));
                }
            }
            CopiedFile.cleanup();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void find() throws IOException {
        Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                CopiedFile.copied.add(new CopiedFile(file));
                return super.visitFile(file, attrs);
            }
        });
    }
}
