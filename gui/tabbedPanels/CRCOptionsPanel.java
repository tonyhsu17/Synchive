package gui.tabbedPanels;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
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

/**
 * JPanel to handle CRC Options
 * @author Tony Hsu
 */
@SuppressWarnings("serial")
public class CRCOptionsPanel extends JPanel
{
    /**
     * Delegate methods for CRCOptionsPanel
     */
    public interface CRCOptionsPanelDelegate {
        /**
         * Event notifier if text changed for CRC Delimiters
         * @param field JTextField of text changed
         * @param str New text
         */
        public void crcDelimiterTextChanged(JTextField field, String str);
        /**
         * Event notifier if button pressed for checking without delimiters in filename
         * @param button JRadioButton of button pressed
         * @param state State of the button
         */
        public void checkWithoutDelimStateChange(JRadioButton button, int state);
        
        /**
         * Event notifier if button pressed for adding CRC to filename
         * @param button JRadioButton of button pressed
         * @param state State of the button
         */
        public void addCRCToFileNameStateChanged(JRadioButton button, int state);
        /**
         * Event notifier if text changed for adding CRC to extension type
         * @param field JTextField of text changed
         * @param str New text
         */
        public void crcForExtensionTypeTextChanged(JTextField field, String str);
        /**
         * Event notifier if text changed for leading delimiter when adding CRC to filename
         * @param field JTextField of text changed
         * @param str New text
         */
        public void crcLeadingDelimiterTextChanged(JTextField field, String str);
        /**
         * Event notifier if text changed for trailing delimiter when adding CRC to filename
         * @param field JTextField of text changed
         * @param str New text
         */
        public void crcTrailingDelimiterTextChanged(JTextField field, String str);
    }
    
    /**
     * Common methods for controller to implement to handle events
     */
    private CRCOptionsPanelDelegate delegate;
    /**
     * Check through CRC Delimiter's textField
     */
    private JTextField crcDelimiterTextField;
    /**
     * Add CRC to Extension Type's textField
     */
    private JTextField extensionTypeTextField;
    /**
     * Leading delimiter for adding CRC to filename's textField
     */
    private JTextField crcDelimiterLeadingTextField;
    /**
     * Trailing delimiter for adding CRC to filename's textField
     */
    private JTextField crcDelimiterTrailingTextField;
    /**
     * Include checking with delimiters for CRC's radioButton
     */
    private JRadioButton checkWithoutDelimiterButton;
    /**
     * Include CRC in filename's radioButton
     */
    private JRadioButton addCrcFilenameButton;
    
    /**
     * Initialize the view.
     * @param delegate Controller to handle events
     */
    public CRCOptionsPanel(CRCOptionsPanelDelegate delegate)
    {
        super();
        initialize();
        this.delegate = delegate;
    }
    
    /**
     * Initialize the contents of the view.
     */
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
                delegate.addCRCToFileNameStateChanged(addCrcFilenameButton, arg0.getStateChange());
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
                        String extensions = Utilities.addSeparator(extensionTypeTextField.getText(), ",", true);
                        Set<String> extenSet = Utilities.getExtensionsForFiles(files);
                        
                        for(String str : extenSet)
                        {
                            extensions += str + ", ";
                        }
                        extensionTypeTextField.setText(extensions);
                        delegate.crcForExtensionTypeTextChanged(extensionTypeTextField, extensions);
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
    
    // ~~~~~ Getters & Setters ~~~~~~ //
    /**
     * Set the different options in the view
     * @param crcDelim CRC delimiter to check from
     * @param scanWithoutDelim Flag to check CRC without delimiters
     * @param addCRCFilename Flag to add CRC to filename
     * @param extensionText Extension Types to add CRC to filename
     * @param crcLeadingText Add leading delimiter for CRC in filename
     * @param crcTrailingText Add trailing delimiter for CRC in filename
     */
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
