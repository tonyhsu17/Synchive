package fileManagement.fileProcessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import fileManagement.SynchiveDirectory.FileFlag;
import support.Utilities;
import synchive.EventCenter;
import synchive.EventCenter.Events;

public class DestinationFileProcessor extends FileProcessorBase
{
    // "Folder Name" -> "Directory"; Directory contains "FileID" -> "FlagInfo"
    private Hashtable<String, SynchiveDirectory> directoryList; // structured mapping for destination
    
    public DestinationFileProcessor(File directory)
    {
        super(directory);
        directoryList = new Hashtable<String, SynchiveDirectory>(); // des uses structural mapping
        EventCenter.getInstance().postEvent(Events.Status, "Processing Destination");
        readinIDs();
        EventCenter.getInstance().postEvent(Events.Status, "Finished Processing Destination");
    }
    
    public Hashtable<String, SynchiveDirectory> getFiles()
    {
        return directoryList;
    }
    
    @Override
    public void didProcessFile(SynchiveFile file, SynchiveDirectory dir)
    {
        SynchiveDirectory fileList = directoryList.get(dir.getUniqueID());
        
        if(fileList == null)
        {
            System.out.println("damg");
        }
        else
            directoryList.get(dir.getUniqueID()).addFile(file.getUniqueID(), FileFlag.FILE_NOT_EXIST);
    }
    
    @Override
    public void willProcessDirectory(SynchiveDirectory dir)
    {
        directoryList.put(dir.getUniqueID(), new SynchiveDirectory(dir.getUniqueID()));
    }
    
    public void writeToFile(boolean checkExist) throws IOException
    {
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getRoot().getPath() + "\\" + Utilities.CRC_FILE_NAME)), encoder));
        output.write("Synchive v0.1 - root=" + getRoot().getPath());
        output.newLine();
        
        Enumeration<String> keys = directoryList.keys();
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            SynchiveDirectory dir = directoryList.get(key);
            output.write(dir.getUniqueID());
            output.newLine();
            
            Hashtable<String, FileFlag> file = dir.getFiles();
            Enumeration<String> fileEnum = file.keys();
            while(fileEnum.hasMoreElements())
            {
                String fileID = fileEnum.nextElement();
                if(checkExist && file.get(fileID) != FileFlag.FILE_EXIST)
                {
                    continue;
                }
                output.write(fileID);
                output.newLine();
            }
        }
        output.close();
    }
    
    public String toString()
    {
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
