package gui.tabbedPanels;

import java.awt.Color;
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
import gui.tabbedPanels.TabbedContainerPaneView.TabbedContainerPaneViewDelegate;
import support.BlinkTab;
import synchive.EventCenter;
import synchive.Settings;
import synchive.EventCenter.Events;

/**
 * Controller class to manage the tabbedViews
 * @author Tony Hsu
 */
public class TabbedController implements FlagPanelDelegate, CRCOptionsPanelDelegate, TabbedContainerPaneViewDelegate
{
    /**
     * View containing all the tabs
     */
    private TabbedContainerPaneView tabView;
    /**
     * Summary View Controller 
     * (need to null this reference if deinit since SummaryController creates TabbedController object)
     */
    private SummaryController summaryVC;
    /**
     * UniqueID of this class used in EventCenter
     */
    private int id; 
    /**
     * Visual indicator of and error occurred
     */
    private BlinkTab errorTabBlinker;
    /**
     * Error tab index
     */
    private final int ERROR_TAB_INDEX = 3;
    
    /**
     * Initializes the inner components of the GUI (tabs)
     * @param sumVC
     */
    public TabbedController(SummaryController sumVC) 
    {
        Settings s = Settings.getInstance();
        summaryVC = sumVC;
        id = this.hashCode();
        tabView = new TabbedContainerPaneView(new Rectangle(7, 58, 498, 195), new Dimension(5, 150), this);
        errorTabBlinker = new BlinkTab(tabView, ERROR_TAB_INDEX, Color.RED);
        
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
        
        subscribeToReqiuiredNotifications();
        subscribeToAuditNotifications();
    }
    
    /**
     * Subscribes to necessary notifications for functionality.
     */
    private void subscribeToReqiuiredNotifications()
    {
      //always subscribe to errors
        EventCenter.getInstance().subscribeEvent(Events.ErrorOccurred, id, (text) -> {
            ((ErrorPanel)tabView.getErrorLogsPanel()).print((String)text);
            errorTabBlinker.startBlinking(); //blink tab indicating something outputted
        });
        // set the state of the "Run" button
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
    
    /**
     * Subscribes to audit logging if option enabled.
     */
    private void subscribeToAuditNotifications()
    {
        if(!Settings.getInstance().getAuditTrailFlag()) 
        {
            return;
        }
        EventCenter.getInstance().subscribeEvent(Events.ProcessingFile, id, (text) -> {
            ((AuditPanel)tabView.getAuditPanel()).print((String)text);
        });
        EventCenter.getInstance().subscribeEvent(Events.Status, id, (text) -> {
            ((AuditPanel)tabView.getAuditPanel()).print((String)text);
        });
    }
    
    /**
     * Clears the audit and error logs. Also stops error flashing.
     */
    public void clearLogs()
    {
        tabView.clearLogs();
        errorTabBlinker.stopBlinking();
    }
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * @return View of the controller
     */
    public TabbedContainerPaneView getView()
    {
        return tabView;
    }
    
    // ~~~~~ Override methods ~~~~~~ //
    /* FlagPanelDelegate */
    @Override
    public void runNuttySync(JButton button)
    {
        summaryVC.runSynchiveDiffer();
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

    /* CRCOptionsPanelDelegate */
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
    public void addCRCToFileNameStateChanged(JRadioButton button, int state)
    {
        Settings.getInstance().setCrcInFilenameFlag(state == ItemEvent.SELECTED ? true : false); 
    }

    @Override
    public void crcForExtensionTypeTextChanged(JTextField field, String str)
    {
        Settings.getInstance().setAddCrcToExtensionTypeText(str);
    }
    
    /* TabbedContainerPaneViewDelegate */
    @Override
    public void tabChangedIndex(int index)
    {
        if(index == ERROR_TAB_INDEX) 
        {
            errorTabBlinker.stopBlinking();
        }
    }
    
}
