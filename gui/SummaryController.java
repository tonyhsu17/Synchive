package gui;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JTextField;
import javax.swing.UIManager;

import gui.SummaryView.SummaryViewDelegate;
import gui.tabbedPanels.TabbedController;
import support.Differ;

public class SummaryController implements SummaryViewDelegate
{
    private SummaryView summaryView;
    private TabbedController tabController;
    private String sourcePath;
    private String destinationPath;

    public SummaryController()
    {
        summaryView = new SummaryView(this);
        tabController = new TabbedController(this);
        sourcePath = "";
        destinationPath = "";
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
        Differ diff = new Differ(new File(sourcePath), new File(destinationPath));
        diff.syncLocations();
    }
    
    @Override
    public void sourceLabelChanged(JTextField label, String text)
    {
        sourcePath = text;
    }

    @Override
    public void destinationLabelChanged(JTextField label, String text)
    {
        destinationPath = text; 
    }

}
