package tabbedPanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class CRCOptionsPanel extends JPanel
{
    public interface CRCOptionsPanelDelegate {
        
    }

    public CRCOptionsPanel(CRCOptionsPanelDelegate delegate)
    {
        super();
        initialize();
    }
    
    private void initialize()
    {
        setLayout(null);
        
        JLabel delimiterTitleLabel = new JLabel("Use the following delimiters to determine CRC in filename");
        delimiterTitleLabel.setBounds(7, 7, 479, 14);
        add(delimiterTitleLabel);
        
        JLabel crcDelimiterLabel = new JLabel("CRC Delimiter");
        crcDelimiterLabel.setBounds(7, 29, 83, 14);
        add(crcDelimiterLabel);
        
        JTextField crcDelimiterTextField = new JTextField();
        crcDelimiterTextField.setBounds(100, 25, 202, 22);
        crcDelimiterTextField.setMargin(new Insets(1, 2, 3, 2));
        crcDelimiterTextField.setPreferredSize(new Dimension(50, 20));
        crcDelimiterTextField.setMinimumSize(new Dimension(50, 20));
        crcDelimiterTextField.setText("[], {}, (), __");
        add(crcDelimiterTextField);
        crcDelimiterTextField.setColumns(10);
        
        JLabel crcDelimiterExampleLabel = new JLabel("Seperate with ',' (ie. \"[], {}, ()\")");
        crcDelimiterExampleLabel.setBounds(318, 26, 175, 21);
        add(crcDelimiterExampleLabel);
        
        JRadioButton checkWithoutDelimiterButton = new JRadioButton("Check without delimiters");
        checkWithoutDelimiterButton.setBounds(7, 50, 186, 22);
        checkWithoutDelimiterButton.setFocusPainted(false);
        add(checkWithoutDelimiterButton);
        
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBounds(7, 76, 479, 2);
        horizontalBox.setBorder(new LineBorder(Color.LIGHT_GRAY));
        add(horizontalBox);
        
        JRadioButton addCrcFilenameButton = new JRadioButton("Add CRC to filename");
        addCrcFilenameButton.setBounds(7, 82, 143, 22);
        addCrcFilenameButton.setFocusPainted(false);
        addCrcFilenameButton.setToolTipText("Add CRC to both source and destination if CRC not in file name");
        add(addCrcFilenameButton);
        
        JLabel crcDelimiterLeadingLabel = new JLabel("CRC Delimiter - Leading");
        crcDelimiterLeadingLabel.setBounds(7, 112, 143, 14);
        add(crcDelimiterLeadingLabel);
        
        JTextField crcDelimiterLeadingTextField = new JTextField();
        crcDelimiterLeadingTextField.setBounds(155, 109, 70, 21);
        add(crcDelimiterLeadingTextField);
        crcDelimiterLeadingTextField.setColumns(10);
        
        JLabel crcDelimiterTrailingLabel = new JLabel("CRC Delimiter - Trailing");
        crcDelimiterTrailingLabel.setBounds(268, 112, 131, 14);
        add(crcDelimiterTrailingLabel);
        
        JTextField crcDelimiterTrailingTextField = new JTextField();
        crcDelimiterTrailingTextField.setBounds(413, 109, 70, 21);
        add(crcDelimiterTrailingTextField);
        crcDelimiterTrailingTextField.setColumns(10);
    }
}
