
import java.awt.Dimension;
import java.awt.Rectangle;

import tabbedPanels.TabbedContainerPaneView;
import tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import tabbedPanels.FlagPanel.CompletionOptions;
import tabbedPanels.FlagPanel.FlagPanelDelegate;

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
    public void auditTrailButtonStateChange(int state)
    {
        auditTrailFlag = state;
    }

    @Override
    public void crcCheckButtonStateChange(int state)
    {
        crcCheckFlag = state;
    }

    @Override
    public void afterCompletionOptionChange(CompletionOptions option)
    {
        afterCompletion = option;
        // TODO Auto-generated method stub
        /*
        switch (option)
        {
            case doNothing:
                break;
            case standBy:
                break;
            case shutdown:
                break;
            default:
                break;
        }*/
    }

    @Override
    public void runNuttySync()
    {
        summaryVC.runNuttySync();
    }
    
}
