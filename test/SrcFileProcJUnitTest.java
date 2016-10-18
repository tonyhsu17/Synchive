

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
import fileManagement.SynchiveFile;
import fileManagement.fileProcessor.SourceFileProcessor;
import support.Utilities;

public class SrcFileProcJUnitTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private SourceFileProcessor scrFP;
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
        scrFP = new SourceFileProcessor(folder.getRoot());
        HashSet<String> names = new HashSet<String>();
        names.add("00000000 \"file1\"");
        names.add("5AD84AD3 \"file2\"");
        names.add("70c4251b \"HIHI\"");
        
        ArrayList<SynchiveFile> table = scrFP.getFiles();
        assertEquals(names.size(), table.size());
        
        for(SynchiveFile file : table)
        {
            assertEquals(true, names.contains(file.getUniqueID()));
        }        
    }
    
    @Test
    public void testReadFiles() throws Exception
    {
        if(idFile != null)
        {
            idFile.delete();
            idFile = null;
        }
        HashSet<String> fileNames = new HashSet<String>();
        fileNames.add(folder.newFile().getName());
        fileNames.add(folder.newFile().getName());
        fileNames.add(folder.newFile().getName());
        
        File subFolder = folder.newFolder();
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        
        subFolder = folder.newFolder();
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        
        subFolder = new File(subFolder.getPath() + "/anotherSubFolder/");
        subFolder.mkdir();
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        fileNames.add(File.createTempFile("prefix", ".temp", subFolder).getName());
        
        scrFP = new SourceFileProcessor(folder.getRoot());
        ArrayList<SynchiveFile> table = scrFP.getFiles();
        assertEquals(fileNames.size(), table.size());
        
        for(SynchiveFile file : table)
        {
            assertEquals(true, fileNames.contains(file.getName()));
            assertEquals("00000000", file.getCRC());
            assertEquals(true, file.canRead());
        } 
    }
}
