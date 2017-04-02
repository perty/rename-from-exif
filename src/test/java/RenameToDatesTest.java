import org.junit.Test;

import java.io.File;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class RenameToDatesTest {

    private static final int FILES = 3;
    private static final int FOLDERS = 2;
    private static final int TOTAL = FILES + FOLDERS;
    private static final String TEST_TESTFOLDER = "src/test/resources/testfolder";

    @Test
    public void countFiles() throws Exception {
        int files = RenameToDates.countFiles(new File(TEST_TESTFOLDER));

        assertThat(files, is(TOTAL));
    }

}
