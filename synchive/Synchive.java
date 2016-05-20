package synchive;

import java.io.File;

import gui.SummaryController;
import synchive.EventCenter.Events;
import synchive.EventCenter.RunningStatusEvents;


/**
 * Created by Tony Hsu.
 * Overview: Used as a backup tool, NuttySync will sync one directory (source or master) and sub folders to
 * another directory (destination or backup). Any files not in destination but
 * in source will be moved to a dump folder. In order to achieve faster
 * performance in subsequent runs, a file will be generated that lists all items
 * in the directory. This file will be used as an alternative to reading in each
 * item in destination.
 */
public class Synchive
{
    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        boolean showGUI = true;
        boolean loadSettings = true;
        String[] locations = {"", ""};
        
        // parse arguments
        if(args.length > 0)
        {
            int index = 0;
            for(String str : args)
            {
                switch (str.toLowerCase().trim())
                {
                    case "-nogui": // don't show GUI and run program immediately
                        showGUI = false;
                        break;
                    case "-default": // load default settings
                        loadSettings = false;
                        break;
                    default:
                        if(!str.trim().startsWith("-") && index < locations.length)
                        {
                            locations[index] = str;
                            index++;
                        }
                        break;
                }
            }
        }
        
        if(!loadSettings)
        {
            Settings.getInstance().resetToDefaults();
        }
        
        if(showGUI)
        {
            SummaryController controller = new SummaryController();
            controller.run();
        }
        else
        {
            Thread executionThread = new Thread()
            {
                public void run() {
                    Settings.getInstance().saveSettings();
                    EventCenter.getInstance().postEvent(Events.RunningStatus, 
                        new Object[] {EventCenter.RunningStatusEvents.Running, "Running"});
                    
                    try
                    {
                        SynchiveDiff diff = new SynchiveDiff(
                            new File(locations[0]), new File(locations[1]));
                        diff.syncLocations();
                    }
                    catch (Error e)
                    {
                        EventCenter.getInstance().postEvent(Events.RunningStatus, 
                            new Object[] {RunningStatusEvents.Error, "Error"});
                    }
                }
            };
            executionThread.start();
        }
    }
    // String str = "ō"; //force file into UTF-8 encoding to change runtime
    // environment encoding
}
