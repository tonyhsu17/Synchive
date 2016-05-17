package gui;

import java.awt.EventQueue;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.UIManager;

import gui.SummaryView.SummaryViewDelegate;
import gui.tabbedPanels.TabbedController;
import support.PowerOptions;
import support.StopWatch;
import support.StopWatch.StopWatchDelegate;
import synchive.EventCenter;
import synchive.EventCenter.Events;
import synchive.EventCenter.RunningStatusEvents;
import synchive.Settings;
import synchive.SynchiveDiff;

/**
 * Controller class to manage between comparing files and UI elements (status, directory location). 
 * Also contains controller for tabs. 
 * 
 * @author Tony Hsu
 * @structure Handles changes and updates to/from the view
 */
public class SummaryController implements SummaryViewDelegate, StopWatchDelegate
{
    /**
     * View containing: source, destination locations and progress status 
     */
    private SummaryView summaryView;
    /**
     * Controller for handling tabs and views
     */
    private TabbedController tabController;
    
    /**
     * Keeps track of running time
     */
    private StopWatch watch;

    /**
     * Initializes the GUI
     */
    public SummaryController()
    {
        summaryView = new SummaryView(this);
        tabController = new TabbedController(this);
        summaryView.loadSettings(
            Settings.getInstance().getSourcePath(), Settings.getInstance().getDestinationPath());
        watch = new StopWatch(this);
        
        EventCenter.getInstance().subscribeEvent(Events.RunningStatus, this.hashCode(), (arr) -> {
            summaryView.setStatus("Status - " + ((Object[])arr)[1]);
            if(((Object[])arr)[0] == EventCenter.RunningStatusEvents.Completed)
            {
                watch.stop();
                try
                {
                    completionHandler();
                }
                catch (Exception e)
                {
                    EventCenter.getInstance().postEvent(Events.ErrorOccurred, "Unable to execute Completion Option");
                }
            }
            else if(((Object[])arr)[0] == EventCenter.RunningStatusEvents.Error)
            {
                watch.stop();
            }
        });
    }

    /**
     * Shows the GUI. 
     * TODO refactor?
     */
    public void run()
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    summaryView.getFrame().getContentPane().add(tabController.getView());
                    summaryView.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Start the location synchronization
     * TODO: refactor for cleaner threading aka SynchiveDiff implements Runnable
     */
    public void runSynchiveDiffer()
    {
//        SynchiveDiff diff = new SynchiveDiff(new File("E:\\TestA"), new File("E:\\TestB"));
        Thread executionThread = new Thread()
        {
            public void run() {
                Settings.getInstance().saveSettings();
                tabController.clearLogs();
                watch.start();
                EventCenter.getInstance().postEvent(Events.RunningStatus, 
                    new Object[] {EventCenter.RunningStatusEvents.Running, "Running"});
                
                try
                {
                    SynchiveDiff diff = new SynchiveDiff(
                        new File(Settings.getInstance().getSourcePath()), new File(Settings.getInstance().getDestinationPath()));
                    diff.syncLocations();
                }
                catch (IOException | Error e)
                {
                    EventCenter.getInstance().postEvent(Events.RunningStatus, 
                        new Object[] {RunningStatusEvents.Error, "Error"});
                }
            }
        };
        executionThread.start();
    }
    
    /**
     * Handles what to do after completion. Will write to errors to "output.txt" for close and shutdown option.
     * @throws IOException Unable to write to file
     */
    public void completionHandler() throws IOException
    {
        BufferedWriter output = new BufferedWriter(new FileWriter("output.txt"));
        switch(Settings.getInstance().getCompletionFlag())
        {
            case doNothing:
                output.close();
                break;
            case close:
                output.write(tabController.getView().getErrorLogs());
                output.newLine();
                output.write("Completed in: " + watch.toString());
                output.close();
                System.exit(0);
                break;
            case standBy:
                output.close();
                PowerOptions.standby();
                break;
            case shutdown:
                output.write(tabController.getView().getErrorLogs());
                output.newLine();
                output.write("Completed in: " + watch.toString());
                output.close();
                PowerOptions.shutdown();
                break;
        }
    }
    
    // ~~~~~ Override methods ~~~~~~ //
    @Override
    public void sourceTextChanged(JTextField label, String text)
    {
        Settings.getInstance().setSourcePath(text);
    }

    @Override
    public void destinationTextChanged(JTextField label, String text)
    {
        Settings.getInstance().setDestinationPath(text);
    }

    @Override
    public void timeChanged(StopWatch watch, String string)
    {
        summaryView.setRunningTime("Running Time - " + string);
    }
}
