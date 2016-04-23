package gui;

import java.awt.EventQueue;
import java.io.File;
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

public class SummaryController implements SummaryViewDelegate, StopWatchDelegate
{
    private SummaryView summaryView;
    private TabbedController tabController;
    
    private StopWatch watch;
    private SynchiveDiff diff;

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
                    diff = new SynchiveDiff(
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
    
    public void completionHandler() throws RuntimeException, IOException
    {
        switch(Settings.getInstance().getCompletionFlag())
        {
            case doNothing:
                break;
            case close:
                System.exit(0);
                break;
            case standBy:
                PowerOptions.standby();
                break;
            case shutdown:
                PowerOptions.shutdown();
                break;
        }
    }
    
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
