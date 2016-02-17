package FileManagement;
import java.util.Hashtable;

/**
 * @Category: Supporting Class: To store each item (file) with a flag to determine if both source and
 *            destination have the same file.
 * @Structure: A hashtable containing all the items in a directory. Parameters for the hash entry is the file
 *             name and a flag value.
 * @Process: Items are stored initially stored as FILE_NOT_EXIST. Once doesFileExist() is called, if the file
 *           is found, then the FileFlag is modified to FILE_EXIST.
 */
public class DirectoryCRC
{
   // FileFlag is used to determine if FILE_EXIST then no need to copy file over, otherwise if FILE_NOT_EXIST,
   // then the file will need to be copied over.
   public static enum FileFlag
   {
      FILE_EXIST, FILE_NOT_EXIST
   }

   private Hashtable<String, FileFlag> files; // <FileName, FILE_TYPE>
   private String folderName; // name of folder including relative path
   private String realFolderName; // name of folder

   public DirectoryCRC(String folderName)
   {
      this.setFolderName(folderName);
      files = new Hashtable<String, FileFlag>();
   }

   public void addFile(String fileName, FileFlag FILE_FLAG)
   {
      files.put(fileName, FILE_FLAG);
   }

   public FileFlag getValueForKey(String fileName)
   {
      return files.get(fileName);
   }

   public boolean doesFileExist(String fileName)
   {
      FileFlag flag = files.get(fileName);
      //as long as file is found we mark it.
      if(flag != null) // && flag == FILE_NOT_EXIST) // to exclude duplicates
      {
         files.put(fileName, FileFlag.FILE_EXIST);
         return true;
      }
      return false;
   }

   //~~~~~ Getters ~~~~~//
   public String getFolderName()
   {
      return folderName;
   }

   public String getRealFolderName()
   {
      return realFolderName;
   }

   public Hashtable<String, FileFlag> getFiles()
   {
      return files;
   }

   //~~~~~ Setters ~~~~~//
   public void setFolderName(String folderName)
   {
      this.folderName = folderName;
   }

   public void setRealFolderName(String realFolderName)
   {
      this.realFolderName = realFolderName;
   }

   public void setFiles(Hashtable<String, FileFlag> files)
   {
      this.files = files;
   }
}
