package FileManagement;

import java.io.File;


/** @Category: Extension Class: To provide each file with a crc32 value and
 *            depth level.
 * @Structure: A hashtable containing all the items in a directory. Parameters
 *             for the hash entry is the file name and a flag value.
 * @Process: Items are stored initially stored as FILE_NOT_EXIST. Once
 *           doesFileExist() is called, if the file is found, then the FileFlag
 *           is modified to FILE_EXIST. */
@SuppressWarnings("serial")
public class FileWithProperties extends File
{
    private String crc32ValueName;
    private int    level;                   // 0 = root, 1+ directory in root

    public FileWithProperties(File file)
    {
        super(file.getPath());
        this.level = 0;
    }

    public FileWithProperties(File file, int level)
    {
        super(file.getPath());
        this.level = level;
    }

    public FileWithProperties(String pathname)
    {
        super(pathname);
        level = 0;
    }

    // ~~~~~~ Getters ~~~~~~//
    public String getCRC32Value()
    {
        return crc32ValueName;
    }

    public int getLevel()
    {
        return level;
    }

    // ~~~~~~ Getters ~~~~~~//
    public void setCRC32Value(String crc32Value)
    {
        this.crc32ValueName = crc32Value;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

}
