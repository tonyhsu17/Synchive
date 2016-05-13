package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * JPanel to handle audit logging
 * TODO modularize - make base class for Audit & Error Panel
 * @author Tony Hsu
 */
@SuppressWarnings("serial")
public class AuditPanel extends JPanel
{
    /**
     * Initializes a JPanel with a JTextArea
     */
    public AuditPanel()
    {
        super();
        initialize();
    }
    
    /**
     * JTextArea for audit logs
     */
    private JTextArea auditTextArea;
    
    /**
     * Initialize the contents of the view.
     */
    private void initialize()
    {
        setLayout(null);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBackground(Color.WHITE);
        scrollPane_1.setBorder(new EmptyBorder(0, 3, 0, 3));
        scrollPane_1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        scrollPane_1.setBounds(0, 0, 497, 168);
        add(scrollPane_1);
        
        auditTextArea = new JTextArea();
        auditTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        auditTextArea.setEditable(false);
        auditTextArea.setBorder(null);
        scrollPane_1.setViewportView(auditTextArea);
        auditTextArea.setWrapStyleWord(true);
        auditTextArea.setLineWrap(true);
    }
    
    /**
     * Clears the logs
     */
    public void clear()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                auditTextArea.setText("");
            }
        });
    }
    
    /**
     * Append text to the textArea in the panel
     * @param str Text to be added
     */
    public void print(String str)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                auditTextArea.append(str + "\n");
            }
        });
    }
}
