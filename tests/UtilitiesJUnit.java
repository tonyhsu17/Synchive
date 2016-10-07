/**
 * 
 */
package tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import support.Utilities;

/**
 * @author Ikersaro
 *
 */
public class UtilitiesJUnit
{

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
    public void test()
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
        String fileWithCRC = Utilities.getFilenameWithCRC("asd.exe", ".exe", "ffffff", new String[] {"[","]"});
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
    }
    
    public void testGetExtensionsForFiles()
    {
    }
    
    public void testAddSeparator()
    {
        String string = Utilities.addSeparator("asd, ", ",", true);
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
    
    public void testCalculateCRC32()
    {
    }
}
