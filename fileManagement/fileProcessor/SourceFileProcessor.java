package fileManagement.fileProcessor;

import java.io.File;
import java.util.ArrayList;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import synchive.EventCenter;
import synchive.EventCenter.Events;

/**
 * Store the entire source location into memory for processing. 
 * 
 * @author Tony Hsu
 * @structure Contains a list of FileIDs.
 */
public class SourceFileProcessor extends FileProcessorBase
{
    /**
     * List of files for source
     */
    private ArrayList<SynchiveFile> fileList;
    
    /**
     * @param directory
     */
    public SourceFileProcessor(File directory)
    {
        super(directory);
        fileList = new ArrayList<SynchiveFile>(); // source uses flat mapping
        EventCenter.getInstance().postEvent(Events.Status, "Processing Source ...");
        readinIDs();
        EventCenter.getInstance().postEvent(Events.Status, "Finished Processing Source");
    }
    
    // Dumps the list of files
    public String toString()
    {
        SynchiveFile[] list = fileList.toArray(new SynchiveFile[0]);
        String str = "[";
        
        for(SynchiveFile f : list)
        {
            str += f.toString() + ", ";
        }
        
        str += "]";
        return str;
    }
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * @return List of each file
     */
    public ArrayList<SynchiveFile> getFiles()
    {
        return fileList;
    }
    
    // ~~~~~ Required override methods ~~~~~~ //  
    @Override
    public void didProcessFile(SynchiveFile file, SynchiveDirectory dir)
    {
        fileList.add(file);
    }

    @Override
    public void willProcessDirectory(SynchiveDirectory dir)
    {
        // unused
    }
}