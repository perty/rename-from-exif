package se.artcomputer.photo;

import javax.swing.*;
import java.io.File;

@SuppressWarnings("WeakerAccess")
public class Traverser {

    private ProgressMonitor progressMonitor;
    private int progress;

    public Traverser(ProgressMonitor progressMonitor) {
        this.progressMonitor = progressMonitor;
        this.progress = 0;
    }

    public void traverseDir(File dir, Visitor visitor) {
        File[] files = dir.listFiles();
        if (files != null) {
            processFiles(files, visitor);
        }
    }

    private void processFiles(File[] files, Visitor visitor) {
        for (File file : files) {
            progressMonitor.setProgress(progress++);
            if (file.isDirectory()) {
                traverseDir(file, visitor);
            } else {
                processFile(file, visitor);
            }
        }
    }

    private void processFile(File file, Visitor visitor) {
        visitor.process(file);
    }
}
