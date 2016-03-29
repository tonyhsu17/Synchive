package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import synchive.Settings;

@SuppressWarnings("serial")
public class CRCOptionsPanel extends JPanel
{
    public interface CRCOptionsPanelDelegate {
        public void crcDelimiterTextChanged(JTextField field, String str);
        public void checkWithoutDelimStateChange(JRadioButton button, int state);
        
        public void addCrcToFileNameStateChanged(JRadioButton button, int state);
        public void crcForExtensionTypeTextChanged(JTextField field, String str);
        public void crcLeadingDelimiterTextChanged(JTextField field, String str);
        public void crcTrailingDelimiterTextChanged(JTextField field, String str);
    }
    
    private CRCOptionsPanelDelegate delegate;
    
    public CRCOptionsPanel(CRCOptionsPanelDelegate delegate)
    {
        super();
        initialize();
        this.delegate = delegate;
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
        checkWithoutDelimiterButton.setSelected(Settings.getInstance().getScanWithoutDelimFlag());
        add(checkWithoutDelimiterButton);
        checkWithoutDelimiterButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                delegate.checkWithoutDelimStateChange(checkWithoutDelimiterButton, arg0.getStateChange());
            }
        });
        
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBounds(7, 76, 479, 2);
        horizontalBox.setBorder(new LineBorder(Color.LIGHT_GRAY));
        add(horizontalBox);
        
        JRadioButton addCrcFilenameButton = new JRadioButton("Add CRC to filename");
        addCrcFilenameButton.setBounds(7, 82, 143, 22);
        addCrcFilenameButton.setFocusPainted(false);
        addCrcFilenameButton.setToolTipText("Add CRC to both source and destination if CRC not in file name");
        addCrcFilenameButton.setSelected(Settings.getInstance().getCrcInFilenameFlag());
        add(addCrcFilenameButton);
        addCrcFilenameButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                delegate.addCrcToFileNameStateChanged(addCrcFilenameButton, arg0.getStateChange());
            }
        });
        
        JLabel extensionTypeLabel = new JLabel("Add to Extension Type");
        extensionTypeLabel.setBounds(7, 111, 131, 14);
        add(extensionTypeLabel);
        
        JTextField extensionTypeTextField = new JTextField();
        extensionTypeTextField.setBounds(155, 108, 328, 20);
        extensionTypeTextField.setText(Settings.getInstance().getAddCrcToExtensionTypeText());
        add(extensionTypeTextField);
        extensionTypeTextField.setColumns(10);
        extensionTypeTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                delegate.crcForExtensionTypeTextChanged(extensionTypeTextField, extensionTypeTextField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                delegate.crcForExtensionTypeTextChanged(extensionTypeTextField, extensionTypeTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });
        
        JLabel crcDelimiterLeadingLabel = new JLabel("CRC Delimiter - Leading");
        crcDelimiterLeadingLabel.setBounds(7, 139, 143, 14);
        add(crcDelimiterLeadingLabel);
        
        JTextField crcDelimiterLeadingTextField = new JTextField();
        crcDelimiterLeadingTextField.setBounds(155, 136, 70, 21);
        crcDelimiterLeadingTextField.setText(Settings.getInstance().getCrcDelimLeadingText());
        add(crcDelimiterLeadingTextField);
        crcDelimiterLeadingTextField.setColumns(10);
        crcDelimiterLeadingTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                delegate.crcLeadingDelimiterTextChanged(crcDelimiterLeadingTextField, crcDelimiterLeadingTextField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                delegate.crcLeadingDelimiterTextChanged(crcDelimiterLeadingTextField, crcDelimiterLeadingTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });
        
        JLabel crcDelimiterTrailingLabel = new JLabel("CRC Delimiter - Trailing");
        crcDelimiterTrailingLabel.setBounds(268, 139, 131, 14);
        add(crcDelimiterTrailingLabel);
        
        JTextField crcDelimiterTrailingTextField = new JTextField();
        crcDelimiterTrailingTextField.setBounds(413, 136, 70, 21);
        crcDelimiterTrailingTextField.setText(Settings.getInstance().getCrcDelimTrailingText());
        add(crcDelimiterTrailingTextField);
        crcDelimiterTrailingTextField.setColumns(10);
        crcDelimiterTrailingTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                delegate.crcTrailingDelimiterTextChanged(crcDelimiterTrailingTextField, crcDelimiterTrailingTextField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                delegate.crcTrailingDelimiterTextChanged(crcDelimiterTrailingTextField, crcDelimiterTrailingTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });
    }
}
