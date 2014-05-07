import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ShowDate {
    public static void main(String[] args) throws ImageProcessingException, IOException {
        File jpegFile = new File("src/main/resources/testImage.jpg");

        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        final ExifSubIFDDirectory exifSubIFDDirectory = metadata.getDirectory(ExifSubIFDDirectory.class);
        final Date date = exifSubIFDDirectory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

        final String s = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss").format(date);
        System.out.println(s);
    }
}
