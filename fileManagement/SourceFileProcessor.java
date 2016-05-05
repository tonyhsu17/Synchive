package fileManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class SourceFileProcessor extends FileProcessorBase
{
    private ArrayList<SynchiveFile> fileList; // flat mapping of files for source
    public SourceFileProcessor(File directory)
    {
        super(directory);
        fileList = new ArrayList<SynchiveFile>(); // source uses flat mapping
        System.out.println("~~~~~~Source~~~~~~~~~~~~~");
        readinIDs();
        System.out.println(toString());
    }
    
    public ArrayList<SynchiveFile> getFiles()
    {
        return fileList;
    }
    
    public void didProcessFile(SynchiveFile file, SynchiveFile dir)
    {
        System.out.println("Scr Storing:" + file.getUniqueID());
        fileList.add(file);
    }
    
    public void willProcessDirectory(SynchiveFile file)
    {
    }
    
    public void directoryReadFromID(SynchiveDirectory dir)
    {
        
    }
    
    public void fileReadFromID(SynchiveFile file, SynchiveDirectory dir)
    {
        fileList.add(file);
    }
    
    public void initWriter()
    {
        
    }
    
    public void closeWriter()
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