package fileManagement.fileProcessor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import fileManagement.SynchiveDirectory.FileFlag;
import support.Utilities;
import support.Utilities.ChecksumException;
import synchive.EventCenter;
import synchive.Globals;
import synchive.Settings;
import synchive.EventCenter.Events;
import synchive.EventCenter.RunningStatusEvents;

/**
 * Abstract class for handling files. Extend to process the information.
 * 
 * @author Tony Hsu
 * @structure Does not store any information processed, requires extending class to store information.
 */
public abstract class FileProcessorBase
{
    /**
     * Prefix to determine if read from file line is a directory
     */
    private final String DIR_LINE_PREFIX = "~";
    /**
     * Location of directory
     */
    private File root;
    /**
     * Stack used to recursively read sub-directories
     */
    private Stack<SynchiveFile> directoriesToProcess;
    
    /**
     * Structured mapping of "DirectoryID" -> "SynchiveDirectory"
     */
    private Hashtable<String, SynchiveDirectory> directoryList;
    
    /**
     * Flag to determine if reading from idFile or not
     */
    private boolean doesRootIDFileExist;
    
    /**
     * Flag to determine if file renaming has occurred
     */
    private boolean hasDoneRenaming;
    
    /**
     * Initializes a directory to be parsed and processed.
     * 
     * @param directory Directory to process
     * @throws Throws folder not a directory
     */
    public FileProcessorBase(File directory) throws Error
    {
        if(!directory.isDirectory()) // break if not a folder
        {
            String errorDescription = "Folder: \"" + directory.getName() + "\" is not a directory";
            postEvent(Events.ErrorOccurred, errorDescription);
            postEvent(Events.RunningStatus, 
                new Object[] {RunningStatusEvents.Error, "Error"});
            throw new Error(errorDescription);
        }
        
        doesRootIDFileExist = false;
        hasDoneRenaming = false;
        this.root = directory;
        
        directoriesToProcess = new Stack<SynchiveFile>(); // used to recurse through all folders
        directoriesToProcess.add(new SynchiveFile(directory)); // adds root dir
        directoryList = new Hashtable<String, SynchiveDirectory>(); // parser uses structural mapping
    }
    
    /**
     * Parses and processes a directory including idFiles. Stores parsed info and providing useful information to abstract methods.
     */
    public void readinIDs()
    {
        while(!directoriesToProcess.isEmpty())
        {
            SynchiveFile file = directoriesToProcess.pop();
            File[] idFiles = file.listFiles(new FileFilter() // filter out every file except idFile
            {
                @Override
                public boolean accept(File arg0)
                {
                    return arg0.getName().equals(Utilities.ID_FILE_NAME);
                }
            });

            // if idFile not found, process each file within directory
            if(idFiles.length == 0)
            {
                String dirID = SynchiveDirectory.getDirectoryUniqueID(file.getPath(), file.getDepth(), root.getPath());
                processingDirectory(new SynchiveDirectory(dirID)); // internally store info & abstract method
                readFilesWithinDirectory(file);
            }
            else
            {
                try
                {
                    // check if idFile is in root or not
                    if(file.getPath().equals(getRoot().getPath())) 
                    {
                        doesRootIDFileExist = true;
                    }
                    else
                    {
                        // if current file is a subIDFile
                        //TODO Delete subIDFile sometime later? or update it?
                    }
                    
                    postEvent(Events.Status, "Reading in fileIDs for \"" + idFiles[0].getParentFile().getName() + "\"");
                    readFromIDFile(idFiles[0], file.getDepth());
                }
                catch (IOException e)
                {
                    //TODO delete IDFIle and readd to top of stack to reprocess
                } //catch bad fileFormat and readFilesWithin instead
            }
        }
    }
    
    /**
     * Read in the directory and add sub-directories to the stack to be processed.
     * Each file within the directory will be processed.
     * @param file Directory to process
     */
    private void readFilesWithinDirectory(SynchiveFile file)
    {
        for(File fileEntry : file.listFiles()) // go through each file in directory
        {
            if(fileEntry.isDirectory() && 
                !fileEntry.getName().equals(Utilities.LEFTOVER_FOLDER) &&
                !Settings.getInstance().getSkipFoldersName().contains(fileEntry.getName())) // add child folders to read as well
            {
                directoriesToProcess.push(new SynchiveFile(fileEntry, file.getDepth() + 1));
            }
            else
            {
                // create new file entry
                SynchiveFile temp = new SynchiveFile(fileEntry, file.getDepth());
                    
                // skip over generated files or extension type not needing to be copied
                if(!temp.getName().equals(Utilities.ID_FILE_NAME) && 
                 !temp.getName().equals(Utilities.AUDIT_FILE_NAME) &&
                 temp.determineProcessingAllowed(Settings.getInstance().getSkipExtensionTypesText()))
                {
                    postEvent(Events.ProcessingFile, "Reading file... " + temp.getName());
                    
                    try
                    {
                        String val = Utilities.calculateCRC32(fileEntry); // get crc value
                        temp.setCRC(val);
                    }
                    catch (ChecksumException e1) // catch file checksum mismatch
                    {
                        temp.setCRC("");
                        postEvent(Events.ErrorOccurred, e1.getMessage());
                    } 
                    
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
                                "Checksum mismatch for: \"" + temp.getName() + "\"\n  " +
                                    "- Calculated: [" + temp.getCRC().toUpperCase() + "] Found: " + e.getMessage());
                        }
                    }
                    
                    if(temp.copyAllowed())
                    {
                        String dirID = SynchiveDirectory.getDirectoryUniqueID(file.getPath(), file.getDepth(), root.getPath());
                        fileProcessed(temp, new SynchiveDirectory(dirID)); // internally store info & abstract method
                    }
                }
            }
        }
    }
    
    /**
     * Process directory and sub-directories through idFile.
     * @param file Directory to process
     * @param baseDepth Depth level relative to root
     * @throws IOException Throws error if unable to open file or bad data.
     */
    private void readFromIDFile(File file, int baseDepth) throws IOException
    {
        BufferedReader sc = new BufferedReader(
            new InputStreamReader(
                             new FileInputStream(file), "UTF8"));
        String str = sc.readLine(); // strip out header
        if(str == null) // in-case of empty file
        {
            sc.close();
            throw new IOException("Empty File");
        }
        
        String locationDir = file.getParentFile().getPath(); // directory of root        
        
        str = sc.readLine();
        //TODO: skip directory && extension types
        while(str != null && str.startsWith(DIR_LINE_PREFIX)) // not finished and is a folder
        {
            String[] splitDir = str.split(" ", 2); // [level, path]
            if(splitDir.length != 2)
            {
                sc.close();
                throw new IOException("Bad format found");
            }
            int newLevel = Integer.parseInt(String.valueOf(splitDir[0].charAt(1))) + baseDepth;
            
            String path = locationDir + splitDir[1];
            String dirID = SynchiveDirectory.getDirectoryUniqueID(path, newLevel, getRoot().getPath());
            SynchiveDirectory dir = new SynchiveDirectory(dirID);
            
            processingDirectory(dir); // internally store info & abstract method
            
            str = sc.readLine();
            while(str != null && !str.startsWith(DIR_LINE_PREFIX)) // store files in folder
            {
                String[] splitStr = str.split(" ", 2); // [crc, name]
                if(splitStr.length != 2)
                {
                    sc.close();
                    throw new IOException("Bad format found");
                }
                
                // reconstruct file path (root path + directory path + fileName)
                String fileLoc = locationDir + splitDir[1] + "\\" + 
                    splitStr[1].substring(1, splitStr[1].length() - 1);
                
                // add crc to filename is flag checked 
                // TODO improve efficiency by checking if file generated with Synchive Monitor)
                SynchiveFile info = new SynchiveFile(
                    new File(fileLoc), newLevel, splitStr[0]);
                SynchiveFile temp = addCRCToFilename(info); // will return normal name if option not checked
                
                
                fileProcessed(temp, dir); // internally store info & abstract method
                str = sc.readLine();
            }
        }
        sc.close();
    }
    
    /**
     * Add CRC to filename if there is flag checked and no CRC already in filename
     * @param temp File to add CRC to filename
     * @return Original file if setting not enabled, or rename error. New file if rename successful.
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
                            hasDoneRenaming = true;
                            return new SynchiveFile(newFile, temp.getDepth(), temp.getCRC());
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
     * Write-out the structural mapping for storage.
     * @param checkExist If true, skip files with FILE_NOT_EXIST flag. If false, include every file.
     * @throws IOException Exceptions thrown from BufferWriter
     */
    public void writeToFile(boolean checkExist) throws IOException
    {
        CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
        BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(getRoot().getPath() + "\\" + Utilities.ID_FILE_NAME)), encoder));
        output.write("Synchive " + Globals.VERSION + " - root=" + getRoot().getPath());
        output.newLine();
        
        Enumeration<String> keys = directoryList.keys();
        while(keys.hasMoreElements())
        {
            String key = keys.nextElement();
            SynchiveDirectory dir = directoryList.get(key);
            output.write(dir.getUniqueID());
            output.newLine();
            
            Hashtable<String, FileFlag> file = dir.getLookupTable();
            Enumeration<String> fileEnum = file.keys();
            while(fileEnum.hasMoreElements())
            {
                String fileID = fileEnum.nextElement();
                if(checkExist && file.get(fileID) != FileFlag.FILE_EXIST)
                {
                    continue;
                }
                output.write(fileID);
                output.newLine();
            }
        }
        output.close();
        postEvent(Events.Status, "Finished writting to IDFile");
    }
    
    /**
     * Method gets called for each file (non directory) processed.
     * Stores information and calls abstract method.
     * @param file File processed
     * @param dir Directory of file
     */
    private void fileProcessed(SynchiveFile file, SynchiveDirectory dir)
    {
        // Stores the file within it's directory 
        directoryList.get(dir.getUniqueID()).addFile(file.getUniqueID(), FileFlag.FILE_NOT_EXIST);
        didProcessFile(file, dir);
    }
    
    /**
     * Method gets called for each directory processed
     * Stores information and calls abstract method.
     * @param dir Directory to be processed
     */
    private void processingDirectory(SynchiveDirectory dir)
    {
        // Stores the directory into hashtable
        directoryList.put(dir.getUniqueID(), new SynchiveDirectory(dir.getUniqueID()));
        willProcessDirectory(dir);
    }
    
    /**
     * Short handed method
     * @param e Events
     * @param obj Any data
     */
    private void postEvent(Events e, Object obj)
    {
        EventCenter.getInstance().postEvent(e, obj);
    }
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * Handles delimiters to use for CRC in filename.
     * @return Delimiters or empty string if ScanWithoutDelimFlag checked
     */
    private String getCRCDelimiters()
    {
        return Settings.getInstance().getScanWithoutDelimFlag() ? "" : Settings.getInstance().getCrcDelimiterText();
    }
    
    /**
     * @return Root file (location origin)
     */
    public File getRoot()
    {
        return root;
    }
    
    /**
     * @return True if idFile of root exist
     */
    public boolean doesRootIDFileExist()
    {
        return doesRootIDFileExist;
    }
    
    /**
     * @return True if any file has been renamed
     */
    public boolean hasDoneFileRenaming()
    {
        return hasDoneRenaming;
    }
    
    /**
     * @return Structural mapping of each folder
     */
    protected Hashtable<String, SynchiveDirectory> getStructuredMapping()
    {
        return directoryList;
    }
    // ~~~~~ Required override methods ~~~~~~ //
    /**
     * Method gets called for each file (non directory) processed
     * @param file File processed
     * @param dir Directory of file
     */
    public abstract void didProcessFile(SynchiveFile file, SynchiveDirectory dir);
    
    /**
     * Method gets called for each directory processed
     * @param dir Directory to be processed
     */
    public abstract void willProcessDirectory(SynchiveDirectory dir);
}
