package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

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
        errorTextArea.setEditable(false);
        errorTextArea.setLineWrap(true);
        errorTextArea.setWrapStyleWord(true);
        scrollPane.setViewportView(errorTextArea);
    }
    
    public void print(String str)
    {
        String text = errorTextArea.getText();
        text += "\n" + str;
        errorTextArea.setText(text);
    }
}
