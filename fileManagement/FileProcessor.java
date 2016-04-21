package fileManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;

import fileManagement.SynchiveFile.ChecksumException;
import support.Utilities;
import synchive.EventCenter;
import synchive.EventCenter.Events;
import synchive.Settings;


/**
 * Generate a list of CRC32 values for each file in the directory including subdirectories
 * @author Tony Hsu
 * @category Model
 * @structure: An arrayList of all files (flat mapping) and hashtable of all files by directories (structured mapping)
 * @process: Add initial directory to a stack. While the stack is not empty, remove the directory and process
 *           all files in the directory. If the file is a directory, add it to the stack. Repeat the process
 *           until the stack is empty. If it is a file, generate unique id with CRC32 value. 
 *           Depending on location, a flat mapping or structured mapping is generated.
 */
public class FileProcessor
{
    public static final String FOLDER_PREFIX = "~";
    public static final String LEFTOVER_FOLDER = "~leftovers";

    private ArrayList<SynchiveFile> fileList; // flat mapping of files for source
    // "Folder Name" -> "Directory"; Directory contains "FileID" -> "FlagInfo"
    private Hashtable<String, SynchiveDirectory> directoryList; // structured mapping for destination
    
    private File directory; // location of directory
    private File idFile; // id file in directory
    private Stack<SynchiveFile> directoriesToProcess;
    private BufferedWriter output;

    int location; // Des or Src

    // folder = folder location
    // location = constant DES or SRC
    public FileProcessor(File directory, int location) throws IOException
    {
        if(!directory.isDirectory()) // break if not a folder
        {
            String errorDescription = "Folder: \"" + directory.getName() + "\" is not a directory";
            postEvent(Events.ErrorOccurred, errorDescription);
            throw new Error(errorDescription);
        }

        this.location = location;
        this.directory = directory;

        if(location == Utilities.SOURCE)
        {
            fileList = new ArrayList<SynchiveFile>(); // source uses flat mapping
        }
        else
        {
            directoryList = new Hashtable<String, SynchiveDirectory>(); // des uses structural mapping
            idFile = new File(directory.getPath() + "\\" + Utilities.CRC_FILE_NAME);
        }
        
        directoriesToProcess = new Stack<SynchiveFile>(); // used to recurse through all folders

        if(location == Utilities.DESTINATION)
        {   // Generate id's if it does not exist. 
            if(!idFile.exists())
            {
                postEvent(Events.Status,
                    "Reading and Generating Destination FileID List... " + "This may take some time, but subsequent runs will be quick.");
                output = new BufferedWriter(new FileWriter(idFile));
                generateIDs(output);
                output.close(); // close file
            }
            postEvent(Events.Status, "Reading in fileIDs");
            readinFileIDs();
            postEvent(Events.Status, "Finished reading in fileIDs");
        }
        else
        {
            postEvent(Events.Status, "Reading in Source files... This may take some time...");
            generateIDs(null);
        }
        postEvent(Events.Status,
            location == Utilities.DESTINATION ? "Finished Generating Destination FileID List" : "Finished Reading Source Files");
       
    }

    // returns all files with unique id
    public ArrayList<SynchiveFile> getCRCFileList()
    {
        return fileList;
    }

    // returns all files with unique id in directory structure with
    public Hashtable<String, SynchiveDirectory> getDirectoryList()
    {
        return directoryList;
    }

    // creates a structure mapping from fileIDs
    private void readinFileIDs() throws IOException
    {
        Scanner sc = new Scanner(idFile);
        if(!sc.hasNextLine()) // in-case empty file
        {
            sc.close();
            postEvent(Events.Status,
                "Empty ID file, Regenerating Destination FileID List... ");
            output = new BufferedWriter(new FileWriter(idFile));
            generateIDs(output);
            output.close(); // close file
            postEvent(Events.Status,
                "Finished regenerating ID file... Continuing reading in");
            sc = new Scanner(idFile);
        }
        
        String str = sc.nextLine();

        while(sc.hasNextLine() && str.startsWith(FOLDER_PREFIX)) // not finished and is a folder
        {
            SynchiveDirectory dir = new SynchiveDirectory(str);
            directoryList.put(str, dir); // store folder in hashtable
            postEvent(Events.Status, "Parsing folder... " + dir.getRealFolderName());
            str = sc.nextLine();
            while(!str.startsWith(FOLDER_PREFIX)) // store files in folder
            {
                dir.addFile(str, SynchiveDirectory.FileFlag.FILE_NOT_EXIST);
                if(sc.hasNextLine())
                    str = sc.nextLine();
                else
                    break;
            }
        }
        sc.close();
    }

    // scans through directory and calls helper method to calculate crc value
    private void generateIDs(BufferedWriter writeOut) throws IOException
    {
        directoriesToProcess.add(new SynchiveFile(directory)); // adds root dir

        while(!directoriesToProcess.isEmpty()) // repeat until all folders are read
        {
            SynchiveFile f = directoriesToProcess.pop();
            if(writeOut != null) // only write to file if destination
            {
                writeOut.write(Utilities.convertToDirectoryLvl(f.getPath(), f.getLevel(), directory.getPath()));
                writeOut.newLine();
            }
            readinFilesInDirectory(f, writeOut); // read the files in the folder
        }
    }

    // helper method for readinDirectory()
    private void readinFilesInDirectory(SynchiveFile f, BufferedWriter writeOut) throws IOException
    {
        // skip over generated folder or folder not needing to be copied
        if(f.getName() == LEFTOVER_FOLDER || 
            Settings.getInstance().getSkipFoldersName().contains(f.getName()))
        {
            return;
        }
        for(File fileEntry : f.listFiles()) // go through each file in directory
        {
            if(fileEntry.isDirectory()) // add child folders to read as well
            {
                directoriesToProcess.push(new SynchiveFile(fileEntry, f.getLevel() + 1));
            }
            else
            {
                // create new file entry
                SynchiveFile temp = new SynchiveFile(fileEntry, f.getLevel());
                    
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
                            temp.determineCopyingAllowed(Settings.getInstance().getScanWithoutDelimFlag() ? 
                                "" : Settings.getInstance().getCrcDelimiterText());
                        }
                        catch (ChecksumException e) // catch file checksum mismatch
                        {
                            postEvent(Events.ErrorOccurred, 
                                "Checksum mismatch for: \"" + temp.getName() + "\"\n  - Calculated: [" + temp.getCRC() + "] Found: " + e.getMessage());
                        }
                    }

                    if(writeOut != null) // only write to file if destination
                    {
                        writeOut.write(temp.generateUniqueID()); // wrtie uid to file
                        writeOut.newLine();
                    }
                    else
                    {
                        // add  to source mapping
                        fileList.add(temp); // adds to file list
                    }
                }
            }
        }
    }
    
    /**
     * Add CRC to filename if there is flag checked and no CRC already in filename
     * @param temp File to add CRC to filename
     * @return Original file if setting not enabled, or rename error. New file if rename sucessful.
     */
    private SynchiveFile addCRCToFilename(SynchiveFile temp)
    {
        if(Settings.getInstance().getCrcInFilenameFlag() 
            && !temp.getHasCRCInFilename(Settings.getInstance().getCrcDelimiterText()))
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

    private void postEvent(Events e, String str)
    {
        EventCenter.getInstance().postEvent(e, str);
    }
}
