import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Support.FileDrop;
public class SummaryView
{
    public interface SummaryViewDelegate {
        public void setTest(int i);
        
        public static void testMethod() {
            System.out.println("");
        }
    }
    
    private SummaryController vc;
    private SummaryViewDelegate delegate;
    private JFrame nuttySyncFrame;
    /**
     * Create the application.
     */
    public SummaryView(SummaryViewDelegate delegate)
    {
        this.delegate = delegate;
        initialize();
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        nuttySyncFrame = new JFrame();
        nuttySyncFrame.setTitle("NuttySync");
        nuttySyncFrame.setBounds(100, 100, 526, 320);
        nuttySyncFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel sourceLabel = new JLabel("Source:");
        sourceLabel.setPreferredSize(new Dimension(57, 14));
        sourceLabel.setMinimumSize(new Dimension(57, 14));
        sourceLabel.setMaximumSize(new Dimension(57, 14));
        sourceLabel.setBounds(7, 7, 67, 22);
        sourceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sourceLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        JTextField sourceTextField = new JTextField();
        sourceTextField.setBounds(93, 7, 352, 22);
        sourceTextField.setMinimumSize(new Dimension(50, 20));
        sourceTextField.setToolTipText("");
        sourceTextField.setColumns(10);
        
        JButton sourceMoreButton = new JButton("...");
        sourceMoreButton.setBounds(463, 7, 40, 22);
        sourceMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sourceMoreButton.setMinimumSize(new Dimension(23, 23));
        sourceMoreButton.setMaximumSize(new Dimension(45, 22));
        sourceMoreButton.setDefaultCapable(false);
        sourceMoreButton.setFocusPainted(false);
        
        new FileDrop(sourceTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                System.out.println(files[0].getPath());
                sourceTextField.setText(files[0].getPath());
                delegate.setTest(1);
                
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setBounds(7, 33, 67, 22);
        destinationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        destinationLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        JTextField destinationTextField = new JTextField();
        destinationTextField.setBounds(93, 33, 352, 22);
        destinationTextField.setMinimumSize(new Dimension(50, 20));
        destinationTextField.setColumns(10);
        
        new FileDrop(destinationTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                System.out.println(files[0].getPath());
                destinationTextField.setText(files[0].getPath());
                
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JButton destinationMoreButton = new JButton("...");
        destinationMoreButton.setBounds(463, 33, 40, 22);
        destinationMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        destinationMoreButton.setMinimumSize(new Dimension(23, 23));
        destinationMoreButton.setMaximumSize(new Dimension(45, 22));
        destinationMoreButton.setDefaultCapable(false);
        destinationMoreButton.setFocusTraversalKeysEnabled(false);
        destinationMoreButton.setFocusPainted(false);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setBounds(7, 58, 498, 169);
        tabbedPane.setPreferredSize(new Dimension(5, 150));
        
        JPanel flagPanel = new JPanel();
        tabbedPane.addTab("Flags", null, flagPanel, null);
        flagPanel.setLayout(null);
        
        JRadioButton auditTrailButton = new JRadioButton("Enable Audit Trail");
        auditTrailButton.setFocusPainted(false);
        auditTrailButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent arg0) {
            }
        });
        auditTrailButton.setBounds(7, 7, 231, 23);
        flagPanel.add(auditTrailButton);
        
        JRadioButton crcCheckButton = new JRadioButton("Enable CRC Check");
        crcCheckButton.setFocusPainted(false);
        crcCheckButton.setBounds(7, 34, 231, 23);
        flagPanel.add(crcCheckButton);
        
        Box horizontalBox_1 = Box.createHorizontalBox();
        horizontalBox_1.setBorder(new LineBorder(Color.LIGHT_GRAY));
        horizontalBox_1.setBounds(7, 64, 476, 2);
        flagPanel.add(horizontalBox_1);
        
        JLabel afterCompletionLabel = new JLabel("After Completion");
        afterCompletionLabel.setBounds(7, 77, 109, 14);
        flagPanel.add(afterCompletionLabel);
        
        JRadioButton doNothingButton = new JRadioButton("Do nothing");
        doNothingButton.setFocusPainted(false);
        doNothingButton.setBounds(7, 95, 109, 23);
        flagPanel.add(doNothingButton);
        
        JRadioButton standbyButton = new JRadioButton("Standby");
        standbyButton.setFocusPainted(false);
        standbyButton.setBounds(191, 95, 109, 23);
        flagPanel.add(standbyButton);
        
        JRadioButton shutdownButton = new JRadioButton("Shutdown");
        shutdownButton.setFocusPainted(false);
        shutdownButton.setBounds(341, 95, 109, 23);
        flagPanel.add(shutdownButton);
        
        JButton btnRun = new JButton("Run");
        btnRun.setFocusPainted(false);
        btnRun.setBounds(244, 7, 240, 50);
        flagPanel.add(btnRun);
        
        JPanel crcOptionPanel = new JPanel();
        crcOptionPanel.setPreferredSize(new Dimension(10, 150));
        crcOptionPanel.setMaximumSize(new Dimension(32767, 165));
        tabbedPane.addTab("CRC Options", null, crcOptionPanel, null);
        crcOptionPanel.setLayout(null);
        
        JLabel delimiterTitleLabel = new JLabel("Use the following delimiters to determine CRC in filename");
        delimiterTitleLabel.setBounds(7, 7, 479, 14);
        crcOptionPanel.add(delimiterTitleLabel);
        
        JLabel crcDelimiterLabel = new JLabel("CRC Delimiter");
        crcDelimiterLabel.setBounds(7, 29, 83, 14);
        crcOptionPanel.add(crcDelimiterLabel);
        
        JTextField crcDelimiterTextField = new JTextField();
        crcDelimiterTextField.setBounds(100, 25, 202, 22);
        crcDelimiterTextField.setMargin(new Insets(1, 2, 3, 2));
        crcDelimiterTextField.setPreferredSize(new Dimension(50, 20));
        crcDelimiterTextField.setMinimumSize(new Dimension(50, 20));
        crcDelimiterTextField.setText("[], {}, (), __");
        crcOptionPanel.add(crcDelimiterTextField);
        crcDelimiterTextField.setColumns(10);
        
        JLabel crcDelimiterExampleLabel = new JLabel("Seperate with ',' (ie. \"[], {}, ()\")");
        crcDelimiterExampleLabel.setBounds(318, 26, 175, 21);
        crcOptionPanel.add(crcDelimiterExampleLabel);
        
        JRadioButton checkWithoutDelimiterButton = new JRadioButton("Check without delimiters");
        checkWithoutDelimiterButton.setBounds(7, 50, 186, 22);
        checkWithoutDelimiterButton.setFocusPainted(false);
        crcOptionPanel.add(checkWithoutDelimiterButton);
        
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBounds(7, 76, 479, 2);
        horizontalBox.setBorder(new LineBorder(Color.LIGHT_GRAY));
        crcOptionPanel.add(horizontalBox);
        
        JRadioButton addCrcFilenameButton = new JRadioButton("Add CRC to filename");
        addCrcFilenameButton.setBounds(7, 82, 143, 22);
        addCrcFilenameButton.setFocusPainted(false);
        addCrcFilenameButton.setToolTipText("Add CRC to both source and destination if CRC not in file name");
        crcOptionPanel.add(addCrcFilenameButton);
        
        JLabel crcDelimiterLeadingLabel = new JLabel("CRC Delimiter - Leading");
        crcDelimiterLeadingLabel.setBounds(7, 112, 143, 14);
        crcOptionPanel.add(crcDelimiterLeadingLabel);
        
        JTextField crcDelimiterLeadingTextField = new JTextField();
        crcDelimiterLeadingTextField.setBounds(155, 109, 70, 21);
        crcOptionPanel.add(crcDelimiterLeadingTextField);
        crcDelimiterLeadingTextField.setColumns(10);
        
        JLabel crcDelimiterTrailingLabel = new JLabel("CRC Delimiter - Trailing");
        crcDelimiterTrailingLabel.setBounds(268, 112, 131, 14);
        crcOptionPanel.add(crcDelimiterTrailingLabel);
        
        JTextField crcDelimiterTrailingTextField = new JTextField();
        crcDelimiterTrailingTextField.setBounds(413, 109, 70, 21);
        crcOptionPanel.add(crcDelimiterTrailingTextField);
        crcDelimiterTrailingTextField.setColumns(10);
        
        JPanel auditPanel = new JPanel();
        tabbedPane.addTab("Audit (Logs)", null, auditPanel, null);
        auditPanel.setLayout(null);
        
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBackground(Color.WHITE);
        scrollPane_1.setBorder(new EmptyBorder(0, 3, 0, 3));
        scrollPane_1.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        scrollPane_1.setBounds(0, 0, 493, 141);
        auditPanel.add(scrollPane_1);
        
        JTextArea auditTextPane = new JTextArea();
        auditTextPane.setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        auditTextPane.setEditable(false);
        auditTextPane.setBorder(null);
        scrollPane_1.setViewportView(auditTextPane);
        auditTextPane.setWrapStyleWord(true);
        auditTextPane.setLineWrap(true);
        auditTextPane.setText("This is a very long text that might occur when we have very long audit log and or long directory nagmes\r\n2\r\n3\r\n4\r\n5\r\n6\r\n7\r\n8\r\n9\r\n0\r\n-");
        nuttySyncFrame.getContentPane().setLayout(null);
        nuttySyncFrame.getContentPane().add(sourceLabel);
        nuttySyncFrame.getContentPane().add(sourceTextField);
        nuttySyncFrame.getContentPane().add(sourceMoreButton);
        nuttySyncFrame.getContentPane().add(destinationLabel);
        nuttySyncFrame.getContentPane().add(destinationTextField);
        nuttySyncFrame.getContentPane().add(destinationMoreButton);
        nuttySyncFrame.getContentPane().add(tabbedPane);
        
        JPanel errorLogsPanel = new JPanel();
        tabbedPane.addTab("Error Logs", null, errorLogsPanel, null);
        errorLogsPanel.setLayout(null);
        
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBackground(Color.WHITE);
        scrollPane.setBorder(new EmptyBorder(0, 3, 0, 3));
        scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        scrollPane.setBounds(0, 0, 493, 141);
        errorLogsPanel.add(scrollPane);
        
        JTextArea textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        scrollPane.setViewportView(textArea);
        
        JLabel sizeReadLabel = new JLabel("Read: ");
        sizeReadLabel.setBounds(7, 238, 99, 14);
        nuttySyncFrame.getContentPane().add(sizeReadLabel);
        
        JLabel sizeReadSpeedLabel = new JLabel("Read Speed:");
        sizeReadSpeedLabel.setBounds(116, 238, 154, 14);
        nuttySyncFrame.getContentPane().add(sizeReadSpeedLabel);
        
        JLabel totalRunningTimeLabel = new JLabel("Running Time:");
        totalRunningTimeLabel.setBounds(280, 238, 223, 14);
        nuttySyncFrame.getContentPane().add(totalRunningTimeLabel);
        
        JProgressBar processingFileProgressBar = new JProgressBar();
        processingFileProgressBar.setBounds(7, 256, 496, 14);
        nuttySyncFrame.getContentPane().add(processingFileProgressBar);
        
    }
    
    public void setVisible(Boolean flag) {
        nuttySyncFrame.setVisible(flag);
    }
}
