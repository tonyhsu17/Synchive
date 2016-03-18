
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.JRadioButton;
import javax.swing.JTextField;

import tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import tabbedPanels.FlagPanel.CompletionOptions;
import tabbedPanels.FlagPanel.FlagPanelDelegate;
import tabbedPanels.TabbedContainerPaneView;

public class TabbedController implements FlagPanelDelegate, CRCOptionsPanelDelegate
{
    private TabbedContainerPaneView tabView;
    private CompletionOptions afterCompletion;
    private int auditTrailFlag, crcCheckFlag;
    private SummaryController summaryVC;
    
    public TabbedController(SummaryController sumVC) 
    {
        summaryVC = sumVC;
        tabView = new TabbedContainerPaneView(new Rectangle(7, 58, 498, 169), new Dimension(5, 150), this);
    }
    
    public TabbedContainerPaneView getView()
    {
        return tabView;
    }

    @Override
    public void crcDelimiterFinished(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void crcLeadingDelimiterFinished(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void crcTrailingDelimiterFinished(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void noDelimiterStateChange(JRadioButton button, ItemEvent state)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void auditTrailStateChange(JRadioButton button, int state)
    {
        auditTrailFlag = state;
    }

    @Override
    public void crcCheckStateChange(JRadioButton button, int state)
    {
        crcCheckFlag = state;
        
    }

    @Override
    public void afterCompletionOptionChange(JRadioButton button, CompletionOptions option)
    {
        afterCompletion = option;
    }

    @Override
    public void runNuttySync(JRadioButton button, int state)
    {
        summaryVC.runNuttySync();
    }
    
}
