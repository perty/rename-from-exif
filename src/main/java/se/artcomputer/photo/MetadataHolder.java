package se.artcomputer.photo;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("WeakerAccess")
public class MetadataHolder {

    private final Metadata metadata;

    public MetadataHolder(File jpegFile) {
        try {
            this.metadata = ImageMetadataReader.readMetadata(jpegFile);
        } catch (ImageProcessingException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public ExifSubIFDDirectoryHolder getExifSubIFDDirectory() {
        ExifSubIFDDirectory directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
        if (directory == null) {
            return null;
        }
        return new ExifSubIFDDirectoryHolder(directory);
    }
}
