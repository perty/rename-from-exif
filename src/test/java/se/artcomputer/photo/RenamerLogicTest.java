package se.artcomputer.photo;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RenamerLogicTest {

    private static final Date SOME_DATE = new Date();
    private static final String SOME_DATE_EXIF_STRING_FORMAT = "2014:04:19 17:48:18";
    private static final String EXIF_DATE_TIME_FORMAT = "yyyy:MM:dd HH:mm:ss";

    @Test
    public void extract_date_converts_correctly() throws ParseException, URISyntaxException {
        RenamerLogic renamerLogic = new RenamerLogic(new Logger(new File(".")));

        MetadataHolder metadata = mockedMetaData();

        Date date = renamerLogic.extractDate(metadata);

        Date expected = new SimpleDateFormat(EXIF_DATE_TIME_FORMAT).parse(SOME_DATE_EXIF_STRING_FORMAT);
        assertThat(date, is(expected));
    }

    private MetadataHolder mockedMetaData() throws URISyntaxException {
        Class<? extends RenamerLogicTest> aClass = getClass();
        ClassLoader classLoader = aClass.getClassLoader();
        URL resource = classLoader.getResource("testimage.jpg");
        assert resource != null;
        return new TestMetaData(new File(resource.toURI()));
    }

    @SuppressWarnings("WeakerAccess")
    class TestMetaData extends MetadataHolder {

        public TestMetaData(File file) {
            super(file);
        }

        @Override
        public ExifSubIFDDirectoryHolder getExifSubIFDDirectory() {
            return new TestExifDirectory();
        }
    }

    @SuppressWarnings("WeakerAccess")
    private class TestExifDirectory extends ExifSubIFDDirectoryHolder {
        public TestExifDirectory() {
            super(null);
        }

        @Override
        public Date getDate(int tagType) {
            return SOME_DATE;
        }

        @Override
        public String getDateTimeOriginalAsString() {
            return SOME_DATE_EXIF_STRING_FORMAT;
        }
    }

}