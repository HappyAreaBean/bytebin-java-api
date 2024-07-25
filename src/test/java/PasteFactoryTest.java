import cc.happyareabean.paste.PasteException;
import cc.happyareabean.paste.PasteFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PasteFactoryTest {

    private static final String SERVER_URL = "https://bytebin.happyareabean.cc";
    private static final String TEST_FILE_PATH = "src/test/resources/test.txt";
    private static String testContent;

    @BeforeClass
    public static void setup() throws IOException {
        testContent = new String(Files.readAllBytes(Paths.get(TEST_FILE_PATH)), StandardCharsets.UTF_8);
    }

    @Test
    public void testWriteAndFind() throws PasteException {
        PasteFactory pasteFactory = PasteFactory.create(SERVER_URL);

        String key = pasteFactory.write(testContent);
        System.out.println(SERVER_URL + "/" + key);
        assertNotNull(key);

        String content = pasteFactory.find(key).trim();
        System.out.println(content);
        assertEquals(testContent, content);
    }
}
