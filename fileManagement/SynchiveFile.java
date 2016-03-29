package fileManagement;

import java.io.File;


/**
 * @Category: Extension Class: To provide each file with a crc32 value and
 *            depth level.
 * @Structure: A hashtable containing all the items in a directory. Parameters
 *             for the hash entry is the file name and a flag value.
 * @Process: Items are stored initially stored as FILE_NOT_EXIST. Once
 *           doesFileExist() is called, if the file is found, then the FileFlag
 *           is modified to FILE_EXIST.
 */
@SuppressWarnings("serial")
public class SynchiveFile extends File
{
    private String uniqueID; // file path + CRC value
    private int level; // 0 = root, 1+ directory in root
    private String crc;

    public SynchiveFile(File file)
    {
        super(file.getPath());
        this.level = 0;
        crc = "";
    }

    public SynchiveFile(File file, int level)
    {
        super(file.getPath());
        this.level = level;
        crc = "";
    }

    //UNUSED
    public SynchiveFile(String pathname)
    {
        super(pathname);
        level = 0;
        crc = "";
    }

    public String getUniqueID()
    {
        if(uniqueID == null)
        {
            return generateUniqueID();
        }
        return uniqueID;
    }

    public String generateUniqueID()
    {
        uniqueID = crc + " \"" + getName() + "\"";
        return uniqueID;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getCRC()
    {
        return crc;
    }

    public void setCRC(String crc)
    {
        this.crc = crc;
    }
}
