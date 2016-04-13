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

//    public static final String getEncodedFileString(String fileName, String CRC32)
//    {
//        return CRC32 + " \"" + fileName + "\"";
//    }

    public static final String convertToDirectoryLvl(String filePath, int level, String rootPath)
    {
        return "~" + level + ": " + filePath.substring(rootPath.length());
    }
    
    // returns appropriate naming including CRC value. 
    /**
     * @param filename Filename with extension
     * @param extension Extension of filename (including '.')
     * @param CRC CRC value to put into filename
     * @param encasement Delimiters to surround CRC in
     * @return
     */
    public static final String getFilenameWithCRC(String filename, String extension, String CRC, String[] encasement)
    {
        final String[] delimiters = {")", "}", "]", "-", "_", "+", "="}; // used to check if extra spacing is required or not.
        boolean addExtraSpacing = true;
        String additionalSpacing = ""; // default no spacing
        String fullName = filename.substring(0, filename.length() - extension.length()); //strip out extension from name
        
        // loop through each trailing delimiter type to skip adding spacing
        for(String delim : delimiters)
        {
            if(fullName.endsWith(delim))
            {
                addExtraSpacing = false;
            }
        }
        if(addExtraSpacing) // if no tags detected, determine if using spaces or underscores
        {
            String[] spacesSplit = fullName.split(" ");
            // if spaces found use space, else use underscore
            additionalSpacing = spacesSplit.length > 1 ? " " : "_";
        }
        
        fullName += additionalSpacing + encasement[0] + CRC.toUpperCase() + encasement[1] + extension;
        System.out.println("fullName post: " + fullName);
        return fullName;
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
