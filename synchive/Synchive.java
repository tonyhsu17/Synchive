package synchive;

import java.io.File;
import java.io.IOException;

import gui.SummaryController;
import synchive.EventCenter.Events;
import synchive.EventCenter.RunningStatusEvents;


/**
 * <p>Used as a backup tool, Synchive will sync directory (source) to a backup location (destination). 
 * Any files in destination not found in source will be moved to a dump folder.
 * In order to achieve faster performance, an idFile will be generated in the destination location.
 * The idFile will be parsed and read-in instead of reading each file. </p>
 * 
 * <p>As the idFile is interchangeable as source or destination, 
 * one may use the destination as the source for another back up. </p>
 * 
 * <p>To achieve quick file reading for source, using (yet to be made: directory monitoring system) 
 * will monitor the source location and keep an up-to-date idFile for the source.</p>
 * 
 * @author Tony Hsu
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
        
        if(!loadSettings) // don't load settings
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
                    catch (Error | IOException e)
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
}
