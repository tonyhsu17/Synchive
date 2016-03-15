import java.awt.EventQueue;

import javax.swing.UIManager;


public class SummaryController implements SummaryView.SummaryViewDelegate
{
    private SummaryView view;

    SummaryController()
    {
        view = new SummaryView(this);
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

                    System.out.println("Look and feel: " + UIManager.getSystemLookAndFeelClassName());
                    
                    view.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setTest(int i)
    {
        System.out.println("YAY");
        // TODO Auto-generated method stub

    }

}
