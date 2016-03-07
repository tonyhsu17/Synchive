import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;

import Support.FileDrop;
import net.miginfocom.swing.MigLayout;

public class MainUI
{

    private JFrame frmNuttysync;
    private JTextField sourceTextField;
    private JTextField destinationTextField;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;

    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                try
                {
                    MainUI window = new MainUI();
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    
                    
                    window.frmNuttysync.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the application.
     */
    public MainUI()
    {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        frmNuttysync = new JFrame();
        frmNuttysync.setTitle("NuttySync");
        frmNuttysync.setBounds(100, 100, 576, 258);
        frmNuttysync.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JLabel sourceLabel = new JLabel("Source:");
        sourceLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        sourceLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        sourceTextField = new JTextField();
        sourceTextField.setToolTipText("");
        sourceTextField.setColumns(10);
        
        JButton sourceMoreButton = new JButton("...");
        sourceMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sourceMoreButton.setMinimumSize(new Dimension(23, 23));
        sourceMoreButton.setPreferredSize(new Dimension(45, 22));
        sourceMoreButton.setMaximumSize(new Dimension(45, 22));
        sourceMoreButton.setDefaultCapable(false);
        sourceMoreButton.setFocusPainted(false);
        
        JLabel destinationLabel = new JLabel("Destination:");
        destinationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        destinationLabel.setFont(new Font("Arial", Font.BOLD, 11));
        
        destinationTextField = new JTextField();
        destinationTextField.setColumns(10);
        
        new FileDrop(sourceTextField, new FileDrop.Listener()
        {
            @Override
            public void filesDropped(File[] files)
            {
                System.out.println(files[0].getPath());
                // TODO Auto-generated method stub
                
            } // end filesDropped
        }); // end FileDrop.Listener
        
        JButton destinationMoreButton = new JButton("...");
        destinationMoreButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        destinationMoreButton.setMinimumSize(new Dimension(23, 23));
        destinationMoreButton.setMaximumSize(new Dimension(45, 22));
        destinationMoreButton.setDefaultCapable(false);
        destinationMoreButton.setFocusTraversalKeysEnabled(false);
        destinationMoreButton.setFocusPainted(false);
        
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        JPanel flagPanel = new JPanel();
        tabbedPane.addTab("Flags", null, flagPanel, null);
        flagPanel.setLayout(null);
        
        JPanel crcOptionPanel = new JPanel();
        tabbedPane.addTab("CRC Options", null, crcOptionPanel, null);
        crcOptionPanel.setLayout(null);
        
        JLabel lblUseTheFollowing = new JLabel("Use the following delimiters to determine CRC in filename");
        lblUseTheFollowing.setBounds(6, 5, 423, 14);
        crcOptionPanel.add(lblUseTheFollowing);
        
        JLabel lblCrcDelimiter = new JLabel("CRC Delimiter");
        lblCrcDelimiter.setBounds(6, 24, 65, 14);
        crcOptionPanel.add(lblCrcDelimiter);
        
        textField_2 = new JTextField();
        textField_2.setBounds(75, 21, 138, 20);
        textField_2.setText("[], {}, (), __");
        crcOptionPanel.add(textField_2);
        textField_2.setColumns(10);
        
        JLabel lblFindCrcValue = new JLabel("Seperate with ',' (ie. \"[], {}, ()\")");
        lblFindCrcValue.setBounds(220, 19, 152, 20);
        crcOptionPanel.add(lblFindCrcValue);
        
        JRadioButton rdbtnIncludeS = new JRadioButton("Check without delimiters");
        rdbtnIncludeS.setBounds(6, 45, 152, 14);
        crcOptionPanel.add(rdbtnIncludeS);
        
        JRadioButton rdbtnAddCrcTo = new JRadioButton("Add CRC to filename");
        rdbtnAddCrcTo.setBounds(6, 79, 125, 14);
        rdbtnAddCrcTo.setToolTipText("Add CRC to both source and destination if CRC not in file name");
        crcOptionPanel.add(rdbtnAddCrcTo);
        
        JLabel lblCrcDelimiter_1 = new JLabel("CRC Delimiter - Leading");
        lblCrcDelimiter_1.setBounds(6, 100, 112, 14);
        crcOptionPanel.add(lblCrcDelimiter_1);
        
        textField_3 = new JTextField();
        textField_3.setBounds(128, 97, 85, 20);
        crcOptionPanel.add(textField_3);
        textField_3.setColumns(10);
        
        JLabel lblCrcDelimiter_2 = new JLabel("CRC Delimiter - Trailing");
        lblCrcDelimiter_2.setBounds(228, 100, 115, 14);
        crcOptionPanel.add(lblCrcDelimiter_2);
        
        textField_4 = new JTextField();
        textField_4.setBounds(346, 97, 83, 20);
        crcOptionPanel.add(textField_4);
        textField_4.setColumns(10);
        
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.setBounds(13, 70, 521, 2);
        horizontalBox.setBorder(new LineBorder(Color.LIGHT_GRAY));
        crcOptionPanel.add(horizontalBox);
        
        JPanel afterCompletionPanel = new JPanel();
        tabbedPane.addTab("After Completion", null, afterCompletionPanel, null);
        
        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Log", null, panel_1, null);
        panel_1.setLayout(null);
        frmNuttysync.getContentPane().setLayout(new MigLayout("", "[66px][5px][417px][23px][34px]", "[24px][23px][149px]"));
        frmNuttysync.getContentPane().add(sourceLabel, "cell 0 0,grow");
        frmNuttysync.getContentPane().add(sourceTextField, "cell 2 0,grow");
        frmNuttysync.getContentPane().add(sourceMoreButton, "cell 4 0,growx,aligny top");
        frmNuttysync.getContentPane().add(destinationLabel, "cell 0 1,grow");
        frmNuttysync.getContentPane().add(destinationTextField, "cell 2 1,grow");
        frmNuttysync.getContentPane().add(destinationMoreButton, "cell 4 1,growx,aligny top");
        frmNuttysync.getContentPane().add(tabbedPane, "cell 0 2 5 1,grow");
    }
    private static void addPopup(Component component, final JPopupMenu popup) {
        component.addMouseListener(new MouseAdapter() {
        	public void mousePressed(MouseEvent e) {
        		if (e.isPopupTrigger()) {
        			showMenu(e);
        		}
        	}
        	public void mouseReleased(MouseEvent e) {
        		if (e.isPopupTrigger()) {
        			showMenu(e);
        		}
        	}
        	private void showMenu(MouseEvent e) {
        		popup.show(e.getComponent(), e.getX(), e.getY());
        	}
        });
    }
}
