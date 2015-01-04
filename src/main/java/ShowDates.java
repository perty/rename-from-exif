import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowDates {
    public static void main(String[] args) throws ImageProcessingException, IOException {
        if (args.length != 1) {
            System.err.println("Usage: folder");
            System.exit(1);
        }
        File dir = new File(args[0]);
        if (!dir.isDirectory()) {
            System.err.println(args[0] + " is not a directory");
            System.exit(2);
        }
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
        for (File jpegFile : dir.listFiles()) {
            if (jpegFile.getName().endsWith("jpg")) {
                Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
                final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
                if (exifSubIFDDirectory == null) {
                    System.err.println("No exif: " + jpegFile.getName());
                    continue;
                }
                final Date date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

                System.out.print(jpegFile.getName() + ": ");
                if (date == null) {
                    System.out.println("No date");
                } else {
                    final String s = format.format(date);
                    System.out.println(s);
                }
            }
        }

    }
}
