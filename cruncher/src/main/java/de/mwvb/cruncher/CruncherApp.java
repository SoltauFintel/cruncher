package de.mwvb.cruncher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import de.mwvb.cruncher.service.CruncherService;

public class CruncherApp {

    /**
     * @param args 0: source dir, 1: target dir, 2: config file for exclusions (optional)
     */
    public static void main(String[] args) {
        if (args.length >= 3) {
            new CruncherApp().start(args[0], args[1], args[2]);
        } else if (args.length == 2) {
            new CruncherApp().start(args[0], args[1], "-");
        } else {
            System.out.println("Please specify cruncher command line arguments: sourceDir targetDir [configFile]");
        }
    }

    public void start(String dir, String targetDir, String configFile) {
        File cf = null;
        if (configFile != null && !configFile.isEmpty() && !"-".equals(configFile)) {
            cf = new File(configFile);
        }
        File td = new File(targetDir);
        if (!td.exists()) {
            td.mkdirs();
        }
        start(new File(dir), td, cf);
    }

    public void start(File dir, File targetDir, File configFile) {
        new CruncherService(abs(dir), abs(targetDir)).crunch();
    }
    
    // TODO den abs-Kram nach CruncherService verschieben, falls das überhaupt noch notwendig ist
    private Path abs(File file) {
        return Paths.get(file.getAbsolutePath());
    }
}
