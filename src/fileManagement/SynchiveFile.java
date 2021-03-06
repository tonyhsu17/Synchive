package fileManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import support.Utilities;
import support.Utilities.ChecksumException;
import org.apache.commons.io.*;

/**
 * Class to provide each file with additional properties: crc32 value, depth level, is copy allowed.
 * 
 * @author Tony Hsu
 * @structure Extends File for additional properties
 */
@SuppressWarnings("serial")
public class SynchiveFile extends File
{
    /**
     * Special characters to sanitize if in input
     */
    private static final String SPECIAL_CHARACTERS = "[]{}()+\\^&.$?*|:<>=!";

    /**
     * Unique Name to identify file. Property is lazy-loaded
     */
    private String uniqueID; 
    /**
     * Depth of the file relative to root
     */
    private int level;
    /**
     * CRC32 representation in 8 hexadecimal
     */
    private String crc;
    /**
     * Flag to determine if copying allowed
     */
    private boolean copyAllowed;
    /**
     * Possible CRC32 in filename. Property is lazy-loaded
     */
    private String[] possibleCRCInFilename; 

    /**
     * Constructs file with default properties.
     * (Hierarchy = root, no CRC32 value, copying allowed)
     * 
     * @param file Provide additional properties to file.
     */
    public SynchiveFile(File file)
    {
        this(file, 0, "");
    }

    /**
     * Constructs file with additional properties.
     * (Hierarchy = level, no CRC32 value, copying allowed)
     * 
     * @param file Provide additional properties to file
     * @param level Hierarchy level. (0 for root, > 0 for directories in root)
     */
    public SynchiveFile(File file, int level)
    {
        this(file, level, "");
    }
    
    /**
     * Constructs file with additional properties.
     * (Hierarchy = level, CRC32 value, copying allowed)
     * 
     * @param file Provide additional properties to file
     * @param level Hierarchy level. (0 for root, > 0 for directories in root)
     * @param crc CRC value
     */
    public SynchiveFile(File file, int level, String crc)
    {
        super(file.getPath());
        this.level = level;
        copyAllowed = true;
        this.crc = crc;
    }

    /**
     * Returns if copy is allowed on file.
     * 
     * @return Flag if copy is allowed
     */
    public boolean copyAllowed()
    {
        return copyAllowed;
    }
    
    /**
     * Determine if copying is allowed by checking exclude extensions list
     * @param extensions List of extensions to exclude
     * @return Copy allowed if not in exclude list
     */
    public boolean determineProcessingAllowed(String extensions)
    {
        copyAllowed = !extensions.contains(Utilities.getExtensionType(getName()));
        return copyAllowed;
    }

    /**
     * Determine if copying is allowed by finding CRC32 value in fileName.
     * If it cannot find CRC32 in fileName, assume none provided and skip comparison with calculated CRC32.
     * 
     * @param delimiter Characters encasing a CRC32 value. Empty string for any CRC32 match
     * @return true if CRC not found or CRC match.
     * @throws ChecksumException CRC32 from filename and calculated CRC32 mismatch. Possible corrupted file
     */
    public boolean determineCopyingAllowed(String delimiter) throws ChecksumException
    {
        if(possibleCRCInFilename == null)
        {
           possibleCRCInFilename = findCRCInFilename(delimiter);
        }
        
        if(possibleCRCInFilename.length == 0) // no crc32 in fileName found
        {
            return true;
        }
        else
        {
            copyAllowed = false;
            for(String possible : possibleCRCInFilename) // only set copyAllowed if matching crc
            {
                if(crc.compareToIgnoreCase(possible) == 0)
                {
                    copyAllowed = true;
                    return true;
                }
            }
            // cannot find matching CRC, throw exception
            throw new ChecksumException(Arrays.toString(possibleCRCInFilename));
        }
    }

    /**
     * Find CRC32 value from filename.
     * @param delimiter Constraints to find CRC32 value. Can be empty
     * @return A list of possible CRC32
     */
    private String[] findCRCInFilename(String delimiter)
    {
        ArrayList<String> possibleCRC = new ArrayList<String>();
        String[] splitDelim = delimiter.split(",");
        boolean emptyDelim = delimiter.isEmpty();
        
        for(String delim : splitDelim) // go through each delimiter
        {
            String trimmed = delim.trim(); // get rid of extra spacings and don't allow "space" delim
            
            // Allow empty delim if specified otherwise don't allow it
            // only handle even number, doesn't make sense having mismatching lengths
            if((emptyDelim || trimmed.length() > 0) && trimmed.length() % 2 == 0)
            {
                String leading = trimmed.substring(0, delim.length() / 2); // first half is left side
                String trailing = trimmed.substring(delim.length() / 2); // last half is right side
                String sanitizedLeading = "";
                String sanitizedTrailing = "";

                // sanitized the strings in case of special characters used in pattern matching
                for(int i = 0; i < leading.length(); i++)
                {
                    char left = leading.charAt(i);
                    char right = trailing.charAt(i);

                    sanitizedLeading += charContains(left, SPECIAL_CHARACTERS) ? (File.separator + left) : left;
                    sanitizedTrailing += charContains(right, SPECIAL_CHARACTERS) ? (File.separator + right) : right;
                }

                // String pattern matching using delimiters and set formatting for CRC32 in hex
                Formatter regx = new Formatter(new StringBuilder(
                        sanitizedLeading + "[a-fA-F0-9]{" + Utilities.CRC32_LENGTH + "}+" + sanitizedTrailing));
                Matcher m = Pattern.compile(regx.toString()).matcher(getName());

                // go through matcher and to list all possible CRC values
                while(m.find())
                {
                    String str = m.group(0);
                    //strip out delimiters
                    possibleCRC.add(str.substring(leading.length(), str.length() - trailing.length()));
                }
                regx.close();
            }
        }
        return (String[])possibleCRC.toArray(new String[0]);
    }

    /**
     * Checks if a character is in string
     * 
     * @param c Character to check
     * @param pattern String to find character in
     * @return Character found in pattern
     */
    private boolean charContains(char c, String pattern)
    {
        for(char p : pattern.toCharArray())
        {
            if(c == p)
            {
                return true;
            }
        }
        return false;
    }
    
    // Uses apache.org.commons getName for name consistency on Windows and Unix
    public String getName()
    {
        return FilenameUtils.getName(super.getName());
    }
    
    // Returns uniqueID of file
    public String toString()
    {
        return getUniqueID();
    }

    // ~~~~~ Getters & Setters ~~~~~ //
    /**
     * @return UniqueID of file
     */
    public String getUniqueID()
    {
        if(uniqueID == null)
        {
            uniqueID = crc + " \"" + getName() + "\"";
            return uniqueID;
        }
        return uniqueID;
    }

    /**
     * @return Depth of file relative to source or destination root
     */
    public int getDepth()
    {
        return level;
    }

    /**
     * @return CRC32 value in 8 hexadecimal
     */
    public String getCRC()
    {
        return crc;
    }

    /**
     * Set the crc value of the file
     * @param crc CRC32 value in 8 hexadecimal
     */
    public void setCRC(String crc)
    {
        this.crc = crc;
    }
    
    /**
     * Check if CRC exist in filename
     * @param delimiters Delimiters to check CRC in, can be empty
     * @return True of CRC found, false if not found in filename
     */
    public boolean getHasCRCInFilename(String delimiters)
    {
        if(possibleCRCInFilename == null)
        {
            possibleCRCInFilename = findCRCInFilename(delimiters);
        }
        return possibleCRCInFilename.length > 0;
    }
}
