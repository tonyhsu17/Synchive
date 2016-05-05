package fileManagement;

import java.util.Enumeration;
import java.util.Hashtable;

 
/**
 * Component class to store a list of files with a flag to determine 
 * if both source and destination have the same file.
 * 
 * @author Tony Hsu
 * @category Component
 * @structure A hashtable containing all the items in a directory. Parameters
 *             for the hash entry is the file name and a file found flag.
 */
public class SynchiveDirectory
{
    // FileFlag is used to determine if FILE_EXIST then no need to copy file over, otherwise if
    // FILE_NOT_EXIST, then the file will need to be copied over.
    public static enum FileFlag
    {
        FILE_EXIST, FILE_NOT_EXIST
    }

    private Hashtable<String, FileFlag> files; // FileName to if file exist
    private String uniqueID; // name of folder including relative path
    private String directoryPath; // path of directory from relative to root

    /**
     * Creates an empty directory with path parsed from uniqueID.
     * 
     * @param uniqueID Format: "~<"Hierarchy Level">: <"Path">"
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
     * @param fileName Name of file
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
     * Change file exist to true for fileName
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
    
    public String toString()
    {
        Enumeration<String> list = files.keys();
        String str = "[";
        while(list.hasMoreElements())
        {
            str += list.nextElement() + ", ";
        }
        str += "]";
        return str;
    }

    // ~~~~~ Getters & Setters ~~~~~//
    public String getUniqueID()
    {
        return uniqueID;
    }
    
    public void setUniqueID(String folderName)
    {
        this.uniqueID = folderName;
    }

    public String getRealFolderName()
    {
        return directoryPath;
    }
    
    public void setRealFolderName(String realFolderName)
    {
        this.directoryPath = realFolderName;
    }

    public Hashtable<String, FileFlag> getFiles()
    {
        return files;
    }
    
    public void setFiles(Hashtable<String, FileFlag> files)
    {
        this.files = files;
    }
}
