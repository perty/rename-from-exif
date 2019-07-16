package se.artcomputer.photo;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.String.format;

@SuppressWarnings("WeakerAccess")
public class RenamerLogic implements Visitor {
    private static final String EXIF_DATE_TIME_FORMAT = "yyyy:MM:dd HH:mm:ss";
    private static final String FILE_NAME_FROM_DATE = "yyyy-MM-dd HH.mm.ss";

    private Logger logger;

    public RenamerLogic(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void process(File file) {
        if (fileEndsWith(file, "jpg")) {
            renameJpgFile(file);
        } else if (fileEndsWith(file, "mov")) {
            renameMovFile(file);
        } else {
            logger.logNl("Skip file: '" + file.getName() + "'");
        }
    }

    private void renameJpgFile(File jpegFile) {
        logger.log(jpegFile.getName() + ": ");
        Date date = extractDateFromExifOrFallBackOnModifiedDate(jpegFile);
        final String newName = getFileNameFromDate(date);
        logger.logNl(newName);
        renameToNewNameAddXWhenNeeded(jpegFile, newName, ".jpg");
    }

    private void renameMovFile(File file) {
        logger.log(file.getName() + ": ");
        Date fileDate = new Date(file.lastModified());
        final String newName = getFileNameFromDate(fileDate);
        logger.logNl(newName);
        renameToNewNameAddXWhenNeeded(file, newName, ".mov");
    }

    private Date extractDateFromExifOrFallBackOnModifiedDate(File jpegFile) {
        MetadataHolder metadata = null;
        try {
            metadata = new MetadataHolder(jpegFile);
        } catch (IllegalArgumentException e) {
            logger.logNl(e.getMessage());
        }
        Date date = null;
        if (metadata == null) {
            logger.logNl("No metadata.");
        } else {
            date = extractDate(metadata);
        }
        if (date == null) {
            date = new Date(jpegFile.lastModified());
            logger.logNl("No date in Exif, using last modified instead: " + getFileNameFromDate(date));
        }
        return date;
    }

    Date extractDate(MetadataHolder metadata) {
        final ExifSubIFDDirectoryHolder exifSubIFDDirectory = metadata.getExifSubIFDDirectory();
        if (exifSubIFDDirectory == null) {
            logger.logNl("No Exif IF directory.");
            return null;
        } else {
            return getExifDate(exifSubIFDDirectory);
        }
    }

    private Date getExifDate(ExifSubIFDDirectoryHolder exifSubIFDDirectory) {
        Date date = null;
        String dateString = exifSubIFDDirectory.getDateTimeOriginalAsString();
        try {
            date = new SimpleDateFormat(EXIF_DATE_TIME_FORMAT).parse(dateString);
        } catch (ParseException e) {
            logger.logNl("Could not parse " + dateString);
        }
        return date;
    }

    private void renameToNewNameAddXWhenNeeded(File file, String newName, String suffix) {
        StringBuilder nameBase = new StringBuilder(newName);
        while (true) {
            final File destination = createDestinationFileName(file, nameBase.toString(), suffix);
            if (destination.exists()) {
                if (isTheSameFile(file, destination)) {
                    logger.logNl("Already renamed: " + destination.getAbsolutePath());
                    return;
                } else {
                    nameBase.append("x");
                }
            } else {
                doRename(file, destination);
                return;
            }
        }
    }

    private File createDestinationFileName(File jpegFile, String newName, String suffix) {
        return new File(jpegFile.getParent() + File.separator + newName + suffix);
    }

    private void doRename(File source, File destination) {
        logger.logNl(format("Rename from '%s' to '%s'", source, destination));
        if (!source.renameTo(destination)) {
            logger.flush();
            throw new RuntimeException("Rename failed for " + source.getAbsolutePath());
        }
    }

    private String getFileNameFromDate(Date fileDate) {
        return new SimpleDateFormat(FILE_NAME_FROM_DATE).format(fileDate);
    }

    private static boolean isTheSameFile(File file, File destination) {
        return destination.getAbsolutePath().equalsIgnoreCase(file.getAbsolutePath());
    }

    private static boolean fileEndsWith(File file, String suffix) {
        return file.getName().toLowerCase().endsWith(suffix);
    }


}
