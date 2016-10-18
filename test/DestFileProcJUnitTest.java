

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveDirectory.FileFlag;
import fileManagement.SynchiveFile;
import fileManagement.fileProcessor.DestinationFileProcessor;
import support.Utilities;

public class DestFileProcJUnitTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private DestinationFileProcessor destFP;
    private File idFile;
    
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
    
    public void setUpIDFile() throws IOException
    {
         idFile = folder.newFile(Utilities.ID_FILE_NAME);
         FileWriter writer = new FileWriter(idFile);
         writer.write("Synchive v1.1 - root=D:\\TestA\n");
         writer.write("~0: \n");
         writer.write("00000000 \"file1\"\n");
         writer.write("5AD84AD3 \"file2\"\n");
         writer.write("~1: \\Test\n");
         writer.write("70c4251b \"HIHI\"");
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
    public void testDecodedFile() throws IOException
    {
        setUpIDFile();
        destFP = new DestinationFileProcessor(folder.getRoot());
        
        Hashtable<String, SynchiveDirectory> table = destFP.getFiles();
        assertEquals(2, table.size());
        
        String key = "~0: ";
        assertEquals(true, table.containsKey(key));
        
        SynchiveDirectory dir = table.get(key);
        assertEquals(2, dir.getLookupTable().size());
        assertEquals(true, dir.doesFileExist("00000000 \"file1\""));
        assertEquals(true, dir.doesFileExist("5AD84AD3 \"file2\""));

        key = "~1: \\Test";
        assertEquals(true, table.containsKey(key));
        
        dir = table.get(key);
        assertEquals(1, dir.getLookupTable().size());
        assertEquals(true, dir.doesFileExist("70c4251b \"HIHI\""));
    }
    
    @Test
    public void testReadFiles() throws Exception
    {        
        if(idFile != null)
        {
            idFile.delete();
            idFile = null;
        }
        Hashtable<String, HashSet<String>> directory = new Hashtable<String, HashSet<String>>();
        
        HashSet<String> fileNames = new HashSet<String>();
        fileNames.add(folder.newFile().getName());
        fileNames.add(folder.newFile().getName());
        fileNames.add(folder.newFile().getName());
        directory.put("~0: ", fileNames);
        
        File subFolder = folder.newFolder();
        fileNames = new HashSet<String>();
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        directory.put("~1: \\" + subFolder.getName(), fileNames);
        
        subFolder = folder.newFolder("testInner");
        fileNames = new HashSet<String>();
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        directory.put("~1: \\" + subFolder.getName(), fileNames);
        
        subFolder = new File(subFolder.getPath() + "/anotherSubFolder/");
        fileNames = new HashSet<String>();
        subFolder.mkdir();
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        directory.put("~2: \\testInner\\" + subFolder.getName(), fileNames);
        
        
        destFP = new DestinationFileProcessor(folder.getRoot());
        Hashtable<String, SynchiveDirectory> table = destFP.getFiles();
        assertEquals(directory.size(), table.size());
        
        for(String folderName : directory.keySet())
        {
            assertEquals(true, table.containsKey(folderName));
            
            SynchiveDirectory testDir = table.get(folderName);
            assertEquals(directory.get(folderName).size(), testDir.getLookupTable().size());
            for(String fileName : directory.get(folderName))
            {
                assertEquals(true, testDir.doesFileExist("00000000 \"" + fileName + "\""));
            }
            
        } 
    }
}
