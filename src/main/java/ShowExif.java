import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;

public class ShowExif {
    public static void main(String[] args) throws ImageProcessingException, IOException {
        File jpegFile = new File("src/main/resources/testImage.jpg");

        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        for (Directory directory : metadata.getDirectories()) {
            System.out.println("Directory: " + directory.getName());
            for (Tag tag : directory.getTags()) {
                System.out.println(tag.getTagName() + ": " + tag.getDescription());
            }
        }
    }
}
