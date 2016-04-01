package fileManagement;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.ExceptionList;

import synchive.Settings;


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
    private final int numOfCharInCRC = 8;
    private final String specialCharacters = "[]{}()+\\^&.$?*|:<>=!";
    private String uniqueID; // file path + CRC value
    private int level; // 0 = root, 1+ directory in root
    private String crc;
    private boolean copyAllowed; // determine if calculated CRC matches filename CRC

    public SynchiveFile(File file)
    {
        super(file.getPath());
        this.level = 0;
        crc = "";
        copyAllowed = true;
    }

    public SynchiveFile(File file, int level)
    {
        super(file.getPath());
        this.level = level;
        crc = "";
        copyAllowed = true;
    }
    
    public boolean copyAllowed()
    {
        return copyAllowed;
    }
    
    //determines if copying is allowed by finding CRC value in fileName.
    //if it cannot find CRC in fileName, assume none exist and dont check
    //else scan through possibleCRC and return if matching crc otherwise throw an exception
    public void determineCopyingAllowed(String delimiter) throws ChecksumException
    {
        String[] possibleCRC = findCRCInFileName(delimiter);
        if(possibleCRC.length == 0) //no crc found
        {
            return;
        }
        else
        {
            copyAllowed = false;
            for(String possible: possibleCRC) // only set copyAllowed if matching crc
            {
                if(crc.compareToIgnoreCase(possible) == 0)
                {
                    copyAllowed = true;
                    return;
                }
            }
            System.out.println("Bad crc:" + Arrays.toString(possibleCRC));
            //cannot find matching CRC, throw exception
            throw new ChecksumException(Arrays.toString(possibleCRC));
        }
    }
    
    private String[] findCRCInFileName(String delimiter)
    {
        ArrayList<String> possibleCRC = new ArrayList<String>();
        String[] splitDelim = delimiter.split(",");
        
        for(String delim : splitDelim) // go through each delimiter
        {
            String trimmed = delim.trim(); // get rid of spacings
            // only handle even number, doesn't make sense having mismatching lengths
            if(trimmed.length() % 2 == 0)
            {
                String leading = trimmed.substring(0, delim.length()/2); // first half is left side
                String trailing = trimmed.substring(delim.length()/2); // last half is right side
                String sanitizedLeading = ""; 
                String sanitizedTrailing = "";
                
                // sanitized the strings in case of special characters used in pattern matching
                for(int i = 0; i < leading.length(); i++)
                {
                    char right = leading.charAt(i);
                    char left = trailing.charAt(i);
                    
                    sanitizedLeading += charContains(right, specialCharacters) ? ("\\" + right) : right;
                    sanitizedTrailing += charContains(left, specialCharacters) ? ("\\" + left) : left;
                }
                
                // String pattern matching using delimiters + only letters and numbers for CRC
                // Hex can only be a-f and 0-9
                Formatter regx = new Formatter(
                    new StringBuilder(sanitizedLeading + "[a-fA-F0-9]{" + numOfCharInCRC + "}+" + sanitizedTrailing));
                Pattern p = Pattern.compile(regx.toString());
                Matcher m = p.matcher(getName());
                
                // go through matcher and to list all possible CRC values
                while(m.find())
                {
                    String str = m.group(0);
                    System.out.println(str);
                    possibleCRC.add(str.substring(leading.length(), str.length() - trailing.length()));
                }
                regx.close();
            }
        }
        for(String s : possibleCRC)
        {
            System.out.println(s);
        }
        return (String[])possibleCRC.toArray(new String[0]);
    }
        
    //checks if character is in pattern
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
    
    // ~~~~~ Getters & Setters ~~~~~ //
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
    
    public class ChecksumException extends Exception
    {
        public ChecksumException(String message)
        {
            super(message);
        }
    }
}


