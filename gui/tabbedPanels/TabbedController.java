package gui.tabbedPanels;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;

import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import gui.SummaryController;
import gui.tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import gui.tabbedPanels.FlagPanel.CompletionOptions;
import gui.tabbedPanels.FlagPanel.FlagPanelDelegate;
import synchive.EventCenter;
import synchive.Settings;
import synchive.EventCenter.Events;

public class TabbedController implements FlagPanelDelegate, CRCOptionsPanelDelegate
{
    private TabbedContainerPaneView tabView;
    private SummaryController summaryVC;
    private int id;
    
    public TabbedController(SummaryController sumVC) 
    {
        Settings s = Settings.getInstance();
        summaryVC = sumVC;
        id = this.hashCode();
        tabView = new TabbedContainerPaneView(new Rectangle(7, 58, 498, 195), new Dimension(5, 150), this);
        
        ((FlagPanel)tabView.getFlagPanel()).loadSettings(
            s.getAuditTrailFlag(), 
            s.getCrcCheckFlag(), 
            s.getSkipFoldersName(), 
            s.getSkipExtensionTypesText(), 
            s.getCompletionFlag());
        
        ((CRCOptionsPanel)tabView.getCrcOptionPanel()).loadSettings(
            s.getCrcDelimiterText(), 
            s.getScanWithoutDelimFlag(), 
            s.getCrcInFilenameFlag(), 
            s.getAddCrcToExtensionTypeText(), 
            s.getCrcDelimLeadingText(), 
            s.getCrcDelimTrailingText());
        
        if(Settings.getInstance().getAuditTrailFlag())
            subscribeToNotifications();
        //always subscribe to errors
        EventCenter.getInstance().subscribeEvent(Events.ErrorOccurred, id, (text) -> {
            ((ErrorPanel)tabView.getErrorLogsPanel()).print((String)text);
        });
    }
    
    public TabbedContainerPaneView getView()
    {
        return tabView;
    }
    
    private void subscribeToNotifications()
    {
        EventCenter.getInstance().subscribeEvent(Events.ProcessingFile, id, (text) -> {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    ((AuditPanel)tabView.getAuditPanel()).print((String)text);
                }
            });
        });
        EventCenter.getInstance().subscribeEvent(Events.Status, id, (text) -> {
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    ((AuditPanel)tabView.getAuditPanel()).print((String)text);
                }
            });
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
        Settings.getInstance().setCrcDelimiterText(str);
    }

    @Override
    public void crcLeadingDelimiterTextChanged(JTextField field, String str)
    {
        Settings.getInstance().setCrcDelimLeadingText(str);
    }

    @Override
    public void crcTrailingDelimiterTextChanged(JTextField field, String str)
    {
        Settings.getInstance().setCrcDelimTrailingText(str);
    }
    
    @Override
    public void checkWithoutDelimStateChange(JRadioButton button, int state)
    {
        Settings.getInstance().setScanWithoutDelimFlag(state == ItemEvent.SELECTED ? true : false); 
    }

    @Override
    public void auditTrailStateChange(JRadioButton button, int state)
    {
        Settings.getInstance().setAuditTrailFlag(state == ItemEvent.SELECTED ? true : false); 
    }

    @Override
    public void crcCheckStateChange(JRadioButton button, int state)
    {
        Settings.getInstance().setCrcCheckFlag(state == ItemEvent.SELECTED ? true : false); 
    }

    @Override
    public void afterCompletionOptionChanged(JRadioButton button, CompletionOptions option)
    {
        Settings.getInstance().setCompletionFlag(option);
    }

    @Override
    public void skipFolderTextChanged(JTextField textField, String str)
    {
        Settings.getInstance().setSkipFoldersName(str);
    }

    @Override
    public void skipExtensionTextChanged(JTextField textField, String str)
    {
        Settings.getInstance().setSkipExtensionTypesText(str);
    }

    @Override
    public void addCrcToFileNameStateChanged(JRadioButton button, int state)
    {
        Settings.getInstance().setCrcInFilenameFlag(state == ItemEvent.SELECTED ? true : false); 
    }

    @Override
    public void crcForExtensionTypeTextChanged(JTextField field, String str)
    {
        Settings.getInstance().setAddCrcToExtensionTypeText(str);
    }
    
}
