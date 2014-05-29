import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RenameToDates {
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
    private static String currentLog = "";
    private static PrintWriter logStream;

    public static void main(String[] args) throws ImageProcessingException, IOException {
        File dir = checkArgumentIsAFolderOrExit(args);
        openLogStream();
        loopOverDirectoryAndRenameFiles(dir);
    }

    private static File checkArgumentIsAFolderOrExit(String[] args) {
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

    private static void openLogStream() {
        try {
            logStream = new PrintWriter(format.format(new Date()) + ".log");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(3);
        }
    }

    private static void loopOverDirectoryAndRenameFiles(File dir) throws ImageProcessingException, IOException {
        logNl("Doing folder '" + dir.getAbsolutePath() + "'");
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                loopOverDirectoryAndRenameFiles(file);
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
        final String newName = format.format(date);
        logNl(newName);
        renameToNewNameAddXWhenNeeded(jpegFile, newName, ".jpg");
    }

    private static Date extractDateFromExifOrFallBackOnModifiedDate(File jpegFile) throws ImageProcessingException, IOException {
        Date date = null;
        Metadata metadata = null;
        try {
            metadata = ImageMetadataReader.readMetadata(jpegFile);
        } catch (Exception e) {
            logNl(e.getMessage());
        }
        if (metadata == null) {
            logNl("No metadata.");
        } else {
            final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
            if (exifSubIFDDirectory == null) {
                logNl("No Exif IF directory.");
            } else {
                date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
            }
        }
        if (date == null) {
            date = new Date(jpegFile.lastModified());
            log("No date in Exif, using last modified instead: " + format.format(date) + " ");
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
