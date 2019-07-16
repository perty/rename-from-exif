import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.io.IOException;

public class ShowExif {
    public static void main(String[] args) throws ImageProcessingException, IOException {

        showMeta(new File("src/main/resources/2018-01-19 22.59.44.jpg"));
        showMeta(new File("src/main/resources/testimage.jpg"));
    }

    private static void showMeta(File jpegFile) throws ImageProcessingException, IOException {
        System.out.println("-------------" + jpegFile.getName() + "------------");
        Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
        for (Directory directory : metadata.getDirectories()) {
            System.out.println("Directory: " + directory.getName());
            for (Tag tag : directory.getTags()) {
                System.out.println("\t" + tag.getTagName() + ": " + tag.getDescription());
            }
        }
    }
}
