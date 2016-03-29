package fileManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Stack;

import javax.rmi.CORBA.Util;

import support.Utilities;
import synchive.EventCenter;
import synchive.EventCenter.Events;


/**
 * @Category: Processing Class: To generate a list of crc values for each file in the directory
 * @Structure: A ArrayList of FileWithProperties that include it's unique id (flat mapping)
 * @Process: Add initial directory to a stack. While the stack is not empty, remove the directory and process
 *           all files in the directory. If the file is a directory, add it to the stack. Repeat the process
 *           until the stack is empty. If it is a file, generate unique id with crc value. If
 *           location is source, deletes previous crcFile and generates a new one with an arrayList to be used
 */
public class FileProcessor
{
    public static final String FOLDER_PREFIX = "~";
    public static final String LEFTOVER_FOLDER = "~leftovers";

    File folder, idFile;
    Stack<SynchiveFile> directoriesToProcess;
    BufferedWriter output;

    ArrayList<SynchiveFile> fileList; // flat mapping of files

    // "Folder Name" -> "Directory"; Directory contains "FileID" -> "FlagInfo"
    private Hashtable<String, SynchiveDirectory> directoryList; // structured mapping

    int location; // Des or Src

    // folder = folder location
    // location = constant DES or SRC
    public FileProcessor(File folder, int location) throws IOException
    {
        if(!folder.isDirectory()) // break if not a folder
        {
            String errorDescription = "Folder: \"" + folder.getName() + "\" is not a directory";
            postEvent(Events.ErrorOccurred, errorDescription);
            throw new Error(errorDescription);
        }

        this.location = location;
        this.folder = folder;

        if(location == Utilities.SOURCE)
        {
            fileList = new ArrayList<SynchiveFile>(); // source uses flat mapping
        }
        else
        {
            directoryList = new Hashtable<String, SynchiveDirectory>(); // des uses structural mapping
            idFile = new File(folder.getPath() + "\\" + Utilities.CRC_FILE_NAME);
        }
        
        directoriesToProcess = new Stack<SynchiveFile>(); // used to recurse through all folders

        if(location == Utilities.DESTINATION)
        {   // Generate id's if it does not exist. 
            if(!idFile.exists())
            {
                postEvent(Events.Status,
                    "Reading and Generating Destination FileID List... " + "This may take some time, but subsequent runs will be quick.");
                output = new BufferedWriter(new FileWriter(idFile));
                generateIDs(true);
                output.close(); // close file
            }
            postEvent(Events.Status, "Reading in fileIDs");
            readinFileIDs();
            postEvent(Events.Status, "Finished reading in fileIDs");
        }
        else
        {
            postEvent(Events.Status, "Reading in Source files... This may take some time...");
            generateIDs(false);
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
    private void generateIDs(boolean writeOut) throws IOException
    {
        directoriesToProcess.add(new SynchiveFile(folder)); // adds root dir

        while(!directoriesToProcess.isEmpty()) // repeat until all folders are read
        {
            SynchiveFile f = directoriesToProcess.pop();
            if(writeOut) // only write to file if destination
            {
                output.write(Utilities.convertToDirectoryLvl(f.getPath(), f.getLevel(), folder.getPath()));
                output.newLine();
            }
            readinFilesInDirectory(f, writeOut); // read the files in the folder
        }
    }

    // helper method for readinDirectory()
    private void readinFilesInDirectory(SynchiveFile f, boolean writeOut) throws IOException
    {
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

                if(!temp.getName().equals(Utilities.CRC_FILE_NAME) // skip over generated files
                && !temp.getName().equals(Utilities.AUDIT_FILE_NAME))
                {
                    postEvent(Events.ProcessingFile, "Reading file... " + fileEntry.getName());
                    String val = Utilities.calculateCRC32(fileEntry); // get crc value
                    temp.setCRC(val);

                    fileList.add(temp); // adds to file list

                    if(writeOut) // only write to file if destination
                    {
                        output.write(temp.generateUniqueID()); // wrtie uid to file
                        output.newLine();
                    }
                }
            }
        }
    }

    private void postEvent(Events e, String str)
    {
        EventCenter.getInstance().postEvent(e, str);
    }
}
