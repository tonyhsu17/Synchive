package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

/**
 * JPanel to handle error logging
 * TODO modularize - make base class for Audit & Error Panel
 * @author Tony Hsu
 */
@SuppressWarnings("serial")
public class ErrorPanel extends JPanel
{
    /**
     * Initializes a JPanel with a JTextArea
     */
    public ErrorPanel()
    {
        super();
        initialize();
    }
    
    /**
     * JTextArea for error logs
     */
    private JTextArea errorTextArea;

    /**
     * Initialize the contents of the view.
     */
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
                errorTextArea.setText("");
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
                errorTextArea.append(str + "\n");
            }
        });
    }
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * @return The entire log in the textArea
     */
    public String getLog()
    {
        return errorTextArea.getText();
    }
}
