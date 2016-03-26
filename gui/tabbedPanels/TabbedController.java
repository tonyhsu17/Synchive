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
import synchive.EventCenter;
import synchive.EventCenter.EventFunction;
import synchive.EventCenter.Events;

public class TabbedController implements FlagPanelDelegate, CRCOptionsPanelDelegate
{
    private TabbedContainerPaneView tabView;
    private CompletionOptions afterCompletion;
    private int auditTrailFlag, crcCheckFlag;
    private SummaryController summaryVC;
    private int id;
    
    public TabbedController(SummaryController sumVC) 
    {
        summaryVC = sumVC;
        id = this.hashCode();
        tabView = new TabbedContainerPaneView(new Rectangle(7, 58, 498, 195), new Dimension(5, 150), this);
        subscribeToNotifications();
    }
    
    public TabbedContainerPaneView getView()
    {
        return tabView;
    }
    
    private void subscribeToNotifications()
    {
        EventCenter.getInstance().subscribeEvent(Events.ProcessingFile, id, (text) -> {
            ((AuditPanel)tabView.getAuditPanel()).print((String)text);
        });
        EventCenter.getInstance().subscribeEvent(Events.ErrorOccurred, id, (text) -> {
            ((ErrorPanel)tabView.getErrorLogsPanel()).print((String)text);
        });
    }
    
    @Override
    public void runNuttySync(JButton button)
    {
        summaryVC.runNuttySync();
    }

    @Override
    public void crcDelimiterTextChanged(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void crcLeadingDelimiterTextChanged(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void crcTrailingDelimiterTextChanged(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void checkWithoutDelimStateChange(JRadioButton button, int state)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void auditTrailStateChange(JRadioButton button, int state)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void crcCheckStateChange(JRadioButton button, int state)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void afterCompletionOptionChanged(JRadioButton button, CompletionOptions option)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void skipFolderTextChanged(JTextField textField, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void skipExtensionTextChanged(JTextField textField, String str)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addCrcToFileNameStateChanged(JRadioButton button, int state)
    {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void crcForExtensionTypeTextChanged(JTextField field, String str)
    {
        // TODO Auto-generated method stub
        
    }
    
}
