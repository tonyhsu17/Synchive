package fileManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.annotation.Generated;

import fileManagement.SynchiveDirectory.FileFlag;
import support.Utilities;

public class DestinationFileProcessor extends FileProcessorBase
{
    // "Folder Name" -> "Directory"; Directory contains "FileID" -> "FlagInfo"
    private Hashtable<String, SynchiveDirectory> directoryList; // structured mapping for destination
    private BufferedWriter output;
    
    public DestinationFileProcessor(File directory)
    {
        super(directory);
        directoryList = new Hashtable<String, SynchiveDirectory>(); // des uses structural mapping
        readinIDs();
        System.out.println(toString());
    }
    
    public Hashtable<String, SynchiveDirectory> getFiles()
    {
        return directoryList;
    }
    
    public void didProcessFile(SynchiveFile file, SynchiveFile dir)
    {
        try
        {
            output.write(file.generateUniqueID());// write uid to file
            output.newLine(); 
            
            String dirID = Utilities.convertToDirectoryLvl(dir.getPath(), dir.getLevel(), getRoot().getPath());
            System.out.println("ProcessFile: " + file.toString() + " dir: " + dir.toString());
            SynchiveDirectory fileList = directoryList.get(dirID);
            
            if(fileList == null)
            {
                System.out.println("damg");
            }
            else
                directoryList.get(dirID).addFile(file.getUniqueID(), FileFlag.FILE_NOT_EXIST);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    public void willProcessDirectory(SynchiveFile file)
    {
        try
        {
            String id = Utilities.convertToDirectoryLvl(file.getPath(), file.getLevel(), getRoot().getPath());
            output.write(id);
            output.newLine();
            
            System.out.println("Processing Dir: " + id);
            directoryList.put(id, new SynchiveDirectory(id));
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    public void directoryReadFromID(SynchiveDirectory dir)
    {
        System.out.println("DirReadFromID: " + dir.getUniqueID());        
        directoryList.put(dir.getUniqueID(), dir);
    }
    
    public void fileReadFromID(SynchiveFile file, SynchiveDirectory dir)
    {
        System.out.println("fileReadFromID: " + dir.getUniqueID() + " file:" + file.getUniqueID());
        directoryList.get(dir.getUniqueID()).addFile(file.getUniqueID(), FileFlag.FILE_NOT_EXIST);
    }
    
    public void initWriter()
    {
        if(output != null)
        {
            return;
        }
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        try
        {
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getRoot().getPath() + "\\" + Utilities.CRC_FILE_NAME)), encoder));
            output.write("Synchive v0.1 - location=" + getRoot().getPath());
            output.newLine();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }  
    }
    
    public void closeWriter()
    {
        if(output == null)
        {
            return;
        }
        try
        {
            output.close(); // close file
            if(hasSubIDFile()) // if there's subIDFiles write idFile to include subIDFiles
            {
                CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
                output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getRoot().getPath() + "\\" + Utilities.CRC_FILE_NAME)), encoder));
                writeToFile(output);
            }
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }
    
    public void writeToFile(BufferedWriter writer) throws IOException
    {
        writer.write("Synchive v0.1 - location=" + getRoot().getPath());
        writer.newLine();
        
        Enumeration<String> keys = directoryList.keys();
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            SynchiveDirectory dir = directoryList.get(key);
            writer.write(dir.getUniqueID());
            writer.newLine();
            
            Hashtable<String, FileFlag> file = dir.getFiles();
            Enumeration<String> fileEnum = file.keys();
            while(fileEnum.hasMoreElements())
            {
                writer.write(fileEnum.nextElement());
                writer.newLine();
            }
        }
        writer.close();
    }
    
    public String toString()
    {
//        Hashtable<String, SynchiveDirectory>
        Enumeration<String> keys = directoryList.keys();
        String str = "{\n";
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            SynchiveDirectory dir = directoryList.get(key);
            str += "\t{" + key + ": " + dir.toString() + "\n";
        }
        str += "}";
        return str;
    }
}
