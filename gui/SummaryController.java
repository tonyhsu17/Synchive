package gui;

import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;

import javax.swing.JTextField;
import javax.swing.UIManager;

import gui.SummaryView.SummaryViewDelegate;
import gui.tabbedPanels.TabbedController;
import synchive.Settings;
import synchive.SynchiveDiff;

public class SummaryController implements SummaryViewDelegate
{
    private SummaryView summaryView;
    private TabbedController tabController;

    public SummaryController()
    {
        summaryView = new SummaryView(this);
        tabController = new TabbedController(this);
        summaryView.loadSettings(
            Settings.getInstance().getSourcePath(), Settings.getInstance().getDestinationPath());
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
    
    public void runNuttySync()
    {
//        SynchiveDiff diff = new SynchiveDiff(new File("E:\\TestA"), new File("E:\\TestB"));
        Thread executionThread = new Thread()
        {
            public void run() {
                Settings.getInstance().saveSettings();
                SynchiveDiff diff;
                try
                {
                    diff = new SynchiveDiff(
                        new File(Settings.getInstance().getSourcePath()), new File(Settings.getInstance().getDestinationPath()));
                    diff.syncLocations();
                }
                catch (IOException | Error e)
                {
                }
            }
        };
        executionThread.start();
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

}
