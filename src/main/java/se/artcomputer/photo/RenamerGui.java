package se.artcomputer.photo;

import javax.swing.*;
import java.io.File;

import static java.lang.String.format;

@SuppressWarnings("WeakerAccess")
public class RenamerGui {

    private Logger logger;

    public void show() {
        File file = promptUserForFolder();
        if (file != null) {
            logger = new Logger(file);
            loopOverDirectoryAndRenameFiles(file);
            logger.close();
        }
    }

    private void loopOverDirectoryAndRenameFiles(File dir) {
        ProgressMonitor progressMonitor = getProgressMonitor(dir);
        new Traverser(progressMonitor).traverseDir(dir, new RenamerLogic(logger));
        progressMonitor.close();
    }

    private File promptUserForFolder() {
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userAction = jFileChooser.showOpenDialog(null);
        if (userAction == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        }
        return null;
    }

    private ProgressMonitor getProgressMonitor(File dir) {
        int files = countFiles(dir);
        logger.logNl(format("Doing %d files and folders.", files));
        return new ProgressMonitor(new JFrame(), "Renaming files", "in progress", 0, files);
    }

    private int countFiles(File dir) {
        int sum = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    sum += countFiles(file);
                }
                sum++;
            }
        }
        return sum;
    }

}
