package se.artcomputer.photo;

import com.drew.metadata.exif.ExifSubIFDDirectory;

import java.util.Date;

@SuppressWarnings("WeakerAccess")
public class ExifSubIFDDirectoryHolder {
    private ExifSubIFDDirectory exifSubIFDDirectory;

    public ExifSubIFDDirectoryHolder(ExifSubIFDDirectory exifSubIFDDirectory) {
        this.exifSubIFDDirectory = exifSubIFDDirectory;
    }

    public Date getDate(int tagType) {
        return exifSubIFDDirectory.getDate(tagType);
    }

    public String getDateTimeOriginalAsString() {
        return exifSubIFDDirectory.getString(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }
}
