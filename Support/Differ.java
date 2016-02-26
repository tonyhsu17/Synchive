package Support;

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

import FileManagement.DirectoryCRC;
import FileManagement.FileWithProperties;
import FileManagement.Reader;


/** @Category: Processing Class: Main grunt of the NuttySync
 * @Structure:
 * @Process: Reads in uid for source and if desCRC does not exist. After reading
 *           in every file, scans through array */
public class Differ
{
    private final String                    FOLDER_PREFIX   = "~";
    private final String                    LEFTOVER_FOLDER = "~leftovers";
    // srcLoc = source directory, desLoc = destination directory
    // crcFile = source crc file, desAudit = audit file
    private File                            srcLoc, desLoc, crcFile, desAudit;
    private BufferedWriter                  desAuditWriter;
    private Hashtable<String, DirectoryCRC> folders;                                                     // Level
                                                                                                         // +
                                                                                                         // Directory
                                                                                                         // to
                                                                                                         // DirectoryWithFiles
    private ArrayList<FileWithProperties>   sourceCRCFiles;                                       // list
                                                                                                  // of
                                                                                                  // all
                                                                                                  // files
                                                                                                  // in
                                                                                                  // scr
                                                                                                  // location

    public Differ(File curDir, File backupDir)
    {
        this.srcLoc = curDir;
        this.desLoc = backupDir;
        desAudit = new File(desLoc.getPath() + "\\"
                + Utilities.AUDIT_FILE_NAME);

        try
        {
            desAuditWriter = new BufferedWriter(new FileWriter(desAudit));
            crcFile = new File(backupDir.getPath() + "\\"
                    + Utilities.CRC_FILE_NAME);
            folders = new Hashtable<String, DirectoryCRC>();
            desAuditWriter.write("Audit Start");
            desAuditWriter.newLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        if(!crcFile.exists()) // Case: First Time - If generation of crcFile of
                              // des doesn't exist
        {
            System.out.println("Generating Des CRCFile...");
            generateCRCFile();
            System.out.println("Finished Generating Des CRCFile");
        }
        readinCRCFile();
    }

    private void generateCRCFile()
    {
        try
        {
            Reader rd = new Reader(desLoc, Utilities.DESTINATION);
            rd.readinDirectory();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void readinCRCFile()
    {
        try
        {
            Scanner sc = new Scanner(crcFile);
            String str = sc.nextLine();

            while(sc.hasNextLine() && str.startsWith(FOLDER_PREFIX)) // not
                                                                     // finished
                                                                     // and is a
                                                                     // folder
            {
                DirectoryCRC dir = new DirectoryCRC(str);
                String[] splitStr = str.split(" ");
                dir.setRealFolderName(str.substring(splitStr[0].length() + 1));
                folders.put(str, dir); // store folder in hashtable
                System.out.println("Folder... " + str);
                str = sc.nextLine();
                while(!str.startsWith(FOLDER_PREFIX)) // store files in folder
                {
                    dir.addFile(str, DirectoryCRC.FileFlag.FILE_NOT_EXIST);
                    if(sc.hasNextLine())
                        str = sc.nextLine();
                    else
                        break;
                }
            }
            sc.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void syncLocations()
    {
        Reader rd;
        try
        {
            rd = new Reader(srcLoc, Utilities.SOURCE);
            System.out.println("Generating Source CRCFile...");
            sourceCRCFiles = rd.getCRCValues();
            System.out.println("Finished Generating Source CRCFile");

            System.out.println("Comparing Differences...");
            for(int i = 0; i < sourceCRCFiles.size(); i++)
            {
                boolean isRoot = false;
                // get file to search and search in hashTable of directories
                FileWithProperties temp = sourceCRCFiles.get(i); // file to
                                                                 // parse throgh
                DirectoryCRC dir = folders.get(Utilities.convertToDirectoryLvl(temp.getParentFile().getPath(), temp.getLevel(), srcLoc.getPath()));
                if(temp.getParent().equals(srcLoc.getPath())) // if file is in
                                                              // root dir
                    isRoot = true;
                if(dir != null && dir.getFiles().size() > 0)
                {

                    // if directory exist find file in director
                    boolean flag = dir.doesFileExist(temp.getCRC32Value());
                    if(!flag) // file not exist
                    {
                        dir.addFile(temp.getCRC32Value(), DirectoryCRC.FileFlag.FILE_EXIST); // add
                                                                                             // to
                                                                                             // hashTable
                        copyFile(temp, StandardCopyOption.REPLACE_EXISTING); // Copy
                                                                             // file
                                                                             // over
                        if(isRoot)
                        {
                            writeToAudit(desLoc, "Added \"" + temp.getName()
                                    + "\" to \"root\"");
                        }
                        else
                        {
                            writeToAudit(desLoc, "Added \"" + temp.getName()
                                    + "\" to \"" + dir.getRealFolderName()
                                    + "\"");
                        }
                    }
                }
                else
                {
                    // make new directory
                    String relativeDir = temp.getParentFile().getName();
                    String relativeDirFromRoot = temp.getParent().substring(srcLoc.getPath().length());
                    String destinationDir = desLoc.getPath()
                            + relativeDirFromRoot;
                    if(isRoot)
                        relativeDir = "\\";

                    File fd = new File(destinationDir);
                    if(!isRoot)
                        createDirectory(fd);
                    DirectoryCRC newDir = (isRoot)
                            ? new DirectoryCRC(Utilities.convertToDirectoryLvl(desLoc.getPath(), 0, desLoc.getPath()))
                            : new DirectoryCRC(Utilities.convertToDirectoryLvl(fd.getPath(), temp.getLevel(), desLoc.getPath()));

                    newDir.setRealFolderName(relativeDir);
                    newDir.addFile(temp.getCRC32Value(), DirectoryCRC.FileFlag.FILE_EXIST); // add
                                                                                            // file
                                                                                            // to
                                                                                            // new
                                                                                            // folder
                    folders.put(newDir.getFolderName(), newDir); // add newDir
                                                                 // to
                                                                 // folderHashTable

                    copyFile(temp, StandardCopyOption.REPLACE_EXISTING); // copy
                                                                         // file
                                                                         // over
                    if(isRoot)
                    {
                        writeToAudit(desLoc, "Added \"" + temp.getName()
                                + "\" to \"root\"");
                    }
                    else
                    {
                        writeToAudit(desLoc, "Added \"" + temp.getName()
                                + "\" to \"" + newDir.getRealFolderName()
                                + "\"");
                    }
                }
            }

            // clean up stuff
            insertToFile(); // write newly added files to crcFile
            // srcAuditWriter.write("Audit Done");
            desAuditWriter.write("Audit Done");
            // srcAuditWriter.close();
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
        writeToAudit(desLoc, "Directory \"" + location.getName()
                + "\" Created");
        location.mkdir();
    }

    // copies file from source to des with same name and folder
    private void copyFile(File file, StandardCopyOption op) throws IOException
    {
        String relativePath = file.getParent().substring(srcLoc.getPath().length());
        String destinationPath = desLoc.getPath() + relativePath + "\\"
                + file.getName();
        try
        {
            Files.copy(Paths.get(file.getPath()), Paths.get(destinationPath), op);
        }
        catch (IOException e)
        {
            System.out.println("Unable to copy file " + file.getName());
        }
        // CRC32 Check
        // unoptimized, should grab src crcVal from file
        // if failed... delete file and try again?
        if(!Utilities.calculateCRC32(file).equals(Utilities.calculateCRC32(new File(destinationPath))))
        {
            System.out.println("@@@@@@@@@@@ Copy Error! " + file.getName()
                    + " @@@@@@@@@@@@");
        }
    }

    // copies file from des to leftovers and deletes original file
    private void cutFile(File file, StandardCopyOption op) throws IOException
    {
        String relativePath = file.getParent().substring(desLoc.getPath().length());
        String destinationPath = desLoc.getPath() + "\\" + LEFTOVER_FOLDER
                + relativePath + "\\" + file.getName();

        try
        {
            Files.copy(Paths.get(file.getPath()), Paths.get(destinationPath), op);
            if(!Utilities.calculateCRC32(file).equals(Utilities.calculateCRC32(new File(destinationPath))))
            {
                System.out.println("@@@@@@@@@@@ Copy Error! " + file.getName()
                        + " @@@@@@@@@@@@");
            }
            file.delete();
            writeToAudit(desLoc, "File \"" + file.getName() + "\" in \""
                    + relativePath + "\" not found in source. Moved to \""
                    + LEFTOVER_FOLDER + "\"");
        }
        catch (IOException e)
        {
            System.out.println("Unable to cut file " + file.getName());
        }
        // CRC32 Check
        // unoptimized, should grab src crcVal from file
        // if failed... delete file and try again?

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
            Enumeration<String> enu = folders.keys();
            while(enu.hasMoreElements()) // go through folders
            {
                String folderName = enu.nextElement();
                output.write(folderName);
                output.newLine();
                // go through files in folder
                DirectoryCRC dir = folders.get(folderName);
                Enumeration<String> enuFiles = dir.getFiles().keys();
                while(enuFiles.hasMoreElements())
                {
                    String fileCRC = enuFiles.nextElement();
                    DirectoryCRC.FileFlag val = dir.getValueForKey(fileCRC);
                    if(val != null)
                    {
                        if(val == DirectoryCRC.FileFlag.FILE_EXIST) // only
                                                                    // write
                                                                    // matching
                                                                    // files
                        {
                            output.write(fileCRC);
                            output.newLine();
                        }
                        else if(val == DirectoryCRC.FileFlag.FILE_NOT_EXIST)
                        {
                            // make leftover folder for not found ones
                            File leftoversFolder = new File(desLoc.getPath()
                                    + "\\" + LEFTOVER_FOLDER);
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
        String retVal = desLoc.getPath() + "\\" + LEFTOVER_FOLDER
                + file.getPath().substring(desLoc.getPath().length());
        return retVal;
    }

    private String getFilePathFromFileCRC(File desLoc, DirectoryCRC fileDir,
            String fileName)
    {
        String[] splitDir = fileDir.getFolderName().split(": ");
        String[] splitFile = fileName.split("\"");
        String retVal = desLoc.getPath() + splitDir[1] + "\\" + splitFile[1];
        return retVal;
    }
}
