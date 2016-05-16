package gui.tabbedPanels;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import gui.tabbedPanels.FlagPanel.FlagPanelDelegate;

/**
 * JTabbedPane to handle all the different views
 * @author Tony Hsu
 */
@SuppressWarnings("serial")
public class TabbedContainerPaneView extends JTabbedPane
{    
    /**
     * Delegate methods for TabbedContainerPaneView
     */
    public interface TabbedContainerPaneViewDelegate {
        /**
         * Event notifier when tab focus changed
         * @param index Tab index of focus
         */
        public void tabChangedIndex(int index);
    }
    
    /**
     * First tabbed Panel (Settings)
     */
    private JPanel flagPanel;
    /**
     * Second tabbed Panel (CRC Options)
     */
    private JPanel crcOptionPanel;
    /**
     * Third tabbed Panel (Audit Log)
     */
    private JPanel auditPanel;
    /**
     * Fourth tabbed Panel (Error Log)
     */
    private JPanel errorLogsPanel;
    
    /**
     * Initializes the view.
     * @param bounds Size of the tabbedPane bounds
     * @param prefSize Preferred size of the tabbedPane
     * @param delegate Controller to handle events
     */
    public TabbedContainerPaneView(Rectangle bounds, Dimension prefSize, TabbedContainerPaneViewDelegate delegate) 
    {
        super(JTabbedPane.TOP);
        initialize(bounds, prefSize, delegate);
    }
    
    /**
     * Initialize the contents of the view.
     */
    private void initialize(Rectangle rect, Dimension prefSize, TabbedContainerPaneViewDelegate delegate)
    {
        setBounds(rect);
        setPreferredSize(prefSize);
        
        flagPanel = new FlagPanel((FlagPanelDelegate)delegate);
        addTab("Flags", null, flagPanel, null);
        
        crcOptionPanel = new CRCOptionsPanel((CRCOptionsPanelDelegate)delegate);
        addTab("CRC Options", null, crcOptionPanel, null);
        
        auditPanel = new AuditPanel();
        addTab("Audit (Logs)", null, auditPanel, null);
        
        errorLogsPanel = new ErrorPanel();
        addTab("Error Logs", null, errorLogsPanel, null);

        addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                delegate.tabChangedIndex(getSelectedIndex());
            }
        });
    }
    
    public void clearLogs()
    {
        ((AuditPanel)auditPanel).clear();
        ((ErrorPanel)errorLogsPanel).clear();
    }
    
    public String getErrorLogs()
    {
        return ((ErrorPanel)errorLogsPanel).getLog();
    }
    
    public JPanel getFlagPanel()
    {
        return flagPanel;
    }
    
    public JPanel getCrcOptionPanel()
    {
        return crcOptionPanel;
    }
    
    public JPanel getAuditPanel()
    {
        return auditPanel;
    }
    
    public JPanel getErrorLogsPanel()
    {
        return errorLogsPanel;
    }
}
