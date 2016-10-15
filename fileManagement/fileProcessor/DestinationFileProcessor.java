package fileManagement.fileProcessor;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import synchive.EventCenter;
import synchive.EventCenter.Events;

/**
 * Store the entire destination location into memory for processing. 
 * 
 * @author Tony Hsu
 * @structure Lookup table of DirectoryIDs to SynchiveDirectorys is same as parser. No additional table needed.
 */
public class DestinationFileProcessor extends FileProcessorBase
{    
    /**
     * Processes directory location into a lookup table of files to folders.
     * @param directory Destination Directory
     */
    public DestinationFileProcessor(File directory)
    {
        super(directory);
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
    
    // Dumps the lookup table sorted of DirectoryID including what's in each directory 
    public String toString()
    {
        Hashtable<String, SynchiveDirectory> directories = getFiles();
        Enumeration<String> keys = directories.keys();
        
        String[] result = new String[directories.size()];
        int index = 0;
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            SynchiveDirectory dir = directories.get(key);
            result[index++] = "\n" + key + "\n  " + dir.toString();
        }
        Arrays.sort(result);
        if(result.length > 0)
        {
            result[result.length - 1] += "\n";
        }
        return Arrays.toString(result);
    }
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * @return Structural mapping of each folder
     */
    public Hashtable<String, SynchiveDirectory> getFiles()
    {
        return getStructuredMapping();
    }
    
    // ~~~~~ Required override methods ~~~~~~ //
    @Override
    public void didProcessFile(SynchiveFile file, SynchiveDirectory dir)
    {
        // unused
    }
    
    @Override
    public void willProcessDirectory(SynchiveDirectory dir)
    {  
        // unused
    }
}
