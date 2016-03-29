package support;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Scanner;

import fileManagement.SynchiveDirectory;
import fileManagement.SynchiveFile;
import synchive.EventCenter;
import synchive.EventCenter.Events;
import fileManagement.FileProcessor;


/**
 * @Category: Processing Class: Main grunt of the NuttySync
 * @Process: Reads in uid for source and if desCRC does not exist generate file as well as reading uid for des.
 *           After reading in every file, scan through source and for each file in src, mark if found in des, otherwise
 *           copy into des. Afterwards, if file in des has not been marked, "delete" aka move to a separate location.
 */
public class Differ
{
    private String LEFTOVER_FOLDER = FileProcessor.LEFTOVER_FOLDER;
    // srcLoc = source directory, desLoc = destination directory
    // crcFile = source crc file, desAudit = audit file
    private File srcLoc, desLoc, crcFile, desAudit;
    private BufferedWriter desAuditWriter;
    private Hashtable<String, SynchiveDirectory> destinationList; // Level + Directory to DirectoryWithFiles
    private ArrayList<SynchiveFile> sourceCRCFiles; // list of all files in scr location

    public Differ(File curDir, File backupDir)
    {
        this.srcLoc = curDir;
        this.desLoc = backupDir;
        desAudit = new File(desLoc.getPath() + "\\" + Utilities.AUDIT_FILE_NAME);

        try
        {
            desAuditWriter = new BufferedWriter(new FileWriter(desAudit));
            crcFile = new File(backupDir.getPath() + "\\" + Utilities.CRC_FILE_NAME);
            FileProcessor desReader = new FileProcessor(backupDir, Utilities.DESTINATION);
            destinationList = desReader.getDirectoryList();
            desAuditWriter.write("Audit Start");
            desAuditWriter.newLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }
    }

   

    public void syncLocations()
    {
        FileProcessor rd;
        try
        {
            rd = new FileProcessor(srcLoc, Utilities.SOURCE);
            sourceCRCFiles = rd.getCRCFileList();
            
            postEvent(Events.Status, "Comparing Differences...");
            for(int i = 0; i < sourceCRCFiles.size(); i++)
            {
                // get file to search and search in hashTable of directories
                SynchiveFile temp = sourceCRCFiles.get(i); // file to parse through
                SynchiveDirectory dir =
                    destinationList.get(Utilities.convertToDirectoryLvl(
                        temp.getParentFile().getPath(), temp.getLevel(), srcLoc.getPath()));
                boolean isRoot = temp.getParent().equals(srcLoc.getPath()) ? true : false; // if file is in root dir

                if(dir != null && dir.getFiles().size() > 0)
                {
                    // if directory exist find file in director
                    boolean flag = dir.doesFileExist(temp.getUniqueID());
                    if(!flag) // file not exist
                    {
                        dir.addFile(temp.getUniqueID(), SynchiveDirectory.FileFlag.FILE_EXIST); // add to hashTable
                        copyFile(temp, StandardCopyOption.REPLACE_EXISTING); // Copy file over
                        postEvent(Events.ProcessingFile, isRoot ? "Added \"" + temp.getName() + "\" to \"root\"" : 
                            "Added \"" + temp.getName() + "\" to \"" + dir.getRealFolderName() + "\"");
                    }
                }
                else
                {
                    // make new directory
                    String relativeDir = isRoot ? "\\" : temp.getParentFile().getName();
                    String relativeDirFromRoot = temp.getParent().substring(srcLoc.getPath().length());
                    String destinationDir = desLoc.getPath() + relativeDirFromRoot;
                    File fd = new File(destinationDir);
                    
                    if(!isRoot)
                        createDirectory(fd);
                    
                    SynchiveDirectory newDir =
                        isRoot ? new SynchiveDirectory(Utilities.convertToDirectoryLvl(desLoc.getPath(), 0, desLoc.getPath()))
                            : new SynchiveDirectory(Utilities.convertToDirectoryLvl(fd.getPath(), temp.getLevel(), desLoc.getPath()));

                    newDir.setRealFolderName(relativeDir);
                    newDir.addFile(temp.getUniqueID(), SynchiveDirectory.FileFlag.FILE_EXIST); // add file to new folder
                    destinationList.put(newDir.getFolderName(), newDir); // add newDir to folderHashTable

                    copyFile(temp, StandardCopyOption.REPLACE_EXISTING); // copy file over
                    if(isRoot)
                    {
                        writeToAudit(desLoc, "Added \"" + temp.getName() + "\" to \"root\"");
                    }
                    else
                    {
                        writeToAudit(desLoc, "Added \"" + temp.getName() + "\" to \"" + newDir.getRealFolderName() + "\"");
                    }
                }
            }

            // clean up stuff
            insertToFile(); // write newly added files to crcFile
            desAuditWriter.write("Audit Done");
            desAuditWriter.close();
            System.out.println("Operation Completed");
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

    }

    private void writeToAudit(File location, String msg) throws IOException
    {
        System.out.println("Audit Writing: " + msg);
        if(location == srcLoc)
        {
            System.out.println("Src Audit Writing Failed");
        }
        else if(location == desLoc)
        {
            desAuditWriter.write(msg);
            desAuditWriter.newLine();
        }
    }

    private void createDirectory(File location) throws IOException
    {
        Path parentDir = location.getParentFile().toPath();

        if(!Files.exists(parentDir))
        {
            createDirectory(location.getParentFile());
        }
        writeToAudit(desLoc, "Directory \"" + location.getName() + "\" Created");
        location.mkdir();
    }

    // copies file from source to des with same name and folder
    private void copyFile(File file, StandardCopyOption op) throws IOException
    {
        String relativePath = file.getParent().substring(srcLoc.getPath().length());
        String destinationPath = desLoc.getPath() + relativePath + "\\" + file.getName();
        try
        {
            Files.copy(Paths.get(file.getPath()), Paths.get(destinationPath), op);
        }
        catch (IOException e)
        {
            System.out.println("Unable to copy file " + file.getName());
        }
        // CRC32 Check
        // unoptimized, should grab src crcVal from file if failed... delete file and try again?
        if(!Utilities.calculateCRC32(file).equals(Utilities.calculateCRC32(new File(destinationPath))))
        {
            System.out.println("@@@@@@@@@@@ Copy Error! " + file.getName() + " @@@@@@@@@@@@");
        }
    }

    // copies file from des to leftovers and deletes original file
    private void cutFile(File file, StandardCopyOption op) throws IOException
    {
        String relativePath = file.getParent().substring(desLoc.getPath().length());
        String destinationPath = desLoc.getPath() + "\\" + LEFTOVER_FOLDER + relativePath + "\\" + file.getName();

        try
        {
            Files.copy(Paths.get(file.getPath()), Paths.get(destinationPath), op);
            if(!Utilities.calculateCRC32(file).equals(Utilities.calculateCRC32(new File(destinationPath))))
            {
                System.out.println("@@@@@@@@@@@ Copy Error! " + file.getName() + " @@@@@@@@@@@@");
            }
            file.delete();
            writeToAudit(desLoc,
                "File \"" + file.getName() + "\" in \"" + relativePath + "\" not found in source. Moved to \"" + LEFTOVER_FOLDER + "\"");
        }
        catch (IOException e)
        {
            System.out.println("Unable to cut file " + file.getName());
        }
        // CRC32 Check
        // unoptimized, should grab src crcVal from file if failed... delete file and try again?

    }

    private void removeEmptyDirectories(File file) throws IOException
    {
        if(file.isDirectory() && file.list().length == 0)
        {
            File parent = file.getParentFile(); // get parent directory
            file.delete(); // delete current directory
            writeToAudit(desLoc, "Deleted empty directory \"" + file.getName());

            if(parent.getPath().equals(desLoc.getPath())) // safety check
                return;
            removeEmptyDirectories(parent);
        }
    }

    // rewrites crcFile in des to include new files
    private void insertToFile()
    {
        System.out.println("Rewritting CRC file...");
        try
        {
            crcFile.delete();
            BufferedWriter output = new BufferedWriter(new FileWriter(crcFile));
            Enumeration<String> enu = destinationList.keys();
            while(enu.hasMoreElements()) // go through folders
            {
                String folderName = enu.nextElement();
                output.write(folderName);
                output.newLine();
                // go through files in folder
                SynchiveDirectory dir = destinationList.get(folderName);
                Enumeration<String> enuFiles = dir.getFiles().keys();
                while(enuFiles.hasMoreElements())
                {
                    String fileCRC = enuFiles.nextElement();
                    SynchiveDirectory.FileFlag val = dir.getValueForKey(fileCRC);
                    if(val != null)
                    {
                        if(val == SynchiveDirectory.FileFlag.FILE_EXIST) // only write matching files
                        {
                            output.write(fileCRC);
                            output.newLine();
                        }
                        else if(val == SynchiveDirectory.FileFlag.FILE_NOT_EXIST)
                        {
                            // make leftover folder for not found ones
                            File leftoversFolder = new File(desLoc.getPath() + "\\" + LEFTOVER_FOLDER);
                            if(!Files.exists(leftoversFolder.toPath()))
                                createDirectory(leftoversFolder);

                            // Get file that needs to be removed
                            String filePath = getFilePathFromFileCRC(desLoc, dir, fileCRC);
                            File toRemove = new File(filePath);
                            String toRemoveLeftoverPath = getLeftOverPath(toRemove);
                            File toRemoveLeftover = new File(toRemoveLeftoverPath);
                            if(!Files.exists(toRemoveLeftover.getParentFile().toPath()))
                                createDirectory(toRemoveLeftover.getParentFile());

                            cutFile(toRemove, StandardCopyOption.REPLACE_EXISTING);
                            removeEmptyDirectories(toRemove.getParentFile());
                        }
                    }
                }
            }
            output.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private String getLeftOverPath(File file)
    {
        String retVal = desLoc.getPath() + "\\" + LEFTOVER_FOLDER + file.getPath().substring(desLoc.getPath().length());
        return retVal;
    }

    private String getFilePathFromFileCRC(File desLoc, SynchiveDirectory fileDir, String fileName)
    {
        String[] splitDir = fileDir.getFolderName().split(": ");
        String[] splitFile = fileName.split("\"");
        String retVal = desLoc.getPath() + splitDir[1] + "\\" + splitFile[1];
        return retVal;
    }
    
    private void postEvent(Events e, String str)
    {
        EventCenter.getInstance().postEvent(e, str);
    }
}
