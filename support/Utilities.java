package support;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;


/**
 * Static Methods Class containing useful helper methods.
 * @author Tony Hsu
 */
public final class Utilities
{
    /**
     * Folder directory name of files not in source
     */
    public static final String LEFTOVER_FOLDER = "~leftovers";
    /**
     * Generated ID filename of the contents of the folder
     */
    public static final String ID_FILE_NAME = "~listOfFilesInCRC.txt";
    /**
     * Filename of audit logs
     */
    public static final String AUDIT_FILE_NAME = "~auditTrail.txt";
    /**
     * Count of CRC32 value represented in hexadecimal
     */
    public static final int CRC32_LENGTH = 8;
    
    /**
     * Parse and return the extension type
     * 
     * @param filename Filename to parse
     * @return Extension of filename including '.'
     */
    public static final String getExtensionType(String filename)
    {
        String[] splitStr = filename.split("[.]");
        if(splitStr.length == 1) // no extension found
        {
            return "";
        }
        return "." + splitStr[splitStr.length-1];
    }
    
    /**
     * Returns file name including CRC value. 
     * 
     * @param filename Filename with extension
     * @param extension Extension of filename (including '.')
     * @param CRC CRC value to put into filename
     * @param encasement Delimiters to surround CRC in
     * @return Filename with CRC
     */
    public static final String getFilenameWithCRC(String filename, String extension, String CRC, String[] encasement)
    {
        // passing in extension instead of calling Utilities.getExtensionType to save an additional call
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
                break;
            }
        }
        if(addExtraSpacing) // if no tags detected, determine if using spaces, underscores, or dots
        {
            String[] dotsSplit = fullName.split(".");
            String[] underscoresSplit = fullName.split("_");
            String[] spacesSplit = fullName.split(" ");
            
            // check which one is used most, default is no space
            // examples: Hello.World, Hello World, Hello_World, HelloWorld
            // Length must be greater than one to exist
            if (dotsSplit.length > 1 && 
                dotsSplit.length > Math.max(underscoresSplit.length, spacesSplit.length))
            {
                additionalSpacing = ".";
            }
            else if (underscoresSplit.length > 1 && 
                underscoresSplit.length > Math.max(dotsSplit.length, spacesSplit.length))
            {
                additionalSpacing = "_";
            }
            else if (spacesSplit.length > 1 && 
                spacesSplit.length > Math.max(dotsSplit.length, underscoresSplit.length))
            {
                additionalSpacing = " ";
            }
        }
       
        fullName += additionalSpacing + encasement[0] + CRC.toUpperCase() + encasement[1] + extension;
        return fullName;
    }
    
    /**
     * Returns a set of all possible extension types in a file or directory. Including sub-directories.
     * 
     * @param file File or directory to search through
     * @return A set of all extensions in the file or directory
     */
    public static final Set<String> getExtensionsForFiles(File[] file)
    {
        Set<String> types = new HashSet<String>();
        for(File f : file)
        {
            if(f.isDirectory())
            {
                types.addAll(getExtensionsForFiles(f.listFiles())); // recurse through sub-directories
            }
            else
            {
                types.add(Utilities.getExtensionType(f.getName()));
            }
        }
        return types;
    }
    
    /**
     * Checks if string ends with patterns.
     * 
     * @param string String to compare to
     * @param pattern List of patterns to compare with
     * @return True if string ends with a pattern
     */
    public static final boolean stringEndsWith(String string, String[] pattern)
    {
        for(String str : pattern)
        {
            if((str.equals("") && str.isEmpty()) || string.endsWith(str))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the CRC32 value of a file.
     * 
     * @param file File to compute the CRC value
     * @return CRC value formatted in 8 length hexadecimal
     */
    public static final String calculateCRC32(File file) throws ChecksumException
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
            throw new ChecksumException("Unable to determine CRC32 value for file: " + file.getName());
        }
        for(int i = hex.length(); i < CRC32_LENGTH; i++)
        {
            hex = "0" + hex;
        }
        return hex;
    }
    
    /**
     * Exception Class for if CRC32 errors. Exception includes "mismatch found" or "unable to calculate crc32".
     */
    @SuppressWarnings("serial")
    public static class ChecksumException extends Exception
    {
        public ChecksumException(String message)
        {
            super(message);
        }
    }
}
