package fileManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Scanner;
import java.util.Stack;

import fileManagement.SynchiveFile.ChecksumException;
import support.Utilities;
import synchive.EventCenter;
import synchive.Settings;
import synchive.EventCenter.Events;
import synchive.EventCenter.RunningStatusEvents;

public class FileProcessorBase
{
    public static final String FOLDER_PREFIX = "~";
    public static final String LEFTOVER_FOLDER = "~leftovers";
    
    private File root; // location of directory
    private boolean hasSubIDFile;
    
    private Stack<SynchiveFile> directoriesToProcess;
    
    public FileProcessorBase(File directory)
    {
        if(!directory.isDirectory()) // break if not a folder
        {
            String errorDescription = "Folder: \"" + directory.getName() + "\" is not a directory";
            postEvent(Events.ErrorOccurred, errorDescription);
            postEvent(Events.RunningStatus, 
                new Object[] {RunningStatusEvents.Error, "Error"});
            throw new Error(errorDescription);
        }
        
        this.root = directory;
        hasSubIDFile = false;
        
        directoriesToProcess = new Stack<SynchiveFile>(); // used to recurse through all folders
        directoriesToProcess.add(new SynchiveFile(directory)); // adds root dir
        
//        postEvent(Events.Status, "Reading in fileIDs");
//        readinFileIDs();
//        postEvent(Events.Status, "Finished reading in fileIDs");
    }
    
    public void readinIDs()
    {
        while(!directoriesToProcess.isEmpty())
        {
            SynchiveFile file = directoriesToProcess.pop();
            File[] idFiles = file.listFiles(new FileFilter()
            {
                @Override
                public boolean accept(File arg0)
                {
                    return arg0.getName().equals(Utilities.CRC_FILE_NAME);
                }
            });

            // if idFile not found, process each file within directory
            if(idFiles.length == 0)
            {
                initWriter();
                try
                {
                    willProcessDirectory(file);
                    readFilesWithinDirectory(file);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                try
                {
                    // if there isn't a subIDFile, and current file is one
                    if(!hasSubIDFile && !file.getPath().equals(getRoot())) 
                    {
                        hasSubIDFile = true;
                    }
                    
                    postEvent(Events.Status, "Reading in fileIDs for \"" + idFiles[0].getParentFile().getName() + "\"");
                    readFromIDFile(idFiles[0], file.getLevel());
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } //catch bad fileFormat and readFilesWithin instead
            }
        }
        closeWriter();
    }
    
    private void readFilesWithinDirectory(SynchiveFile file) throws IOException
    {
        for(File fileEntry : file.listFiles()) // go through each file in directory
        {
            if(fileEntry.isDirectory() && !fileEntry.getName().equals(Utilities.LEFTOVER_FOLDER)) // add child folders to read as well
            {
                directoriesToProcess.push(new SynchiveFile(fileEntry, file.getLevel() + 1));
            }
            else
            {
                // create new file entry
                SynchiveFile temp = new SynchiveFile(fileEntry, file.getLevel());
                    
                // skip over generated files or extension type not needing to be copied
                if(!temp.getName().equals(Utilities.CRC_FILE_NAME) && 
                 !temp.getName().equals(Utilities.AUDIT_FILE_NAME) &&
                 temp.determineProcessingAllowed(Settings.getInstance().getSkipExtensionTypesText()))
                {
                    postEvent(Events.ProcessingFile, "Reading file... " + temp.getName());
                    String val = Utilities.calculateCRC32(fileEntry); // get crc value
                    temp.setCRC(val);
                    // could probably optimize this part or reassigning
                    temp = addCRCToFilename(temp); //add CRC to filename if conditions met
                    
                    // do a checksum check if flag enabled
                    if(Settings.getInstance().getCrcCheckFlag())
                    {
                        try
                        {
                            // either scan without delimiters or with delimiters based on flag
                            temp.determineCopyingAllowed(getCRCDelimiters());
                        }
                        catch (ChecksumException e) // catch file checksum mismatch
                        {
                            postEvent(Events.ErrorOccurred, 
                                "Checksum mismatch for: \"" + temp.getName() + "\"\n  - Calculated: [" + temp.getCRC().toUpperCase() + "] Found: " + e.getMessage());
                        }
                    }
                    
                    if(temp.copyAllowed())
                    {
                        didProcessFile(temp, file);
                    }
                }
            }
        }
    }
    
    private void readFromIDFile(File file, int baseLevel) throws IOException
    {
        Scanner sc = new Scanner(file);
        String str = sc.nextLine();
        
        if(str == null) // in-case of empty file
        {
            sc.close();
            throw new IOException("Empty File");
        }
        
        String[] header = str.split("="); // strip out header
        String locationDir = header[1]; // directory of root
        
        str = getNextLine(sc);
        while(str != null && str.startsWith(FOLDER_PREFIX)) // not finished and is a folder
        {
            String[] splitDir = str.split(" ", 2); // [level, path]
            int newLevel = Integer.parseInt(String.valueOf(splitDir[0].charAt(1))) + baseLevel;
            
            String path = locationDir + splitDir[1];
            String dirID = Utilities.convertToDirectoryLvl(path, newLevel, getRoot().getPath());
            SynchiveDirectory dir = new SynchiveDirectory(dirID);
            
            postEvent(Events.Status, "Parsing folder... " + dir.getRealFolderName());
            
            directoryReadFromID(dir);
            
            str = getNextLine(sc);
            while(str != null && !str.startsWith(FOLDER_PREFIX)) // store files in folder
            {
                String[] splitStr = str.split(" ", 2); // [crc, name]
                // reconstruct file path (root path + directory path + fileName)
                String fileLoc = locationDir + splitDir[1] + "\\" + 
                    splitStr[1].substring(1, splitStr[1].length() - 1);
                SynchiveFile temp = new SynchiveFile(
                    new File(fileLoc), newLevel, splitStr[0]);
                
                fileReadFromID(temp, dir);
                str = getNextLine(sc);
            }
        }
        sc.close();
    }
    
    private String getNextLine(Scanner sc)
    {
        return sc.hasNextLine() ? sc.nextLine() : null;
    }
    
    /**
     * Add CRC to filename if there is flag checked and no CRC already in filename
     * @param temp File to add CRC to filename
     * @return Original file if setting not enabled, or rename error. New file if rename sucessful.
     */
    private SynchiveFile addCRCToFilename(SynchiveFile temp)
    {
        if(Settings.getInstance().getCrcInFilenameFlag() && !temp.getHasCRCInFilename(getCRCDelimiters()))
        {
            String[] addCRCToExtentions = Settings.getInstance().getAddCRCToExtensionTypes();
            for(String extension : addCRCToExtentions)
            {
                if(temp.getName().endsWith(extension))
                {
                    postEvent(Events.ProcessingFile, "Adding CRC to filename... " + temp.getName());
                    String[] delimiter = {Settings.getInstance().getCrcDelimLeadingText(), 
                        Settings.getInstance().getCrcDelimTrailingText()};
                    String path = temp.getParent() + "\\" + 
                        Utilities.getFilenameWithCRC(temp.getName(), extension, temp.getCRC(), delimiter);
                    File newFile = new File(path);
                    
                    try
                    {
                        if(temp.renameTo(newFile))
                        {
                            return new SynchiveFile(newFile, temp.getLevel(), temp.getCRC());
                        }
                        else
                        {
                            postEvent(Events.ErrorOccurred, "Unable to add CRC to filename... " + temp.getName());
                            return temp;
                        }
                    }
                    catch(SecurityException e)
                    {
                        postEvent(Events.ErrorOccurred, "Unable to add CRC to filename... " + temp.getName());
                        return temp;
                    }
                }
            }
        }
        return temp;
    }
    
    /**
     * Handles delimiters to use for CRC in filename.
     * @return Delimiters or empty string if ScanWithoutDelimFlag checked
     */
    private String getCRCDelimiters()
    {
        return Settings.getInstance().getScanWithoutDelimFlag() ? "" : Settings.getInstance().getCrcDelimiterText();
    }
    
    // Methods to override
    public void didProcessFile(SynchiveFile file, SynchiveFile dir)
    {
    }
    
    public void willProcessDirectory(SynchiveFile file)
    {
    }
    
    public void directoryReadFromID(SynchiveDirectory dir)
    {
        
    }
    
    public void fileReadFromID(SynchiveFile file, SynchiveDirectory dir)
    {
        
    }
    
    public void initWriter()
    {
        
    }
    
    public void closeWriter()
    {
        
    }
    
    public File getRoot()
    {
        return root;
    }
    
    public Boolean hasSubIDFile()
    {
        return hasSubIDFile;
    }
    
    private void postEvent(Events e, Object obj)
    {
        EventCenter.getInstance().postEvent(e, obj);
    }
}
