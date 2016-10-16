

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import support.Utilities;
import support.Utilities.ChecksumException;

/**
 * JUnit Test Runner
 * 
 * @author Tony Hsu
 */
public class UtilitiesJUnitTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

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
        testGetExtensionType();
        testGetFilenameWithCRC();
        testGetExtensionsForFiles();
        testAddSeparator();
        testCalculateCRC32();
    }

    public void testGetExtensionType()
    {
        String extension = Utilities.getExtensionType("asd.exe"); 
        assertEquals(".exe", extension);
        
        extension = Utilities.getExtensionType("asd"); 
        assertEquals("", extension);
        
        extension = Utilities.getExtensionType("asd."); 
        assertEquals("", extension);
        
        extension = Utilities.getExtensionType(""); 
        assertEquals("", extension);
        
        extension = Utilities.getExtensionType("asd.mkv.avi"); 
        assertEquals(".avi", extension);
        
        extension = Utilities.getExtensionType("..."); 
        assertEquals("", extension);
    }
    
    public void testGetFilenameWithCRC()
    {
        String fileWithCRC = Utilities.getFilenameWithCRC(null, ".exe", "ffffff", new String[] {"[","]"});
        assertEquals(null, fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("asd.exe", ".exe", "ffffff", new String[] {"[","]"});
        assertEquals("asd [FFFFFF].exe", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("a s d.exe", ".exe", "ffffff", new String[] {"[","]"});
        assertEquals("a s d [FFFFFF].exe", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("asd.exe", ".exe", "9da4fea9", new String[] {"{[]","][]"});
        assertEquals("asd {[]9DA4FEA9][].exe", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("asd", "", "aaaaa", new String[] {"",""});
        assertEquals("asd AAAAA", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("a_e_I_-_01.mkv", ".mkv", "aaaa", new String[] {"[","]"});
        assertEquals("a_e_I_-_01_[AAAA].mkv", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("a e I - 01 [BD].mkv", ".mkv", "aaaa", new String[] {"[","]"});
        assertEquals("a e I - 01 [BD][AAAA].mkv", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("a-e-I - 01 [m].mkv", ".mkv", "aaaa", new String[] {"[","]"});
        assertEquals("a-e-I - 01 [m][AAAA].mkv", fileWithCRC);
        
        fileWithCRC = Utilities.getFilenameWithCRC("asd.exe", null, "ffffff", new String[] {"[","]"});
        assertEquals("asd [FFFFFF].exe", fileWithCRC);
    }
    
    public void testGetExtensionsForFiles() throws IOException
    {
        assertEquals(null, Utilities.getExtensionsForFiles(null));
        
        folder.newFile("asd.txt");
        folder.newFile("asd.exe");
        folder.newFile("asd.wav");
        folder.newFile("asd.mp3");
        folder.newFile("hello.world.py");
        folder.newFile("no_extension");
        folder.newFile(".hidden");
        File subFolder = folder.newFolder();
        File.createTempFile("prefix", ".folder", subFolder);
        
        // sort arrays for consistent assert
        Set<String> actual = Utilities.getExtensionsForFiles(new File[] {folder.getRoot()});
        String[] actualArr = actual.toArray(new String[actual.size()]);
        Arrays.sort(actualArr);
        
        String[] expectedArr = {".txt", ".exe", ".wav", ".mp3", ".py", ".hidden", ".folder"};
        Arrays.sort(expectedArr);
        
        assertEquals(Arrays.toString(expectedArr), Arrays.toString(actualArr));
    }
    
    public void testAddSeparator()
    {
        String string = Utilities.addSeparator(null, ",", true);
        assertEquals(null, string);
        
        string = Utilities.addSeparator("asd, ", ",", true);
        assertEquals("asd, ", string);
        
        string = Utilities.addSeparator("asd, ", ", ", false);
        assertEquals("asd, ", string);
        
        string = Utilities.addSeparator("asd,", ",", true);
        assertEquals("asd,", string);
        
        string = Utilities.addSeparator("asd,", ", ", false);
        assertEquals("asd,", string);
        
        string = Utilities.addSeparator("asd ", ",", true);
        assertEquals("asd, ", string);
        
        string = Utilities.addSeparator("asd", ",", true);
        assertEquals("asd, ", string);
        
        string = Utilities.addSeparator("asd ", " ", true);
        assertEquals("asd ", string);
        
        string = Utilities.addSeparator("", ",", true);
        assertEquals("", string);
    }
    
    public void testCalculateCRC32() throws IOException
    {
        try
        {
            assertEquals(null, Utilities.calculateCRC32(null));
            
            File temp = folder.newFile();
            FileWriter writer = new FileWriter(temp);
            writer.write("abc");
            writer.close();
            
            assertEquals("00000000", Utilities.calculateCRC32(folder.newFile()));
            assertEquals("352441c2", Utilities.calculateCRC32(temp));
            
            Utilities.calculateCRC32(new File("asd"));
            assert(false);
        }
        catch (ChecksumException e)
        {
            assert(true);
        }
    }
    
}
