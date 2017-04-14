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
     */
    public void runSynchiveDiffer()
    {
//        SynchiveDiff diff = new SynchiveDiff(new File("E:\\TestA"), new File("E:\\TestB"));
        File src = new File(Settings.getInstance().getSourcePath());
        File des = new File(Settings.getInstance().getDestinationPath());
        
        EventCenter.getInstance().postEvent(Events.RunningStatus, 
            new Object[] {EventCenter.RunningStatusEvents.Running, "Running"});
        
        Settings.getInstance().saveSettings();
        tabController.clearLogs(); // clear previous logs
        watch.restart(); // starts running time
        
        SynchiveDiff diff;
        try
        {
            diff = new SynchiveDiff(src, des);
            
            Thread executionThread = new Thread(diff);
            executionThread.start(); // run the diff in a separate thread
        }
        catch (IOException e)
        {
            EventCenter.getInstance().postEvent(Events.ErrorOccurred, "Unable to make destination folder.");
        }
        
        
    }
    
    /**
     * Handles what to do after completion. Will write to errors to "output.txt" for close and shutdown option.
     * @throws IOException Unable to write to file
     */
    public void completionHandler() throws IOException
    {
        BufferedWriter output; 
        switch(Settings.getInstance().getCompletionFlag())
        {
            case doNothing:
                break;
            case close:
                output = new BufferedWriter(new FileWriter("output.txt"));
                output.write(tabController.getView().getErrorLogs());
                output.newLine();
                output.write("Completed in: " + watch.toString());
                output.close();
                System.exit(0);
                break;
            case standBy:
                PowerOptions.sleep();
                break;
            case shutdown:
                output = new BufferedWriter(new FileWriter("output.txt"));
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
