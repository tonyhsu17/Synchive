package gui.tabbedPanels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Set;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import support.FileDrop;
import support.Utilities;
import synchive.Settings;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class FlagPanel extends JPanel
{
    public static enum CompletionOptions { doNothing, standBy, shutdown }
    
    public interface FlagPanelDelegate {
        public void auditTrailStateChange(JRadioButton button, int state);
        public void crcCheckStateChange(JRadioButton button, int state);
        public void afterCompletionOptionChanged(JRadioButton button, CompletionOptions option);
        public void runNuttySync(JButton button);
        public void skipFolderTextChanged(JTextField textField, String str);
        public void skipExtensionTextChanged(JTextField textField, String str);
    }
    
    private JRadioButton previousOptionButton, auditTrailButton, crcCheckButton, 
        doNothingButton, standbyButton, shutdownButton;
    private FlagPanelDelegate delegate;
    private JTextField skipFolderTextField, skipExtensionTextField;

    public FlagPanel(FlagPanelDelegate del)
    {
        super();
        delegate = del;
        initialize();
    }
    
    private void initialize()
    {
        setLayout(null);
        
        auditTrailButton = new JRadioButton("Enable Audit Trail");
        auditTrailButton.setFocusPainted(false);
        auditTrailButton.setBounds(7, 7, 231, 23);
        auditTrailButton.setSelected(Settings.getInstance().getAuditTrailFlag());
        add(auditTrailButton);
        auditTrailButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                delegate.auditTrailStateChange(auditTrailButton, arg0.getStateChange());
            }
        });
        
        crcCheckButton = new JRadioButton("Enable CRC Check");
        crcCheckButton.setFocusPainted(false);
        crcCheckButton.setBounds(7, 34, 231, 23);
        crcCheckButton.setSelected(Settings.getInstance().getCrcCheckFlag());
        add(crcCheckButton);
        crcCheckButton.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent arg0) {
                delegate.crcCheckStateChange(crcCheckButton, arg0.getStateChange());
            }
        });
        
        JButton runButton = new JButton("Run");
        runButton.setFocusPainted(false);
        runButton.setBounds(244, 7, 240, 50);
        add(runButton);
        runButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                delegate.runNuttySync(runButton);
            }
        });
        
        JLabel skipFolderLabel = new JLabel("Skip Folder");
        skipFolderLabel.setToolTipText("");
        skipFolderLabel.setBounds(7, 67, 90, 14);
        add(skipFolderLabel);
        
        skipFolderTextField = new JTextField();
        skipFolderTextField.setToolTipText("Example: \"Folder One\", \"Folder Two\"");
        skipFolderTextField.setBounds(144, 64, 340, 20);
        skipFolderTextField.setText(Settings.getInstance().getSkipFoldersName());
        add(skipFolderTextField);
        skipFolderTextField.setColumns(10);
        skipFolderTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                delegate.skipFolderTextChanged(skipFolderTextField, skipFolderTextField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                delegate.skipFolderTextChanged(skipFolderTextField, skipFolderTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });
        new FileDrop(skipFolderTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String str = skipFolderTextField.getText();
                        for(File file : files)
                        {
                            if(file.isDirectory())
                            {
                             // don't add a comma in the beginning if there is text
                                str += (Utilities.stringEndsWith(str, new String[] {"", ",", " "}) ? "" : ", ") + file.getName() + ", ";
                            }
                        }
                        skipFolderTextField.setText(str);
                        delegate.skipFolderTextChanged(skipFolderTextField, str);
                    }
                });
                
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JLabel skipExtensionLabel = new JLabel("Skip Extension Type");
        skipExtensionLabel.setBounds(7, 92, 129, 14);
        add(skipExtensionLabel);
        
        skipExtensionTextField = new JTextField();
        skipExtensionTextField.setToolTipText("Example: .abc, .xyz,.nmo");
        skipExtensionTextField.setBounds(144, 89, 340, 20);
        skipExtensionTextField.setText(Settings.getInstance().getSkipExtensionTypesText());
        add(skipExtensionTextField);
        skipExtensionTextField.setColumns(10);
        skipExtensionTextField.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                delegate.skipExtensionTextChanged(skipExtensionTextField, skipExtensionTextField.getText());
            }
            
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                delegate.skipExtensionTextChanged(skipExtensionTextField, skipExtensionTextField.getText());
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
            }
        });
        new FileDrop(skipExtensionTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                SwingUtilities.invokeLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        String extensions = skipExtensionTextField.getText();
                        Set<String> extenSet = Utilities.getExtensionsForFiles(files);
                        for(String str : extenSet)
                        {
                            // don't add a comma in the beginning if there is text
                            extensions += (Utilities.stringEndsWith(str, new String[] {"", ",", " "}) ? "" : ", ") + str + ", ";
                        }
                        skipExtensionTextField.setText(extensions);
                        delegate.skipExtensionTextChanged(skipExtensionTextField, extensions);
                    }
                });
            } // end filesDropped
        }); // end FileDrop.Listener
        
        Box horizontalBox_1 = Box.createHorizontalBox();
        horizontalBox_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
        horizontalBox_1.setBounds(8, 114, 476, 2);
        add(horizontalBox_1);
        
        JLabel afterCompletionLabel = new JLabel("After Completion");
        afterCompletionLabel.setBounds(7, 119, 109, 14);
        add(afterCompletionLabel);
        
        // After Completion //
        doNothingButton = new JRadioButton("Do nothing", false);
        doNothingButton.setFocusPainted(false);
        doNothingButton.setBounds(7, 137, 109, 23);
        previousOptionButton = doNothingButton; //used to keep selection highlighted if same clicked
        add(doNothingButton);
        doNothingButton.addMouseListener(new MouseAdapter() {
        public void mouseReleased(MouseEvent arg0) {
            afterCompletionDidChange(arg0, CompletionOptions.doNothing);
            }
        });
        
        standbyButton = new JRadioButton("Standby", false);
        standbyButton.setFocusPainted(false);
        standbyButton.setBounds(191, 137, 109, 23);
        add(standbyButton);
        standbyButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent arg0) {
                afterCompletionDidChange(arg0, CompletionOptions.standBy);
            }
        });
        
        shutdownButton = new JRadioButton("Shutdown", false);
        shutdownButton.setFocusPainted(false);
        shutdownButton.setBounds(341, 137, 109, 23);
        add(shutdownButton);
        shutdownButton.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent arg0) {
                afterCompletionDidChange(arg0, CompletionOptions.shutdown);
            }
        });
    }
    
    private void afterCompletionDidChange(MouseEvent item, CompletionOptions option)
    {
        JRadioButton selectedButton = (JRadioButton)item.getSource();
        if(selectedButton == previousOptionButton)
        {
            previousOptionButton.setSelected(true);
        }
        else
        {
            previousOptionButton.setSelected(false);
            previousOptionButton = selectedButton;
            delegate.afterCompletionOptionChanged(selectedButton, option);
        }
    }
    
    public void loadSettings(boolean auditTrailFlag, boolean crcCheckFlag, String skipFolderText, 
        String skipExenText, CompletionOptions completion)
    {
        auditTrailButton.setSelected(auditTrailFlag);
        crcCheckButton.setSelected(crcCheckFlag);
        skipFolderTextField.setText(skipFolderText);
        skipExtensionTextField.setText(skipExenText);       
        
        switch (completion)
        {
            case doNothing:
                doNothingButton.setSelected(true);
                previousOptionButton = doNothingButton;
                break;
            case standBy:
                standbyButton.setSelected(true);
                previousOptionButton = standbyButton;
                break;
            case shutdown:
                shutdownButton.setSelected(true);
                previousOptionButton = shutdownButton;
                break;
        }
    }
}
