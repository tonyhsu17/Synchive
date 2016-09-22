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
import synchive.Globals;

/**
 * Store the entire destination location into memory for processing. 
 * 
 * @author Tony Hsu
 * @structure Contains a lookup table of DirectoryIDs to SynchiveDirectorys.
 */
public class DestinationFileProcessor extends FileProcessorBase
{
    /**
     * Structured mapping of "DirectoryID" -> "SynchiveDirectory"
     */
    private Hashtable<String, SynchiveDirectory> directoryList;
    
    /**
     * Processes directory location into a lookup table of files to folders.
     * @param directory Destination Directory
     */
    public DestinationFileProcessor(File directory)
    {
        super(directory);
        
        directoryList = new Hashtable<String, SynchiveDirectory>(); // des uses structural mapping
        EventCenter.getInstance().postEvent(Events.Status, "Processing Destination ...");
        readinIDs();
        
        try
        {
            writeToFile(false); // write to file in-case source and destination is same location
        }
        catch (IOException e)
        {
            EventCenter.getInstance().postEvent(Events.ErrorOccurred, "Failed to write to idFile.");
        }
        EventCenter.getInstance().postEvent(Events.Status, "Finished Processing Destination");
    }
    
    /**
     * Write-out the structural mapping for storage.
     * @param checkExist If true, skip files with FILE_NOT_EXIST flag. If false, include every file.
     * @throws IOException Exceptions thrown from BufferWriter
     */
    public void writeToFile(boolean checkExist) throws IOException
    {
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getRoot().getPath() + "\\" + Utilities.ID_FILE_NAME)), encoder));
        output.write("Synchive " + Globals.VERSION + " - root=" + getRoot().getPath());
        output.newLine();
        
        Enumeration<String> keys = directoryList.keys();
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            SynchiveDirectory dir = directoryList.get(key);
            output.write(dir.getUniqueID());
            output.newLine();
            
            Hashtable<String, FileFlag> file = dir.getLookupTable();
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
    
    // Dumps the lookup table of DirectoryID including what's in each directory
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
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * @return Structural mapping of each folder
     */
    public Hashtable<String, SynchiveDirectory> getFiles()
    {
        return directoryList;
    }
    
    // ~~~~~ Required override methods ~~~~~~ //
    @Override
    public void didProcessFile(SynchiveFile file, SynchiveDirectory dir)
    {
        // Stores the file within it's directory 
       directoryList.get(dir.getUniqueID()).addFile(file.getUniqueID(), FileFlag.FILE_NOT_EXIST);
    }
    
    @Override
    public void willProcessDirectory(SynchiveDirectory dir)
    {
        // Stores the directory into hashtable
        directoryList.put(dir.getUniqueID(), new SynchiveDirectory(dir.getUniqueID()));
    }
}
