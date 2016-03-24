package support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;


/** Static Methods Class */
public class Utilities
{
    public static final String CRC_FILE_NAME = "~listOfFilesInCRC.txt";
    public static final String AUDIT_FILE_NAME = "~auditTrail.txt";
    public static final int SOURCE = 5;
    public static final int DESTINATION = 10;

    public static final String convertToCRCFile(String fileName, String CRC32)
    {
        return CRC32 + " \"" + fileName + "\"";
    }

    public static final String convertToDirectoryLvl(String filePath, int level, String rootPath)
    {
        return "~" + level + ": " + filePath.substring(rootPath.length());
    }

    public static final String calculateCRC32(File file) throws IOException
    {
        String hex = "";
        try
        {
            CheckedInputStream cis = new CheckedInputStream(new FileInputStream(file), new CRC32());
            byte[] buf = new byte[10240]; // 10mb

            while(cis.read(buf) >= 0)
                ;

            hex = Long.toHexString(cis.getChecksum().getValue());
            cis.close();
        }
        catch (IOException e)
        {
            System.out.println("Unable to determine crc32 value for file: " + file.getName());
            return "";
        }
        return hex;
    }

}
