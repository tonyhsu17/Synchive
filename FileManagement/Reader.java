package FileManagement;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import FileManagement.Utilities;

/**
 * 
 */
public class Reader
{
   File folder, crcFile;
   Stack<FileWithProperties> directories;
   BufferedWriter output;
   ArrayList<FileWithProperties> files;
   int location;

   public Reader(File folder, int location) throws IOException
   {
      this.location = location;
      if(folder.isDirectory())
         this.folder = folder;
      else
         throw new Error("ERROR - Not a Directory");

      files = new ArrayList<FileWithProperties>(); //list of all files
      directories = new Stack<FileWithProperties>(); //used to recurse through all folders

      crcFile = new File(folder.getPath() + "\\" + Utilities.CRC_FILE_NAME);
      output = new BufferedWriter(new FileWriter(crcFile));
   }

   //returns all files with unique id
   public ArrayList<FileWithProperties> getCRCValues() throws IOException
   {
      readinDirectory();
      if(location == Utilities.SOURCE)
         crcFile.delete();
      return files;
   }

   public void readinDirectory() throws IOException
   {
      directories.add(new FileWithProperties(folder)); //adds root dir

      while(!directories.isEmpty()) //repeat until all folders are read
      {
         FileWithProperties f = directories.pop();
         output.write(Utilities.convertToDirectoryLvl(f.getPath(), f.getLevel(), folder.getPath()));
         output.newLine();
         readinFilesInDirectory(f); //read the files in the folder
      }
      output.close();
   }

   private void readinFilesInDirectory(FileWithProperties f) throws IOException
   {
      for(File fileEntry : f.listFiles())
      {
         if(fileEntry.isDirectory()) //add child folders to read as well
         {
            directories.push(new FileWithProperties(fileEntry, f.getLevel() + 1));
         }
         else
         {
            System.out.println("Reading file... " + fileEntry.getName());
            FileWithProperties temp = new FileWithProperties(fileEntry, f.getLevel());
            
            if(!temp.getName().equals(Utilities.CRC_FILE_NAME)
                  && !temp.getName().equals(Utilities.AUDIT_FILE_NAME))
            {
               String val = Utilities.calculateCRC32(fileEntry);

               temp.setCRC32Value(Utilities.convertToCRCFile(temp.getName(), val));
               files.add(temp); //adds to file list

               output.write(Utilities.convertToCRCFile(temp.getName(), val));
               output.newLine();
            }
         }
      }
   }
}
