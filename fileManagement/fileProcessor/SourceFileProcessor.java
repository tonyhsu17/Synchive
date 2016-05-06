package fileManagement.fileProcessor;

import java.io.File;
import java.util.ArrayList;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import synchive.EventCenter;
import synchive.EventCenter.Events;

public class SourceFileProcessor extends FileProcessorBase
{
    private ArrayList<SynchiveFile> fileList; // flat mapping of files for source
    public SourceFileProcessor(File directory)
    {
        super(directory);
        fileList = new ArrayList<SynchiveFile>(); // source uses flat mapping
        EventCenter.getInstance().postEvent(Events.Status, "Processing Source");
        readinIDs();
        EventCenter.getInstance().postEvent(Events.Status, "Finished Processing Source");
    }
    
    public ArrayList<SynchiveFile> getFiles()
    {
        return fileList;
    }
    
    @Override
    public void didProcessFile(SynchiveFile file, SynchiveDirectory dir)
    {
        fileList.add(file);
    }

    @Override
    public void willProcessDirectory(SynchiveDirectory dir)
    {
    }
    
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
}