package FileManagement;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import Support.Utilities;


/** @Category: Processing Class: To generate a list of crc values for each file in the directory
 * @Structure: A ArrayList of FileWithProperties that include it's unique id
 * @Process: Add initial directory to a stack. While the stack is not empty, remove the directory
 *           and process
 *           all files in the directory. If the file is a directory, add it to the stack. Repeat the
 *           process
 *           until the stack is empty. If it is a file, generate unique id with crc value. If
 *           location is source, deletes
 *           previous crcFile and generates a new one with an arrayList to be used */
public class Reader
{
    File folder, crcFile;
    Stack<FileWithProperties> directories;
    BufferedWriter output;
    ArrayList<FileWithProperties> files;
    int location;

    // folder = folder location
    // location = constant DES or SRC
    public Reader(File folder, int location) throws IOException
    {
        this.location = location;
        if(folder.isDirectory())
            this.folder = folder;
        else
            throw new Error("ERROR - Not a Directory");

        files = new ArrayList<FileWithProperties>(); // list of all files
        directories = new Stack<FileWithProperties>(); // used to recurse through all folders

        crcFile = new File(folder.getPath() + "\\" + Utilities.CRC_FILE_NAME);
        output = new BufferedWriter(new FileWriter(crcFile));
    }

    // returns all files with unique id
    public ArrayList<FileWithProperties> getCRCValues() throws IOException
    {
        readinDirectory();
        if(location == Utilities.SOURCE) // delete source crc file as new one will be generated
            crcFile.delete();
        return files;
    }

    // scans through directory and calls helper method to calculate crc value
    public void readinDirectory() throws IOException
    {
        directories.add(new FileWithProperties(folder)); // adds root dir

        while(!directories.isEmpty()) // repeat until all folders are read
        {
            FileWithProperties f = directories.pop();
            output.write(Utilities.convertToDirectoryLvl(f.getPath(), f.getLevel(), folder.getPath()));
            output.newLine();
            readinFilesInDirectory(f); // read the files in the folder
        }
        output.close(); // close new crc file
    }

    // helper method for readinDirectory()
    private void readinFilesInDirectory(FileWithProperties f) throws IOException
    {
        for(File fileEntry : f.listFiles())
        {
            if(fileEntry.isDirectory()) // add child folders to read as well
            {
                directories.push(new FileWithProperties(fileEntry, f.getLevel() + 1));
            }
            else
            {
                System.out.println("Reading file... " + fileEntry.getName());
                FileWithProperties temp = new FileWithProperties(fileEntry, f.getLevel());

                if(!temp.getName().equals(Utilities.CRC_FILE_NAME) // skip over generated files
                && !temp.getName().equals(Utilities.AUDIT_FILE_NAME))
                {
                    String val = Utilities.calculateCRC32(fileEntry); // get crc value

                    temp.setCRC32Value(Utilities.convertToCRCFile(temp.getName(), val)); // uid
                    files.add(temp); // adds to file list

                    output.write(Utilities.convertToCRCFile(temp.getName(), val)); // write to file
                    output.newLine();
                }
            }
        }
    }
}
