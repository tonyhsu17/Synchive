package gui.tabbedPanels;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import gui.SummaryController;
import gui.tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import gui.tabbedPanels.FlagPanel.CompletionOptions;
import gui.tabbedPanels.FlagPanel.FlagPanelDelegate;

public class TabbedController implements FlagPanelDelegate, CRCOptionsPanelDelegate
{
    private TabbedContainerPaneView tabView;
    private CompletionOptions afterCompletion;
    private int auditTrailFlag, crcCheckFlag;
    private SummaryController summaryVC;
    
    public TabbedController(SummaryController sumVC) 
    {
        summaryVC = sumVC;
        tabView = new TabbedContainerPaneView(new Rectangle(7, 58, 498, 195), new Dimension(5, 150), this);
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
    public void runNuttySync(JButton button)
    {
        summaryVC.runNuttySync();
    }
    
}
