import java.io.File;
import java.io.IOException;

/** Created by Tony Hsu. Free to use with credit.
 * Overview: Used as a backup tool, NuttySync will sync one directory (source or master) and sub folders to
 * another directory (destination or copy). Any files not in destination but in source will be moved to a dump
 * folder. In order to achieve faster performance in subsequence runs, a file will be generated that lists all
 * items in the directory. This file will be used as an alternative to reading in each item in destination. * 
 */
public class NuttySync
{
	public static void main(String[] args)
	{
		if(args.length != 2)
			return;
		// String curDir = "E:\\Tony Hsu\\Apps";
		// String backupDir = "E:\\Tony Hsu\\TEST";
		// System.out.println("YAY: " + Arrays.toString(args));
		// String curDir = "E:\\Videos\\New Videos\\2015 Winter";
		// String backupDir = "E:\\BACKUP TEST";
		// @SuppressWarnings("unused")
		// String str = "ō"; //force file into UTF-8 encoding to change runtime environment encoding
		// String curDir = "E:\\SOUCE";
		// String backupDir = "E:\\DESTINATION";
		// Differ diff = new Differ(new File(curDir), new File(backupDir));
		Differ diff = new Differ(new File(args[0]), new File(args[1]));
		diff.syncLocations();

		return;
	}

}
