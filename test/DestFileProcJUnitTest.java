

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fileManagement.fileProcessor.DestinationFileProcessor;
import support.Utilities;

public class DestFileProcJUnitTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private DestinationFileProcessor destFP;
    
    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
        
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {

    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
        setUpDirectory();
        destFP = new DestinationFileProcessor(folder.getRoot());
    }
    
    private void setUpDirectory() throws IOException
    {
        File idFile = folder.newFile(Utilities.ID_FILE_NAME);
//        File temp = folder.newFile();
        FileWriter writer = new FileWriter(idFile);
        writer.write("Synchive v1.1 - root=D:\\TestA\n");
        writer.write("~0: \n");
        writer.write("00000000 \"file1\"\n");
        writer.write("5AD84AD3 \"file2\"\n");
        writer.write("~1: \\Test\n");
        writer.write("70c4251b \"HIHI\"");
//        writer.write("~:0");
//        writer.write("Synchive v1.1 - root=D:\\TestA");
        writer.close();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
        
    }

    @Test
    public void test() throws IOException
    {
        testDecodedFile();
    }
    
    public void testDecodedFile() throws IOException
    {
        assert(true);
//        InputStream stream = getClass().getResourceAsStream(JUnitTestSupport.RELATIVE_FOLDER + "destHashtableOutput");
//        assertEquals(JUnitTestSupport.readFile(stream), destFP.toString());
//        stream.close();
    }
    
}
