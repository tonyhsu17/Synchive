package fileManagement;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Set;

 
/**
 * Class to store a list of files with a flag to determine 
 * if both source and destination have the same file.
 * 
 * @author Tony Hsu
 * @structure A hashtable containing all the items in a directory. Parameters
 *             for the hash entry is the file name and a file found flag.
 */
public class SynchiveDirectory
{
    /**
     * Get the UniqueID of a directory.
     * 
     * @param filePath Path of file
     * @param level Depth level relative to rootPath
     * @param rootPath Directory starting from path
     * @return
     */
    public static final String getDirectoryUniqueID(String filePath, int level, String rootPath)
    {
        return "~" + level + ": " + filePath.substring(rootPath.length());
    }
    
    /**
     * Used to determine file needs to be copied.
     * If FILE_EXIST then no need to copy file over, otherwise if
     * FILE_NOT_EXIST, then the file will need to be copied over.
     */
    public static enum FileFlag
    {
        FILE_EXIST, FILE_NOT_EXIST
    }

    /**
     * Lookup table of "FileName" to "if file exist"
     */
    private Hashtable<String, FileFlag> files;
    /**
     * Unique name to identify directory.
     */
    private String uniqueID; 
    /**
     * Path of directory relative to root
     */
    private String directoryPath;

    /**
     * Creates an empty directory with path parsed from uniqueID.
     * 
     * @param uniqueID Format: "~<"Depth Level">: <"Path">"
     */
    public SynchiveDirectory(String uniqueID)
    {
        this.uniqueID = uniqueID;
        files = new Hashtable<String, FileFlag>();
        
        String[] splitStr = uniqueID.split(" ", 2); // [level, path]
        if(splitStr.length == 1) // if root directory
        {
            directoryPath = "\\";
        }
        else if(splitStr.length == 2) // if directory in root
        {
            directoryPath = splitStr[1]; 
        }
        else // unable to parse uniqueID 
        {
            directoryPath = uniqueID;
        }
    }

    /**
     * Add file to directory.
     * 
     * @param fileName UniqueID of file
     * @param FILE_FLAG File exist state
     */
    public void addFile(String fileName, FileFlag FILE_FLAG)
    {
        files.put(fileName, FILE_FLAG);
    }

    /**
     * Returns file exist state for fileName.
     * 
     * @param fileName Name of file to lookup
     * @return File exist state flag. Null if not found
     */
    public FileFlag getValueForKey(String fileName)
    {
        return files.get(fileName);
    }

    /**
     * Change file exist to true for fileName if found
     * 
     * @param fileName Name of file to change
     * @return file found in list
     */
    public boolean doesFileExist(String fileName)
    {
        FileFlag flag = files.get(fileName);
        // as long as file is found we mark it.
        if(flag != null) // && flag == FILE_NOT_EXIST) // to exclude duplicates
        {
            files.put(fileName, FileFlag.FILE_EXIST);
            return true;
        }
        return false;
    }
    
    // Dumps a sorted list of files with flag value
    public String toString()
    {
        Set<Entry<String, FileFlag>> asd = files.entrySet();
        String[] list = new String[asd.size()];
        int index = 0;
        
        for(Entry<String, FileFlag> entry : asd)
        {
            list[index++] = "\n  name: " + entry.getKey() + " flag: " + entry.getValue();
        }
        
        Arrays.sort(list);
        
        if(list.length > 0)
        {
            list[list.length - 1] += "\n  ";
        }
        return Arrays.toString(list);
    }

    // ~~~~~ Getters & Setters ~~~~~ //
    /**
     * @return UniqueID of the directory
     */
    public String getUniqueID()
    {
        return uniqueID;
    }

    /**
     * @return Directory's relative path
     */
    public String getRelativeDirectoryPath()
    {
        return directoryPath;
    }
    
    /**
     * Sets the name of directory
     * @param directoryPath Path of directory
     */
    public void setRelativeDirectoryPath(String directoryPath)
    {
        this.directoryPath = directoryPath;
    }

    /**
     * @return Lookup table of "fileUID" to "FileFlag"
     */
    public Hashtable<String, FileFlag> getLookupTable()
    {
        return files;
    }
}
