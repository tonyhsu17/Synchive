

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Hashtable;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import org.apache.commons.io.*;

import fileManagement.SynchiveDirectory;
import fileManagement.fileProcessor.DestinationFileProcessor;
import support.Utilities;

public class DestFileProcJUnitTest
{
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();
    private DestinationFileProcessor destFP;
    private File idFile;
    
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

    public String getName(File f)
    {
        return FilenameUtils.getName(f.getName());
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
        fileNames.add(getName(folder.newFile()));
        fileNames.add(getName(folder.newFile()));
        fileNames.add(getName(folder.newFile()));
        directory.put("~0: ", fileNames);
        
        File subFolder = folder.newFolder();
        fileNames = new HashSet<String>();
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        directory.put("~1: \\" + getName(subFolder), fileNames);
        
        subFolder = folder.newFolder("testInner");
        fileNames = new HashSet<String>();
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        directory.put("~1: \\" + getName(subFolder), fileNames);
        
        subFolder = new File(subFolder.getPath() + "/anotherSubFolder/");
        fileNames = new HashSet<String>();
        subFolder.mkdir();
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        fileNames.add(getName(File.createTempFile("prefix", ".temp", subFolder)));
        directory.put("~2: \\testInner\\" + getName(subFolder), fileNames);
        
        
        destFP = new DestinationFileProcessor(folder.getRoot());
        Hashtable<String, SynchiveDirectory> table = destFP.getFiles();
        assertEquals(directory.size(), table.size());
        System.out.println(destFP.toString());
        for(String folderName : directory.keySet())
        {
            System.out.println("Dir: " + folderName);
            assertEquals(true, table.containsKey(folderName));
            
            SynchiveDirectory testDir = table.get(folderName);
            assertEquals(directory.get(folderName).size(), testDir.getLookupTable().size());
            for(String fileName : directory.get(folderName))
            {
                System.out.println("File: " + fileName);
                assertEquals(true, testDir.doesFileExist("00000000 \"" + fileName + "\""));
            }
            
        } 
    }
}
