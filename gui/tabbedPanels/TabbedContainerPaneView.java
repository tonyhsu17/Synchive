package gui.tabbedPanels;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import gui.tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import gui.tabbedPanels.FlagPanel.FlagPanelDelegate;

@SuppressWarnings("serial")
public class TabbedContainerPaneView extends JTabbedPane
{    
    private JPanel flagPanel, crcOptionPanel, auditPanel, errorLogsPanel;
    
    public TabbedContainerPaneView(Rectangle bounds, Dimension prefSize, Object delegate) 
    {
        super(JTabbedPane.TOP);
        initialize(bounds, prefSize, delegate);
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize(Rectangle rect, Dimension prefSize, Object delegate)
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
