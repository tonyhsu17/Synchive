package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import support.FileDrop;
import support.Utilities;
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
    private JTextField crcDelimiterTextField, extensionTypeTextField, 
        crcDelimiterLeadingTextField, crcDelimiterTrailingTextField;
    private JRadioButton checkWithoutDelimiterButton, addCrcFilenameButton;
    
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
        
        crcDelimiterTextField = new JTextField();
        crcDelimiterTextField.setBounds(100, 25, 202, 22);
        crcDelimiterTextField.setMargin(new Insets(1, 2, 3, 2));
        add(crcDelimiterTextField);
        crcDelimiterTextField.setColumns(10);
        crcDelimiterTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                delegate.crcDelimiterTextChanged(crcDelimiterTextField, crcDelimiterTextField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                delegate.crcDelimiterTextChanged(crcDelimiterTextField, crcDelimiterTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });
        
        JLabel crcDelimiterExampleLabel = new JLabel("Seperate with ',' (ie. \"[], {}, ()\")");
        crcDelimiterExampleLabel.setBounds(318, 26, 175, 21);
        add(crcDelimiterExampleLabel);
        
        checkWithoutDelimiterButton = new JRadioButton("Check without delimiters");
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
        
        addCrcFilenameButton = new JRadioButton("Add CRC to filename");
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
        
        extensionTypeTextField = new JTextField();
        extensionTypeTextField.setMargin(new Insets(1, 2, 3, 2));
        extensionTypeTextField.setBounds(155, 108, 328, 22);
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
        new FileDrop(extensionTypeTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String extensions = "";
                        Set<String> extenSet = getExtensionsForFiles(files);
                        for(String str : extenSet)
                        {
                            extensions += ", " + str;
                        }
                        extensionTypeTextField.setText(extensionTypeTextField.getText() + extensions);
                    }
                });
               
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JLabel crcDelimiterLeadingLabel = new JLabel("CRC Delimiter - Leading");
        crcDelimiterLeadingLabel.setBounds(7, 139, 143, 14);
        add(crcDelimiterLeadingLabel);
        
        crcDelimiterLeadingTextField = new JTextField();
        crcDelimiterLeadingTextField.setMargin(new Insets(1, 2, 3, 2));
        crcDelimiterLeadingTextField.setBounds(155, 136, 70, 22);
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
        
        crcDelimiterTrailingTextField = new JTextField();
        crcDelimiterTrailingTextField.setMargin(new Insets(1, 2, 3, 2));
        crcDelimiterTrailingTextField.setBounds(413, 136, 70, 22);
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
    
    /**
     * Returns a set of all possible extension types in a file or directory. Including sub-directories.
     * @param file File or directory to search through
     * @return A set of all extensions in the file or directory
     */
    private Set<String> getExtensionsForFiles(File[] file)
    {
        Set<String> types = new HashSet<String>();
        for(File f : file)
        {
            if(f.isDirectory())
            {
                types.addAll(getExtensionsForFiles(f.listFiles())); // recurse through sub-directories
            }
            else
            {
                types.add(Utilities.getExtensionType(f.getName()));
            }
        }
        return types;
    }
    
    public void loadSettings(String crcDelim, boolean scanWithoutDelim, boolean addCRCFilename, 
        String extensionText, String crcLeadingText, String crcTrailingText)
    {
        crcDelimiterTextField.setText(crcDelim);
        checkWithoutDelimiterButton.setSelected(scanWithoutDelim);
        addCrcFilenameButton.setSelected(addCRCFilename);
        extensionTypeTextField.setText(extensionText);
        crcDelimiterLeadingTextField.setText(crcLeadingText);
        crcDelimiterTrailingTextField.setText(crcTrailingText);
    }
}
