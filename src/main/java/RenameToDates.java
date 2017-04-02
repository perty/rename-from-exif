import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenameToDates {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    private static Date BAD_DATE;
    private static int progress;

    static {
        try {
            BAD_DATE = format.parse("2003-01-01 00.00.00");
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(4);
        }
    }

    private static String currentLog = "";
    private static PrintWriter logStream;
    private static ProgressMonitor progressMonitor;

    public static void main(String[] args) throws ImageProcessingException, IOException {
        File dir = getDirFromUserOrArguments(args);
        openLogStream(dir);
        loopOverDirectoryAndRenameFiles(dir);
        closeLogStream();
    }

    private static void closeLogStream() {
        logStream.close();
    }

    private static File getDirFromUserOrArguments(String[] args) {
        if (args.length == 0) {
            return promptUserForFolder();
        }
        if (args.length != 1) {
            System.err.println("Usage: folder");
            System.exit(1);
        }
        File dir = new File(args[0]);
        if (!dir.isDirectory()) {
            System.err.println(args[0] + " is not a directory");
            System.exit(2);
        }
        return dir;
    }

    private static File promptUserForFolder() {
        JFileChooser jFileChooser = new JFileChooser(".");
        jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int userAction = jFileChooser.showOpenDialog(null);
        if (userAction == JFileChooser.APPROVE_OPTION) {
            return jFileChooser.getSelectedFile();
        }
        System.err.println("No action from user");
        System.exit(3);
        return null;
    }

    private static void openLogStream(File dir) {
        try {
            logStream = new PrintWriter(dir.getAbsolutePath() + File.separator + format.format(new Date()) + ".log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private static void loopOverDirectoryAndRenameFiles(File dir) throws ImageProcessingException, IOException {
        progressMonitor = getProgressMonitor(dir);
        RenameToDates.progress = 0;
        doDirectory(dir);
        progressMonitor.close();
    }

    private static ProgressMonitor getProgressMonitor(File dir) {
        return new ProgressMonitor(new JFrame(), "Renaming files", "in progress", 0, countFiles(dir));
    }

    static int countFiles(File dir) {
        int sum = 0;
        for (File file : listFiles(dir)) {
            if (file.isDirectory()) {
                sum += countFiles(file);
            }
            sum++;
        }
        return sum;
    }

    private static File[] listFiles(File dir) {
        return dir.listFiles();
    }

    private static void doDirectory(File dir) throws ImageProcessingException, IOException {
        logNl("Doing folder '" + dir.getAbsolutePath() + "'");
        progressMonitor.setProgress(progress);
        sleep();
        for (File file : listFiles(dir)) {
            progress++;
            if (file.isDirectory()) {
                doDirectory(file);
            } else {
                if (fileEndsWith(file, "jpg")) {
                    renameJpgFile(file);
                } else if (fileEndsWith(file, "mov")) {
                    renameMovFile(file);
                } else {
                    logNl("Skip file: '" + file.getName() + "'");
                }
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(5);
        }
    }

    private static boolean fileEndsWith(File file, String suffix) {
        return file.getName().toLowerCase().endsWith(suffix);
    }

    private static void renameMovFile(File file) {
        log(file.getName() + ": ");
        final String newName = format.format(new Date(file.lastModified()));
        logNl(newName);
        renameToNewNameAddXWhenNeeded(file, newName, ".mov");
    }

    private static void renameJpgFile(File jpegFile) throws ImageProcessingException, IOException {
        log(jpegFile.getName() + ": ");
        Date date = extractDateFromExifOrFallBackOnModifiedDate(jpegFile);
        if(date.equals(BAD_DATE)) {
            logNl("Bad date detected, skip file.");
            return;
        }
        final String newName = format.format(date);
        logNl(newName);
        renameToNewNameAddXWhenNeeded(jpegFile, newName, ".jpg");
    }

    private static Date extractDateFromExifOrFallBackOnModifiedDate(File jpegFile) throws ImageProcessingException, IOException {
        Date date = null;
        Metadata metadata = getMetadata(jpegFile);
        if (metadata == null) {
            logNl("No metadata.");
        } else {
            date = extractDate(metadata);
        }
        if (date == null) {
            date = new Date(jpegFile.lastModified());
            log("No date in Exif, using last modified instead: " + format.format(date) + " ");
        }
        return date;
    }

    private static Metadata getMetadata(File jpegFile) {
        try {
            return ImageMetadataReader.readMetadata(jpegFile);
        } catch (Exception e) {
            logNl(e.getMessage());
        }
        return null;
    }

    private static Date extractDate(Metadata metadata) {
        Date date = null;
        final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (exifSubIFDDirectory == null) {
            logNl("No Exif IF directory.");
        } else {
            date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
        }
        return date;
    }

    private static void renameToNewNameAddXWhenNeeded(File file, String nameBase, String suffix) {
        while (true) {
            final File destination = createDestinationFileName(file, nameBase, suffix);
            if (destination.exists()) {
                if (isTheSameFile(file, destination)) {
                    logNl("Already renamed: " + destination.getAbsolutePath());
                    return;
                } else {
                    nameBase += "x";
                }
            } else {
                doRename(file, destination);
                return;
            }
        }
    }

    private static boolean isTheSameFile(File file, File destination) {
        return destination.getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath());
    }

    private static File createDestinationFileName(File jpegFile, String newName, String suffix) {
        return new File(jpegFile.getParent() + File.separator + newName + suffix);
    }

    private static void doRename(File source, File destination) {
        logNl(String.format("Rename from '%s' to '%s'", source, destination));
        if (!source.renameTo(destination)) {
            logStream.flush();
            throw new RuntimeException("Rename failed for " + source.getAbsolutePath());
        }
    }

    private static void log(String message) {
        System.out.print(message);
        currentLog += message;
    }

    private static void logNl(String message) {
        log(message);
        System.out.println();
        logStream.println(currentLog);
        currentLog = "";
    }
}
