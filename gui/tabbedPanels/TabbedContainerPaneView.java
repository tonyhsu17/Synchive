package gui.tabbedPanels;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gui.tabbedPanels.CRCOptionsPanel.CRCOptionsPanelDelegate;
import gui.tabbedPanels.FlagPanel.FlagPanelDelegate;
import java.awt.Color;

@SuppressWarnings("serial")
public class TabbedContainerPaneView extends JTabbedPane
{    
    public interface TabbedContainerPaneViewDelegate {
        public void tabChangedIndex(int index);
    }
    
    private JPanel flagPanel, crcOptionPanel, auditPanel, errorLogsPanel;
    private Object delegate;
    
    public TabbedContainerPaneView(Rectangle bounds, Dimension prefSize, Object delegate) 
    {
        super(JTabbedPane.TOP);
        this.delegate = delegate;
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

        addChangeListener(new ChangeListener()
        {
            @Override
            public void stateChanged(ChangeEvent e)
            {
                ((TabbedContainerPaneViewDelegate)delegate).tabChangedIndex(getSelectedIndex());
            }
        });
    }
    
    public void clearLogs()
    {
        ((AuditPanel)auditPanel).clear();
        ((ErrorPanel)errorLogsPanel).clear();
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
