package synchive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import fileManagement.fileProcessor.DestinationFileProcessor;
import fileManagement.fileProcessor.SourceFileProcessor;
import support.Utilities;
import support.Utilities.ChecksumException;
import synchive.EventCenter.Events;
import synchive.EventCenter.RunningStatusEvents;

/**
 * Compares a source and destination location and syncs up destination to be exactly the same as source.
 * Files not found in source but exist in destination are moved into a separate directory.
 * 
 * @author Tony Hsu
 */
public class SynchiveDiff
{
    /**
     * Folder name of extra files in destination
     */
    private String LEFTOVER_FOLDER = Utilities.LEFTOVER_FOLDER;
    /**
     * Source location directory
     */
    private File srcLoc;
    /**
     * Destination location directory
     */
    private File desLoc;
    /**
     * Mapping of each file in directory format
     */
    private Hashtable<String, SynchiveDirectory> destinationList;
    /**
     * List of all files in source location
     */
    private ArrayList<SynchiveFile> sourceList;
    /**
     * File processor for destination
     */
    private DestinationFileProcessor desReader;
    
    /**
     * Initializes the sync.
     * 
     * @param curDir Source location
     * @param backupDir Destination location
     */
    public SynchiveDiff(File curDir, File backupDir) 
    {
        this.srcLoc = curDir;
        this.desLoc = backupDir;

        desReader = new DestinationFileProcessor(backupDir);
        
    }
    
    /**
     *  Reads in destination and source locations.
     */
    private void readInLocations()
    {
        destinationList = desReader.getFiles();
        SourceFileProcessor rd = new SourceFileProcessor(srcLoc);
        sourceList = rd.getFiles();
    }

    /**
     * Scan through sourceList and for each file in source,
     *  mark if found in destination, otherwise copy into destination. 
     * Afterwards, if file in destination has not been marked, 
     * delete it by moving the files into a separate location.
     */
    public void syncLocations()
    {
        readInLocations(); // populate file list
        try
        {
            postEvent(Events.Status, "Comparing Differences...");
            for(int i = 0; i < sourceList.size(); i++)
            {
                // get file to search and search in hashTable of directories
                SynchiveFile temp = sourceList.get(i); // file to parse through
                
                if(!temp.copyAllowed()) // if file determined to be bad, skip file
                {
                    postEvent(Events.ErrorOccurred, "Did not copy \"" + temp.getName() + "\" due to CRC mismatch.");
                    continue;
                }
                
                String dirUID = SynchiveDirectory.getDirectoryUniqueID(
                    temp.getParentFile().getPath(), temp.getDepth(), srcLoc.getPath());
                SynchiveDirectory dir = destinationList.get(dirUID);
                boolean isRoot = temp.getParent().equals(srcLoc.getPath()) ? true : false; // if file is in root dir

                if(dir != null && dir.getLookupTable().size() > 0) // if directory exist find file in directory
                {
                    if(!dir.doesFileExist(temp.getUniqueID())) // if file does not exist
                    {
                        dir.addFile(temp.getUniqueID(), SynchiveDirectory.FileFlag.FILE_EXIST); // add to hashTable
                        copyFile(temp, StandardCopyOption.REPLACE_EXISTING); // Copy file over
                        postEvent(Events.ProcessingFile, isRoot ? "Added \"" + temp.getName() + "\" to \"root\"" : 
                            "Added \"" + temp.getName() + "\" to \"" + dir.getRelativeDirectoryPath() + "\"");
                    }
                }
                else
                {
                    // else make new directory and add to destinationList
                    String relativeDir = isRoot ? "\\" : temp.getParentFile().getName();
                    String relativeDirFromRoot = temp.getParent().substring(srcLoc.getPath().length());
                    String destinationDir = desLoc.getPath() + relativeDirFromRoot;
                    File fd = new File(destinationDir);
                    
                    if(!isRoot)
                    {
                        createDirectory(fd);
                    }
                    
                    SynchiveDirectory newDir =
                        isRoot ? new SynchiveDirectory(SynchiveDirectory.getDirectoryUniqueID(desLoc.getPath(), 0, desLoc.getPath()))
                            : new SynchiveDirectory(SynchiveDirectory.getDirectoryUniqueID(fd.getPath(), temp.getDepth(), desLoc.getPath()));

                    newDir.setRelativeDirectoryPath(relativeDir);
                    newDir.addFile(temp.getUniqueID(), SynchiveDirectory.FileFlag.FILE_EXIST); // add file to new folder
                    destinationList.put(newDir.getUniqueID(), newDir); // add newDir to folderHashTable
                    copyFile(temp, StandardCopyOption.REPLACE_EXISTING); // copy file over
                    
                    postEvent(Events.ProcessingFile, isRoot ? 
                        "Added \"" + temp.getName() + "\" to \"root\"" :
                        "Added \"" + temp.getName() + "\" to \"" + newDir.getRelativeDirectoryPath() + "\"");
                }
            }
            
            // after completing all files
            postEvent(Events.ProcessingFile, "Rewritting CRC file...");
            desReader.writeToFile(true); // writes idFile for destination
            cleanupDestination(); // cleanup
            postEvent(Events.Status, "Operation Completed");
            postEvent(Events.RunningStatus, 
                new Object[] {RunningStatusEvents.Completed, "Completed"});
        }
        catch (IOException | Error e)
        {
        }
    }

    /**
     * Makes directory for file. Will recurse through ensuring all directories created.
     * 
     * @param location File with missing directory
     * @throws IOException Throws unable to make directory
     */
    private void createDirectory(File location) throws IOException
    {
        // recurse through parent directories to ensure there is a proper path
        Path parentDir = location.getParentFile().toPath();
        if(!Files.exists(parentDir)) 
        {
            createDirectory(location.getParentFile());
        }
        
        location.mkdir(); // makes the directory
        postEvent(Events.ProcessingFile, "Directory \"" + location.getName() + "\" Created");
    }

    /**
     * Copies file from source to destination with same name and relative directory.
     * 
     * @param file File to be copied over to
     * @param op Copy options
     * @throws IOException Throws unable to copy file
     */
    private void copyFile(SynchiveFile file, StandardCopyOption op) throws IOException
    {
        String relativePath = file.getParent().substring(srcLoc.getPath().length());
        String destinationPath = desLoc.getPath() + relativePath + "\\" + file.getName();
        try
        {
            Files.copy(Paths.get(file.getPath()), Paths.get(destinationPath), op);
        }
        catch (IOException | UnsupportedOperationException | SecurityException e)
        {
            postEvent(Events.ErrorOccurred, "Unable to copy file " + file.getName());
        }
        // CRC32 Check
        // if failed... delete file and try again?
        // is this even necessary?
        try
        {
            if(file.getCRC().compareToIgnoreCase(Utilities.calculateCRC32(new File(destinationPath))) != 0)
            {
                postEvent(Events.ErrorOccurred, "Copy CRC MISMATCH for file: " + file.getName());
            }
        }
        catch (ChecksumException e)
        {
            postEvent(Events.ErrorOccurred, e.getMessage());
        }
    }

    /**
     * Moves file from destination to leftovers folder. Will remove original file.
     * 
     * @param file File to be moved
     * @param op Copy options
     * @throws IOException Throws unable to move file
     */
    private void moveFile(File file, StandardCopyOption op) throws IOException
    {
        String relativePath = file.getParent().substring(desLoc.getPath().length());
        String destinationPath = desLoc.getPath() + "\\" + LEFTOVER_FOLDER + relativePath + "\\" + file.getName();

        try
        {
            Files.move(Paths.get(file.getPath()), Paths.get(destinationPath), op);
            postEvent(Events.ProcessingFile, 
                "File \"" + file.getName() + "\" in \"" + relativePath + 
                "\" not found in source. Moved to \"" + LEFTOVER_FOLDER + "\"");
        }
        catch (IOException e)
        {
            postEvent(Events.ErrorOccurred, "Unable to move file: " + file.getName() + " to \"" + LEFTOVER_FOLDER + "\"");
        }
    }

    /**
     * Removes empty directories. Will recurse through removing empty parent directories.
     * 
     * @param file Directory to remove
     * @throws IOException Throws unable to remove directory.
     */
    private void removeEmptyDirectories(File file) throws IOException
    {
        if(file.isDirectory() && file.list().length == 0)
        {
            File parent = file.getParentFile(); // get parent directory
            file.delete(); // delete current directory
            postEvent(Events.ProcessingFile, "Deleted empty directory \"" + file.getName());

            if(parent.getPath().equals(desLoc.getPath())) { // return if root directory
                return;
            } 
            
            removeEmptyDirectories(parent);
        }
    }

    /**
     * Cleanup directory by moving files not found in source into a leftover directory and removing empty directories.
     */
    private void cleanupDestination()
    {
        try
        {
            //TODO grab element directory instead of key
            Enumeration<String> enu = destinationList.keys(); // gets each directory in destination
            while(enu.hasMoreElements()) // go through each directory
            {
                String folderName = enu.nextElement();
                // go through files in folder
                SynchiveDirectory dir = destinationList.get(folderName);
                Enumeration<String> enuFiles = dir.getLookupTable().keys();
                while(enuFiles.hasMoreElements()) // go through each file in directory
                {
                    String fileCRC = enuFiles.nextElement();
                    SynchiveDirectory.FileFlag val = dir.getValueForKey(fileCRC);
                    if(val != null)
                    {
                        if(val == SynchiveDirectory.FileFlag.FILE_NOT_EXIST)
                        {
                            // make leftover folder for not found ones
                            File leftoversFolder = new File(desLoc.getPath() + "\\" + LEFTOVER_FOLDER);
                            if(!Files.exists(leftoversFolder.toPath()))
                                createDirectory(leftoversFolder);

                            // Get file that needs to be removed
                            String filePath = getFilePathFromFileCRC(desLoc, dir, fileCRC);
                            File toRemove = new File(filePath);
                            String toRemoveLeftoverPath = getLeftoverPath(toRemove);
                            File toRemoveLeftover = new File(toRemoveLeftoverPath);
                            if(!Files.exists(toRemoveLeftover.getParentFile().toPath()))
                                createDirectory(toRemoveLeftover.getParentFile());

                            moveFile(toRemove, StandardCopyOption.REPLACE_EXISTING);
                            removeEmptyDirectories(toRemove.getParentFile());
                        }
                    }
                }
            }
        }
        catch (IOException e)
        {
            postEvent(Events.ErrorOccurred, "Unable to cleanup destination");
        }
    }

    /**
     * @param file File to be moved
     * @return New location for file
     */
    private String getLeftoverPath(File file)
    {
        String retVal = desLoc.getPath() + "\\" + LEFTOVER_FOLDER + file.getPath().substring(desLoc.getPath().length());
        return retVal;
    }

    /**
     * @param desLoc Destination path
     * @param fileDir Directory of file
     * @param fileName Name of file
     * @return Path of file
     */
    private String getFilePathFromFileCRC(File desLoc, SynchiveDirectory fileDir, String fileName)
    {
        String[] splitDir = fileDir.getUniqueID().split(": ");
        String[] splitFile = fileName.split("\"");
        String retVal = desLoc.getPath() + (splitDir.length == 2 ? splitDir[1] : "") + "\\" + splitFile[1];
        return retVal;
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
}
