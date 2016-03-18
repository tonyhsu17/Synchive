package tabbedPanels;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class AuditPanel extends JPanel
{
    public AuditPanel()
    {
        super();
        initialize();
    }
    
    private void initialize()
    {
        setLayout(null);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBackground(Color.WHITE);
        scrollPane_1.setBorder(new EmptyBorder(0, 3, 0, 3));
        scrollPane_1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        scrollPane_1.setBounds(0, 0, 493, 141);
        add(scrollPane_1);
        
        JTextArea auditTextPane = new JTextArea();
        auditTextPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        auditTextPane.setEditable(false);
        auditTextPane.setBorder(null);
        scrollPane_1.setViewportView(auditTextPane);
        auditTextPane.setWrapStyleWord(true);
        auditTextPane.setLineWrap(true);
        auditTextPane.setText("This is a very long text that might occur when we have very long audit log and or long directory nagmes\r\n2\r\n3\r\n4\r\n5\r\n6\r\n7\r\n8\r\n9\r\n0\r\n-");
       
    }
}
