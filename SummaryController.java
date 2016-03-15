import java.awt.EventQueue;

import javax.swing.UIManager;

public class SummaryController implements SummaryView.SummaryViewDelegate
{
    private SummaryView summaryView;
    private TabbedController tabController;

    SummaryController()
    {
        summaryView = new SummaryView(this);
        tabController = new TabbedController(this);
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
        System.out.println("RUN");
    }

    @Override
    public void setTest(int i)
    {
        System.out.println("YAY");
        // TODO Auto-generated method stub

    }

}
