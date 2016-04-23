package gui.tabbedPanels;

import java.awt.Color;
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
import gui.tabbedPanels.TabbedContainerPaneView.TabbedContainerPaneViewDelegate;
import support.BlinkTab;
import synchive.EventCenter;
import synchive.Settings;
import synchive.EventCenter.Events;

public class TabbedController implements FlagPanelDelegate, CRCOptionsPanelDelegate, TabbedContainerPaneViewDelegate
{
    private TabbedContainerPaneView tabView;
    private SummaryController summaryVC;
    private int id; 
    private BlinkTab errorColorState;
    private final static int ERROR_TAB_INDEX = 3;
    
    public TabbedController(SummaryController sumVC) 
    {
        Settings s = Settings.getInstance();
        summaryVC = sumVC;
        id = this.hashCode();
        tabView = new TabbedContainerPaneView(new Rectangle(7, 58, 498, 195), new Dimension(5, 150), this);
        errorColorState = new BlinkTab(tabView, 3, Color.red);
        
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
            subscribeToAdditionalNotifications();
        //always subscribe to errors
        EventCenter.getInstance().subscribeEvent(Events.ErrorOccurred, id, (text) -> {
            ((ErrorPanel)tabView.getErrorLogsPanel()).print((String)text);
            //blink tab indicating something outputted
            errorColorState.startBlinking();
        });
    }
    
    public TabbedContainerPaneView getView()
    {
        return tabView;
    }
    
    private void subscribeToAdditionalNotifications()
    {
        EventCenter.getInstance().subscribeEvent(Events.ProcessingFile, id, (text) -> {
            ((AuditPanel)tabView.getAuditPanel()).print((String)text);
        });
        EventCenter.getInstance().subscribeEvent(Events.Status, id, (text) -> {
            ((AuditPanel)tabView.getAuditPanel()).print((String)text);
        });
        EventCenter.getInstance().subscribeEvent(Events.RunningStatus, id, (arr) -> {
            if(((Object[])arr)[0] == EventCenter.RunningStatusEvents.Completed ||
                ((Object[])arr)[0] == EventCenter.RunningStatusEvents.Error) 
            {
                ((FlagPanel)tabView.getFlagPanel()).getRunButton().setEnabled(true);
            }
            else
            {
                ((FlagPanel)tabView.getFlagPanel()).getRunButton().setEnabled(false);
            }
        });
    }
    
    public void clearLogs()
    {
        tabView.clearLogs();
    }
    
    @Override
    public void runNuttySync(JButton button)
    {
        summaryVC.runSynchiveDiffer();
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

    @Override
    public void tabChangedIndex(int index)
    {
        if(index == ERROR_TAB_INDEX) 
        {
            errorColorState.stopBlinking();
        }
    }
    
}
