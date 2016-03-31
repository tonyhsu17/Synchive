package synchive;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gui.SummaryController;

/** Created by Tony Hsu. Free to use with credit. Overview: Used as a backup
 * tool, NuttySync will sync one directory (source or master) and sub folders to
 * another directory (destination or backup). Any files not in destination but
 * in source will be moved to a dump folder. In order to achieve faster
 * performance in subsequent runs, a file will be generated that lists all items
 * in the directory. This file will be used as an alternative to reading in each
 * item in destination. */
public class Synchive
{
    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        SummaryController controller = new SummaryController();
        controller.run();
    }
    
    // String str = "ō"; //force file into UTF-8 encoding to change runtime
    // environment encoding
}
