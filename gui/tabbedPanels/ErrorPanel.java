package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class ErrorPanel extends JPanel
{
    public ErrorPanel()
    {
        super();
        initialize();
    }
    
    private JTextArea errorTextArea;
    
    private void initialize()
    {
        setLayout(null);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 3, 0, 3));
        scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        scrollPane.setBounds(0, 0, 497, 168);
        add(scrollPane);
        
        errorTextArea = new JTextArea();
        errorTextArea.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        errorTextArea.setEditable(false);
        errorTextArea.setLineWrap(true);
        errorTextArea.setWrapStyleWord(true);
        scrollPane.setViewportView(errorTextArea);
    }
    
    public void clear()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                errorTextArea.setText("");
            }
        });
    }
    
    public String getLog()
    {
        return errorTextArea.getText();
    }
    
    public void print(String str)
    {
        SwingUtilities.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                String text = errorTextArea.getText();
                text += str + "\n";
                
                errorTextArea.setText(text);
            }
        });
    }
}
