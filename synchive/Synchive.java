package synchive;

import gui.SummaryController;

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
                        Settings.getInstance().resetToDefaults();
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
        
        SummaryController controller = new SummaryController();
        
        if(showGUI)
        {
            controller.run();
        }
        else
        {
            Settings.getInstance().setSourcePath(locations[0]);
            Settings.getInstance().setDestinationPath(locations[1]);
            controller.runSynchiveDiffer();
        }
    }
    // String str = "ō"; //force file into UTF-8 encoding to change runtime
}
